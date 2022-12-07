package model.portfolio;

import common.pair.Pair;
import common.triplet.Triplet;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import model.chart.IChart;
import model.chart.IChartService;

/**
 * This interface represents a flexible portfolio which is able to do all the operations of a
 * non-flexible portfolio, and a few new operations.
 */
public interface IFlexiblePortfolio extends IAbstractPortfolio {


  /**
   * Gets the flexible stocks in this flexible portfolio.
   *
   * @return A list containing the stocks that are in this portfolio.
   */
  List<IObservableFlexiblePortfolioStock> getStocks();

  /**
   * The composition of the portfolio.
   *
   * @param date the date on which composition is to be calculated.
   * @return a list of pairs containing the name of the stock and the volume of the stock in the
   *     portfolio.
   * @throws IllegalArgumentException when the provided input is invalid.
   */
  List<Pair<String, BigDecimal>> getComposition(Date date) throws IllegalArgumentException;

  /**
   * A command to buy stocks.
   *
   * @param source         the data source which is to be used to fetch the values of the stocks.
   * @param stocks         the stocks which are being purchased in the symbol, purchase date, volume
   *                       format.
   * @param commissionFees the commission fees charged per transaction.
   * @return the portfolio with the new state.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source
   * @throws IllegalStateException    when the transactions by this action make the state of the
   *                                  portfolio invalid.
   */
  IFlexiblePortfolio buyStocks(IStockDataSource source,
      List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFees)
      throws IllegalArgumentException, StockDataSourceException, IllegalStateException;

  /**
   * A command to sell stocks.
   *
   * @param source         the data source which is to be used to fetch the values of the stocks.
   * @param stocks         the stocks which are being sold in the symbol, sale date, volume format.
   * @param commissionFees the commission fees charged per transaction.
   * @return the portfolio with the new state.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source
   * @throws IllegalStateException    when the transactions by this action make the state of the
   *                                  portfolio invalid.
   */
  IFlexiblePortfolio sellStocks(IStockDataSource source,
      List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFees)
      throws IllegalArgumentException, StockDataSourceException, IllegalStateException;

  /**
   * Gets the cost basis of the portfolio. The cost basis is the total amount of money invested into
   * this portfolio.
   *
   * @param source the data source which is to be used to fetch the values of the stocks.
   * @param date   the date on which cost basis is to be calculated.
   * @return the value of cost basis for the specified date.
   * @throws IllegalArgumentException when provided date is not valid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  BigDecimal getCostBasis(IStockDataSource source, Date date)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Gets the performance chart of the portfolio in the specified time window.
   *
   * @param source       the data source which is to be used to fetch the values of the stocks.
   * @param chartService the chart service used to generate the chart.
   * @param startDate    the start date of the window.
   * @param endDate      the end date of the window.
   * @return a chart representing the performance of the portfolio over time in the specified time
   *     window.
   * @throws IllegalArgumentException when provided date is not valid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  IChart getPerformanceChart(IStockDataSource source, IChartService chartService, Date startDate,
      Date endDate) throws IllegalArgumentException, StockDataSourceException;

  /**
   * Adds a dollar cost investment for this flexible portfolio.
   *
   * @param source               the data source which is to be used to fetch the values of the
   *                             stocks.
   * @param date                 the first date on which the dollar cost investment is to be
   *                             triggered.
   * @param amount               the fixed amount to be invested on a regular interval.
   * @param commissionFees       the commission fees to be charged for every transaction done under
   *                             this dollar cost investment.
   * @param stocksWithPercentage a list of pairs containing a stock, and it's percentage weight for
   *                             the buy order.
   * @param isRecurring          whether the dollar cost investment is a recurring investment.
   * @param endDate              if the investment is recurring, the end date of the recurring
   *                             investment. Can be null, indicating that the investment has no end
   *                             date.
   * @param intervalType         if the investment is recurring, the type of the interval for
   *                             recurring investments.
   * @param intervalDelta        if the investment is recurring, the difference of interval type
   *                             between two recurring investments.
   * @return the portfolio with the new state.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  IFlexiblePortfolio addDollarCostInvestment(IStockDataSource source, Date date, BigDecimal amount,
      BigDecimal commissionFees, List<Pair<String, BigDecimal>> stocksWithPercentage,
      boolean isRecurring, Date endDate, eRecurringIntervalType intervalType,
      Integer intervalDelta) throws IllegalArgumentException, StockDataSourceException;

  /**
   * Gets all the dollar cost investments scheduled for stocks of this flexible portfolio.
   *
   * @return a list of pairs containing the stock symbol and the dollar cost investment for that
   *     stock.
   */
  List<Pair<String, IDollarCostInvestment>> getDollarCostInvestments();
}
