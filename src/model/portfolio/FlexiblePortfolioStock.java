package model.portfolio;

import common.Utils;
import common.pair.Pair;
import common.triplet.Triplet;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * This class represents a stock in a portfolio with the name, symbol, exchange it is traded in, the
 * volume of the stock, and the date on which it was purchased.
 */
public class FlexiblePortfolioStock extends PortfolioStock implements IFlexiblePortfolioStock {

  //<editor-fold desc="State variables">

  /**
   * Date of transaction: Volume of transaction, Purchase Price, CommissionFees of transaction.
   */
  // volume is positive when buying, negative when selling
  private final Map<Date, List<Triplet<BigDecimal, BigDecimal, BigDecimal>>> transactionHistory;

  private final Map<Date, IDollarCostInvestment> dollarCostInvestmentMap;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  private FlexiblePortfolioStock() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  FlexiblePortfolioStock(IStockDataSource source, String symbol, String name, String exchange,
      IDollarCostInvestment dollarCostInvestment) throws StockDataSourceException {
    super(symbol, name, exchange, new BigDecimal(0));
    // using Tree map, so we can traverse through items in map in order of dates
    this.transactionHistory = new TreeMap<>();
    this.dollarCostInvestmentMap = new TreeMap<>();

    this.addStockDollarCostInvestment(source, dollarCostInvestment);
  }

  /**
   * Creates a {@code FlexiblePortfolioStock} which contains stock data and volume.
   *
   * @param symbol       The symbol of the stock with which it is traded on the exchange.
   * @param name         The name of the stock.
   * @param exchange     the exchange on which the stock is traded.
   * @param volume       The volume of the stock in the portfolio.
   * @param purchaseDate The date on which the stock was purchased.
   */
  FlexiblePortfolioStock(String symbol, String name, String exchange, BigDecimal volume,
      Date purchaseDate, BigDecimal purchasePrice, BigDecimal commissionFees)
      throws IllegalArgumentException {
    super(symbol, name, exchange, volume);
    Utils.validateFutureDate(purchaseDate);

    // using Tree map, so we can traverse through items in map in order of dates
    this.transactionHistory = new TreeMap<>();
    this.dollarCostInvestmentMap = new TreeMap<>();

    List<Triplet<BigDecimal, BigDecimal, BigDecimal>> stocksInTransaction = new ArrayList<>();
    stocksInTransaction.add(new Triplet<>(volume, purchasePrice, commissionFees));
    transactionHistory.put(purchaseDate, stocksInTransaction);
  }

  /**
   * Creates a {@code FlexiblePortfolioStock} which contains stock data and volume.
   *
   * @param symbol       The symbol of the stock with which it is traded on the exchange.
   * @param name         The name of the stock.
   * @param exchange     the exchange on which the stock is traded.
   * @param totalVolume  The total volume of the stock in the portfolio.
   * @param transactions The transactions made on this flexible portfolio stock.
   */
  FlexiblePortfolioStock(String symbol, String name, String exchange, BigDecimal totalVolume,
      List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>> transactions)
      throws IllegalArgumentException {
    super(symbol, name, exchange, totalVolume);
    this.transactionHistory = new TreeMap<>();
    this.dollarCostInvestmentMap = new TreeMap<>();

    // using Tree map, so we can traverse through items in map in order of dates
    BigDecimal totalVolumeTest = new BigDecimal(0);

    for (var transaction : transactions) {
      totalVolumeTest = totalVolumeTest.add(transaction.getO2().getO1());

      List<Triplet<BigDecimal, BigDecimal, BigDecimal>> currentDateTransactions;

      if (transactionHistory.containsKey(transaction.getO1())) {
        // transactions have already been done on this date
        currentDateTransactions = this.transactionHistory.get(transaction.getO1());
      } else {
        // transaction haven't been done on this date before
        currentDateTransactions = new ArrayList<>();
      }
      currentDateTransactions.add(
          new Triplet<>(transaction.getO2().getO1(), transaction.getO2().getO2(),
              transaction.getO2().getO3()));
      transactionHistory.put(transaction.getO1(), currentDateTransactions);
    }

    // validate whether totalVolume is correct
    if (!totalVolumeTest.equals(totalVolume)) {
      throw new IllegalArgumentException(
          "The total volume does not match the transactions sum of volumes!");
    }

    this.transactionsConsistencyCheck();
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  /**
   * Checks the consistency of the transactions for this stock.
   *
   * @throws IllegalStateException when the consistency is not correct.
   */
  private void transactionsConsistencyCheck() throws IllegalStateException {
    BigDecimal availableVolume = new BigDecimal(0);
    for (var transaction : this.transactionHistory.entrySet()) {
      for (var dateTransaction : transaction.getValue()) {
        availableVolume = availableVolume.add(dateTransaction.getO1());
        if (availableVolume.floatValue() < 0) {
          throw new IllegalStateException("The transaction on Symbol: " + this.getSymbol()
              + " cannot be performed as stock volume cannot be negative at "
              + "any given point in time!");
        }
      }
    }
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>> getFirstTransaction() {
    var firstEntry =
        this.transactionHistory.entrySet().stream().findFirst().get();
    var firstDateEntry = firstEntry.getValue().stream().findFirst().get();
    return new Pair<>(firstEntry.getKey(),
        new Triplet<>(firstDateEntry.getO1(), firstDateEntry.getO2(), firstDateEntry.getO3()));
  }

  @Override
  public List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>> getTransactions() {
    List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>> result = new ArrayList<>();

    for (var transaction : this.transactionHistory.entrySet()) {
      for (var dateTransaction : transaction.getValue()) {
        result.add(new Pair<>(transaction.getKey(),
            new Triplet<>(dateTransaction.getO1(), dateTransaction.getO2(),
                dateTransaction.getO3())));
      }
    }
    return result;
  }

  @Override
  public void addTransaction(Date date, BigDecimal volume, BigDecimal purchasePrice,
      BigDecimal commissionFees)
      throws IllegalArgumentException, IllegalStateException {
    Utils.validateFutureDate(date);
    if (volume.equals(new BigDecimal(0))) {
      throw new IllegalArgumentException("Volume for transaction cannot be 0!");
    }

    // before incorporating given transaction
    this.transactionsConsistencyCheck();

    // check whether the current transaction can be incorporated
    BigDecimal currentVolume = new BigDecimal(0);
    boolean isGivenTransactionTaken = false;

    for (var transaction : this.transactionHistory.entrySet()) {
      if (!isGivenTransactionTaken && transaction.getKey().after(date)) {
        // transaction is after the given date.
        // take the given transaction

        isGivenTransactionTaken = true;
        currentVolume = currentVolume.add(volume);

        if (currentVolume.floatValue() < 0) {
          throw new IllegalStateException("The transaction on Symbol: " + this.getSymbol()
              + " cannot be performed as stock volume cannot be negative "
              + "at any given point in time!");
        }
      }

      for (var dateTransaction : transaction.getValue()) {
        currentVolume = currentVolume.add(dateTransaction.getO1());

        if (currentVolume.floatValue() < 0) {
          throw new IllegalStateException("The transaction on Symbol: " + this.getSymbol()
              + " cannot be performed as stock volume cannot be negative "
              + "at any given point in time!");
        }
      }
    }

    // Incorporate given transaction as consistency is good after taking the transaction
    List<Triplet<BigDecimal, BigDecimal, BigDecimal>> dateTransactions;
    if (this.transactionHistory.containsKey(date)) {
      // update existing entry if date already exists
      dateTransactions = this.transactionHistory.get(date);
    } else {
      // create new entry in transaction list
      dateTransactions = new ArrayList<>();
    }
    dateTransactions.add(new Triplet<>(volume, purchasePrice, commissionFees));
    this.transactionHistory.put(date, dateTransactions);

    // after incorporating given transaction
    this.transactionsConsistencyCheck();

    this.volume = this.volume.add(volume);
  }

  @Override
  public Pair<BigDecimal, BigDecimal> getValueOnDate(IStockDataSource source, Date date)
      throws IllegalArgumentException, StockDataSourceException {
    Utils.validateFutureDate(date);
    BigDecimal volumeOnDate = new BigDecimal(0);

    for (var transaction : this.transactionHistory.entrySet()) {
      if (transaction.getKey().after(date)) {
        break;
      }

      for (var dateTransaction : transaction.getValue()) {
        // value = price * volume
        volumeOnDate = volumeOnDate.add(dateTransaction.getO1());
      }
    }

    return new Pair<>(volumeOnDate,
        source.getStockPrice(this.getSymbol(), date, false).multiply(volumeOnDate));
  }

  @Override
  public BigDecimal getCostBasis(IStockDataSource source, Date date)
      throws IllegalArgumentException, StockDataSourceException {
    BigDecimal costBasisTotal = new BigDecimal(0);

    // adding cost basis from transactions before given date
    for (var transaction : this.transactionHistory.entrySet()) {
      if (transaction.getKey().after(date)) {
        break;
      }

      BigDecimal totalPurchasePrice = new BigDecimal(0);
      BigDecimal commissionFees = new BigDecimal(0);

      for (var dateTransaction : transaction.getValue()) {
        // take volume only if it was a buy transaction
        if (dateTransaction.getO1().compareTo(new BigDecimal(0)) > 0) {
          // purchase value = purchase price * volume
          totalPurchasePrice = totalPurchasePrice
              .add(dateTransaction.getO2().multiply(dateTransaction.getO1()));
        }
        commissionFees = commissionFees
            .add(dateTransaction.getO3());
      }

      costBasisTotal = costBasisTotal
          .add(totalPurchasePrice)
          .add(commissionFees);
    }

    // adding values from dollar cost investments scheduled for future if date is in future
    if (Utils.isFutureDate(date)) {
      for (var dollarCostInvestmentEntry : this.dollarCostInvestmentMap.entrySet()) {
        IDollarCostInvestment dci = dollarCostInvestmentEntry.getValue();

        if (dci.getRecurringEvent() == null) {
          if (dci.getDate().before(date)) {
            costBasisTotal = costBasisTotal
                .add(dci.getAmount())
                .add(dci.getCommissionFees());
          }
        } else {
          Date currentDate = Utils.getTodayDate();

          try {

            while (currentDate.before(date)
                && (dci.getRecurringEvent().getEndDate() == null
                || currentDate.before(dci.getRecurringEvent().getEndDate()))) {
              costBasisTotal = costBasisTotal
                  .add(dci.getAmount())
                  .add(dci.getCommissionFees());
              currentDate = RecurringEvent.getNextDate(currentDate,
                  dci.getRecurringEvent().getRecurringIntervalType(),
                  dci.getRecurringEvent().getRecurringIntervalDelta());
            }
          } catch (NotImplementedException e) {
            throw new IllegalArgumentException(e.getMessage());
          }
        }
      }
    }

    return costBasisTotal;
  }

  @Override
  public void addStockDollarCostInvestment(IStockDataSource source,
      IDollarCostInvestment dollarCostInvestment)
      throws IllegalArgumentException, StockDataSourceException {

    // add dollar cost investments in the past as transactions
    Date currentDate = dollarCostInvestment.getDate();

    Date dciEndDate = null;
    boolean isRecurring = dollarCostInvestment.getRecurringEvent() != null;
    if (isRecurring) {
      dciEndDate = dollarCostInvestment.getRecurringEvent().getEndDate();
    }

    try {
      while (currentDate.before(Utils.getTodayDate())
          && (!isRecurring || dciEndDate == null || currentDate.equals(dciEndDate)
          || currentDate.before(dciEndDate))) {
        var purchasePrice =
            source.getStockPrice(this.getSymbol(), currentDate, true);
        var currentVolume = dollarCostInvestment.getAmount()
            .divide(purchasePrice, 2, RoundingMode.UP);
        this.addTransaction(currentDate, currentVolume, purchasePrice,
            dollarCostInvestment.getCommissionFees());

        if (!isRecurring) {
          break;
        }

        currentDate = RecurringEvent.getNextDate(currentDate,
            dollarCostInvestment.getRecurringEvent().getRecurringIntervalType(),
            dollarCostInvestment.getRecurringEvent().getRecurringIntervalDelta());


      }
    } catch (NotImplementedException e) {
      throw new IllegalArgumentException(e.getMessage());
    }

    // only future dollar cost investments are stored in the map
    Date todayDate = Utils.getTodayDate();

    if (currentDate.equals(todayDate) || currentDate.after(todayDate)) {
      var newDollarCostInvestment = new DollarCostInvestment(currentDate,
          dollarCostInvestment.getAmount(), dollarCostInvestment.getCommissionFees(),
          dollarCostInvestment.getRecurringEvent());
      this.dollarCostInvestmentMap.put(currentDate, newDollarCostInvestment);
    }
  }

  @Override
  public List<IDollarCostInvestment> getDollarCostInvestments() {
    List<IDollarCostInvestment> result = new ArrayList<>();

    for (var d : this.dollarCostInvestmentMap.entrySet()) {
      result.add(d.getValue());
    }

    return result;
  }

  //</editor-fold>
}
