package model.portfolio;

import java.math.BigDecimal;

/**
 * This interface represents a stock in a portfolio: the stock symbol, name and volume.
 */
public interface IPortfolioStock extends IStock {

  /**
   * Gets the volume of stock in the portfolio.
   *
   * @return The volume of stock in {@code BigDecimal} format.
   */
  BigDecimal getVolume();
}
