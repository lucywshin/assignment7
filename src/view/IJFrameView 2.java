package view;

import common.pair.Pair;
import features.IPortfolioManagerFeatures;
import java.math.BigDecimal;
import java.util.List;
import model.chart.IChart;
import model.portfolio.IDollarCostInvestment;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolioStockValue;

/**
 * This interface represents the JFrame GUI representation of the application.
 */
public interface IJFrameView {

  /**
   * Grants access to the features of the application to the view via a features object.
   *
   * @param features the features available in the application.
   */
  void addFeatures(IPortfolioManagerFeatures features);

  /**
   * Displays an error message.
   *
   * @param errorMessage the provided error message to display.
   */
  void displayErrorMessage(String errorMessage);

  /**
   * Loads the list of portfolios.
   *
   * @param portfolioNames a list of pairs of portfolio ids and names.
   */
  void loadPortfoliosList(List<Pair<Integer, String>> portfolioNames);

  /**
   * Creates a portfolio.
   *
   * @param portfolioName the user inputted portfolio name.
   */
  void createPortfolio(String portfolioName);

  /**
   * Loads the list of stocks of a portfolio.
   *
   * @param portfolioStocks a list of flexible portfolio stocks.
   */
  void loadPortfolioStocksList(List<IObservableFlexiblePortfolioStock> portfolioStocks);

  /**
   * Loads the portfolio's composition.
   *
   * @param composition a list of pairs of stock names and volumes.
   */
  void loadPortfolioComposition(List<Pair<String, BigDecimal>> composition);

  /**
   * Loads the portfolio's value.
   *
   * @param value a pair of the total portfolio value and a list of the individual stock values.
   */
  void loadPortfolioValue(Pair<BigDecimal, List<IPortfolioStockValue>> value);

  /**
   * Loads the portfolio's cost basis.
   *
   * @param costBasis the portfolio's cost basis on a given date.
   */
  void loadPortfolioCostBasis(BigDecimal costBasis);

  /**
   * Loads the portfolio's dollar-cost investment strategies.
   *
   * @param dollarCostInvestments a list of the portfolio's dollar-cost investments.
   */
  void loadPortfolioDollarCostInvestments(
      List<Pair<String, IDollarCostInvestment>> dollarCostInvestments);

  /**
   * Loads the performance chart of a portfolio.
   *
   * @param chart the performance chart of a portfolio for a user provided time range.
   */
  void loadPerformanceChart(IChart chart);
}
