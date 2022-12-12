package view;

import common.pair.Pair;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import model.chart.IChart;
import model.portfolio.IAbstractPortfolio;
import model.portfolio.IFlexiblePortfolio;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolio;
import model.portfolio.IPortfolioStock;
import model.portfolio.IPortfolioStockValue;

/**
 * An interface representing a Visual representation of the application.
 */
public interface IPortfolioView {

  /**
   * Displays the home page. The application starts on this page.
   *
   * @param portfolios   the current list of portfolios that exist in the application.
   * @param flexiblePortfolios the current list of flexible portfolios that exist in
   *                           the application.
   * @param prompts      the available prompts on this page.
   * @param errorMessage the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayHomePage(List<Pair<Integer, IPortfolio>> portfolios,
      List<Pair<Integer, IFlexiblePortfolio>> flexiblePortfolios, List<String> prompts,
      String errorMessage);

  /**
   * Displays the generic page which requests a portfolio to be entered. Used for Composition and
   * Value prompt state.
   *
   * @param pageName     the name of the page.
   * @param portfolios   the current list of portfolios that exist in the application.
   * @param prompts      the available prompts on this page.
   * @param errorMessage the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   * @param <T> the type parameter.
   */
  <T extends IAbstractPortfolio> String displayGenericPortfolioPromptPage(String pageName,
      List<Pair<Integer, T>> portfolios, List<String> prompts, String errorMessage);

  /**
   * Displays a page with the composition of the specified portfolio.
   *
   * @param portfolioComposition a pair containing the name of the portfolio and the stocks in the
   *                             specified portfolio.
   * @param prompts              the available prompts on this page.
   * @param errorMessage         the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayCompositionResultPage(Pair<String, List<String>> portfolioComposition,
      List<String> prompts,
      String errorMessage);

  /**
   * Displays a page with prompt for the date on which value of portfolio is to be fetched.
   *
   * @param portfolioName the name of the portfolio.
   * @param prompts       the available prompts on this page.
   * @param errorMessage  the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayValueDatePromptPage(String portfolioName, List<String> prompts,
      String errorMessage);

  /**
   * Displays a page with the value of the specified portfolio.
   *
   * @param portfolioName        the name of the portfolio.
   * @param portfolioStockValues the values of the stocks in the portfolio.
   * @param prompts              the available prompts on this page.
   * @param errorMessage         the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayValueResultPage(String portfolioName,
      Pair<BigDecimal, List<IPortfolioStockValue>> portfolioStockValues, List<String> prompts,
      String errorMessage);

  /**
   * Displays a page with a prompt for the name of the new portfolio to be added.
   *
   * @param prompts      the available prompts on this page.
   * @param errorMessage the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayAddPortfolioNamePromptPage(List<String> prompts, String errorMessage);

  /**
   * Displays a page with a prompt for the stocks to be added to the portfolio. Also shows the
   * stocks that are currently in the portfolio that is being built.
   *
   * @param name                   the name of the portfolio being built.
   * @param currentPortfolioStocks the current list of stocks in the portfolio being built.
   * @param prompts                the available prompts on this page.
   * @param errorMessage           the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayAddPortfolioStocksPromptPage(String name,
      List<IPortfolioStock> currentPortfolioStocks,
      List<String> prompts, String errorMessage);

  /**
   * Displays a generic result page.
   *
   * @param pageName     the name of the result page.
   * @param isSuccessful whether the result of the operation is successful.
   * @param prompts      the available prompts on this page.
   * @param errorMessage the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayGenericResultPage(String pageName, boolean isSuccessful,
      List<String> prompts, String errorMessage);

  /**
   * Displays a generic date prompt page.
   *
   * @param pageHeading  the heading of the page .
   * @param prompts      the available prompts on this page.
   * @param errorMessage the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayGenericDatePromptPage(String pageHeading, List<String> prompts,
      String errorMessage);

  /**
   * Displays a page with a prompt for the flexible stocks to be added to the flexible portfolio.
   * Also shows the stocks that are currently in the flexible portfolio.
   *
   * @param portfolioName          the portfolioName of the flexible portfolio.
   * @param actionName             the portfolioName of the action: "buy" or "sell".
   * @param currentPortfolioStocks the current list of stocks in the flexible portfolio.
   * @param prompts                the available prompts on this page.
   * @param errorMessage           the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayFlexiblePortfolioStocksPromptPage(String portfolioName, String actionName,
      List<IObservableFlexiblePortfolioStock> currentPortfolioStocks,
      List<String> prompts, String errorMessage);

  /**
   * Displays a generic page with a prompt for a file path. This page is used for import and
   * export.
   *
   * @param pageName     the name of the page.
   * @param prompts      the available prompts on this page.
   * @param errorMessage the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayGenericPathPromptPage(String pageName, List<String> prompts, String errorMessage);

  /**
   * Displays a generic page with the result of the file path related operation. This page is used
   * for import and export.
   *
   * @param pageName     the name of the page.
   * @param filePath     the path of the file specified for the operation.
   * @param isSuccessful whether the operation was successful.
   * @param prompts      the available prompts on this page.
   * @param errorMessage the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayGenericPathPromptResultPage(String pageName, String filePath, boolean isSuccessful,
      List<String> prompts, String errorMessage);

  /**
   * Displays the cost basis result page for the flexible portfolio.
   *
   * @param portfolioName the name of the flexible portfolio.
   * @param costBasis     the cost basis of the portfolio.
   * @param prompts       the available prompts on this page.
   * @param errorMessage  the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayFlexiblePortfolioCostBasisPage(String portfolioName, BigDecimal costBasis,
      List<String> prompts, String errorMessage);

  /**
   * Displays the performance chart page for the flexible portfolio specified.
   *
   * @param portfolioName the name of the portfolio.
   * @param startDate     the start date of the performance chart.
   * @param endDate       the end date of the performance chart.
   * @param chart         the chart object containing the chart to be displayed.
   * @param prompts       the available prompts on this page.
   * @param errorMessage  the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayFlexiblePortfolioPerformancePage(String portfolioName, Date startDate,
      Date endDate, IChart chart,
      List<String> prompts, String errorMessage);

  /**
   * Displays the commission fee prompt page.
   *
   * @param currentCommissionFee the current commission fee.
   * @param prompts              the available prompts on this page.
   * @param errorMessage         the error message to be displayed if there is an error.
   * @return a string with the contents of the page to be displayed.
   */
  String displayFlexiblePortfolioCommissionFeePromptPage(BigDecimal currentCommissionFee,
      List<String> prompts,
      String errorMessage);
}
