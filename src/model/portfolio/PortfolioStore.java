package model.portfolio;

import common.pair.Pair;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.portfolio.Portfolio.PortfolioImplBuilder;

/**
 * A store of portfolios.
 */
public class PortfolioStore extends AbstractPortfolioStore<IPortfolio> {

  //<editor-fold desc="Constructors">

  private PortfolioStore() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Instantiates a store of portfolios with zero items.
   *
   * @param dataSource the data source being used to validate stocks and fetch stock data.
   */
  public PortfolioStore(IStockDataSource dataSource) {
    super(dataSource);
  }

  //</editor-fold>

  //<editor-fold desc="Differential methods">

  @Override
  protected String getCsvRowHeaders() {
    return "PortfolioName,"
        + "StockSymbol,"
        + "StockName,"
        + "StockExchange,"
        + "StockVolume\n";
  }

  @Override
  int getCsvRowCount() {
    return 5;
  }

  @Override
  List<IPortfolio> getPortfoliosFromCsvRows(List<String> rows)
      throws IllegalArgumentException, InstantiationException {
    if (rows.size() == 0) {
      throw new IllegalArgumentException(
          "Conversion from CSV to Portfolio: at least one row needs to be provided!");
    }

    Map<String, List<List<String>>> parsedPortfolios = new HashMap<>();

    List<IPortfolio> portfoliosResult = new ArrayList<>();
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
      this.validateCsvRowVolume(rowSplit.get(4), true);
      this.validateCsvRowStockData(rowSplit.get(1), rowSplit.get(2), rowSplit.get(3));

      // separate rows in the csv according to portfolio Names
      this.addCsvRowToPortfoliosMap(parsedPortfolios, rowSplit.get(0), rowSplit);
    }

    if (parsedPortfolios.size() == 0) {
      throw new IllegalArgumentException("Invalid input: The file does not have any valid rows!");
    }

    PortfolioImplBuilder portfolioImplBuilder = Portfolio.getBuilder().clearName()
        .clearStocks();

    for (var p : parsedPortfolios.entrySet()) {
      String key = p.getKey();
      List<List<String>> stockStringList = p.getValue();

      portfolioImplBuilder.setName(key);
      for (List<String> stockString : stockStringList) {
        portfolioImplBuilder.addStocks(this.dataSource,
            List.of(new Pair<>(stockString.get(1), new BigDecimal(stockString.get(4)))));
      }

      portfoliosResult.add(portfolioImplBuilder.create());
    }

    return portfoliosResult;
  }

  //</editor-fold>
}
