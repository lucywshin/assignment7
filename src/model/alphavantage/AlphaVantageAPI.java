package model.alphavantage;

import common.Utils;
import common.pair.Pair;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import model.portfolio.IStock;
import model.portfolio.IStockDataSource;
import model.portfolio.Stock;
import model.portfolio.StockDataSourceException;

/**
 * This class handles the interaction with the AlphaVantage API.
 */
public class AlphaVantageAPI implements IStockDataSource {

  //<editor-fold desc="Inner interfaces">

  /**
   * A generic representation of output from the alpha vantage API.
   */
  interface IAVResult {

  }

  /**
   * A generic representation of the result builder for alpha vantage API.
   */
  interface IAVResultBuilder {

    /**
     * Creates the result.
     *
     * @param s the result in csv row format.
     * @return the result in an object.
     */
    IAVResult create(String s);
  }

  /**
   * An interface representing the result from the Alpha Vantage API Listing status endpoint.
   */
  interface IAVListingStatusResult extends IAVResult {

    /**
     * Gets the symbol of the stock.
     *
     * @return the symbol of the stock.
     */

    String getSymbol();

    /**
     * Gets the name of the stock.
     *
     * @return the name of the stock.
     */
    String getName();

    /**
     * Gets the exchange the stock is traded in.
     *
     * @return the name of the exchange.
     */
    String getExchange();

    /**
     * Gets the type of asset.
     *
     * @return the name of the type of asset.
     */
    String getAssetType();

    /**
     * Gets the IPO date of the stock.
     *
     * @return the IPO date of the stock.
     */
    Date getIpoDate();

    /**
     * Gets the delisting date of the stock if the stock is de-listed. Is null if not de-listed.
     *
     * @return the de-listing date of the stock.
     */
    Date getDelistingDate();

    /**
     * Gets the status of the stock: whether the stock is de-listed.
     *
     * @return the status of the stock.
     */
    String getStatus();
  }

  /**
   * An interface representing the result from the Alpha Vantage API time series daily endpoint.
   */
  interface IAVTimeSeriesDailyResult extends IAVResult {

    /**
     * Gets the time stamp of the result row.
     *
     * @return the time stamp of the result row.
     */
    Date getTimeStamp();

    /**
     * Gets the opening value of the stock's price on the day.
     *
     * @return the value of the stock at the opening bell of the exchange.
     */
    BigDecimal getOpenValue();

    /**
     * Gets the high value of the stock's price on the day.
     *
     * @return the highest value of the stock on the day.
     */
    BigDecimal getHighValue();

    /**
     * Gets the low value of the stock's price on the day.
     *
     * @return the lowest value of the stock on the day.
     */
    BigDecimal getLowValue();

    /**
     * Gets the closing value of the stock's price on the day.
     *
     * @return the value of the stock at the closing bell of the exchange.
     */
    BigDecimal getCloseValue();

    /**
     * Gets the volume of the stock that was traded on the day.
     *
     * @return the trade volume.
     */
    long getVolume();
  }

  //</editor-fold>

  //<editor-fold desc="Inner classes">

  /**
   * The Alpha vantage Listing Status endpoint result.
   */
  class AVListingStatusResult implements IAVListingStatusResult {

    //<editor-fold desc="State variables">

    private final String symbol;
    private final String name;
    private final String exchange;
    private final String assetType;
    private final Date ipoDate;
    private final Date delistingDate;
    private final String status;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    /**
     * Creates an object containing a row if the listing status API results.
     *
     * @param text the text form of the API result row.
     * @throws IllegalArgumentException if the format isn't as expected for the API result row.
     */
    AVListingStatusResult(String text) throws IllegalArgumentException {
      String[] values = text.split(",");

      if (values.length != 7) {
        throw new IllegalArgumentException("Provided String is not in expected format!");
      }

      this.ipoDate = Utils.convertStringToDate(values[4], "yyyy-MM-dd");
      this.delistingDate =
          values[5].equals("null") ? null : Utils.convertStringToDate(values[4], "yyyy-MM-dd");

      try {
        this.symbol = values[0];
        this.name = values[1];
        this.exchange = values[2];
        this.assetType = values[3];
        this.status = values[6];
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "One or more provided values is not in the expected format!");
      }
    }

    //</editor-fold>

    //<editor-fold desc="Getters">

    @Override
    public String getSymbol() {
      return symbol;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getExchange() {
      return exchange;
    }

    @Override
    public String getAssetType() {
      return assetType;
    }

    @Override
    public Date getIpoDate() {
      return ipoDate;
    }

    @Override
    public Date getDelistingDate() {
      return delistingDate;
    }

    @Override
    public String getStatus() {
      return status;
    }

    //</editor-fold>
  }

  /**
   * A builder class for the Listing Status Result.
   */
  class AVListingStatusResultBuilder implements IAVResultBuilder {

    @Override
    public IAVResult create(String text) {
      return new AVListingStatusResult(text);
    }
  }

  /**
   * The Alpha Vantage Time series daily result.
   */
  class AVTimeSeriesDailyResult implements IAVTimeSeriesDailyResult {

    //<editor-fold desc="State Variables">

    private final Date timeStamp;
    private final BigDecimal openValue;
    private final BigDecimal highValue;
    private final BigDecimal lowValue;
    private final BigDecimal closeValue;
    private final long volume;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    /**
     * Creates an object containing a row if the time series daily API results.
     *
     * @param text the text form of the API result row.
     * @throws IllegalArgumentException if the format isn't as expected for the API result row.
     */
    AVTimeSeriesDailyResult(String text) throws IllegalArgumentException {
      String[] values = text.split(",");

      if (values.length != 6) {
        throw new IllegalArgumentException("Provided String is not in expected format!");
      }

      this.timeStamp = Utils.convertStringToDate(values[0], "yyyy-MM-dd");

      try {
        this.openValue = new BigDecimal(values[1]);
        this.highValue = new BigDecimal(values[2]);
        this.lowValue = new BigDecimal(values[3]);
        this.closeValue = new BigDecimal(values[4]);
        this.volume = Long.parseLong(values[5]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("One or more provided values is not a number!");
      }
    }

    //</editor-fold>

    //<editor-fold desc="Getters">

    @Override
    public Date getTimeStamp() {
      return this.timeStamp;
    }

    @Override
    public BigDecimal getOpenValue() {
      return this.openValue;
    }

    @Override
    public BigDecimal getHighValue() {
      return this.highValue;
    }

    @Override
    public BigDecimal getLowValue() {
      return this.lowValue;
    }

    @Override
    public BigDecimal getCloseValue() {
      return this.closeValue;
    }

    @Override
    public long getVolume() {
      return this.volume;
    }

    //</editor-fold>
  }

  /**
   * A builder class for the time series result.
   */
  class AVTimeSeriesDailyResultBuilder implements IAVResultBuilder {

    @Override
    public IAVResult create(String text) {
      return new AVTimeSeriesDailyResult(text);
    }
  }

  //</editor-fold>

  // <editor-fold desc="Enums and Constants">

  /**
   * An enum for the supported functions of the Alpha Vantage API.
   */
  enum eAlphaVantageFunction {
    // Stock data with per day interval
    TIME_SERIES_DAILY("TIME_SERIES_DAILY"),
    // Listing status of the stocks
    LISTING_STATUS("LISTING_STATUS");

    private final String string;

    eAlphaVantageFunction(String functionString) {
      this.string = functionString;
    }

    String getFunctionVal() {
      return this.string;
    }
  }

  //</editor-fold>

  //<editor-fold desc="State variables">

  private final String apiKey;

  //</editor-fold>

  //<editor-fold desc="Internal caching">

  private Map<String, IAVListingStatusResult> supportedStocks;

  private Map<Pair<String, Date>, IAVTimeSeriesDailyResult> timeSeriesCache;

  private Set<String> fetchedTimeSeriesForCache;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Initializes the Alpha Vantage API with a default API key.
   *
   * @throws StockDataSourceException when an error occurred in the Alpha Vantage API.
   */
  public AlphaVantageAPI() throws StockDataSourceException {
    // default API key with 60 requests per minute and no daily limit
    this.apiKey = "4FKEALJIRNBQQBDM";
    this.setupSupportedStocksCache();
    this.setupTimeSeriesCache();
  }

  /**
   * Initializes the Alpha Vantage API with the provided API key.
   *
   * @param apiKey The API key required to communicate with the Alpha Vantage API.
   * @throws StockDataSourceException when an error occurred in the Alpha Vantage API.
   */
  public AlphaVantageAPI(String apiKey) throws StockDataSourceException {
    this.apiKey = apiKey;
    this.setupSupportedStocksCache();
    this.setupTimeSeriesCache();
  }

  //</editor-fold>

  //<editor-fold desc="Supported Stocks Cache and Time Series Cache Setup">

  private void setupSupportedStocksCache() throws StockDataSourceException {
    this.supportedStocks = new HashMap<>();

    try {
      List<IAVListingStatusResult> activeStocks = this.alphaVantageEndpointListingStatus(true);
      List<IAVListingStatusResult> delistedStocks = this.alphaVantageEndpointListingStatus(false);

      for (IAVListingStatusResult stock : activeStocks) {
        this.supportedStocks.put(stock.getSymbol(), stock);
      }
      for (IAVListingStatusResult stock : delistedStocks) {
        this.supportedStocks.put(stock.getSymbol(), stock);
      }
    } catch (StockDataSourceException e) {
      throw new StockDataSourceException("Setup of cache failed!");
    }
  }

  private void setupTimeSeriesCache() {
    this.fetchedTimeSeriesForCache = new HashSet<>();
    this.timeSeriesCache = new HashMap<>();
  }

  //</editor-fold>

  //<editor-fold desc="Query helpers">

  private String buildQueryUrl(eAlphaVantageFunction function, List<Pair<String, String>> params) {
    StringBuilder urlBuilder = new StringBuilder();

    String baseUrl = "https://www.alphavantage.co";
    urlBuilder.append(baseUrl).append("/query?");
    urlBuilder.append("function=").append(function.getFunctionVal());

    for (Pair<String, String> p : params) {
      urlBuilder.append("&").append(p.getO1()).append("=").append(p.getO2());
    }
    return urlBuilder.toString();
  }

  private StringBuilder query(eAlphaVantageFunction function, List<Pair<String, String>> params)
      throws StockDataSourceException {
    URL url = null;
    String urlString = this.buildQueryUrl(function, params);

    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      throw new RuntimeException("The alpha Vantage API has either changed or no longer works!");
    }

    InputStream in;
    StringBuilder output = new StringBuilder();

    try {
      /*
      Execute this query. This returns an InputStream object.
      In the csv format, it returns several lines, each line being separated
      by commas. Each line contains the date, price at opening time, the highest
      price for that date, the lowest price for that date, price at closing time
      and the volume of trade (no. of shares bought/sold) on that date.

      This is printed below.
       */
      in = url.openStream();
      int ioByte;

      while ((ioByte = in.read()) != -1) {
        output.append((char) ioByte);
      }
    } catch (IOException e) {
      throw new StockDataSourceException("No price data found for provided symbol!");
    }

    return output;
  }

  private List<String> splitStringByLine(String text) {
    List<String> result = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
      String line = reader.readLine();
      while (line != null) {
        result.add(line);
        line = reader.readLine();
      }
    } catch (IOException exc) {
      // quit
    }

    return result;
  }

  //</editor-fold>

  //<editor-fold desc="API endpoints helpers">

  private List<IAVResult> convertQueryResultToObjectList(IAVResultBuilder builder,
      String queryResult) {
    // split results by line and remove first line (the first line contains the column names)
    List<String> queryResultByLine = this.splitStringByLine(queryResult);
    queryResultByLine.remove(0);

    // convert the provided list of strings to list of usable objects
    List<IAVResult> result = new ArrayList<>();

    for (String queryResultLine : queryResultByLine) {
      result.add(builder.create(queryResultLine));
    }

    return result;
  }

  //</editor-fold>

  //<editor-fold desc="API endpoints">

  /**
   * Gets the time series from Alpha Vantage API for the specified stock.
   *
   * @param symbol the stock symbol.
   * @return the result from the API
   * @throws StockDataSourceException when an error occurs in the API.
   */
  List<IAVTimeSeriesDailyResult> alphaVantageEndpointTimeSeriesDaily(String symbol)
      throws StockDataSourceException {
    this.validateSupportedStock(symbol);

    // setup parameters for query to be sent to Alpha Vantage
    ArrayList<Pair<String, String>> params = new ArrayList<>();

    params.add(new Pair<>("outputsize", "full"));
    params.add(new Pair<>("symbol", symbol));
    params.add(new Pair<>("apikey", this.apiKey));
    params.add(new Pair<>("datatype", "csv"));

    String queryResult = this.query(eAlphaVantageFunction.TIME_SERIES_DAILY, params).toString();

    try {
      return this.convertQueryResultToObjectList(new AVTimeSeriesDailyResultBuilder(), queryResult)
          .stream()
          .map(r -> (AVTimeSeriesDailyResult) r)
          .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
      throw new StockDataSourceException(
          "One or more errors occurred while parsing the response from the API: " + e.getMessage());
    }
  }

  /**
   * Gets the status of the stocks supported by the Alpha Vantage API.
   *
   * @param getActive a boolean to decide what status stocks to fetch.
   * @return the result from the API
   * @throws StockDataSourceException when an error occurs in the API.
   */
  List<IAVListingStatusResult> alphaVantageEndpointListingStatus(boolean getActive)
      throws StockDataSourceException {
    // setup parameters for query to be sent to Alpha Vantage
    ArrayList<Pair<String, String>> params = new ArrayList<>();

    params.add(new Pair<>("apikey", this.apiKey));
    if (!getActive) {
      params.add(new Pair<>("state", "delisted"));
    }

    String queryResult = this.query(eAlphaVantageFunction.LISTING_STATUS, params).toString();

    try {
      return this.convertQueryResultToObjectList(new AVListingStatusResultBuilder(), queryResult)
          .stream()
          .map(r -> (IAVListingStatusResult) r)
          .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
      throw new StockDataSourceException(
          "One or more errors occurred while parsing the response from the API: " + e.getMessage());
    }
  }

  //</editor-fold>

  //<editor-fold desc="Result converter helpers">

  private IStock convertListingResultToStock(IAVListingStatusResult s) {
    return new Stock(s.getSymbol(), s.getName(), s.getExchange());
  }

  //</editor-fold>

  //<editor-fold desc="Validation helper methods">

  /**
   * Validates whether the stock is supported by the Alpha Vantage API.
   *
   * @param symbol The symbol of the stock using which the stock is traded.
   * @throws IllegalArgumentException when the stock is not valid or is not supported by the API.
   */
  private void validateSupportedStock(String symbol) throws IllegalArgumentException {
    if (!this.supportedStocks.containsKey(symbol)) {
      throw new IllegalArgumentException(
          "Provided stock symbol: " + symbol + " is not supported by the API!");
    }
  }

  //</editor-fold>

  //<editor-fold desc="Public Stock methods">

  @Override
  public IStock getStock(String symbol) throws IllegalArgumentException {
    this.validateSupportedStock(symbol);
    return this.convertListingResultToStock(this.supportedStocks.get(symbol));
  }

  @Override
  public Date getIPODate(String symbol) throws IllegalArgumentException {
    this.validateSupportedStock(symbol);
    return this.supportedStocks.get(symbol).getIpoDate();
  }

  @Override
  public Date getDelistingDate(String symbol) throws IllegalArgumentException {
    this.validateSupportedStock(symbol);
    return this.supportedStocks.get(symbol).getDelistingDate();
  }

  @Override
  public BigDecimal getStockPrice(String symbol, Date date, boolean takeFuturePrice)
      throws StockDataSourceException {
    Utils.validateFutureDate(date);
    this.validateSupportedStock(symbol);

    // fetch and store values from API if value isn't available in cache
    if (!this.fetchedTimeSeriesForCache.contains(symbol)) {
      List<IAVTimeSeriesDailyResult> timeSeriesResults = this.alphaVantageEndpointTimeSeriesDaily(
          symbol);
      for (IAVTimeSeriesDailyResult tsr : timeSeriesResults) {
        this.timeSeriesCache.put(new Pair<>(symbol, tsr.getTimeStamp()), tsr);
      }

      this.fetchedTimeSeriesForCache.add(symbol);
    }

    if (date.before(this.getIPODate(symbol))
        || (this.getDelistingDate(symbol) != null && date.after(this.getDelistingDate(symbol)))) {
      return new BigDecimal(0);
    }

    Pair<String, Date> timeSeriesTargetPair = new Pair<>(symbol, date);

    // checking days before the provided date in case value is not found on given date
    // trying upto 10 days before given date
    for (int i = 1; i <= 10; i++) {
      if (this.timeSeriesCache.containsKey(timeSeriesTargetPair)) {
        return this.timeSeriesCache.get(timeSeriesTargetPair).getCloseValue();
      }

      int dayInMS = 86400000;
      int dayMultiplier = i;
      if (takeFuturePrice) {
        dayMultiplier = -dayMultiplier;
      }
      Date backtrackDate = new Date(date.getTime() - dayInMS * dayMultiplier);
      timeSeriesTargetPair = new Pair<>(symbol, backtrackDate);
    }

    return new BigDecimal(0);

  }

  //</editor-fold>
}
