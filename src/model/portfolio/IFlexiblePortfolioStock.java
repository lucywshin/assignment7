package model.portfolio;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This interface represents a portfolio stock in a flexible portfolio.
 */
public interface IFlexiblePortfolioStock extends IObservableFlexiblePortfolioStock {

  /**
   * Adds a transaction for the stock in the portfolio.
   *
   * @param date           the date on which the transaction occurs.
   * @param volume         the volume of data in the transaction. Positive volume represents a 'buy'
   *                       transaction and negative volume represents a 'sell' transaction.
   * @param purchasePrice  the price at which this stock was purchased.
   * @param commissionFees the commission fees that was charged for this transaction.
   * @throws IllegalArgumentException when the date or volume provided is not valid.
   */
  void addTransaction(Date date, BigDecimal volume, BigDecimal purchasePrice,
      BigDecimal commissionFees)
      throws IllegalArgumentException;

  /**
   * Add a dollar cost investment for this flexible portfolio stock.
   *
   * @param source               the data source which is to be used to fetch the values of the
   *                             stocks.
   * @param dollarCostInvestment the dollar cost investment to be added.
   * @throws IllegalArgumentException when the date provided is not valid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  void addStockDollarCostInvestment(IStockDataSource source,
      IDollarCostInvestment dollarCostInvestment)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Add rebalanced investments for this flexible portfolio stock.
   *
   * @param rebalance the investment to be added.
   * @throws IllegalArgumentException when the date provided is not valid.
   */
  void addStockRebalance(IStockDataSource source, IRebalance rebalance)
          throws IllegalArgumentException;

  List<IRebalance> getRebalanceData(Map<Date, IRebalance> map);


}
