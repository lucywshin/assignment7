package model.portfolio;

import java.util.List;

/**
 * This interface represents a portfolio of stocks.
 */
public interface IPortfolio extends IAbstractPortfolio {

  /**
   * Gets the stocks in this portfolio.
   *
   * @return A list containing the stocks that are in this portfolio.
   */
  List<IPortfolioStock> getStocks();

  /**
   * The composition of the portfolio.
   *
   * @return the names of the stocks in this portfolio.
   */
  List<String> getComposition();
}
