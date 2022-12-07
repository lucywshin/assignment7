package view;

import static org.junit.Assert.assertEquals;

import common.pair.Pair;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import model.StockDataSourceMock;
import model.chart.Chart;
import model.chart.IChart;
import model.portfolio.IFlexiblePortfolio;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolio;
import model.portfolio.IPortfolioStock;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.Portfolio;
import model.portfolio.PortfolioStock;
import org.junit.Before;
import org.junit.Test;

/**
 * A JUnit test class for the PortfolioViewImpl class.
 */
public class PortfolioViewTest {

  //<editor-fold desc="State Variables">

  private enum eDisplayPage {
    HOME,
    ADD_PORTFOLIO_NAME_PROMPT,
    ADD_PORTFOLIO_STOCKS_PROMPT,
    COMPOSITION_PORTFOLIO_PROMPT,
    COMPOSITION_RESULT,
    VALUE_PORTFOLIO_PROMPT,
    VALUE_DATE_PROMPT,
    VALUE_RESULT,
    EXPORT_PATH_PROMPT,
    EXPORT_RESULT,
    IMPORT_PATH_PROMPT,
    IMPORT_RESULT
  }

  final int titlePosition = 0;
  final int errorPosition = 2;
  final String errorMessage = "This is an error message.";
  final String lineBreak = "------------------------------------------------------------";
  private IPortfolioView view;

  private IPortfolioStock portfolioStock1;
  private IPortfolioStock portfolioStock2;
  private IPortfolioStock portfolioStock3;
  private Pair<String, BigDecimal> stock1;
  private Pair<String, BigDecimal> stock2;
  private Pair<String, BigDecimal> stock3;
  private final List<IPortfolioStock> currentPortfolioStocks = new ArrayList<>();
  private final List<Pair<Integer, IPortfolio>> emptyPortfolioList = new ArrayList<>();
  private final List<Pair<Integer, IPortfolio>> portfolioList = new ArrayList<>();
  private final List<Pair<Integer, IFlexiblePortfolio>> flexiblePortfolioList = new ArrayList<>();
  private Pair<String, List<String>> portfolioComposition;
  private StockDataSourceMock stockDataSource;

  //</editor-fold>

  //<editor-fold desc="Setup">

  /**
   * Setting up the view, PortfolioStocks, Portfolios and a List of Portfolios.
   *
   * @throws InstantiationException if portfolio creation fails.
   */
  @Before
  public void setUp() throws InstantiationException {
    view = new PortfolioView();

    this.portfolioStock1 = new PortfolioStock("GOOG", "Google",
        "NASDAQ", new BigDecimal(100));

    this.portfolioStock2 = new PortfolioStock("MSFT", "Microsoft",
        "NASDAQ", new BigDecimal(100));

    this.portfolioStock3 = new PortfolioStock("AMZN", "Amazon",
        "NASDAQ", new BigDecimal(100));

    stock1 = new Pair<>(portfolioStock1.getSymbol(), portfolioStock1.getVolume());
    stock2 = new Pair<>(portfolioStock2.getSymbol(), portfolioStock2.getVolume());
    stock3 = new Pair<>(portfolioStock3.getSymbol(), portfolioStock3.getVolume());

    stockDataSource = new StockDataSourceMock();

    IPortfolio p1 = Portfolio.getBuilder()
        .setName("PortfolioOne")
        .addStocks(this.stockDataSource, List.of(stock1, stock2, stock3))
        .create();

    IPortfolioStock portfolioStock4 = new PortfolioStock("GOOG", "Google",
        "NASDAQ", new BigDecimal(500));

    IPortfolioStock portfolioStock5 = new PortfolioStock("MSFT", "Microsoft",
        "NASDAQ", new BigDecimal(500));

    IPortfolioStock portfolioStock6 = new PortfolioStock("AMZN", "Amazon",
        "NASDAQ", new BigDecimal(500));

    Pair<String, BigDecimal> stock4 = new Pair<>(portfolioStock4.getSymbol(),
        portfolioStock4.getVolume());
    Pair<String, BigDecimal> stock5 = new Pair<>(portfolioStock5.getSymbol(),
        portfolioStock5.getVolume());
    Pair<String, BigDecimal> stock6 = new Pair<>(portfolioStock6.getSymbol(),
        portfolioStock6.getVolume());

    IPortfolio p2 = Portfolio.getBuilder()
        .setName("PortfolioTwo")
        .addStocks(this.stockDataSource, List.of(stock4, stock5, stock6))
        .create();

    portfolioList.add(new Pair<>(0, p1));
    portfolioList.add(new Pair<>(1, p2));
  }

  //</editor-fold>

  //<editor-fold desc="Regular Portfolio View Tests">

  /**
   * Testing displaying the home page.
   */
  @Test
  public void testRegular_DisplayHomePage() {

    List<String> prompts = getPromptsForPage(eDisplayPage.HOME);

    String result =
        view.displayHomePage(portfolioList, flexiblePortfolioList, prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Home Page", lines[titlePosition]);

    int lineNum = titlePosition;
    assertEquals("Available Regular Portfolios:", lines[lineNum + 2]);
    int i = checkAvailablePortfoliosDisplay(lines, lineNum + 3, portfolioList);
    checkLineBreakDisplay(lines, i + 2);
    checkPromptsAndInputDisplay(lines, i + 3, prompts);
  }

  /**
   * Testing displaying the home page when there is an error message.
   */
  @Test
  public void testRegular_HomePage_WithError() {

    List<String> prompts = getPromptsForPage(eDisplayPage.HOME);

    String result = view.displayHomePage(portfolioList, flexiblePortfolioList, prompts,
        errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Home Page", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;

    assertEquals("Available Regular Portfolios:", lines[lineNum + 2]);
    int i = checkAvailablePortfoliosDisplay(lines, lineNum + 3, portfolioList);
    checkLineBreakDisplay(lines, i + 2);
    checkPromptsAndInputDisplay(lines, i + 3, prompts);
  }

  /**
   * Testing displaying the home page when there are no portfolios.
   */
  @Test
  public void testRegular_DisplayHomePage_WithNoPortfolios() {

    List<String> prompts = getPromptsForPage(eDisplayPage.HOME);

    String result =
        view.displayHomePage(emptyPortfolioList, flexiblePortfolioList, prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Home Page", lines[titlePosition]);

    int lineNum = titlePosition;

    assertEquals("Available Regular Portfolios:", lines[lineNum + 2]);
    int i = checkAvailablePortfoliosDisplay(lines, lineNum + 3, emptyPortfolioList);
    checkLineBreakDisplay(lines, i + 2);
    checkPromptsAndInputDisplay(lines, i + 3, prompts);
  }

  /**
   * Testing displaying the add portfolio name prompt page.
   */
  @Test
  public void testRegular_DisplayAddPortfolioNamePromptPage() {
    List<String> prompts = getPromptsForPage(eDisplayPage.ADD_PORTFOLIO_NAME_PROMPT);

    String result = view.displayAddPortfolioNamePromptPage(prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Add Portfolio - Name Prompt", lines[titlePosition]);

    int lineNum = titlePosition;
    assertEquals("Enter the name of your portfolio.", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the add portfolio name prompt page when there is an error message.
   */
  @Test
  public void testRegular_DisplayAddPortfolioNamePromptPage_WithError() {
    List<String> prompts = getPromptsForPage(eDisplayPage.ADD_PORTFOLIO_NAME_PROMPT);

    String result = view.displayAddPortfolioNamePromptPage(prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Add Portfolio - Name Prompt", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;

    assertEquals("Enter the name of your portfolio.", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);

  }

  /**
   * Testing displaying the add portfolio stocks prompt page.
   */
  @Test
  public void testRegular_DisplayAddPortfolioStocksPromptPage() {
    currentPortfolioStocks.add(portfolioStock1);
    currentPortfolioStocks.add(portfolioStock2);
    currentPortfolioStocks.add(portfolioStock3);

    List<String> prompts = getPromptsForPage(eDisplayPage.ADD_PORTFOLIO_STOCKS_PROMPT);

    String result = view.displayAddPortfolioStocksPromptPage("PortfolioOne",
        currentPortfolioStocks, prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Add Portfolio - Stocks Prompt", lines[titlePosition]);

    int lineNum = titlePosition;

    assertEquals("Enter in the stocks to be added to the \"" + "PortfolioOne\""
        + " portfolio.", lines[lineNum + 2]);
    assertEquals("Below are the stocks currently in the portfolio:", lines[lineNum + 4]);
    int i = checkCurrentStocksInPortfolio(lines, lineNum + 5, currentPortfolioStocks);
    checkLineBreakDisplay(lines, i + 1);
    checkPromptsAndInputDisplay(lines, i + 2, prompts);
  }

  /**
   * Testing displaying the add portfolio stocks prompt page when there is an error message.
   */
  @Test
  public void testRegular_DisplayAddPortfolioStocksPromptPage_WithError() {
    currentPortfolioStocks.add(portfolioStock1);
    currentPortfolioStocks.add(portfolioStock2);
    currentPortfolioStocks.add(portfolioStock3);

    List<String> prompts = getPromptsForPage(eDisplayPage.ADD_PORTFOLIO_STOCKS_PROMPT);

    String result = view.displayAddPortfolioStocksPromptPage("PortfolioOne",
        currentPortfolioStocks, prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Add Portfolio - Stocks Prompt", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;
    assertEquals("Enter in the stocks to be added to the \"" + "PortfolioOne\""
        + " portfolio.", lines[lineNum + 2]);
    assertEquals("Below are the stocks currently in the portfolio:", lines[lineNum + 4]);
    int i = checkCurrentStocksInPortfolio(lines, lineNum + 5, currentPortfolioStocks);
    checkLineBreakDisplay(lines, i + 1);
    checkPromptsAndInputDisplay(lines, i + 2, prompts);
  }

  /**
   * Testing displaying the composition portfolio prompt page.
   */
  @Test
  public void testRegular_DisplayCompositionPortfolioPromptPage() {
    List<String> prompts = getPromptsForPage(eDisplayPage.COMPOSITION_PORTFOLIO_PROMPT);

    String result = view.displayGenericPortfolioPromptPage("Composition", portfolioList,
        prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Composition Portfolio Prompt", lines[titlePosition]);

    int lineNum = titlePosition;

    assertEquals("Select a portfolio:", lines[lineNum + 2]);
    int i = checkAvailablePortfoliosDisplay(lines, lineNum + 3, portfolioList);
    checkLineBreakDisplay(lines, i + 1);
    checkPromptsAndInputDisplay(lines, i + 2, prompts);
  }

  /**
   * Testing displaying the composition portfolio prompt page when there is an error message.
   */
  @Test
  public void testRegular_DisplayCompositionPortfolioPromptPage_WithError() {
    List<String> prompts = getPromptsForPage(eDisplayPage.COMPOSITION_PORTFOLIO_PROMPT);

    String result = view.displayGenericPortfolioPromptPage("Composition", portfolioList,
        prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Composition Portfolio Prompt", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;

    assertEquals("Select a portfolio:", lines[lineNum + 2]);
    int i = checkAvailablePortfoliosDisplay(lines, lineNum + 3, portfolioList);
    checkLineBreakDisplay(lines, i + 1);
    checkPromptsAndInputDisplay(lines, i + 2, prompts);
  }

  /**
   * Testing displaying the composition result page.
   */
  @Test
  public void testRegular_DisplayCompositionResultPage() {
    List<String> stocks = new ArrayList<>();
    stocks.add("GOOG");
    stocks.add("MSFT");
    stocks.add("AMZN");

    portfolioComposition = new Pair<>("myPortfolio", stocks);

    List<String> prompts = getPromptsForPage(eDisplayPage.COMPOSITION_RESULT);

    String result =
        view.displayCompositionResultPage(portfolioComposition, prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Composition Result", lines[titlePosition]);

    int lineNum = titlePosition;
    assertEquals("The portfolio \"" + portfolioComposition.getO1() + "\" is composed of:",
        lines[lineNum + 2]);
    int i = checkCompositionResultDisplay(lines, lineNum + 3, portfolioComposition);
    checkLineBreakDisplay(lines, i + 1);
    checkPromptsAndInputDisplay(lines, i + 2, prompts);
  }

  /**
   * Testing displaying the composition result page when there is an error message.
   */
  @Test
  public void testRegular_DisplayCompositionResultPage_WithError() {
    List<String> stocks = new ArrayList<>();
    stocks.add("GOOG");
    stocks.add("MSFT");
    stocks.add("AMZN");

    portfolioComposition = new Pair<>("myPortfolio", stocks);

    List<String> prompts = getPromptsForPage(eDisplayPage.COMPOSITION_RESULT);

    String result = view.displayCompositionResultPage(portfolioComposition, prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Composition Result", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;
    assertEquals("The portfolio \"" + portfolioComposition.getO1() + "\" is composed of:",
        lines[lineNum + 2]);
    int i = checkCompositionResultDisplay(lines, lineNum + 3, portfolioComposition);
    checkLineBreakDisplay(lines, i + 1);
    checkPromptsAndInputDisplay(lines, i + 2, prompts);

  }

  /**
   * Testing displaying the value portfolio prompt page.
   */
  @Test
  public void testRegular_DisplayValuePortfolioPromptPage() {
    List<String> prompts = getPromptsForPage(eDisplayPage.VALUE_PORTFOLIO_PROMPT);

    String result = view.displayGenericPortfolioPromptPage("Value", portfolioList,
        prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Value Portfolio Prompt", lines[titlePosition]);

    int lineNum = titlePosition;

    assertEquals("Select a portfolio:", lines[lineNum + 2]);
    int i = checkAvailablePortfoliosDisplay(lines, lineNum + 3, portfolioList);
    checkLineBreakDisplay(lines, i + 1);
    checkPromptsAndInputDisplay(lines, i + 2, prompts);
  }

  /**
   * Testing displaying the value portfolio prompt page when there is an error message.
   */
  @Test
  public void testRegular_DisplayValuePortfolioPromptPage_WithError() {
    List<String> prompts = getPromptsForPage(eDisplayPage.VALUE_PORTFOLIO_PROMPT);

    String result = view.displayGenericPortfolioPromptPage("Value", portfolioList,
        prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Value Portfolio Prompt", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;

    assertEquals("Select a portfolio:", lines[lineNum + 2]);
    int i = checkAvailablePortfoliosDisplay(lines, lineNum + 3, portfolioList);
    checkLineBreakDisplay(lines, i + 1);
    checkPromptsAndInputDisplay(lines, i + 2, prompts);
  }

  /**
   * Testing displaying the value date prompt page.
   *
   * @throws InstantiationException if portfolio creation fails.
   */
  @Test
  public void testRegular_DisplayValueDatePromptPage()
      throws InstantiationException {
    IPortfolio p = Portfolio.getBuilder()
        .setName("PortfolioOne")
        .addStocks(this.stockDataSource, List.of(stock1, stock2, stock3))
        .create();

    List<String> prompts = getPromptsForPage(eDisplayPage.VALUE_DATE_PROMPT);

    String result = view.displayValueDatePromptPage(p.getName(), prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Value Date Prompt", lines[titlePosition]);

    int lineNum = titlePosition;

    assertEquals("Enter the date for which you would like to view the \""
            + p.getName() + "\" portfolio's value.",
        lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the value date prompt page when there is an error message.
   *
   * @throws InstantiationException if portfolio creation fails.
   */
  @Test
  public void testRegular_DisplayValueDatePromptPage_WithError()
      throws InstantiationException {
    IPortfolio p = Portfolio.getBuilder()
        .setName("PortfolioOne")
        .addStocks(this.stockDataSource, List.of(stock1, stock2, stock3))
        .create();

    List<String> prompts = getPromptsForPage(eDisplayPage.VALUE_DATE_PROMPT);

    String result = view.displayValueDatePromptPage(p.getName(), prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Value Date Prompt", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;

    assertEquals("Enter the date for which you would like to view the \""
            + p.getName() + "\" portfolio's value.",
        lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the value result page.
   */
  @Test
  public void testRegular_DisplayValueResultPage() {
    Pair<BigDecimal, List<IPortfolioStockValue>> portfolioStockValues = new Pair<>(
        new BigDecimal("0"), new ArrayList<>());
    List<String> prompts = getPromptsForPage(eDisplayPage.VALUE_RESULT);

    String result = view.displayValueResultPage("myPortfolio", portfolioStockValues,
        prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Value Result", lines[titlePosition]);

    int lineNum = titlePosition;
    assertEquals("The portfolio \"myPortfolio\" contains the following stocks:",
        lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 5);
    checkPromptsAndInputDisplay(lines, lineNum + 6, prompts);
  }

  /**
   * Testing displaying the value result page when there is an error message.
   */
  @Test
  public void testRegular_DisplayValueResultPage_Error() {
    Pair<BigDecimal, List<IPortfolioStockValue>> portfolioStockValues = new Pair<>(
        new BigDecimal("0"), new ArrayList<>());
    List<String> prompts = getPromptsForPage(eDisplayPage.VALUE_RESULT);

    String result = view.displayValueResultPage("myPortfolio", portfolioStockValues,
        prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Value Result", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;
    assertEquals("The portfolio \"myPortfolio\" contains the following stocks:",
        lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 5);
    checkPromptsAndInputDisplay(lines, lineNum + 6, prompts);
  }

  /**
   * Testing displaying the export path prompt page.
   */
  @Test
  public void testRegular_DisplayExportPathPromptPage() {
    List<String> prompts = getPromptsForPage(eDisplayPage.EXPORT_PATH_PROMPT);

    String result = view.displayGenericPathPromptPage("Export", prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Export Prompt", lines[titlePosition]);

    int lineNum = titlePosition;
    assertEquals("Provide the file path for the file being exported.", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the export path prompt page when there is an error message.
   */
  @Test
  public void testRegular_DisplayExportPathPromptPage_WithError() {
    List<String> prompts = getPromptsForPage(eDisplayPage.EXPORT_PATH_PROMPT);

    String result = view.displayGenericPathPromptPage("Export", prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Export Prompt", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;
    assertEquals("Provide the file path for the file being exported.", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the export result page.
   */
  @Test
  public void testRegular_DisplayExportResultPage() {
    List<String> prompts = getPromptsForPage(eDisplayPage.EXPORT_RESULT);
    String result = view.displayGenericPathPromptResultPage("Export",
        "theFilePath", true, prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Export Result", lines[titlePosition]);

    int lineNum = titlePosition;
    assertEquals("File successfully exported to: theFilePath", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the export result page when there is an error message.
   */
  @Test
  public void testRegular_DisplayExportResultPage_WithError() {
    List<String> prompts = getPromptsForPage(eDisplayPage.EXPORT_RESULT);
    String result = view.displayGenericPathPromptResultPage("Export",
        "theFilePath", false, prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Export Result", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;
    assertEquals("Provide the file path for the file being exported.", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the import path prompt page.
   */
  @Test
  public void testRegular_DisplayImportPathPromptPage() {
    List<String> prompts = getPromptsForPage(eDisplayPage.IMPORT_PATH_PROMPT);

    String result = view.displayGenericPathPromptPage("Import", prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Import Prompt", lines[titlePosition]);

    int lineNum = titlePosition;
    assertEquals("Provide the file path to the file being imported.", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the import path prompt page when there is an error message.
   */
  @Test
  public void testRegular_DisplayImportPathPromptPage_WithError() {
    List<String> prompts = getPromptsForPage(eDisplayPage.IMPORT_PATH_PROMPT);
    String result = view.displayGenericPathPromptPage("Import", prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Import Prompt", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;
    assertEquals("Provide the file path to the file being imported.", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);

  }

  /**
   * Testing displaying the import result page.
   */
  @Test
  public void testRegular_DisplayImportResultPage() {
    List<String> prompts = getPromptsForPage(eDisplayPage.IMPORT_RESULT);
    String result = view.displayGenericPathPromptResultPage("Import",
        "theFilePath", true, prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Import Result", lines[titlePosition]);

    int lineNum = titlePosition;
    assertEquals("File successfully imported from: theFilePath", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  /**
   * Testing displaying the import result page when there is an error message.
   */
  @Test
  public void testRegular_DisplayImportResultPage_WithError() {
    List<String> prompts = getPromptsForPage(eDisplayPage.IMPORT_RESULT);
    String result = view.displayGenericPathPromptResultPage("Import",
        "theFilePath", false, prompts, errorMessage);
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Import Result", lines[titlePosition]);
    checkErrorMessageDisplay(lines);

    int lineNum = errorPosition;
    assertEquals("Provide the file path to the file being imported.", lines[lineNum + 2]);
    checkLineBreakDisplay(lines, lineNum + 4);
    checkPromptsAndInputDisplay(lines, lineNum + 5, prompts);
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio View Tests">

  @Test
  public void testFlexible_StocksPromptPage() {
    List<IObservableFlexiblePortfolioStock> currentPortfolioStocks = new ArrayList<>();

    List<String> prompts = new ArrayList<>();
    prompts.add("This is test prompt #1.");
    prompts.add("This is test prompt #2.");
    prompts.add("This is test prompt #3.");

    String result = view.displayFlexiblePortfolioStocksPromptPage("TestPortfolio",
        "buy", currentPortfolioStocks, prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Add Portfolio - Stocks Prompt", lines[titlePosition]);
    assertEquals(
        "Enter in the stocks to buy in the \"" + "TestPortfolio\"" + " portfolio.",
        lines[titlePosition + 2]);

    assertEquals(lineBreak, lines[titlePosition + 4]);
    assertEquals(prompts.get(0), lines[titlePosition + 5]);
    assertEquals(prompts.get(1), lines[titlePosition + 6]);
    assertEquals(prompts.get(2), lines[titlePosition + 7]);
    assertEquals("Your Input:", lines[titlePosition + 9]);
  }

  @Test
  public void testFlexible_CostBasisPage() {
    List<String> prompts = new ArrayList<>();
    prompts.add("This is test prompt #1.");
    prompts.add("This is test prompt #2.");
    prompts.add("This is test prompt #3.");

    String result = view.displayFlexiblePortfolioCostBasisPage("TestPortfolio",
        new BigDecimal(1000), prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Cost Basis", lines[titlePosition]);
    assertEquals(
        "Cost Basis for \"" + "TestPortfolio\"" + " portfolio: $1000",
        lines[titlePosition + 2]);

    assertEquals(lineBreak, lines[titlePosition + 4]);
    assertEquals(prompts.get(0), lines[titlePosition + 5]);
    assertEquals(prompts.get(1), lines[titlePosition + 6]);
    assertEquals(prompts.get(2), lines[titlePosition + 7]);
    assertEquals("Your Input:", lines[titlePosition + 9]);
  }

  @Test
  public void testFlexible_PerformancePage() {
    List<Pair<String, Integer>> dataPoints = new ArrayList<>();
    IChart chart = new Chart("Chart Title", 50, 10, dataPoints);

    Date startDate = new Date(120, 0, 1);
    Date endDate = new Date(120, 11, 31);

    List<String> prompts = new ArrayList<>();
    prompts.add("This is test prompt #1.");
    prompts.add("This is test prompt #2.");
    prompts.add("This is test prompt #3.");

    String result = view.displayFlexiblePortfolioPerformancePage("TestPortfolio",
        startDate, endDate, chart, prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Performance Result", lines[titlePosition]);
    assertEquals("Chart Title", lines[titlePosition + 2]);
    assertEquals("Base: $50", lines[titlePosition + 4]);
    assertEquals("Scale: * = $10", lines[titlePosition + 5]);
    assertEquals(lineBreak, lines[titlePosition + 6]);
    assertEquals(prompts.get(0), lines[titlePosition + 7]);
    assertEquals(prompts.get(1), lines[titlePosition + 8]);
    assertEquals(prompts.get(2), lines[titlePosition + 9]);
    assertEquals("Your Input:", lines[titlePosition + 11]);
  }

  @Test
  public void testFlexible_CommissionFeePage() {
    List<String> prompts = new ArrayList<>();
    prompts.add("This is test prompt #1.");
    prompts.add("This is test prompt #2.");
    prompts.add("This is test prompt #3.");

    String result = view.displayFlexiblePortfolioCommissionFeePromptPage(
        new BigDecimal(5), prompts, "");
    Object[] lines = getResultAsArrayOfLines(result);

    assertEquals("Commission Fee Prompt", lines[titlePosition]);
    assertEquals("Current Commission Fee: $5", lines[titlePosition + 2]);
    assertEquals(
        "Enter the commission fee you would like to set.", lines[titlePosition + 3]);
    assertEquals(lineBreak, lines[titlePosition + 5]);
    assertEquals(prompts.get(0), lines[titlePosition + 6]);
    assertEquals(prompts.get(1), lines[titlePosition + 7]);
    assertEquals(prompts.get(2), lines[titlePosition + 8]);
    assertEquals("Your Input:", lines[titlePosition + 10]);
  }

  //</editor-fold>

  //<editor-fold desc="Helper Methods">

  /**
   * Gets the prompts for the given page.
   *
   * @param thePage the current page.
   * @return a list of strings of the prompts.
   */
  private List<String> getPromptsForPage(eDisplayPage thePage) {
    List<String> prompts = new ArrayList<>();

    switch (thePage) {
      case ADD_PORTFOLIO_NAME_PROMPT:
        prompts.add("Enter the name for the portfolio you would like to create.");
        prompts.add("(H) Go back to Home page.");
        break;

      case ADD_PORTFOLIO_STOCKS_PROMPT:
        prompts.add("Enter the stocks for the portfolio you would like to create "
            + "(enter in "
            + "<Stock1 Symbol>,<Stock1 Volume>;<Stock2 Symbol>,<Stock2 Volume>;... format).");
        prompts.add("(D) Done adding stocks.");
        prompts.add("(H) Go back to Home page.");
        break;

      case COMPOSITION_PORTFOLIO_PROMPT:
        prompts.add("Enter the id of the portfolio you would like to see the composition for: ");
        prompts.add("(H) Go back to Home page.");
        break;

      case VALUE_DATE_PROMPT:
        prompts.add(
            "Enter the date on which you would like to see the value for the portfolio "
                + "(use mm-dd-yyyy format): ");
        prompts.add("(H) Go back to Home page.");
        break;

      case VALUE_PORTFOLIO_PROMPT:
        prompts.add("Enter the id of the portfolio you would like to see the value for: ");
        prompts.add("(H) Go back to Home page.");
        break;

      case EXPORT_PATH_PROMPT:
        prompts.add("Enter the path of file to which the portfolios are to be exported.");
        prompts.add("(H) Go back to Home page.");
        break;

      case IMPORT_PATH_PROMPT:
        prompts.add("Enter the path of file from which the portfolios are to be imported.");
        prompts.add("(H) Go back to Home page.");
        break;

      case COMPOSITION_RESULT:
      case VALUE_RESULT:
      case EXPORT_RESULT:
      case IMPORT_RESULT:
        prompts.add("Press any key to go to the Home page.");
        break;

      case HOME:
      default:
        prompts.add("(A) Add a portfolio.");
        prompts.add("(C) Request Composition for a portfolio.");
        prompts.add("(V) Request Value for a portfolio.");
        prompts.add("(E) Export all portfolios.");
        prompts.add("(I) Import portfolios.");
        break;
    }

    return prompts;
  }

  /**
   * Gets an array of the lines of a page's display.
   *
   * @param result the page's display.
   * @return an array of the lines of the page's display.
   */
  private Object[] getResultAsArrayOfLines(String result) {
    Stream<String> lines = result.lines();
    Object[] linesArray = lines.toArray();

    return linesArray;
  }

  /**
   * Checks if the errorMessage has been displayed in the appropriate position.
   *
   * @param displayArray the array of the lines of the page's display.
   */
  private void checkErrorMessageDisplay(Object[] displayArray) {
    assertEquals("Error: " + errorMessage, displayArray[errorPosition]);
  }

  /**
   * Checks if the available portfolios are displayed in the appropriate positions.
   *
   * @param displayArray the array of the lines of the page's display.
   * @param line         the line number of the page's display to start checking from.
   * @param portfolios   the available portfolios.
   * @return the line number after the display of the portfolios.
   */
  private int checkAvailablePortfoliosDisplay(Object[] displayArray, int line,
      List<Pair<Integer, IPortfolio>> portfolios) {

    for (Pair<Integer, IPortfolio> p : portfolios) {
      assertEquals("\t" + (p.getO1() + 1) + ". " + p.getO2().getName(),
          displayArray[line]);
      line++;
    }

    return line;
  }

  /**
   * Checks if the line break has been displayed in the appropriate position.
   *
   * @param displayArray the array of the lines of the page's display.
   * @param line         the line number of the page's display to start checking from.
   */
  private void checkLineBreakDisplay(Object[] displayArray, int line) {
    assertEquals(lineBreak, displayArray[line]);
  }

  /**
   * Checks if the current stocks in the portfolio, while building the portfolio, are in the
   * appropriate position.
   *
   * @param displayArray           the array of the lines of the page's display.
   * @param line                   the line number of the page's display to start checking from.
   * @param currentPortfolioStocks the current added portfolio stocks.
   * @return the line number after the display of the current added portfolio stocks.
   */
  private int checkCurrentStocksInPortfolio(Object[] displayArray, int line,
      List<IPortfolioStock> currentPortfolioStocks) {

    for (IPortfolioStock ps : currentPortfolioStocks) {
      assertEquals("Symbol: " + ps.getSymbol() + ", Name: " + ps.getName()
          + ", Volume: " + ps.getVolume(), displayArray[line]);
      line++;
    }
    return line;
  }

  /**
   * Checks if the portfolio composition result is displayed in the appropriate position.
   *
   * @param displayArray         the array of the lines of the page's display.
   * @param line                 the line number of the page's display to start checking from.
   * @param portfolioComposition a pair of the portfolio name and the portfolio's composition.
   * @return the line number after the display of the portfolio composition.
   */
  private int checkCompositionResultDisplay(Object[] displayArray, int line,
      Pair<String, List<String>> portfolioComposition) {

    List<String> composition = portfolioComposition.getO2();
    for (String c : composition) {
      assertEquals(c, displayArray[line]);
      line++;
    }
    return line;
  }

  /**
   * Checks if the prompts and input of page are displayed in the appropriate position.
   *
   * @param displayArray the array of the lines of the page's display.
   * @param line         the line number of the page's display to start checking from.
   * @param prompts      a list of strings of a page's prompts.
   */
  private void checkPromptsAndInputDisplay(Object[] displayArray, int line, List<String> prompts) {

    for (String p : prompts) {
      assertEquals(p, displayArray[line]);
      line++;
    }
  }

  //</editor-fold>

}