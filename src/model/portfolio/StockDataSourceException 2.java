package model.portfolio;

/**
 * An exception thrown by the {@code StockDataSource}.
 */
public class StockDataSourceException extends Exception {

  /**
   * An exception with a detailed message.
   *
   * @param message a message indicating the cause for the exception.
   */
  public StockDataSourceException(String message) {
    super(message);
  }

  /**
   * An exception with a detailed message and the error that caused this exception.
   *
   * @param message a message indicating the cause for the exception.
   * @param err     The internal error that caused this exception.
   */
  public StockDataSourceException(String message, Throwable err) {
    super(message, err);
  }
}
