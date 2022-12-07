package controller;

import static org.junit.Assert.assertEquals;

import common.pair.Pair;
import common.triplet.Triplet;
import controller.PortfolioController.IPage;
import controller.PortfolioController.ePage;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.IPortfolioModel;
import model.chart.IChart;
import model.portfolio.IDollarCostInvestment;
import model.portfolio.IFlexiblePortfolio;
import model.portfolio.IFlexiblePortfolioStock;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolio;
import model.portfolio.IPortfolioStock;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.IStock;
import model.portfolio.Stock;
import model.portfolio.StockDataSourceException;
import model.portfolio.eRecurringIntervalType;
import org.junit.Test;
import view.IPortfolioView;

/**
 * A JUnit test class for testing the functionality of the controller with a mock model and mock
 * view.
 */
public class ControllerModelMockTest {

  //<editor-fold desc="Mock classes">

  private class PortfolioModelMock implements IPortfolioModel {

    //<editor-fold desc="State variables">

    private StringBuilder log;
    private IStock stockOutput;
    private String stringOutput;
    private int intOutput;
    private String builderOutput;
    private List<Pair<Integer, IPortfolio>> portfoliosOutput;
    private List<IPortfolioStock> portfoliosStocksOutput;
    private Pair<String, List<String>> portfolioCompositionOutput;
    private List<IPortfolioStockValue> portfoliosStockValueOutput;
    private Pair<BigDecimal, List<IPortfolioStockValue>> portfolioValueOutput;
    private IPortfolio portfolioOutput;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    PortfolioModelMock(StringBuilder log) {
      this.log = log;
    }

    PortfolioModelMock(StringBuilder log, IStock stockInput) {
      this.log = log;
      this.stockOutput = stockInput;
    }

    PortfolioModelMock(StringBuilder log, IFlexiblePortfolioStock stockInput) {
      this.log = log;
      this.stockOutput = stockInput;
    }

    PortfolioModelMock(StringBuilder log, String stringInput) {
      this.log = log;
      this.stringOutput = stringInput;
    }

    PortfolioModelMock(StringBuilder log, int intInput) {
      this.log = log;
      this.intOutput = intInput;
    }

    PortfolioModelMock(StringBuilder log, boolean isAdded,
        List<Pair<Integer, IPortfolio>> portfoliosOutput) {
      this.log = log;
      this.portfoliosOutput = portfoliosOutput;
    }

    PortfolioModelMock(StringBuilder log, List<IPortfolioStock> portfoliosStocksOutput) {
      this.log = log;
      this.portfoliosStocksOutput = portfoliosStocksOutput;
    }

    PortfolioModelMock(StringBuilder log, Pair<String, List<String>> portfolioCompositionOutput) {
      this.log = log;
      this.portfolioCompositionOutput = portfolioCompositionOutput;
    }

    PortfolioModelMock(StringBuilder log, int stockValue,
        List<IPortfolioStockValue> portfoliosStockValueOutput) {
      this.log = log;
      this.portfoliosStockValueOutput = portfoliosStockValueOutput;
    }

    PortfolioModelMock(StringBuilder log, int stockValue, IPortfolio portfolioOutput) {
      this.log = log;
      this.portfolioOutput = portfolioOutput;
    }

    //</editor-fold>

    //<editor-fold desc="Mock methods">

    @Override
    public IStock getStockDetails(String symbol) {
      log.append("hit getStockDetails method\n");
      this.log.append("Received Symbol: ").append(symbol).append("\n");
      return this.stockOutput;
    }

    @Override
    public String setName(String name) {
      log.append("hit setName method\n");
      this.log.append("Received Name: ").append(name).append("\n");
      return this.builderOutput;
    }

    @Override
    public String getName(int id) {
      log.append("hit getName method\n");
      this.log.append("Received Id: ").append(id).append("\n");
      return this.stringOutput;
    }

    @Override
    public List<Pair<Integer, IPortfolio>> getAllPortfolios() {
      log.append("hit getAllPortfolios method\n");
      this.log.append("Received no input\n");
      return this.portfoliosOutput;
    }

    @Override
    public List<Pair<Integer, IFlexiblePortfolio>> getAllFlexiblePortfolios() {
      log.append("hit getAllFlexiblePortfolios method\n");
      return null;
    }

    @Override
    public int getPortfolioCount() {
      log.append("hit getPortfolioCount method\n");
      this.log.append("Received no input\n");
      return this.intOutput;
    }

    @Override
    public int getFlexiblePortfolioCount() {
      return 0;
    }

    @Override
    public String getPortfolioBuilderName() {
      log.append("hit getPortfolioBuilderName method\n");
      this.log.append("Received no input\n");
      return this.stringOutput;
    }

    @Override
    public String setPortfolioBuilderName(String name) {
      log.append("hit setPortfolioBuilderName method\n");
      this.log.append("Received Name: ").append(name).append("\n");
      return this.stringOutput;
    }

    @Override
    public List<IPortfolioStock> getPortfolioBuilderStocks() {
      log.append("hit getPortfolioBuilderStocks method\n");
      this.log.append("Received no input\n");
      return this.portfoliosStocksOutput;
    }

    @Override
    public List<IPortfolioStock> addStocksToPortfolioBuilder(
        List<Pair<String, BigDecimal>> stocks) {
      log.append("hit addStocksToPortfolioBuilder method\n");
      for (Pair<String, BigDecimal> s : stocks) {
        this.log.append("Received Stock: ").append("symbol: ").append(s.getO1())
            .append(", volume: ").append(s.getO2()).append("\n");
      }
      return this.portfoliosStocksOutput;
    }

    @Override
    public int buildPortfolio() throws InstantiationException {
      log.append("hit buildPortfolio method\n");
      this.log.append("Received no input\n");
      return this.intOutput;
    }

    @Override
    public int createFlexiblePortfolio(String portfolioName) throws InstantiationException {
      log.append("hit createFlexiblePortfolio method\n");
      this.log.append("Received portfolioName: ").append(portfolioName).append("\n");
      return 0;
    }

    @Override
    public Pair<String, List<String>> getPortfolioComposition(int id) {
      log.append("hit getPortfolioComposition method\n");
      this.log.append("Received id: ").append(id).append("\n");
      return this.portfolioCompositionOutput;
    }

    @Override
    public Pair<String, List<Pair<String, BigDecimal>>> getFlexiblePortfolioComposition(int id,
        Date date) {
      log.append("hit getFlexiblePortfolioComposition method\n");
      this.log.append("Received id: ").append(id).append("\n");
      this.log.append("Received date: ").append(date).append("\n");
      return null;
    }

    @Override
    public IPortfolio getPortfolio(int id) {
      log.append("hit getPortfolio method\n");
      this.log.append("Received id: ").append(id).append("\n");
      return this.portfolioOutput;
    }

    @Override
    public IFlexiblePortfolio getFlexiblePortfolio(int id) {
      log.append("hit getFlexiblePortfolio method\n");
      this.log.append("Received id: ").append(id).append("\n");
      return null;
    }

    @Override
    public Pair<BigDecimal, List<IPortfolioStockValue>> getPortfolioValue(int id, Date date) {
      log.append("hit getPortfolioValue method\n");
      this.log.append("Received id: ").append(id).append(", date: ").append(date.toString())
          .append("\n");
      return this.portfolioValueOutput;
    }

    @Override
    public Pair<BigDecimal, List<IPortfolioStockValue>> getFlexiblePortfolioValue(int id, Date date)
        throws StockDataSourceException {
      log.append("hit getFlexiblePortfolioValue method\n");
      this.log.append("Received id: ").append(id).append("\n");
      this.log.append("Received date: ").append(date).append("\n");
      return null;
    }

    @Override
    public void importPortfolios(String filePath) throws IOException {
      log.append("hit importPortfolios method\n");
      this.log.append("Received file path: ").append(filePath).append("\n");
    }

    @Override
    public void importFlexiblePortfolios(String filePath)
        throws IOException, InstantiationException {
      log.append("hit exportFlexiblePortfolios method\n");
      this.log.append("Received file path: ").append(filePath).append("\n");
    }

    @Override
    public void exportPortfolios(String filePath) throws IOException {
      log.append("hit exportPortfolios method\n");
      this.log.append("Received file path: ").append(filePath).append("\n");
    }

    @Override
    public void exportFlexiblePortfolios(String filePath) throws IOException {
      log.append("hit exportFlexiblePortfolios method\n");
      this.log.append("Received file path: ").append(filePath).append("\n");
    }

    @Override
    public IChart getPerformanceChart(int portfolioId, Date startDate, Date endDate)
        throws StockDataSourceException {
      log.append("hit getPerformanceChart method\n");
      this.log.append("Received portfolioId: ").append(portfolioId).append("\n");
      this.log.append("Received startDate: ").append(startDate).append("\n");
      this.log.append("Received endDate: ").append(endDate).append("\n");
      return null;
    }

    @Override
    public Pair<Integer, List<IObservableFlexiblePortfolioStock>> buyStocksForFlexiblePortfolio(
        int portfolioId, List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFee)
        throws StockDataSourceException {
      log.append("hit buyStocksForFlexiblePortfolio method\n");
      this.log.append("Received portfolioId: ").append(portfolioId).append("\n");
      for (var stock : stocks) {
        this.log.append("Received stock: ").append(stock).append("\n");
      }
      return null;
    }

    @Override
    public Pair<Integer, List<IObservableFlexiblePortfolioStock>> sellStocksForFlexiblePortfolio(
        int portfolioId, List<Triplet<String, Date, BigDecimal>> stocks, BigDecimal commissionFee)
        throws StockDataSourceException {
      log.append("hit sellStocksForFlexiblePortfolio method\n");
      this.log.append("Received portfolioId: ").append(portfolioId).append("\n");
      for (var stock : stocks) {
        this.log.append("Received stock: ").append(stock).append("\n");
      }

      return null;
    }

    @Override
    public BigDecimal getCostBasis(int portfolioId, Date date) {
      log.append("hit getCostBasis method\n");
      this.log.append("Received portfolioId: ").append(portfolioId).append("\n");
      this.log.append("Received date: ").append(date).append("\n");
      return null;
    }

    @Override
    public Pair<Integer, List<IObservableFlexiblePortfolioStock>> addDollarCostInvestment(
        int portfolioId, Date date, BigDecimal amount,
        List<Pair<String, BigDecimal>> stocksWithPercentage, BigDecimal commissionFee,
        boolean isRecurring, Date endDate, eRecurringIntervalType intervalType,
        Integer intervalDelta) throws IllegalArgumentException, StockDataSourceException {
      return null;
    }

    @Override
    public Pair<Integer, List<Pair<String, IDollarCostInvestment>>> getDollarCostInvestments(
        int portfolioId) throws IllegalArgumentException {
      return null;
    }

    //</editor-fold>
  }

  //</editor-fold>

  //<editor-fold desc="State variables">

  private Reader in;
  private StringBuffer out;
  private IPortfolioController controller;
  private StringBuilder modelLog;
  private StringBuilder viewLog;
  private IPortfolioModel model;
  private IPortfolioView view;
  private String viewExpectedOutput;

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  /**
   * Set up the controller.
   *
   * @param inputString the string of input from the user.
   */
  public void setupController(String inputString) {
    this.modelLog = new StringBuilder();
    this.viewLog = new StringBuilder();

    this.model = new PortfolioModelMock(modelLog);
    this.view = new PortfolioViewMock(viewLog, viewExpectedOutput);

    this.in = new StringReader(inputString);
    this.out = new StringBuffer();

    this.controller = new PortfolioController(model, view, this.in, this.out);
  }

  public String getOutput() {
    return this.out.toString();
  }

  private List<String> getPromptsForPage(ePage page) throws NotImplementedException {

    var controller = (PortfolioController) this.controller;
    Map<ePage, Function<ePage, IPage>> knownPages
        = controller.getKnownPages();
    Function<ePage, IPage> p = knownPages.get(page);

    controller.pageState.setCurrentPage(page);

    return p.apply(page).getPrompts();
  }

  //</editor-fold>

  //<editor-fold desc="Init Tests">

  /**
   * Testing the initial state of the controller.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void initStateTest() throws NotImplementedException {
    this.setupController("X\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());

    StringBuilder expectedViewLogBuilder = new StringBuilder();
    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Home Page Tests">

  /**
   * Tests the state of controller when invalid input is received. The controller should stay on the
   * home page.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void homePageInvalidInputTest() throws NotImplementedException {
    this.setupController("X\n");
    this.controller.runApplication();

    StringBuilder inputBuilder = new StringBuilder();
    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    List<Character> disallowedCharacters = new ArrayList<>(
        Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'B', 'D', 'F', 'G', 'H', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'Y', 'Z', '0', '1', '2',
            '3', '4', '5', '6', '7', '8', '9'));

    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // iterating over all possible characters
    for (Character c : disallowedCharacters) {
      inputBuilder.append(((char) c) + "\n");

      expectedModelLogBuilder.append("hit getAllPortfolios method\n");
      expectedModelLogBuilder.append("Received no input\n");
      expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

      expectedViewLogBuilder.append("displayed home page\n");
      PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
          this.getPromptsForPage(ePage.HOMEPAGE));
      expectedViewLogBuilder.append(
          "Received error message: Input error: Invalid input provided!\n");
    }
    this.setupController(inputBuilder.append("X\n").toString());
    this.controller.runApplication();

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of controller when there are no portfolios to show composition for. The
   * controller should stay on the home page.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void homePageCompositionWithoutPortfoliosTest() throws NotImplementedException {
    this.setupController("C\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: C
    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received error message: "
        + "State error: There are no portfolios in the application to fetch data for!\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of controller when there are no portfolios to show value for. The controller
   * should stay on the home page.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void homePageValueWithoutPortfoliosTest() throws NotImplementedException {
    this.setupController("V\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: V
    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received error message: "
        + "State error: There are no portfolios in the application to fetch data for!\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of controller when there are no portfolios to export to file. The controller
   * should stay on the home page.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void homePageExportWithoutPortfoliosTest() throws NotImplementedException {
    this.setupController("E\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: C
    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received error message: "
        + "State error: There are no portfolios in the application to fetch data for!\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Add Portfolio Tests">

  /**
   * Tests the state of the controller when going back to home page from the add portfolio page.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioBackToHomePageTest() throws NotImplementedException {
    this.setupController("A\nH\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of the controller when trying to add a portfolio with an invalid name.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioInvalidNameInputTest() throws NotImplementedException {
    this.setupController("A\n!@#$%^&*()\nH\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: !@#$%^&*()
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append(
        "Received error message: " + "Input error: Invalid name entered!\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of the controller when adding a portfolio with a name already existing for
   * another portfolio.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioDuplicateNameTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5\nD\nH\nA\nmyPortfolio\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void addPortfolioValidNameTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5\nD\nH\nA\nmyPortfolio\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of the controller when adding a portfolio with invalid stocks.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioInvalidStocksInputTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\n123,ABC\nH\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: 123,ABC
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append(
        "Received error message: " + "Input error: Invalid stocks entered!\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of the controller when adding a portfolio with an invalid format for stocks
   * input.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioStocksInputWithSpacesTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG, 5\nH\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG, 5
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append(
        "Received error message: " + "Input error: Invalid stocks entered!\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of the controller when adding a portfolio with a single stock input.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioSingleStockInputTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5\nD\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of the controller when adding a portfolio with multiple stock inputs.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioMultipleStockInputTest() throws NotImplementedException {

    this.setupController("A\nmyPortfolio\nGOOG,5;MSFT,7;AMZN,8\nD\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5;MSFT,7;AMZN,8
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: MSFT, volume: 7\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: AMZN, volume: 8\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());

  }

  /**
   * Tests the state of the controller when adding a portfolio with repeated stock inputs.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioRepeatedStockInputTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5;GOOG,5\nD\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5;GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  /**
   * Tests the state of the controller when adding a portfolio with a negative stock volume value.
   *
   * @throws NotImplementedException when prompts for page are not implemented.
   */
  @Test
  public void addPortfolioNegativeVolumeStockTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,-5\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,-5
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append(
        "Received error message: " + "Input error: Invalid stocks entered!\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void addPortfolioZeroVolumeStockTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,0\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,0
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 0\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void addPortfolioMultiStageStockInputTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5\nMSFT,7\nD\nH\nX\n");
    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: MSFT,7
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: MSFT, volume: 7\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Composition Tests">

  @Test
  public void compositionInvalidPortfolioTest_Zero() throws NotImplementedException {
    // check 0 as input
    this.setupController("A\nmyPortfolio\nGOOG,5\nD\nH\nC\n0\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: C
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_PORTFOLIO_PROMPT));
    expectedViewLogBuilder.append("\"Received error message: State error: "
        + "There are no portfolios in the application to fetch data for!\n");

    // requested: 0
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition prompt page\n");
    expectedViewLogBuilder.append("Received page name: \n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_PORTFOLIO_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested" H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void compositionForSingleStockPortfolioTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5\nD\nH\nC\n1\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: C
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_PORTFOLIO_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: 1
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    expectedViewLogBuilder.append("Received page name: \n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested" H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void compositionForMultiStockPortfolioTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5;MSFT,7;AMZN,8\nD\nH\nC\n1\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5;MSFT,7;AMZN,8
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: MSFT, volume: 7\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: AMZN, volume: 8\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Value Tests">

  @Test
  public void valueInvalidPortfolioTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5;MSFT,7;AMZN,8\nD\nH\nV\n3\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5;MSFT,7;AMZN,8
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: MSFT, volume: 7\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: AMZN, volume: 8\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void valueValidPortfolioTest() throws NotImplementedException {
    this.setupController("A\nmyPortfolio\nGOOG,5;MSFT,7;AMZN,8\nD\nH\nV\n1\n10-28-2022\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5;MSFT,7;AMZN,8

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Add Tests">

  @Test
  public void add_FlexiblePortfolioBackToHomePageTest() throws NotImplementedException {
    this.setupController("FA\nH\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void add_FlexiblePortfolioInvalidNameInputTest() throws NotImplementedException {
    this.setupController("FA\n!@#$%^&*()\nH\nX\n");
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: !@#$%^&*()
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append(
        "Received error message: " + "Input error: Invalid name entered!\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Composition Tests">

  @Test
  public void compositionFlexible_InvalidPortfolioTest_Zero() throws NotImplementedException {
    // check 0 as input
    this.setupController("FA\nmyPortfolio\nH\nFB\nGOOG,5,01-01-2020\nD\nH\nFC\n0\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: FC
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_PORTFOLIO_PROMPT));
    expectedViewLogBuilder.append("\"Received error message: State error: "
        + "There are no portfolios in the application to fetch data for!\n");

    // requested: 0
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition prompt page\n");
    expectedViewLogBuilder.append("Received page name: \n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_PORTFOLIO_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested" H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void compositionFlexible_ForSingleStockPortfolioTest() throws NotImplementedException {
    this.setupController("FA\nmyPortfolio\nH\nFB\nGOOG,5,01-01-2020\nD\nH\nFC\n1\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: C
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_PORTFOLIO_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: 1
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    expectedViewLogBuilder.append("Received page name: \n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested" H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void compositionFlexible_ForMultiStockPortfolioTest() throws NotImplementedException {
    this.setupController("FA\nmyPortfolio\nH\nFB\n"
        + "GOOG,5,01-01-2020;MSFT,7,01-01-2020;AMZN,8,01-01-2020"
        + "\nD\nH\nC\n1\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5;MSFT,7;AMZN,8
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: MSFT, volume: 7\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: AMZN, volume: 8\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Value Tests">

  @Test
  public void valueFlexible_InvalidPortfolioTest() throws NotImplementedException {
    this.setupController("FA\nmyPortfolio\nH\nFB\n"
        + "GOOG,5,01-01-2020;MSFT,7,01-01-2020;AMZN,8,01-01-2020"
        + "\nD\nH\nFV\n3\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5;MSFT,7;AMZN,8
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: GOOG, volume: 5\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: MSFT, volume: 7\n");
    expectedModelLogBuilder.append("hit addStocksToPortfolioBuilder method\n");
    expectedModelLogBuilder.append("Received Stock: symbol: AMZN, volume: 8\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit buildPortfolio method\n");
    expectedModelLogBuilder.append("Received no input\n");

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    expectedModelLogBuilder.append("hit getPortfolioCount method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  @Test
  public void valueFlexible_ValidPortfolioTest() throws NotImplementedException {
    this.setupController("FA\nmyPortfolio\nH\nFB\n"
        + "GOOG,5,01-01-2020;MSFT,7,01-01-2020;AMZN,8,01-01-2020"
        + "\nD\nH\nFV\n1\n10-28-2022\nH\nX\n");

    this.model = new PortfolioModelMock(this.modelLog,
        new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ"));
    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: A
    expectedViewLogBuilder.append("displayed add portfolio name prompt page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_NAME_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: myPortfolio
    expectedModelLogBuilder.append("hit setPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received Name: myPortfolio\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderName method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getPortfolioBuilderStocks method\n");
    expectedModelLogBuilder.append("Received no input\n");

    expectedViewLogBuilder.append("displayed add portfolio stocks prompt page\n");
    expectedViewLogBuilder.append("Received name: null\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.ADD_PORTFOLIO_STOCKS_PROMPT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: GOOG,5;MSFT,7;AMZN,8

    // requested: D
    expectedModelLogBuilder.append("hit getPortfolioComposition method\n");
    expectedModelLogBuilder.append("Received id: 0\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed composition result page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.COMPOSITION_RESULT));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Performance Tests">

  @Test
  public void performanceFlexible_ValidTest() throws NotImplementedException {
    this.setupController("FA\nmyPortfolio\nH\nFB\n"
        + "GOOG,5,01-01-2020;MSFT,7,01-01-2020;AMZN,8,01-01-2020"
        + "\nD\nH\nFP\n1\n01-01-2020\n06-01-2020\nH\nX\n");

    this.controller = new PortfolioController(model, view, this.in, this.out);
    this.controller.runApplication();

    StringBuilder expectedModelLogBuilder = new StringBuilder();
    StringBuilder expectedViewLogBuilder = new StringBuilder();

    // init state
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    // requested: H
    expectedModelLogBuilder.append("hit getAllPortfolios method\n");
    expectedModelLogBuilder.append("Received no input\n");
    expectedModelLogBuilder.append("hit getAllFlexiblePortfolios method\n");

    expectedViewLogBuilder.append("displayed home page\n");
    PortfolioViewMock.receivedPrompts(expectedViewLogBuilder,
        this.getPromptsForPage(ePage.HOMEPAGE));
    expectedViewLogBuilder.append("Received no error message\n");

    assertEquals(expectedModelLogBuilder.toString(), this.modelLog.toString());
    assertEquals(expectedViewLogBuilder.toString(), this.viewLog.toString());
  }

  //</editor-fold>

}