package model.portfolio;

/**
 * This interface represents a stock.
 */
public interface IStock {

  /**
   * Gets the symbol of the stock.
   *
   * @return the symbol of the stock in {@code String} format.
   */
  String getSymbol();

  /**
   * Gets the name of the stock.
   *
   * @return the name of the stock in {@code String} format.
   */
  String getName();

  /**
   * Gets the exchange the stock is traded in.
   *
   * @return the name of the exchange in {@code String} format.
   */
  String getExchange();

  /**
   * Checks whether the provided object is a {@code Stock}.
   *
   * @param o object to be checked.
   * @return true if the provided object is a {@code Stock}. false otherwise.
   */
  boolean equalsStock(Object o);

  /**
   * Checks whether the provided object is a {@code PortfolioStock}.
   *
   * @param o object to be checked.
   * @return true if the provided object is a {@code PortfolioStock}. false otherwise.
   */
  boolean equalsPortfolioStock(Object o);

  /**
   * Checks whether the provided object is a {@code PortfolioStockValue}.
   *
   * @param o object to be checked.
   * @return true if the provided object is a {@code PortfolioStockValue}. false otherwise.
   */
  boolean equalsPortfolioStockValue(Object o);
}
