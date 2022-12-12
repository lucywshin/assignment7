package model.portfolio;

import common.Utils;
import common.pair.Pair;
import common.triplet.Triplet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.chart.IChart;
import model.chart.IChartService;

/**
 * This class represents a flexible portfolio of stocks which allows the user to buy and sell
 * stocks, measures the cost basis and provides a performance graph.
 */
public class FlexiblePortfolio extends AbstractPortfolio implements IFlexiblePortfolio {

  //<editor-fold desc="State variables">

  private final List<IFlexiblePortfolioStock> stocks;
  private final Set<String> stockSymbolsSet;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  private FlexiblePortfolio() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Creates a {@code FlexiblePortfolio}.
   *
   * @param stockDataSource The data source of stocks to be utilized.
   * @param name            The name of the portfolio.
   * @param stocks          The stocks contained in the portfolio.
   * @throws IllegalArgumentException when an error is present in the provided stocks input.
   */
  public FlexiblePortfolio(IStockDataSource stockDataSource, String name,
      List<IFlexiblePortfolioStock> stocks) throws IllegalArgumentException {
    super(name);

    // setup all stocks available in portfolio
    this.stockSymbolsSet = new HashSet<>();

    this.stocks = new ArrayList<>();
    if (stocks != null) {
      for (IFlexiblePortfolioStock s : stocks) {
        // validate all transactions
        List<Triplet<String, Date, BigDecimal>> transactions = new ArrayList<>();
        for (var transaction : s.getTransactions()) {
          transactions.add(
              new Triplet<>(s.getSymbol(), transaction.getO1(), transaction.getO2().getO1()));
        }
        validateFlexibleStocksInput(transactions);
        validateIPODateAndDelistingDate(stockDataSource, s.getFirstTransaction().getO1(),
            s.getSymbol());

        this.stockSymbolsSet.add(s.getSymbol());
        this.stocks.add(s);
      }
    }
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  protected static void validateFlexibleStocksInput(List<Triplet<String, Date, BigDecimal>> stocks)
      throws IllegalArgumentException {
    if (stocks == null || stocks.size() == 0) {
      throw new IllegalArgumentException("No stocks provided!");
    }

    for (Triplet<String, Date, BigDecimal> stock : stocks) {
      if (stock.getO1().isBlank()) {
        throw new IllegalArgumentException("One or more stock has an empty symbol!");
      }

      if (stock.getO3().floatValue() <= 0) {
        throw new IllegalArgumentException("One or more stock has invalid volume!");
      }
    }
  }

  static void validateIPODateAndDelistingDate(IStockDataSource source, Date date, String symbol)
      throws IllegalArgumentException {
    Date ipoDate = source.getIPODate(symbol);
    Date delistingDate = source.getDelistingDate(symbol);

    if (date.before(ipoDate)) {
      throw new IllegalArgumentException(
          "The transaction date for stock symbol: " + symbol + " cannot be before it's IPO date!");
    }

    if (delistingDate != null && date.after(delistingDate)) {
      throw new IllegalArgumentException(
          "The transaction date for stock symbol: " + symbol
              + " cannot be after it's delisting date!");
    }
  }

  private String generateChartTitle(Date startDate, Date endDate) {
    return "Performance of Portfolio " + this.getName() + " from "
        + Utils.convertDateToDefaultStringFormat(startDate) + " to "
        + Utils.convertDateToDefaultStringFormat(endDate);
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public List<IObservableFlexiblePortfolioStock> getStocks() {
    return this.stocks
        .stream()
        .map(s -> (IObservableFlexiblePortfolioStock) s)
        .collect(Collectors.toList());
  }

  @Override
  public List<Pair<String, BigDecimal>> getComposition(Date date) throws IllegalArgumentException {
    Utils.validateFutureDate(date);

    List<Pair<String, BigDecimal>> result = new ArrayList<>();
    for (IObservableFlexiblePortfolioStock stock : this.getStocks()) {
      BigDecimal currentVolume = new BigDecimal(0);

      for (var transaction : stock.getTransactions()) {
        if (transaction.getO1().after(date)) {
          // the transaction date is after provided date
          // we stop looking through transactions as list is ordered by date
          break;
        }

        currentVolume = currentVolume.add(transaction.getO2().getO1());
      }
      if (currentVolume.compareTo(new BigDecimal(0)) > 0) {
        result.add(new Pair<>(stock.getName(), currentVolume));
      }
    }
    return result;
  }

  private IFlexiblePortfolio addTransaction(IStockDataSource source,
      List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFees, boolean isBuy)
      throws IllegalArgumentException, StockDataSourceException, IllegalStateException {
    validateFlexibleStocksInput(stocks);

    for (Triplet<String, Date, BigDecimal> s : stocks) {
      // check whether provided stock's symbol is a
      // valid stock and provided data is valid

      String newStockSymbol = s.getO1();
      // value is negative if selling
      Date newStockDate = s.getO2();
      BigDecimal newStockVolume = isBuy ? s.getO3() : s.getO3().multiply(new BigDecimal(-1));

      // validate date
      Utils.validateFutureDate(s.getO2());
      validateIPODateAndDelistingDate(source, s.getO2(), s.getO1());

      if (stockSymbolsSet.contains(newStockSymbol)) {
        // update existing FlexiblePortfolioStock by
        // adding transaction to the stock object
        for (IFlexiblePortfolioStock currentStock : this.stocks) {
          if (currentStock.getSymbol().equals(newStockSymbol)) {
            var purchasePrice =
                source.getStockPrice(currentStock.getSymbol(), newStockDate, false);
            currentStock.addTransaction(newStockDate, newStockVolume, purchasePrice,
                commissionFees);

            // no need to go through rest of the list of
            // stocks as we found what we were looking for
            break;
          }
        }
      } else {
        if (isBuy) {
          // add the stock to the portfolio as the stock is new in the portfolio
          IStock sourceStock = source.getStock(newStockSymbol);
          var purchasePrice =
              source.getStockPrice(sourceStock.getSymbol(), newStockDate, false);
          this.stocks.add(
              new FlexiblePortfolioStock(sourceStock.getSymbol(), sourceStock.getName(),
                  sourceStock.getExchange(), newStockVolume, newStockDate, purchasePrice,
                  commissionFees));
          this.stockSymbolsSet.add(newStockSymbol);
        } else {
          throw new IllegalArgumentException("The requested stock doesn't exist in the portfolio!");
        }
      }
    }

    return this;
  }

  @Override
  public IFlexiblePortfolio buyStocks(IStockDataSource source,
      List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFees)
      throws IllegalArgumentException, StockDataSourceException, IllegalStateException {
    return this.addTransaction(source, stocks, commissionFees, true);
  }

  @Override
  public IFlexiblePortfolio sellStocks(IStockDataSource source,
      List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFees)
      throws IllegalArgumentException, StockDataSourceException, IllegalStateException {
    return this.addTransaction(source, stocks, commissionFees, false);
  }

  @Override
  public BigDecimal getCostBasis(IStockDataSource source, Date date)
      throws StockDataSourceException, IllegalArgumentException {
    BigDecimal result = new BigDecimal(0);

    for (IObservableFlexiblePortfolioStock stock : this.getStocks()) {
      BigDecimal stockValue = stock.getCostBasis(source, date);

      result = result.add(stockValue);
    }

    return result;
  }

  @Override
  public Pair<BigDecimal, List<IPortfolioStockValue>> getValue(IStockDataSource source,
      Date date) throws IllegalArgumentException, StockDataSourceException {
    Utils.validateFutureDate(date);

    BigDecimal result = new BigDecimal(0);
    List<IPortfolioStockValue> stockValues = new ArrayList<>();

    for (IObservableFlexiblePortfolioStock stock : this.getStocks()) {
      Pair<BigDecimal, BigDecimal> stockValue = stock.getValueOnDate(source, date);

      stockValues.add(new PortfolioStockValue(stock.getSymbol(), stock.getName(),
          stock.getExchange(), stockValue.getO1(), stockValue.getO2()));
      result = result.add(stockValue.getO2());
    }

    return new Pair<>(result, stockValues);
  }

  @Override
  public IChart getPerformanceChart(IStockDataSource source, IChartService chartService,
      Date startDate, Date endDate) throws IllegalArgumentException, StockDataSourceException {
    Utils.validateFutureDate(startDate);
    Utils.validateStartAndEndDate(startDate, endDate);

    var intervals = chartService.getChartInterval(startDate, endDate);
    List<Pair<Date, Integer>> chartData = new ArrayList<>();

    for (Date date : intervals.getO2()) {
      int value = 0;
      // only populating the value in chart if the date is in the past
      if (date.before(Utils.getTodayDate())) {
        value = this.getValue(source, date).getO1().intValue();
        chartData.add(new Pair<>(date, value));
      } else {
        Date today = Utils.getTodayDate();
        value = this.getValue(source, new Date(today.getYear(), today.getMonth(), today.getDay()))
            .getO1().intValue();
        chartData.add(new Pair<>(date, value));
        break;
      }
    }

    return chartService.generateChart(intervals.getO1(),
        this.generateChartTitle(startDate, endDate), chartData);
  }

  @Override
  public IFlexiblePortfolio addDollarCostInvestment(IStockDataSource source, Date date,
      BigDecimal amount, BigDecimal commissionFees,
      List<Pair<String, BigDecimal>> stocksWithPercentage, boolean isRecurring, Date endDate,
      eRecurringIntervalType intervalType, Integer intervalDelta)
      throws IllegalArgumentException, StockDataSourceException {

    //<editor-fold desc="validations">

    Utils.validatePositiveValue(amount, "Amount");
    Utils.validateNonNegativeValue(commissionFees, "Commission Fees");

    // creation and validation of recurring event
    IRecurringEvent recurringEvent
        = isRecurring ? new RecurringEvent(endDate, intervalType, intervalDelta) : null;

    if (isRecurring && endDate != null) {
      Utils.validateStartAndEndDate(date, endDate);
    }

    // total of percentages has to sum up to 100
    // start date and end date are valid for the stock
    // stocks in list are unique in the given list

    var stocksPercentageTotal = new BigDecimal(0);
    Set<String> stocksList = new HashSet<>();

    for (var swp : stocksWithPercentage) {
      String stockSymbol = swp.getO1();
      if (stocksList.contains(stockSymbol)) {
        throw new IllegalArgumentException("Stocks in the provided list have to be unique!");
      }
      if (swp.getO2().compareTo(new BigDecimal(0)) <= 0) {
        throw new IllegalArgumentException("Percentages have to be positive!");
      }
      stocksList.add(swp.getO1());

      validateIPODateAndDelistingDate(source, date, stockSymbol);
      if (isRecurring && endDate != null) {
        validateIPODateAndDelistingDate(source, endDate, stockSymbol);
      }

      stocksPercentageTotal = stocksPercentageTotal.add(swp.getO2());
    }
    if (!stocksPercentageTotal.equals(new BigDecimal(100))) {
      throw new IllegalArgumentException("The percentages of stocks should total up to 100!");
    }

    //</editor-fold>

    // split stocks with Percentage into Stock-Amount pairs and create DollarCostInvestments,
    // and add the dollar cost investments to FlexiblePortfolioStocks
    for (Pair<String, BigDecimal> swp : stocksWithPercentage) {
      String newStockSymbol = swp.getO1();
      var stockAmount = amount.multiply(swp.getO2()).multiply(new BigDecimal("0.01"));

      IDollarCostInvestment dci = new DollarCostInvestment(date, stockAmount, commissionFees,
          recurringEvent);

      if (this.stockSymbolsSet.contains(newStockSymbol)) {
        for (IFlexiblePortfolioStock currentStock : this.stocks) {
          if (currentStock.getSymbol().equals(newStockSymbol)) {
            currentStock.addStockDollarCostInvestment(source, dci);

            // no need to go through rest of the list of
            // stocks as we found what we were looking for
            break;
          }
        }
      } else {
        // add the stock to the portfolio as the stock is new in the portfolio
        IStock sourceStock = source.getStock(newStockSymbol);
        this.stocks.add(
            new FlexiblePortfolioStock(source, sourceStock.getSymbol(), sourceStock.getName(),
                sourceStock.getExchange(), dci));
        this.stockSymbolsSet.add(newStockSymbol);
      }
    }

    return this;
  }

  @Override
  public List<Pair<String, IDollarCostInvestment>> getDollarCostInvestments() {
    List<Pair<String, IDollarCostInvestment>> result = new ArrayList<>();

    for (var s : this.stocks) {
      List<Pair<String, IDollarCostInvestment>> dollarCostInvestments = s.getDollarCostInvestments()
          .stream()
          .map(dci -> new Pair<>(s.getSymbol(), dci))
          .collect(Collectors.toList());

      result.addAll(dollarCostInvestments);
    }
    return result;
  }

  //</editor-fold>

  //<editor-fold desc="CSV converter methods">

  @Override
  public List<String> toCsvRows() {
    List<String> result = new ArrayList<>();

    for (IFlexiblePortfolioStock stock : this.stocks) {
      for (var stockTransaction : stock.getTransactions()) {
        result.add(this.name + ","
            + stock.getSymbol() + ","
            + stock.getName() + ","
            + stock.getExchange() + ","
            + Utils.convertDateToDefaultStringFormat(stockTransaction.getO1()) + ","
            // transaction Date
            + stockTransaction.getO2().getO1() + "," // transaction Volume
            + stockTransaction.getO2().getO2() + "," // transaction Stock Purchase Price
            + stockTransaction.getO2().getO3() + "\n"); // transaction Commission Fees
      }
    }
    return result;
  }

  //</editor-fold>
}
