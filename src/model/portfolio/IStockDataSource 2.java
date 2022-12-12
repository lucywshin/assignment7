package model.portfolio;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This interface represents a stock data source. Any implementation of an external source must
 * implement this interface.
 */
public interface IStockDataSource {

  /**
   * Gets the {@code Stock} containing the name and symbol of the stock.
   *
   * @param symbol The symbol of the stock using which the stock is traded.
   * @return the {@code Stock} with the name and symbol of the stock.
   * @throws IllegalArgumentException when the provided stock is not supported by the data source.
   */
  IStock getStock(String symbol) throws IllegalArgumentException;

  /**
   * Gets the IPO date of the stock.
   *
   * @param symbol The symbol of the stock using which the stock is traded.
   * @return the {@code Date} on which the stock was listed on the exchange.
   * @throws IllegalArgumentException when the provided stock is not supported by the data source.
   */
  Date getIPODate(String symbol) throws IllegalArgumentException;

  /**
   * Gets the delisting date of the stock.
   *
   * @param symbol The symbol of the stock using which the stock is traded.
   * @return the {@code Date} on which the stock was de-listed. returns {@code null} if the stock
   *     isn't de-listed.
   */
  Date getDelistingDate(String symbol);

  /**
   * Gets the price of the stock on the specified date.
   *
   * @param symbol          The symbol of the stock using which the stock is traded.
   * @param date            the date on which the price of stock has to be retrieved.
   * @param takeFuturePrice a flag to indicate to the data source to take future price instead of
   *                        past price if requested date's price is not available.
   * @return the value of the stock on specified date.
   * @throws StockDataSourceException when an error occurred in the stock data source.
   */
  BigDecimal getStockPrice(String symbol, Date date, boolean takeFuturePrice)
      throws StockDataSourceException;
}
