package model.portfolio;

import common.Utils;
import common.pair.Pair;
import common.triplet.Triplet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * A store of flexible portfolios.
 */
public class FlexiblePortfolioStore extends AbstractPortfolioStore<IFlexiblePortfolio> {

  //<editor-fold desc="Constructors">

  FlexiblePortfolioStore() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Instantiates a store of flexible portfolios with zero items.
   *
   * @param dataSource the data source being used to validate stocks and fetch stock data.
   */
  public FlexiblePortfolioStore(IStockDataSource dataSource) {
    super(dataSource);
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public String getCsvRowHeaders() {
    return "PortfolioName,"
        + "StockSymbol,"
        + "StockName,"
        + "StockExchange,"
        + "StockTransactionDate,"
        + "StockTransactionVolume,"
        + "StockTransactionPurchasePrice,"
        + "StockTransactionCommissionFees\n";
  }

  @Override
  int getCsvRowCount() {
    return 8;
  }

  @Override
  List<IFlexiblePortfolio> getPortfoliosFromCsvRows(List<String> rows)
      throws IllegalArgumentException, InstantiationException {
    if (rows.size() == 0) {
      throw new IllegalArgumentException(
          "Conversion from CSV to Portfolio: at least one row needs to be provided!");
    }

    Map<String, List<List<String>>> parsedPortfolios = new HashMap<>();

    List<IFlexiblePortfolio> portfoliosResult = new ArrayList<>();
    List<String> rowSplit;

    for (String row : rows) {
      // the parsed row is the header row
      if (this.getCsvRowHeaders().equals(row + "\n")) {
        continue;
      }

      rowSplit = List.of(row.split(","));

      // Sanity check for row data
      this.csvRowSanityCheck(rowSplit);

      this.validateCsvRowName(rowSplit.get(0));
      this.validateCsvRowDate(rowSplit.get(4));
      this.validateCsvRowVolume(rowSplit.get(5), false);
      this.validateCsvRowPurchasePrice(rowSplit.get(6));
      this.validateCsvRowCommissionFees(rowSplit.get(7));
      this.validateCsvRowStockData(rowSplit.get(1), rowSplit.get(2), rowSplit.get(3));

      // separate rows in the csv according to portfolio Names
      this.addCsvRowToPortfoliosMap(parsedPortfolios, rowSplit.get(0), rowSplit);
    }

    if (parsedPortfolios.size() == 0) {
      throw new IllegalArgumentException("Invalid input: The file does not have any valid rows!");
    }

    // conversion of string entries to flexible portfolios
    for (var p : parsedPortfolios.entrySet()) {
      String key = p.getKey();
      List<List<String>> stockStringList = p.getValue();

      // segregating stocks and stock transactions in portfolio
      // <symbol, transactions>
      Map<String, List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>>> stocksMap =
          new HashMap<>();
      Map<String, Pair<String, String>> stockDetails = new HashMap<>();
      List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>> transactions;

      // segregating list of strings into stocks and transactions
      for (List<String> stockString : stockStringList) {
        if (!stocksMap.containsKey(stockString.get(1))) {
          // stock doesn't exist in map
          // need to add stock and transactions
          stockDetails.put(stockString.get(1), new Pair<>(stockString.get(2), stockString.get(3)));

          transactions = new ArrayList<>();
        } else {
          // stock exists in map
          // need to add new transaction to existing list of transactions
          transactions = stocksMap.get(stockString.get(1));
        }

        transactions.add(new Pair<>(Utils.convertStringToDate(stockString.get(4)),
            new Triplet<>(
                new BigDecimal(stockString.get(5)),
                new BigDecimal(stockString.get(6)),
                new BigDecimal(stockString.get(7)))));

        stocksMap.put(stockString.get(1), transactions);
      }

      // converting segregated stocks into objects to be fed into FlexiblePortfolio
      List<IFlexiblePortfolioStock> stocks = new ArrayList<>();

      for (var stockMapEntry : stocksMap.entrySet()) {
        Pair<String, String> stockDetail = stockDetails.get(stockMapEntry.getKey());
        List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>> stockTransactions =
            stockMapEntry.getValue();

        BigDecimal totalVolume = new BigDecimal(0);

        for (var stockTransaction : stockTransactions) {
          totalVolume = totalVolume.add(stockTransaction.getO2().getO1());
        }

        stocks.add(new FlexiblePortfolioStock(stockMapEntry.getKey(), stockDetail.getO1(),
            stockDetail.getO2(), totalVolume, stockTransactions));
      }

      IFlexiblePortfolio flexiblePortfolio = new FlexiblePortfolio(this.dataSource, key, stocks);

      portfoliosResult.add(flexiblePortfolio);
    }

    return portfoliosResult;
  }

  //</editor-fold>
}
