package model;

import common.pair.Pair;
import common.triplet.Triplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import model.chart.IChart;
import model.portfolio.IDollarCostInvestment;
import model.portfolio.IFlexiblePortfolio;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolio;
import model.portfolio.IPortfolioStock;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.IRebalance;
import model.portfolio.IStock;
import model.portfolio.StockDataSourceException;
import model.portfolio.eRecurringIntervalType;

/**
 * A model for portfolio management.
 */
public interface IPortfolioModel {

  /**
   * Gets the details for the requested stock.
   *
   * @param symbol the symbol of the stock.
   * @return the stock details in a {@code Stock} object.
   */
  IStock getStockDetails(String symbol);

  /**
   * Sets the name of the portfolio.
   *
   * @param name the name to be assigned for the portfolio.
   * @return the builder for the portfolio.
   */
  String setName(String name);

  /**
   * Gets the name of the requested portfolio.
   *
   * @param id the id of the requested portfolio.
   * @return the name of the requested portfolio.
   */
  String getName(int id);

  /**
   * Gets a list of pairs with the id and portfolios currently in the system.
   *
   * @return a list of pairs with the id of the portfolio and the portfolio.
   */
  List<Pair<Integer, IPortfolio>> getAllPortfolios();

  /**
   * Gets a list of pairs with the id and flexible portfolios currently in the system.
   *
   * @return a list of pairs with the id of the flexible portfolio and the flexible portfolio.
   */
  List<Pair<Integer, IFlexiblePortfolio>> getAllFlexiblePortfolios();

  /**
   * Gets the number of portfolios available in the application.
   *
   * @return the number of portfolios available in the application.
   */
  int getPortfolioCount();

  /**
   * Gets the number of flexible portfolios available in the application.
   *
   * @return the number of flexible portfolios available in the application.
   */
  int getFlexiblePortfolioCount();

  /**
   * Gets the name of the portfolio in the portfolio builder.
   *
   * @return the name of the portfolio being built.
   */
  String getPortfolioBuilderName();

  /**
   * Sets the name of the portfolio in the portfolio builder.
   *
   * @param name the name to be set to the portfolio being built.
   * @return the new name of the portfolio.
   */
  String setPortfolioBuilderName(String name);

  /**
   * Gets the stocks currently in the builder.
   *
   * @return a list of portfolio stocks.
   */
  List<IPortfolioStock> getPortfolioBuilderStocks();

  /**
   * Adds the provided stocks to the list of stocks in the portfolio builder.
   *
   * @param stocks a list of {@code PortfolioStock}s to be added to the portfolio.
   * @return the new list of {@code PortfolioStock}s in the portfolio.
   */
  List<IPortfolioStock> addStocksToPortfolioBuilder(List<Pair<String, BigDecimal>> stocks);

  /**
   * Builds the portfolio using the current state of the portfolio builder.
   *
   * @return a new portfolio.
   * @throws InstantiationException when the current state of the builder is not a valid state for a
   *                                portfolio.
   */
  int buildPortfolio() throws InstantiationException;

  /**
   * Creates a flexible portfolio with the given name and adds it to the persisted list.
   *
   * @param portfolioName the name of the flexible portfolio.
   * @return the id of the newly created flexible portfolio.
   * @throws InstantiationException when the current state of the builder is not a valid state for a
   *                                portfolio.
   */
  int createFlexiblePortfolio(String portfolioName) throws InstantiationException;

  /**
   * Gets the composition of the requested portfolio.
   *
   * @param id the id of the requested portfolio.
   * @return a {@code Pair} containing the name of the portfolio and the stocks in the portfolio.
   */
  Pair<String, List<String>> getPortfolioComposition(int id);

  /**
   * Gets the composition of the requested flexible portfolio on the requested date.
   *
   * @param id   the id of the requested flexible portfolio.
   * @param date the date on which composition is to be calculated.
   * @return a {@code Pair} containing the name of the portfolio and the stocks in the portfolio.
   * @throws IllegalArgumentException when the provided input is invalid.
   */
  Pair<String, List<Pair<String, BigDecimal>>> getFlexiblePortfolioComposition(int id, Date date)
      throws IllegalArgumentException;

  /**
   * Gets the portfolio requested.
   *
   * @param id the id of the requested portfolio.
   * @return the requested portfolio.
   */
  IPortfolio getPortfolio(int id);

  /**
   * Gets the flexible portfolio requested.
   *
   * @param id the id of the requested flexible portfolio.
   * @return the requested flexible portfolio.
   * @throws IllegalArgumentException when the provided id is not valid.
   */
  IFlexiblePortfolio getFlexiblePortfolio(int id) throws IllegalArgumentException;

  /**
   * Gets the list of stocks with values on the requested date for the requested portfolio.
   *
   * @param id   the identifier of the portfolio.
   * @param date the date on which value is to be fetched.
   * @return the list of stocks in the portfolio with the value of the stocks.
   * @throws IllegalArgumentException when the provided input is invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  Pair<BigDecimal, List<IPortfolioStockValue>> getPortfolioValue(int id, Date date)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Gets the list of stocks with values on the requested date for the requested flexible
   * portfolio.
   *
   * @param id   the identifier of the flexible portfolio.
   * @param date the date on which value is to be fetched.
   * @return the list of stocks in the flexible portfolio with the value of the stocks.
   * @throws IllegalArgumentException when the provided input is invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  Pair<BigDecimal, List<IPortfolioStockValue>> getFlexiblePortfolioValue(int id, Date date)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Imports the portfolios in the provided file.
   *
   * @param filePath the path of the file to which the data is to be exported.
   * @throws IOException            when there is an error in the I/O operation with the file.
   * @throws InstantiationException when the creation of portfolios fails.
   */
  void importPortfolios(String filePath) throws IOException, InstantiationException;

  /**
   * Imports the flexible portfolios in the provided file.
   *
   * @param filePath the path of the file to which the data is to be exported.
   * @throws IOException            when there is an error in the I/O operation with the file.
   * @throws InstantiationException when the creation of flexible portfolios fails.
   */
  void importFlexiblePortfolios(String filePath) throws IOException, InstantiationException;

  /**
   * Exports the portfolios to the provided file.
   *
   * @param filePath the path of the file being imported.
   * @throws IOException when there is an error in the I/O operation with the file.
   */
  void exportPortfolios(String filePath) throws IOException;

  /**
   * Exports the flexible portfolios to the provided file.
   *
   * @param filePath the path of the file being imported.
   * @throws IOException when there is an error in the I/O operation with the file.
   */
  void exportFlexiblePortfolios(String filePath) throws IOException;

  /**
   * Gets the performance chart for the selected flexible portfolio.
   *
   * @param portfolioId the identifier of the flexible portfolio.
   * @param startDate   the start date of the chart.
   * @param endDate     the end date of the chart.
   * @return the chart object containing a representation of a bar chart.
   * @throws StockDataSourceException if the data could not be fetched from the data source.
   * @throws IllegalArgumentException when dates provided are invalid.
   */
  IChart getPerformanceChart(int portfolioId, Date startDate, Date endDate)
      throws StockDataSourceException, IllegalArgumentException;

  /**
   * Buys the specified stocks for the specified portfolio.
   *
   * @param portfolioId   the identifier of the flexible portfolio.
   * @param stocks        the stocks to be bought.
   * @param commissionFee the commission fee for a transaction.
   * @return a pair containing the id of the portfolio and the new state of the stocks in the
   *     portfolio.
   * @throws StockDataSourceException when an error occurs in the stock data source
   * @throws IllegalStateException    when the transactions by this action make the state of the
   *                                  portfolio invalid.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   */
  Pair<Integer, List<IObservableFlexiblePortfolioStock>> buyStocksForFlexiblePortfolio(
      int portfolioId,
      List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFee)
      throws StockDataSourceException, IllegalStateException, IllegalArgumentException;

  /**
   * Sells the specified stocks for the specified portfolio.
   *
   * @param portfolioId   the identifier of the flexible portfolio.
   * @param stocks        the stocks to be sold.
   * @param commissionFee the commission fee for a transaction.
   * @return a pair containing the id of the portfolio and the new state of the stocks in the
   *     portfolio.
   * @throws IllegalStateException    when the transactions by this action make the state of the
   *                                  portfolio invalid.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source
   */
  Pair<Integer, List<IObservableFlexiblePortfolioStock>> sellStocksForFlexiblePortfolio(
      int portfolioId,
      List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFee)
      throws StockDataSourceException, IllegalStateException, IllegalArgumentException;

  /**
   * Gets the cost basis of the portfolio. The cost basis is the total amount of money invested into
   * this portfolio.
   *
   * @param portfolioId the id of the portfolio for which the cost basis is to be calculated.
   * @param date        the date on which cost basis is to be calculated.
   * @return the value of cost basis for the specified date.
   * @throws IllegalArgumentException when provided date is not valid.
   * @throws StockDataSourceException when an error occurs in the stock data source
   */
  BigDecimal getCostBasis(int portfolioId, Date date)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Adds a dollar cost investment to the portfolio specified.
   *
   * @param portfolioId          the identifier of the flexible portfolio.
   * @param date                 the first date on which the dollar cost investment is to be
   *                             triggered.
   * @param amount               the fixed amount to be invested on a regular interval.
   * @param stocksWithPercentage a list of pairs containing a stock, and it's percentage weight for
   *                             the buy order.
   * @param commissionFee        the commission fee for a transaction.
   * @param isRecurring          whether the dollar cost investment is a recurring investment.
   * @param endDate              if the investment is recurring, the end date of the recurring
   *                             investment. Can be null, indicating that the investment has no end
   *                             date.
   * @param intervalType         if the investment is recurring, the type of the interval for
   *                             recurring investments.
   * @param intervalDelta        if the investment is recurring, the difference of interval type
   *                             between two recurring investments.
   * @return a pair containing the id of the portfolio and the new list of stocks in the portfolio.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  Pair<Integer, List<IObservableFlexiblePortfolioStock>> addDollarCostInvestment(int portfolioId,
      Date date, BigDecimal amount, List<Pair<String, BigDecimal>> stocksWithPercentage,
      BigDecimal commissionFee, boolean isRecurring, Date endDate,
      eRecurringIntervalType intervalType, Integer intervalDelta)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Rebalances the portfolio specified.
   *
   * @param portfolioId          the identifier of the flexible portfolio.
   * @param date                 the first date on which the dollar cost investment is to be
   *                             triggered.
   * @return a pair containing the id of the portfolio and the new list of stocks in the portfolio.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  Pair<Integer, List<IObservableFlexiblePortfolioStock>> addRebalance(
          int portfolioId, Date date,
          List<Pair<String, BigDecimal>> stocksWithPercentage)
          throws IllegalArgumentException, StockDataSourceException;

  /**
   * Gets all the dollar cost investments in the specified portfolio.
   *
   * @param portfolioId the identifier of the flexible portfolio.
   * @return a pair containing the id of the portfolio and a list of pairs with the stock symbol and
   *     the dollar cost investment on that stock.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   */
  Pair<Integer, List<Pair<String, IDollarCostInvestment>>> getDollarCostInvestments(
      int portfolioId) throws IllegalArgumentException;

  /**
   * Gets all the rebalance investments in the specified portfolio.
   *
   * @param portfolioId the identifier of the flexible portfolio.
   * @return a pair containing the id of the portfolio and a list of pairs with the stock symbol and
   *     the rebalance investment on that stock.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   */
  Pair<Integer, List<Pair<String, IRebalance>>> getRebalance(
          int portfolioId) throws IllegalArgumentException;
}
