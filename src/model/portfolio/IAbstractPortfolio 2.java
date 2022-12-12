package model.portfolio;

import common.pair.Pair;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * This interface represents an abstract portfolio, containing only name.
 */
public interface IAbstractPortfolio {

  /**
   * Gets the name of this portfolio.
   *
   * @return the name of the portfolio in {@code String} format.
   */
  String getName();

  /**
   * Gets the values of the portfolio and the value of the stocks on specified date.
   *
   * @param source the data source which is to be used to fetch the values of the stocks.
   * @param date   the date for which the value of the stock is to be fetched.
   * @return A {@code Pair} with the first value corresponding to total value of the portfolio on *
   *     the specified date, and the second value corresponding to the list of stocks in the *
   *     portfolio containing the value of the stock on the specified date.
   * @throws IllegalArgumentException when the provided input is invalid.
   * @throws StockDataSourceException when an error occurs in the stock data source.
   */
  Pair<BigDecimal, List<IPortfolioStockValue>> getValue(IStockDataSource source, Date date)
      throws IllegalArgumentException, StockDataSourceException;

  /**
   * Converts the contents of the portfolio to a list of {@code String}s, one item corresponding to
   * each stock in the portfolio.
   *
   * @return A list of strings representing the contents of the portfolio.
   */
  List<String> toCsvRows();
}
