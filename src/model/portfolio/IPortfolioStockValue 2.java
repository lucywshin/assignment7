package model.portfolio;

import java.math.BigDecimal;

/**
 * This interface represents the stock in the portfolio with its value.
 */
public interface IPortfolioStockValue extends IPortfolioStock {

  /**
   * Gets the value of the stock.
   *
   * @return the value of the stock in {@code BigDecimal} format. Returns -1 if the value is not
   *     available.
   */
  BigDecimal getValue();
}
