package model.portfolio;

import common.pair.Pair;
import common.triplet.Triplet;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This interface represents a view only version of a flexible portfolio stock.
 */
public interface IObservableFlexiblePortfolioStock extends IPortfolioStock {


  /**
   * Gets the first transaction made on this stock in the portfolio.
   *
   * @return a pair containing the date and volume of the first transaction.
   */
  Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>> getFirstTransaction();

  /**
   * Gets the transactions made on this stock in the portfolio.
   *
   * @return a list of pairs containing the date and a pair of volume and commission fees of the
   *     transactions.
   */
  List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>> getTransactions();

  /**
   * Gets the actual value on the given date for the portfolio stock.
   *
   * @param source the data source which is to be used to fetch the values of the stocks.
   * @param date   the date on which the value is to be fetched.
   * @return a pair with the volume of the stock on requested date and actual value of the stock
   *     considering the history of transactions for this stock.
   * @throws IllegalArgumentException when the date provided is not valid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  Pair<BigDecimal, BigDecimal> getValueOnDate(IStockDataSource source, Date date)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Gets the cost basis on the given date for the portfolio stock.
   *
   * @param source the data source which is to be used to fetch the values of the stocks.
   * @param date   the date on which the cost basis is to be calculated.
   * @return the cost basis of the stock considering the history of transactions for this stock.
   * @throws IllegalArgumentException when the date provided is not valid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  BigDecimal getCostBasis(IStockDataSource source, Date date)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Gets all the dollar cost investments scheduled for this stock.
   *
   * @return a list of dollar cost investments.
   */
  List<IDollarCostInvestment> getDollarCostInvestments();

  /**
   * Gets all the rebalance investments in the specified portfolio.
   *
   * @return a list of pairs with the stock symbol and
   *     the rebalance investment on that stock.
   * @throws IllegalArgumentException when date provided is invalid or stocks provided are invalid.
   */
  List<IRebalance> getRebalance(Map<String, IRebalance> map) throws IllegalArgumentException;
}
