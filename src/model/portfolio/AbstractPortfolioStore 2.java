package model.portfolio;

import common.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.store.Store;

/**
 * A store of Abstract portfolios.
 */
abstract class AbstractPortfolioStore<T extends IAbstractPortfolio> extends
    Store<T> implements IAbstractPortfolioStore<T> {
  //<editor-fold desc="State variables">

  IStockDataSource dataSource;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * This constructor is not to be used.
   *
   * @throws NotImplementedException at all times.
   */
  AbstractPortfolioStore() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Instantiates a store of abstract portfolios with zero items.
   *
   * @param dataSource the data source being used to validate stocks and fetch stock data.
   */
  public AbstractPortfolioStore(IStockDataSource dataSource) {
    super();
    this.dataSource = dataSource;
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  /**
   * Get the row headings for transformation of the store to CSV exportable format.
   *
   * @return a {@code String} with the headings of the rows in the CSV table.
   */
  abstract String getCsvRowHeaders();

  /**
   * Converts the given rows of strings to portfolios.
   *
   * @param rows a list of strings representing rows in a CSV file.
   * @return a list of portfolios generated using the given rows.
   * @throws IllegalArgumentException when provided input is not valid.
   * @throws InstantiationException   when creation of portfolio was not possible.
   */
  abstract List<T> getPortfoliosFromCsvRows(List<String> rows)
      throws IllegalArgumentException, InstantiationException;

  /**
   * Gets the number of rows in the heading of the CSV file for the portfolio.
   *
   * @return the number of rows the CSV file is supposed to have.
   */
  abstract int getCsvRowCount();

  void csvRowSanityCheck(List<String> rowSplit) {
    if (rowSplit.size() != this.getCsvRowCount()) {
      throw new IllegalArgumentException(
          "Invalid input: Portfolio rows need to contain " + this.getCsvRowCount() + " items!");
    }
    for (String rowItem : rowSplit) {
      if (rowItem.isBlank()) {
        throw new IllegalArgumentException("Invalid input: Portfolio row item cannot be blank!");
      }
    }
  }

  void validateCsvRowName(String nameString) throws IllegalArgumentException {
    if (!Utils.isValidWordsInput(nameString)) {
      throw new IllegalArgumentException("Invalid input: Portfolio name format not valid!");
    }
  }

  void validateCsvRowVolume(String volumeString, boolean validateNegativeVolume)
      throws IllegalArgumentException {
    this.validateCsvRowFloatField(volumeString, "volume", validateNegativeVolume);
  }

  void validateCsvRowPurchasePrice(String purchasePriceString)
      throws IllegalArgumentException {
    this.validateCsvRowFloatField(purchasePriceString, "purchase price", true);
  }

  void validateCsvRowDate(String dateString) throws IllegalArgumentException {
    if (!Utils.isValidDateInput(dateString)) {
      throw new IllegalArgumentException(
          "Invalid input: Portfolio stock needs to have valid volume!");
    }

    Date date = Utils.convertStringToDate(dateString);
    Utils.validateFutureDate(date);
  }

  void validateCsvRowCommissionFees(String commissionFeesString) throws IllegalArgumentException {
    this.validateCsvRowFloatField(commissionFeesString, "commission fees", true);
  }

  private void validateCsvRowFloatField(String floatField, String fieldName,
      boolean validateNegativeValue) throws IllegalArgumentException {
    if (!Utils.isValidFloatNumberInput(floatField)) {
      throw new IllegalArgumentException(
          "Invalid input: Portfolio stock needs to have valid " + fieldName + "!");
    }

    float volume = Utils.convertStringToFloatNumber(floatField);
    if (validateNegativeValue && volume <= 0) {
      throw new IllegalArgumentException(
          "Invalid input: Portfolio stock needs to have positive " + fieldName + "!");
    }
  }

  void validateCsvRowStockData(String symbolField, String stockNameField,
      String exchangeField) throws IllegalArgumentException {
    IStock dataSourceStock = this.dataSource.getStock(symbolField);
    if (!dataSourceStock.getName().equals(stockNameField)) {
      throw new IllegalArgumentException(
          "Invalid input: Entered data for stock name is a mismatch!");
    }
    if (!dataSourceStock.getExchange().equals(exchangeField)) {
      throw new IllegalArgumentException(
          "Invalid input: Entered data for stock exchange is a mismatch!");
    }
  }

  void addCsvRowToPortfoliosMap(Map<String, List<List<String>>> parsedPortfolios,
      String portfolioName, List<String> rowSplit) {
    List<List<String>> rowsSplitList;

    if (!parsedPortfolios.containsKey(portfolioName)) {
      rowsSplitList = new ArrayList<>();
      rowsSplitList.add(rowSplit);
    } else {
      rowsSplitList = parsedPortfolios.get(portfolioName);
      rowsSplitList.add(rowSplit);
    }
    parsedPortfolios.put(portfolioName, rowsSplitList);
  }

  private List<String> readInputStream(InputStream inputStream) throws IOException {
    List<String> result = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      while (reader.ready()) {
        result.add(reader.readLine());
      }
    }

    return result;
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public void importItemsFromCsv(InputStream inputStream)
      throws IOException, InstantiationException {
    List<String> stringList = this.readInputStream(inputStream);
    List<T> itemList = this.getPortfoliosFromCsvRows(stringList);
    for (T item : itemList) {
      this.save(item);
    }
  }

  @Override
  public void exportItemsToCsv(OutputStream outputStream) throws IOException {
    // adding headers for csv format
    outputStream.write(this.getCsvRowHeaders().getBytes());

    // add each stock in each portfolio as a row in the csv
    for (IAbstractPortfolio item : this.items.values()) {
      List<String> csvRows = item.toCsvRows();
      for (String csvRow : csvRows) {
        outputStream.write(csvRow.getBytes());
      }
    }
  }

  //</editor-fold>
}
