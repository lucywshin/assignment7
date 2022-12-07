package model.portfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import common.Utils;
import common.pair.Pair;
import common.triplet.Triplet;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.StockDataSourceMock;
import model.chart.ChartService;
import model.chart.eChartInterval;
import model.portfolio.Portfolio.PortfolioImplBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * A test class to test functionality of portfolios at model level.
 */
public class ComprehensivePortfolioTest {

  //<editor-fold desc="State variables">

  private IStockDataSource dataSource;

  /* Abstract Stock Test Variables*/
  protected IStock s;
  protected IPortfolioStock ps;
  protected IPortfolioStockValue psv;

  /* Stock Test Variables*/
  private IStock stock;
  private IStock otherStock;

  /* Portfolio Stock Test Variables*/
  private IPortfolioStock portfolioStock;
  private IPortfolioStock otherPortfolioStock;

  /* Portfolio Stock Value Test Variables*/
  private IPortfolioStockValue portfolioStockValue;
  private IPortfolioStockValue otherPortfolioStockValue;

  /* Portfolio Test Variables */
  private Pair<String, BigDecimal> stock1;
  private Pair<String, BigDecimal> stock2;
  private Pair<String, BigDecimal> stock3;

  /* Portfolio Store Test Variables */
  private PortfolioStore store;
  private List<String> rows;
  private List<IPortfolio> portfolios;

  /* Flexible Portfolio Stock Test Variables */
  private IFlexiblePortfolioStock ps1;
  private BigDecimal commissionFees;
  private final List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>> transactions =
      new ArrayList<>();

  /* Flexible Portfolio Test Variables */
  private IFlexiblePortfolio p;
  private IFlexiblePortfolioStock fps1;
  private IFlexiblePortfolioStock fps2;
  private IFlexiblePortfolioStock fps3;
  private List<Pair<String, BigDecimal>> stocksWithPercentage = new ArrayList<>();

  /* Flexible Portfolio Store Test Variables */
  private FlexiblePortfolioStore flexibleStore;
  private List<String> flexibleRows;
  private List<IFlexiblePortfolio> flexiblePortfolios;
  private InputStream flexibleInput;

  /* Dollar Cost Investing and Recurring Event Test Variables */
  private IRecurringEvent recurringEvent;
  private IDollarCostInvestment dollarCostInvest;

  //</editor-fold>

  //<editor-fold desc="Setup">

  @Before
  public void setUp() {
    this.dataSource = new StockDataSourceMock();

    s = new Stock("GOOG", "Google", "NASDAQ");
    ps = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));
    psv = new PortfolioStockValue(dataSource, new Date(2022, 11, 1), ps);

    IPortfolioStock portfolioStock1 = new PortfolioStock("GOOG", "Google", "NASDAQ",
        new BigDecimal(100));

    IPortfolioStock portfolioStock2 = new PortfolioStock("MSFT", "Microsoft", "NASDAQ",
        new BigDecimal(100));

    IPortfolioStock portfolioStock3 = new PortfolioStock("AMZN", "Amazon", "NASDAQ",
        new BigDecimal(100));

    stock1 = new Pair<>(portfolioStock1.getSymbol(), portfolioStock1.getVolume());
    stock2 = new Pair<>(portfolioStock2.getSymbol(), portfolioStock2.getVolume());
    stock3 = new Pair<>(portfolioStock3.getSymbol(), portfolioStock3.getVolume());

    IPortfolioStock ps = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    IPortfolioStockValue psv = new PortfolioStockValue(this.dataSource, new Date(2022, 11, 1), ps);

    /* Portfolio Store Setup */
    rows = new ArrayList<>();
    portfolios = new ArrayList<>();

    store = new PortfolioStore(this.dataSource);

    /* Flexible Portfolio Store Setup */
    flexibleRows = new ArrayList<>();
    flexiblePortfolios = new ArrayList<>();

    flexibleStore = new FlexiblePortfolioStore(this.dataSource);

    Date startDate = new Date(120, 1, 1);
    Date endDate = new Date(122, 1, 1);

    recurringEvent = new RecurringEvent(
        endDate, eRecurringIntervalType.MONTHLY, 6);

    BigDecimal totalAmount = new BigDecimal(2500);
    BigDecimal commission = new BigDecimal(50);

    dollarCostInvest = new DollarCostInvestment(
        startDate, totalAmount, commission, recurringEvent);
  }

  @Before
  public void flexiblePortfolioStockSetup() {
    this.commissionFees = new BigDecimal(5);

    Date date = new Date(120, 01, 01);
    transactions.add(new Pair<>(date,
        new Triplet<>(new BigDecimal(10), new BigDecimal(1000), new BigDecimal(5))));

    date = new Date(120, 02, 01);
    transactions.add(new Pair<>(date,
        new Triplet<>(new BigDecimal(20), new BigDecimal(1000), new BigDecimal(10))));

    ps1 = new FlexiblePortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100),
        new Date(120, 01, 01), new BigDecimal(1000), this.commissionFees);
  }

  @Before
  public void flexiblePortfolioSetup() {
    this.commissionFees = new BigDecimal(5);

    fps1 = new FlexiblePortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100),
        new Date(120, 01, 01), new BigDecimal(1000), this.commissionFees);

    fps2 = new FlexiblePortfolioStock("MSFT", "Microsoft", "NASDAQ", new BigDecimal(100),
        new Date(119, 01, 01), new BigDecimal(2000), this.commissionFees);

    fps3 = new FlexiblePortfolioStock("AMZN", "Amazon", "NASDAQ", new BigDecimal(100),
        new Date(118, 01, 01), new BigDecimal(3000), this.commissionFees);

    p = new FlexiblePortfolio(this.dataSource, "test Portfolio", null);

    Pair<String, BigDecimal> google = new Pair<>("GOOG", new BigDecimal(50));
    Pair<String, BigDecimal> microsoft = new Pair<>("MSFT", new BigDecimal(50));
    stocksWithPercentage.add(google);
    stocksWithPercentage.add(microsoft);
  }

  //</editor-fold>

  //<editor-fold desc="Helper Methods">

  private void checkPortfolioStocksHelper(List<IPortfolioStock> list) {
    assertEquals("GOOG", list.get(0).getSymbol());
    assertEquals("Alphabet Inc - Class C", list.get(0).getName());
    assertEquals("NASDAQ", list.get(0).getExchange());
    assertEquals(new BigDecimal(100), list.get(0).getVolume());

    assertEquals("MSFT", list.get(1).getSymbol());
    assertEquals("Microsoft Corporation", list.get(1).getName());
    assertEquals("NASDAQ", list.get(1).getExchange());
    assertEquals(new BigDecimal(100), list.get(1).getVolume());

    assertEquals("AMZN", list.get(2).getSymbol());
    assertEquals("Amazon.com Inc", list.get(2).getName());
    assertEquals("NASDAQ", list.get(2).getExchange());
    assertEquals(new BigDecimal(100), list.get(2).getVolume());
  }

  private void checkPortfolioStockValuesHelper(Pair<BigDecimal, List<IPortfolioStockValue>> list) {
    assertEquals("GOOG", list.getO2().get(0).getSymbol());
    assertEquals("Alphabet Inc - Class C", list.getO2().get(0).getName());
    assertEquals("NASDAQ", list.getO2().get(0).getExchange());
    assertEquals(new BigDecimal(100), list.getO2().get(0).getVolume());
    assertEquals(new BigDecimal(100000), list.getO2().get(0).getValue());

    assertEquals("MSFT", list.getO2().get(1).getSymbol());
    assertEquals("Microsoft Corporation", list.getO2().get(1).getName());
    assertEquals("NASDAQ", list.getO2().get(1).getExchange());
    assertEquals(new BigDecimal(100), list.getO2().get(1).getVolume());
    assertEquals(new BigDecimal(200000), list.getO2().get(1).getValue());

    assertEquals("AMZN", list.getO2().get(2).getSymbol());
    assertEquals("Amazon.com Inc", list.getO2().get(2).getName());
    assertEquals("NASDAQ", list.getO2().get(2).getExchange());
    assertEquals(new BigDecimal(100), list.getO2().get(2).getVolume());
    assertEquals(new BigDecimal(-1), list.getO2().get(2).getValue());

    assertEquals(new BigDecimal(300000), list.getO1());
  }

  private List<String> getRowsFromCsv(String filePath) throws IOException {
    InputStream input = new FileInputStream(filePath);
    List<String> rows = this.readInputStream(input);
    return rows;
  }

  private List<String> readInputStream(InputStream inputStream) throws IOException {
    List<String> result = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      while (reader.ready()) {
        result.add(reader.readLine());
      }
    }

    return result;
  }

  private void checkPortfolioContents(String portfolioName) {

    switch (portfolioName) {
      case "test Portfolio 1":
        List<IObservableFlexiblePortfolioStock> stocksList1 = flexiblePortfolios.get(0).getStocks();
        IFlexiblePortfolioStock s1 = new FlexiblePortfolioStock("MSFT", "Microsoft Corporation",
            "NASDAQ", new BigDecimal(20), new Date(120, 0, 01), new BigDecimal(2000),
            new BigDecimal(5));
        assertTrue(stocksList1.contains(s1));
        s1 = new FlexiblePortfolioStock("AMZN", "Amazon.com Inc", "NASDAQ", new BigDecimal(10),
            new Date(120, 0, 01), new BigDecimal(3000), new BigDecimal(5));
        assertTrue(stocksList1.contains(s1));
        s1 = new FlexiblePortfolioStock("GOOG", "Alphabet Inc - Class C", "NASDAQ",
            new BigDecimal(23), new Date(120, 0, 01), new BigDecimal(1000), new BigDecimal(5));
        assertTrue(stocksList1.contains(s1));
        break;
      case "test Portfolio 2":
        List<IObservableFlexiblePortfolioStock> stocksList2 = flexiblePortfolios.get(1).getStocks();
        IFlexiblePortfolioStock s2 = new FlexiblePortfolioStock("A", "Agilent Technologies Inc",
            "NYSE", new BigDecimal(45), new Date(120, 0, 01), new BigDecimal(7000),
            new BigDecimal(5));
        assertTrue(stocksList2.contains(s2));
        s2 = new FlexiblePortfolioStock("B", "Barnes Group Inc", "NYSE", new BigDecimal(33),
            new Date(120, 0, 01), new BigDecimal(8000), new BigDecimal(5));
        assertTrue(stocksList2.contains(s2));
        break;
      case "ligma":
      default:
        List<IObservableFlexiblePortfolioStock> stocksList3 = flexiblePortfolios.get(2).getStocks();
        IFlexiblePortfolioStock s3 = new FlexiblePortfolioStock("AMZN", "Amazon.com Inc", "NASDAQ",
            new BigDecimal(400), new Date(120, 0, 01), new BigDecimal(3000), new BigDecimal(5));
        assertTrue(stocksList3.contains(s3));
        s3 = new FlexiblePortfolioStock("NFLX", "Netflix Inc", "NASDAQ", new BigDecimal(400),
            new Date(120, 0, 01), new BigDecimal(4000), new BigDecimal(5));
        assertTrue(stocksList3.contains(s3));
        s3 = new FlexiblePortfolioStock("TSLA", "Tesla Inc", "NASDAQ", new BigDecimal(400),
            new Date(120, 0, 01), new BigDecimal(5000), new BigDecimal(5));
        assertTrue(stocksList3.contains(s3));
        s3 = new FlexiblePortfolioStock("META", "Meta Platforms Inc - Class A", "NASDAQ",
            new BigDecimal(400), new Date(120, 0, 01), new BigDecimal(6000), new BigDecimal(5));
        assertTrue(stocksList3.contains(s3));
        break;
    }

  }

  private IFlexiblePortfolioStock createStock(String symbol, String name, BigDecimal volume,
      BigDecimal purchasePrice) {
    Date date = new Date(120, 0, 1);
    List<Pair<Date, Triplet<BigDecimal, BigDecimal, BigDecimal>>> transactions = new ArrayList<>();
    transactions.add(new Pair<>(date, new Triplet<>(volume, purchasePrice, new BigDecimal(5))));

    return new FlexiblePortfolioStock(symbol, name, "NASDAQ", volume, transactions);
  }

  //</editor-fold>

  //<editor-fold desc="Abstract Stock Tests">

  @Test
  public void testAbstractStock_Construction() {
    IStock stock = new Stock("GOOG", "Google", "NASDAQ");

    IPortfolioStock portfolioStock = new PortfolioStock("GOOG", "Google", "NASDAQ",
        new BigDecimal(100));

    IPortfolioStockValue portfolioStockValue = new PortfolioStockValue(dataSource,
        new Date(2022, 11, 1), ps);

    assertEquals("GOOG", stock.getSymbol());
    assertEquals("Google", stock.getName());
    assertEquals("NASDAQ", stock.getExchange());

    assertEquals("GOOG", portfolioStock.getSymbol());
    assertEquals("Google", portfolioStock.getName());
    assertEquals("NASDAQ", portfolioStock.getExchange());
    assertEquals(new BigDecimal(100), portfolioStock.getVolume());

    assertEquals("GOOG", portfolioStockValue.getSymbol());
    assertEquals("Google", portfolioStockValue.getName());
    assertEquals("NASDAQ", portfolioStockValue.getExchange());
    assertEquals(new BigDecimal(100), portfolioStockValue.getVolume());
    assertNotNull(portfolioStockValue.getValue());
  }

  /**
   * Testing getting the symbol.
   */
  @Test
  public void testAbstractStock_GetSymbol() {
    assertEquals("GOOG", s.getSymbol());
    assertEquals("GOOG", ps.getSymbol());
    assertEquals("GOOG", psv.getSymbol());
  }

  /**
   * Testing getting the name.
   */
  @Test
  public void testAbstractStock_GetName() {
    assertEquals("Google", s.getName());
    assertEquals("Google", ps.getName());
    assertEquals("Google", psv.getName());
  }

  /**
   * Testing getting the exchange.
   */
  @Test
  public void testAbstractStock_GetExchange() {
    assertEquals("NASDAQ", s.getExchange());
    assertEquals("NASDAQ", ps.getExchange());
    assertEquals("NASDAQ", psv.getExchange());
  }

  /**
   * Testing equals.
   */
  @Test
  public void testAbstractStock_EqualsTrue() {
    IStock other = new Stock("GOOG", "Google", "NASDAQ");
    assertEquals(s, other);
    assertEquals(other, s);

    other = ps;
    assertEquals(ps, other);
    assertEquals(other, ps);

    other = psv;
    assertEquals(psv, other);
    assertEquals(other, psv);
  }

  /**
   * Testing not equals.
   */
  @Test
  public void testAbstractStock_EqualsFalse() {
    IStock other = new Stock("MSFT", "Microsoft", "NASDAQ");
    assertNotEquals(s, other);
    assertNotEquals(other, s);

    IPortfolioStock otherPS = new PortfolioStock("MSFT", "Microsoft", "NASDAQ",
        new BigDecimal(100));
    assertNotEquals(ps, otherPS);
    assertNotEquals(otherPS, ps);

    IPortfolioStockValue otherPSV = new PortfolioStockValue(dataSource, new Date(2022, 11, 1),
        otherPS);
    assertNotEquals(psv, otherPSV);
    assertNotEquals(otherPSV, psv);

  }

  //</editor-fold>

  //<editor-fold desc="Stock Tests">

  /**
   * Testing two equal Stocks.
   */
  @Test
  public void testStock_EqualsStockTrue() {
    stock = new Stock("GOOG", "Google", "NASDAQ");
    otherStock = new Stock("GOOG", "Google", "NASDAQ");

    assertEquals(true, stock.equalsStock(otherStock));
  }

  /**
   * Testing two not equal Stocks.
   */
  @Test
  public void testStock_EqualsStockFalse() {
    stock = new Stock("GOOG", "Google", "NASDAQ");
    otherStock = new Stock("MSFT", "Microsoft", "NASDAQ");

    assertEquals(false, stock.equalsStock(otherStock));
    assertEquals(false, otherStock.equalsStock(stock));
  }

  /**
   * Testing if this Stock is a PortfolioStock.
   */
  @Test
  public void testStock_EqualsPortfolioStock() {

    stock = new Stock("GOOG", "Google", "NASDAQ");

    IPortfolioStock portfolioStock = new PortfolioStock("GOOG", "Google", "NASDAQ",
        new BigDecimal(100));

    assertEquals(false, stock.equalsPortfolioStock(portfolioStock));
  }

  /**
   * Testing if this Stock is a PortfolioStockValue.
   */
  @Test
  public void testStock_EqualsPortfolioStockValue() {

    stock = new Stock("GOOG", "Google", "NASDAQ");

    IStockDataSource stockDataSource = new StockDataSourceMock();

    IPortfolioStock portfolioStock = new PortfolioStock("GOOG", "Google", "NASDAQ",
        new BigDecimal(100));

    IPortfolioStockValue portfolioStockValue = new PortfolioStockValue(stockDataSource,
        new Date(2022, 10, 31), portfolioStock);

    assertEquals(false, stock.equalsPortfolioStockValue(portfolioStockValue));
  }

  //</editor-fold>

  //<editor-fold desc="Portfolio Stock Tests">

  /**
   * Testing getting a PortfolioStock's volume.
   */
  @Test
  public void testPortfolioStock_GetVolume() {
    portfolioStock = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    assertEquals(new BigDecimal(100), portfolioStock.getVolume());
  }

  /**
   * Testing if this PortfolioStock is a Stock.
   */
  @Test
  public void testPortfolioStock_EqualsStock() {
    portfolioStock = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    IStock s = new Stock("GOOG", "Google", "NASDAQ");

    assertEquals(false, portfolioStock.equalsStock(s));
  }

  /**
   * Testing when two PortfolioStocks are equal.
   */
  @Test
  public void testPortfolioStock_EqualsPortfolioStockTrue() {
    portfolioStock = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    otherPortfolioStock = portfolioStock;

    assertEquals(true, portfolioStock.equalsPortfolioStock(otherPortfolioStock));
    assertEquals(true, otherPortfolioStock.equalsPortfolioStock(portfolioStock));
  }

  /**
   * Testing when two PortfolioStocks are not equal.
   */
  @Test
  public void testPortfolioStock_EqualsPortfolioStockFalse() {
    portfolioStock = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    otherPortfolioStock = new PortfolioStock("MSFT", "Microsoft", "NASDAQ", new BigDecimal(100));

    assertEquals(false, portfolioStock.equalsPortfolioStock(otherPortfolioStock));
  }

  /**
   * Testing if this PortfolioStock is a PortfolioStockValue.
   */
  @Test
  public void testPortfolioStock_EqualsPortfolioStockValue() {
    IPortfolioStock portfolioStock = new PortfolioStock("GOOG", "Google", "NASDAQ",
        new BigDecimal(100));

    IPortfolioStockValue portfolioStockValue = new PortfolioStockValue(this.dataSource,
        new Date(2022, 10, 31), portfolioStock);

    assertEquals(false, portfolioStock.equalsPortfolioStockValue(portfolioStockValue));
  }

  //</editor-fold>

  //<editor-fold desc="Portfolio Stock Value Tests">

  @Test
  public void testPortfolioStockValue_GetValue() {
    IPortfolioStock ps = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    portfolioStockValue = new PortfolioStockValue(this.dataSource, new Date(2022, 11, 1), ps);

    assertEquals(new BigDecimal(100000), portfolioStockValue.getValue());
  }

  /**
   * Testing if this PortfolioStockValue is a Stock.
   */
  @Test
  public void testPortfolioStockValue_EqualsStock() {
    IStock stock = new Stock("GOOG", "Google", "NASDAQ");

    IPortfolioStock ps = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    portfolioStockValue = new PortfolioStockValue(this.dataSource, new Date(2022, 11, 1), ps);

    assertFalse(portfolioStockValue.equalsStock(stock));
  }

  /**
   * Testing if this PortfolioStockValue is a PortfolioStock.
   */
  @Test
  public void testPortfolioStockValue_EqualsPortfolioStock() {
    IPortfolioStock ps = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    portfolioStockValue = new PortfolioStockValue(this.dataSource, new Date(2022, 11, 1), ps);

    assertFalse(portfolioStockValue.equalsStock(ps));
  }

  /**
   * Testing when two PortfolioStockValues are equal.
   */
  @Test
  public void testPortfolioStockValue_EqualsPortfolioStockValueTrue() {
    IPortfolioStock ps = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    portfolioStockValue = new PortfolioStockValue(this.dataSource, new Date(2022, 11, 1), ps);
    otherPortfolioStockValue = portfolioStockValue;

    assertTrue(portfolioStockValue.equalsPortfolioStockValue(otherPortfolioStockValue));
    assertTrue(otherPortfolioStockValue.equalsPortfolioStockValue(portfolioStockValue));
  }

  /**
   * Testing when two PortfolioStockValues are not equal.
   */
  @Test
  public void testPortfolioStockValue_EqualsPortfolioStockValueFalse() {
    IPortfolioStock ps = new PortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100));

    portfolioStockValue = new PortfolioStockValue(this.dataSource, new Date(2022, 11, 1), ps);

    ps = new PortfolioStock("MSFT", "Microsoft", "NASDAQ", new BigDecimal(100));
    otherPortfolioStockValue = new PortfolioStockValue(this.dataSource, new Date(2022, 11, 1), ps);

    assertFalse(portfolioStockValue.equalsPortfolioStockValue(otherPortfolioStockValue));
    assertFalse(otherPortfolioStockValue.equalsPortfolioStockValue(portfolioStockValue));
  }

  //</editor-fold>

  //<editor-fold desc="Portfolio Tests">

  /**
   * Testing creation with no name.
   */
  @Test
  public void testPortfolio_Create_NoName() {

    try {
      Portfolio.getBuilder().addStocks(this.dataSource, List.of(stock1, stock2, stock3)).create();
    } catch (InstantiationException e) {
      assertEquals("The name of the portfolio cannot be empty!", e.getMessage());
    }
  }

  /**
   * Testing creation with no stocks.
   */
  @Test
  public void testPortfolio_Create_NoStocks() {

    try {
      Portfolio.getBuilder().setName("myFirstPortfolio").create();
    } catch (InstantiationException e) {
      assertEquals("The portfolio needs to have at least one stock!", e.getMessage());
    }
  }

  /**
   * Testing getting the builder for the PortfolioImpl class.
   *
   * @throws InstantiationException if portfolio creation fails
   */
  @Test
  public void testPortfolio_Create() throws InstantiationException {

    IPortfolio p = Portfolio.getBuilder().setName("myFirstPortfolio")
        .addStocks(this.dataSource, List.of(stock1)).create();

    assertEquals("myFirstPortfolio", p.getName());
  }

  /**
   * Testing setting the name of a portfolio.
   *
   * @throws InstantiationException if portfolio creation fails
   */
  @Test
  public void testPortfolio_SetName() throws InstantiationException {

    PortfolioImplBuilder p = Portfolio.getBuilder();
    assertEquals("", p.getName());

    p.setName("myPortfolio");
    assertEquals("myPortfolio", p.getName());

    IPortfolio portfolio = p.addStocks(this.dataSource, List.of(stock1)).create();
    assertEquals("myPortfolio", portfolio.getName());
  }

  /**
   * Testing setting the name of a portfolio.
   *
   * @throws InstantiationException if portfolio creation fails
   */
  @Test
  public void testPortfolio_OverrideName() throws InstantiationException {

    PortfolioImplBuilder p = Portfolio.getBuilder();
    assertEquals("", p.getName());

    p.setName("myPortfolio");
    assertEquals("myPortfolio", p.getName());

    p.setName("myNewPortfolio");
    assertEquals("myNewPortfolio", p.getName());

    IPortfolio portfolio = p.addStocks(this.dataSource, List.of(stock1)).create();
    assertEquals("myNewPortfolio", portfolio.getName());
  }

  /**
   * Testing clearing the name of a portfolio.
   *
   * @throws InstantiationException if portfolio creation fails
   */
  @Test
  public void testPortfolio_ClearName() throws InstantiationException {

    IPortfolio p = Portfolio.getBuilder().setName("myFirstPortfoilo").clearName()
        .setName("myFirstPortfolio").addStocks(this.dataSource, List.of(stock1)).create();

    assertEquals("myFirstPortfolio", p.getName());
  }

  @Test
  public void testPortfolio_ValidateStocks_EmptySymbol() {

    Pair<String, BigDecimal> invalidStock = new Pair<>("", new BigDecimal(100));
    try {
      Portfolio.getBuilder().setName("myFirstPortfolio")
          .addStocks(this.dataSource, List.of(invalidStock)).create();

    } catch (Exception e) {
      assertEquals("One or more stock has an empty symbol!", e.getMessage());
    }
  }

  /**
   * Testing validation of stocks when the volume of one or more stocks is zero.
   */
  @Test
  public void testPortfolio_ValidateStocks_EmptyVolume() {
    Pair<String, BigDecimal> invalidStock = new Pair<>("GOOG", new BigDecimal(0));
    try {
      Portfolio.getBuilder().setName("myFirstPortfolio")
          .addStocks(this.dataSource, List.of(invalidStock)).create();

    } catch (Exception e) {
      assertEquals("One or more stock has invalid volume!", e.getMessage());
    }
  }

  /**
   * Testing adding stocks to the portfolio.
   *
   * @throws InstantiationException if portfolio creation fails
   */
  @Test
  public void testPortfolio_AddStocks() throws InstantiationException {

    IPortfolio p = Portfolio.getBuilder().setName("myFirstPortfolio")
        .addStocks(this.dataSource, List.of(stock1, stock2, stock3)).create();

    List<IPortfolioStock> stockList;
    stockList = p.getStocks();

    assertEquals(3, stockList.size());

    checkPortfolioStocksHelper(stockList);
  }

  /**
   * Testing getting stocks from a portfolio.
   *
   * @throws InstantiationException if portfolio creation fails
   */
  @Test
  public void testPortfolio_GetStocks() throws InstantiationException {

    IPortfolio p = Portfolio.getBuilder().setName("myFirstPortfolio")
        .addStocks(this.dataSource, List.of(stock1, stock2, stock3)).create();

    List<IPortfolioStock> stockList;
    stockList = p.getStocks();

    assertEquals(3, stockList.size());
  }

  /**
   * Testing removing stocks from a portfolio.
   *
   * @throws InstantiationException if portfolio creation fails
   */
  @Test
  public void testPortfolio_RemoveStocks() throws InstantiationException {

    IPortfolio p = Portfolio.getBuilder().setName("myFirstPortfolio")
        .addStocks(this.dataSource, List.of(stock1, stock2, stock3)).removeStocks(stock1.getO1())
        .removeStocks(stock2.getO1()).create();

    List<IPortfolioStock> stockList;
    stockList = p.getStocks();

    assertEquals(1, stockList.size());

    assertEquals("AMZN", stockList.get(0).getSymbol());
    assertEquals("Amazon.com Inc", stockList.get(0).getName());
    assertEquals("NASDAQ", stockList.get(0).getExchange());
    assertEquals(new BigDecimal(100), stockList.get(0).getVolume());
  }

  /**
   * Testing clearing the stocks of a portfolio.
   *
   * @throws InstantiationException if portfolio creation fails
   */
  @Test
  public void testPortfolio_ClearStocks() throws InstantiationException {

    IPortfolio p = Portfolio.getBuilder().setName("myFirstPortfolio")
        .addStocks(this.dataSource, List.of(stock1, stock2, stock3)).clearStocks()
        .addStocks(this.dataSource, List.of(stock1)).create();

    List<IPortfolioStock> stockList;
    stockList = p.getStocks();

    assertEquals(1, stockList.size());
    assertEquals("GOOG", stockList.get(0).getSymbol());
    assertEquals("Alphabet Inc - Class C", stockList.get(0).getName());
    assertEquals("NASDAQ", stockList.get(0).getExchange());
    assertEquals(new BigDecimal(100), stockList.get(0).getVolume());
  }

  @Test
  public void testPortfolio_GetValue() throws InstantiationException, StockDataSourceException {

    IPortfolio p = Portfolio.getBuilder().setName("myFirstPortfolio")
        .addStocks(this.dataSource, List.of(stock1, stock2, stock3)).create();

    Pair<BigDecimal, List<IPortfolioStockValue>> result;
    result = p.getValue(this.dataSource, new Date(2022, 11, 1));

    assertEquals(3, result.getO2().size());
  }

  @Test
  public void testPortfolio_GetComposition() throws InstantiationException {

    IPortfolio p = Portfolio.getBuilder().setName("myFirstPortfolio")
        .addStocks(this.dataSource, List.of(stock1, stock2, stock3)).create();

    List<String> composition;
    composition = p.getComposition();

    assertEquals(3, composition.size());
    assertEquals("Alphabet Inc - Class C", composition.get(0));
    assertEquals("Microsoft Corporation", composition.get(1));
    assertEquals("Amazon.com Inc", composition.get(2));
  }

  //</editor-fold>

  //<editor-fold desc="Portfolio Store Tests">

  @Test
  public void testPortfolioStore_GetCsvRowHeaders() {
    assertEquals(
        "PortfolioName," + "StockSymbol," + "StockName," + "StockExchange," + "StockVolume\n",
        store.getCsvRowHeaders());
  }

  @Test
  public void testPortfolioStore_GetCsvRowCount() {
    assertEquals(5, store.getCsvRowCount());
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_Empty()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_Empty.csv");

    try {
      portfolios = store.getPortfoliosFromCsvRows(rows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: The file does not have any valid rows!", e.getMessage());
    }
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_FloatVolume()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_FloatVolume.csv");
    assertEquals(7, rows.size());

    portfolios = store.getPortfoliosFromCsvRows(rows);
    assertEquals(2, portfolios.size());
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_NegativeVolume()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_NegativeVolume.csv");
    try {
      portfolios = store.getPortfoliosFromCsvRows(rows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Portfolio stock needs to have valid volume!", e.getMessage());
    }
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_NonNumberVolume()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_NonNumberVolume.csv");
    try {
      portfolios = store.getPortfoliosFromCsvRows(rows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Portfolio stock needs to have valid volume!", e.getMessage());
    }
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_PortfolioNameInvalid()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_PortfolioNameInvalid.csv");
    try {
      portfolios = store.getPortfoliosFromCsvRows(rows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Portfolio name format not valid!", e.getMessage());
    }
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_StockExchangeMismatch()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_StockExchangeMismatch.csv");
    try {
      portfolios = store.getPortfoliosFromCsvRows(rows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Entered data for stock exchange is a mismatch!", e.getMessage());
    }
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_StockNameMismatch()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_StockNameMismatch.csv");
    try {
      portfolios = store.getPortfoliosFromCsvRows(rows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Entered data for stock name is a mismatch!", e.getMessage());
    }
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_StockSymbolDoesntExist()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_StockSymbolDoesntExist.csv");
    try {
      portfolios = store.getPortfoliosFromCsvRows(rows);
    } catch (IllegalArgumentException e) {
      assertEquals("Provided stock symbol is not supported by the API!", e.getMessage());
    }
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_Valid()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_Valid.csv");
    assertEquals(11, rows.size());

    portfolios = store.getPortfoliosFromCsvRows(rows);
    assertEquals(3, portfolios.size());

    assertEquals("test Portfolio 1", portfolios.get(0).getName());
    List<IPortfolioStock> stocksList = portfolios.get(0).getStocks();
    IPortfolioStock ps = new PortfolioStock("MSFT", "Microsoft Corporation", "NASDAQ",
        new BigDecimal(20));
    assertEquals(true, stocksList.contains(ps));
    ps = new PortfolioStock("AMZN", "Amazon.com Inc", "NASDAQ", new BigDecimal(10));
    assertEquals(true, stocksList.contains(ps));
    ps = new PortfolioStock("GOOG", "Alphabet Inc - Class C", "NASDAQ", new BigDecimal(23));
    assertEquals(true, stocksList.contains(ps));

    assertEquals("test Portfolio 2", portfolios.get(1).getName());
    stocksList = portfolios.get(1).getStocks();
    ps = new PortfolioStock("A", "Agilent Technologies Inc", "NYSE", new BigDecimal(45));
    assertEquals(true, stocksList.contains(ps));
    ps = new PortfolioStock("B", "Barnes Group Inc", "NYSE", new BigDecimal(33));
    assertEquals(true, stocksList.contains(ps));
    ps = new PortfolioStock("CA", "CA Inc", "NASDAQ", new BigDecimal(20));
    assertEquals(true, stocksList.contains(ps));

    assertEquals("ligma", portfolios.get(2).getName());
    stocksList = portfolios.get(2).getStocks();
    ps = new PortfolioStock("AMZN", "Amazon.com Inc", "NASDAQ", new BigDecimal(400));
    assertEquals(true, stocksList.contains(ps));
    ps = new PortfolioStock("NFLX", "Netflix Inc", "NASDAQ", new BigDecimal(400));
    assertEquals(true, stocksList.contains(ps));
    ps = new PortfolioStock("TSLA", "Tesla Inc", "NASDAQ", new BigDecimal(400));
    assertEquals(true, stocksList.contains(ps));
    ps = new PortfolioStock("META", "Meta Platforms Inc - Class A", "NASDAQ", new BigDecimal(400));
    assertEquals(true, stocksList.contains(ps));
  }

  @Test
  public void testPortfolioStore_GetPortfoliosFromCsvRows_ZeroVolume()
      throws InstantiationException, IOException {
    rows = getRowsFromCsv("res/testing_import/export_ZeroVolume.csv");
    try {
      portfolios = store.getPortfoliosFromCsvRows(rows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Portfolio stock needs to have positive volume!", e.getMessage());
    }
  }

  @Test
  public void testPortfolioStore_ExportPortfoliosToCsv()
      throws InstantiationException, IOException {
    Pair<String, BigDecimal> s1 = new Pair("GOOG", new BigDecimal(10));
    Pair<String, BigDecimal> s2 = new Pair("MSFT", new BigDecimal(20));
    Pair<String, BigDecimal> s3 = new Pair("AMZN", new BigDecimal(30));

    IPortfolio p1 = Portfolio.getBuilder().setName("Test Portfolio 1")
        .addStocks(this.dataSource, List.of(s1, s2, s3)).create();

    s1 = new Pair<>("NFLX", new BigDecimal(10));
    s2 = new Pair("TSLA", new BigDecimal(20));
    s3 = new Pair("META", new BigDecimal(30));

    IPortfolio p2 = Portfolio.getBuilder().setName("Test Portfolio 2")
        .addStocks(this.dataSource, List.of(s1, s2, s3)).create();

    store.save(p1);
    store.save(p2);

    Path path = Paths.get("res/testing_import/export_test.csv");
    store.exportItemsToCsv(Files.newOutputStream(path));

    rows = getRowsFromCsv("res/testing_import/export_test.csv");
    assertEquals(7, rows.size());

    portfolios = store.getPortfoliosFromCsvRows(rows);
    assertEquals(2, portfolios.size());
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Stock Tests">

  @Test
  public void testFlexiblePortfolioStock_InitTest() {
    Date date = new Date(120, 01, 01);

    assertEquals("GOOG", ps1.getSymbol());
    assertEquals("Google", ps1.getName());
    assertEquals("NASDAQ", ps1.getExchange());
    assertEquals(new BigDecimal(100), ps1.getVolume());
    assertEquals(date, ps1.getTransactions().get(0).getO1());
    assertEquals(new BigDecimal(1000), ps1.getTransactions().get(0).getO2().getO2());
    assertEquals(commissionFees, ps1.getTransactions().get(0).getO2().getO3());
  }

  @Test
  public void testFlexiblePortfolioStock_InitTest_FutureDate() {
    Date date = new Date(125, 01, 01);
    try {
      ps1 = new FlexiblePortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100), date,
          new BigDecimal(1000), this.commissionFees);
    } catch (IllegalArgumentException e) {
      assertEquals("Provided date: " + date + " cannot be in the future!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStock_InitTest_MismatchingVolume() {
    try {
      ps1 = new FlexiblePortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(100),
          transactions);
    } catch (IllegalArgumentException e) {
      assertEquals("The total volume does not match the transactions sum of volumes!",
          e.getMessage());
    }

  }

  @Test
  public void testFlexiblePortfolioStock_GetFirstTransaction() {
    Date date = new Date(120, 01, 01);
    ps1 = new FlexiblePortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(30), transactions);

    assertEquals(date, ps1.getFirstTransaction().getO1());
    assertEquals(new BigDecimal(10), ps1.getFirstTransaction().getO2().getO1());
    assertEquals(new BigDecimal(1000), ps1.getFirstTransaction().getO2().getO2());
    assertEquals(new BigDecimal(5), ps1.getFirstTransaction().getO2().getO3());
  }

  @Test
  public void testFlexiblePortfolioStock_GetTransactions() {
    Date date1 = new Date(120, 01, 01);
    Date date2 = new Date(120, 02, 01);
    ps1 = new FlexiblePortfolioStock("GOOG", "Google", "NASDAQ", new BigDecimal(30), transactions);

    assertEquals(date1, ps1.getTransactions().get(0).getO1());
    assertEquals(new BigDecimal(10), ps1.getTransactions().get(0).getO2().getO1());
    assertEquals(new BigDecimal(1000), ps1.getTransactions().get(0).getO2().getO2());
    assertEquals(new BigDecimal(5), ps1.getTransactions().get(0).getO2().getO3());

    assertEquals(date2, ps1.getTransactions().get(1).getO1());
    assertEquals(new BigDecimal(20), ps1.getTransactions().get(1).getO2().getO1());
    assertEquals(new BigDecimal(1000), ps1.getTransactions().get(1).getO2().getO2());
    assertEquals(new BigDecimal(10), ps1.getTransactions().get(1).getO2().getO3());
  }

  @Test
  public void testFlexiblePortfolioStock_AddTransaction_ZeroVolume() {
    Date date = new Date(120, 01, 01);

    try {
      ps1.addTransaction(date, new BigDecimal(0), new BigDecimal(100), this.commissionFees);
    } catch (IllegalArgumentException e) {
      assertEquals("Volume for transaction cannot be 0!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStock_AddTransaction() {
    Date date = new Date(120, 01, 01);
    ps1.addTransaction(date, new BigDecimal(50), new BigDecimal(100),
        this.commissionFees.subtract(new BigDecimal(1)));

    assertEquals(date, ps1.getTransactions().get(0).getO1());

    // volume
    assertEquals(new BigDecimal(100), ps1.getTransactions().get(0).getO2().getO1());
    assertEquals(new BigDecimal(50), ps1.getTransactions().get(1).getO2().getO1());

    // purchase price
    assertEquals(new BigDecimal(1000), ps1.getTransactions().get(0).getO2().getO2());
    assertEquals(new BigDecimal(100), ps1.getTransactions().get(1).getO2().getO2());

    // commission Fees
    assertEquals(new BigDecimal(5), ps1.getTransactions().get(0).getO2().getO3());
    assertEquals(new BigDecimal(4), ps1.getTransactions().get(1).getO2().getO3());
  }

  @Test
  public void testFlexiblePortfolioStock_GetValueOnDate_FutureDate()
      throws StockDataSourceException {
    Date date = new Date(125, 01, 01);
    try {
      ps1.getValueOnDate(this.dataSource, date);
    } catch (IllegalArgumentException e) {
      assertEquals("Provided date: " + date + " cannot be in the future!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStock_GetValueOnDate() throws StockDataSourceException {
    Date date = new Date(120, 01, 02);
    assertEquals(new BigDecimal(100000), ps1.getValueOnDate(this.dataSource, date).getO2());
  }

  @Test
  public void testFlexiblePortfolioStock_GetDollarCostInvestments_NoEndDate()
      throws StockDataSourceException {

    IFlexiblePortfolioStock fps = new FlexiblePortfolioStock("GOOG", "Google",
        "NASDAQ", new BigDecimal(100),
        new Date(120, 01, 01), new BigDecimal(1000), this.commissionFees);

    fps.addStockDollarCostInvestment(this.dataSource, dollarCostInvest);

    List<IDollarCostInvestment> dollarCostInvestments = fps.getDollarCostInvestments();

    assertEquals(0, dollarCostInvestments.size());
  }

  @Test
  public void testFlexiblePortfolioStock_GetDollarCostInvestments_WithEndDateInPast()
      throws StockDataSourceException {

    IFlexiblePortfolioStock fps = new FlexiblePortfolioStock("GOOG", "Google",
        "NASDAQ", new BigDecimal(100),
        new Date(120, 01, 01), new BigDecimal(1000), this.commissionFees);

    fps.addStockDollarCostInvestment(this.dataSource, dollarCostInvest);

    List<IDollarCostInvestment> dollarCostInvestments = fps.getDollarCostInvestments();

    recurringEvent = new RecurringEvent(
        new Date(123, 01, 01), eRecurringIntervalType.MONTHLY, 6);

    assertEquals(0, dollarCostInvestments.size());
  }

  @Test
  public void testFlexiblePortfolioStock_GetDollarCostInvestments_WithEndDateInFuture()
      throws StockDataSourceException {

    IFlexiblePortfolioStock fps = new FlexiblePortfolioStock("GOOG", "Google",
        "NASDAQ", new BigDecimal(100),
        new Date(120, 01, 01), new BigDecimal(1000), this.commissionFees);

    Date startDate = new Date(120, 1, 1);
    Date endDate = new Date(123, 1, 1);

    recurringEvent = new RecurringEvent(
        endDate, eRecurringIntervalType.MONTHLY, 6);

    BigDecimal totalAmount = new BigDecimal(2500);
    BigDecimal commission = new BigDecimal(50);

    dollarCostInvest = new DollarCostInvestment(
        startDate, totalAmount, commission, recurringEvent);

    fps.addStockDollarCostInvestment(this.dataSource, dollarCostInvest);

    List<IDollarCostInvestment> dollarCostInvestments = fps.getDollarCostInvestments();

    recurringEvent = new RecurringEvent(
        new Date(123, 01, 01), eRecurringIntervalType.MONTHLY, 6);

    assertEquals(1, dollarCostInvestments.size());

    assertEquals(new Date(123, 1, 1),
        dollarCostInvestments.get(0).getDate());

    assertEquals(new BigDecimal(2500),
        dollarCostInvestments.get(0).getAmount());

    assertEquals(new BigDecimal(50),
        dollarCostInvestments.get(0).getCommissionFees());

    assertEquals(recurringEvent,
        dollarCostInvestments.get(0).getRecurringEvent());

    assertEquals(new BigDecimal("115.00"), fps.getVolume());
    assertEquals(new BigDecimal("115000.00"),
        fps.getValueOnDate(dataSource, Utils.getTodayDate()).getO2());
    assertEquals(new BigDecimal("115305.00"), fps.getCostBasis(dataSource, Utils.getTodayDate()));
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Tests">

  @Test
  public void testFlexiblePortfolio_InitTest() {
    assertEquals("test Portfolio", p.getName());
    List<IPortfolioStock> stocks = new ArrayList<>();
    assertEquals(stocks, p.getStocks());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFlexiblePortfolio_InvalidNameInitTest() {
    p = new FlexiblePortfolio(this.dataSource, "$$$", null);

    assertEquals("$$$", p.getName());
    List<IPortfolioStock> stocks = new ArrayList<>();
    assertEquals(stocks, p.getStocks());
  }

  //<editor-fold desc="Flexible Portfolio Composition Tests">

  @Test
  public void testFlexiblePortfolio_EmptyPortfolioComposition() {
    IFlexiblePortfolio p = new FlexiblePortfolio(this.dataSource, "test Portfolio", null);

    assertEquals(new ArrayList<>(List.of()), p.getComposition(new Date(120, 01, 02)));
  }

  @Test
  public void testFlexiblePortfolio_CompositionTest1() {
    p = new FlexiblePortfolio(this.dataSource, "test Portfolio",
        new ArrayList<>(List.of(fps1, fps2, fps3)));

    assertEquals(new ArrayList<>(List.of(new Pair<>("Google", new BigDecimal(100)),
            new Pair<>("Microsoft", new BigDecimal(100)),
            new Pair<>("Amazon", new BigDecimal(100)))),
        p.getComposition(new Date(120, 01, 02)));
  }

  @Test
  public void testFlexiblePortfolio_CompositionTest2() {
    p = new FlexiblePortfolio(this.dataSource, "test Portfolio",
        new ArrayList<>(List.of(fps1, fps2, fps3)));

    assertEquals(new ArrayList<>(List.of(new Pair<>("Microsoft", new BigDecimal(100)),
        new Pair<>("Amazon", new BigDecimal(100)))), p.getComposition(new Date(119, 01, 02)));
  }

  @Test
  public void testFlexiblePortfolio_CompositionTest3() {
    p = new FlexiblePortfolio(this.dataSource, "test Portfolio",
        new ArrayList<>(List.of(fps1, fps2, fps3)));

    assertEquals(new ArrayList<>(List.of(new Pair<>("Amazon", new BigDecimal(100)))),
        p.getComposition(new Date(118, 01, 02)));
  }

  @Test
  public void testFlexiblePortfolio_CompositionTest4() {
    p = new FlexiblePortfolio(this.dataSource, "test Portfolio",
        new ArrayList<>(List.of(fps1, fps2, fps3)));

    assertEquals(new ArrayList<>(), p.getComposition(new Date(117, 01, 02)));
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Value Tests">

  @Test
  public void testFlexiblePortfolio_EmptyPortfolioValue() throws StockDataSourceException {
    IFlexiblePortfolio p = new FlexiblePortfolio(this.dataSource, "test Portfolio", null);

    var value = p.getValue(this.dataSource, new Date(121, 01, 01));
    assertEquals(new BigDecimal(0), value.getO1());
    assertEquals(new ArrayList<>(List.of()), value.getO2());
  }

  @Test
  public void testFlexiblePortfolio_PortfolioValue() throws StockDataSourceException {
    IFlexiblePortfolio p = new FlexiblePortfolio(this.dataSource, "test Portfolio",
        new ArrayList<>(List.of(fps1, fps2, fps3)));

    Date date = new Date(121, 01, 01);

    var psv1 = new PortfolioStockValue(dataSource, date, fps1);
    var psv2 = new PortfolioStockValue(dataSource, date, fps2);
    var psv3 = new PortfolioStockValue(dataSource, date, fps3);

    var value = p.getValue(this.dataSource, date);
    assertEquals(new BigDecimal(600000), value.getO1());

    var portfolioStockValues = value.getO2();
    assertEquals(new ArrayList<>(List.of(psv1, psv2, psv3)), value.getO2());
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Buy Tests">

  @Test
  public void testFlexiblePortfolio_BasicBuy() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    var fps1 = new FlexiblePortfolioStock("GOOG", "Alphabet Inc - Class C", "NASDAQ",
        new BigDecimal(20), date, new BigDecimal(1000), this.commissionFees);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);

    assertEquals(List.of(fps1), p.getStocks());
  }

  @Test
  public void testFlexiblePortfolio_CommissionFeesBuy() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(50);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);
    BigDecimal stockVal = this.dataSource.getStockPrice("GOOG", date, false);

    assertEquals(commissionFees.add(stockVal.multiply(new BigDecimal(20))),
        p.getCostBasis(this.dataSource, date));
  }

  @Test
  public void testFlexiblePortfolio_FutureDateBuy() throws StockDataSourceException {
    Date date = new Date(150, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    try {
      p = p.buyStocks(this.dataSource, stocks, commissionFees);
    } catch (IllegalArgumentException e) {
      assertEquals("Provided date: " + date + " cannot be in the future!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_PreIpoBuy() throws StockDataSourceException {
    Date date = new Date(10, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    try {
      p = p.buyStocks(this.dataSource, stocks, commissionFees);
    } catch (IllegalArgumentException e) {
      assertEquals(
          "The transaction date for stock symbol: " + "GOOG cannot be before it's IPO date!",
          e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_PostDelistingBuy() throws StockDataSourceException {
    Date date = new Date(122, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("AMZN", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    try {
      p = p.buyStocks(this.dataSource, stocks, commissionFees);
    } catch (IllegalArgumentException e) {
      assertEquals(
          "The transaction date for stock symbol: " + "AMZN cannot be after it's delisting date!",
          e.getMessage());
    }
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Sell Tests">

  @Test
  public void testFlexiblePortfolio_BasicSell() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    List<Triplet<String, Date, BigDecimal>> stocksToSell = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(50);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);
    p = p.sellStocks(this.dataSource, stocksToSell, commissionFees);

    assertEquals(new BigDecimal(0), p.getStocks().get(0).getVolume());
  }

  @Test
  public void testFlexiblePortfolio_CommissionFeesSell() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    List<Triplet<String, Date, BigDecimal>> stocksToSell = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(50);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);
    p = p.sellStocks(this.dataSource, stocksToSell, commissionFees);

    BigDecimal stocksVal = (this.dataSource.getStockPrice("GOOG", date, false)).multiply(
        new BigDecimal(20));

    assertEquals(commissionFees.add(stocksVal).add(commissionFees),
        p.getCostBasis(this.dataSource, date));
  }

  @Test
  public void testFlexiblePortfolio_FutureDateSell() throws StockDataSourceException {
    Date date = new Date(150, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    try {
      p = p.sellStocks(this.dataSource, stocks, commissionFees);
    } catch (IllegalArgumentException e) {
      assertEquals("Provided date: " + date + " cannot be in the future!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_PreIpoSell() throws StockDataSourceException {
    Date date = new Date(10, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    try {
      p = p.sellStocks(this.dataSource, stocks, commissionFees);
    } catch (IllegalArgumentException e) {
      assertEquals(
          "The transaction date for stock symbol: " + "GOOG cannot be before it's IPO date!",
          e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_PostDelistingSell() throws StockDataSourceException {
    Date date = new Date(122, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("AMZN", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    try {
      p = p.sellStocks(this.dataSource, stocks, commissionFees);
    } catch (IllegalArgumentException e) {
      assertEquals(
          "The transaction date for stock symbol: " + "AMZN cannot be after it's delisting date!",
          e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_InvalidVolumeSell() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    List<Triplet<String, Date, BigDecimal>> stocksToSell = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(30))));
    BigDecimal commissionFees = new BigDecimal(50);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);

    try {
      p = p.sellStocks(this.dataSource, stocksToSell, commissionFees);
    } catch (IllegalStateException e) {
      assertEquals("The transaction on Symbol: GOOG cannot be performed as stock volume "
          + "cannot be negative at any given point in time!", e.getMessage());
    }
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Cost Basis Tests">

  @Test
  public void testFlexiblePortfolio_EmptyPortfolioCostBasis() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    assertEquals(new BigDecimal(0), p.getCostBasis(this.dataSource, date));
  }

  @Test
  public void testFlexiblePortfolio_PostSellingEmptyCostBasis() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    List<Triplet<String, Date, BigDecimal>> stocksToSell = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);
    p = p.sellStocks(this.dataSource, stocksToSell, commissionFees);

    BigDecimal stocksVal = (this.dataSource.getStockPrice("GOOG", date, false)).multiply(
        new BigDecimal(20));

    assertEquals(commissionFees.add(stocksVal).add(commissionFees),
        p.getCostBasis(this.dataSource, date));
  }

  @Test
  public void testFlexiblePortfolio_BasicCostBasis() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20)),
            new Triplet<>("MSFT", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    BigDecimal stockVal1 = (this.dataSource.getStockPrice("GOOG", date, false)).multiply(
        new BigDecimal(20));
    BigDecimal stockVal2 = (this.dataSource.getStockPrice("MSFT", date, false)).multiply(
        new BigDecimal(20));

    p = p.buyStocks(this.dataSource, stocks, commissionFees);

    assertEquals(commissionFees.add(stockVal1).add(commissionFees).add(stockVal2),
        p.getCostBasis(this.dataSource, date));
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Actual Value Tests">

  @Test
  public void testFlexiblePortfolio_EmptyPortfolioActualValue() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    assertEquals(new BigDecimal(0), p.getValue(this.dataSource, date).getO1());
    assertEquals(0, p.getValue(this.dataSource, date).getO2().size());
  }

  @Test
  public void testFlexiblePortfolio_PostSellingEmptyActualValue() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    List<Triplet<String, Date, BigDecimal>> stocksToSell = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);
    p = p.sellStocks(this.dataSource, stocksToSell, commissionFees);

    BigDecimal stockVal = this.dataSource.getStockPrice("GOOG", date, false);

    assertEquals(new BigDecimal(0), p.getValue(this.dataSource, date).getO1());
    assertEquals(new BigDecimal(0), p.getValue(this.dataSource, date).getO2().get(0).getVolume());
  }

  @Test
  public void testFlexiblePortfolio_BasicActualValue() throws StockDataSourceException {
    Date date = new Date(121, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20)),
            new Triplet<>("MSFT", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    BigDecimal stockVal1 = (this.dataSource.getStockPrice("GOOG", date, false)).multiply(
        new BigDecimal(20));
    BigDecimal stockVal2 = (this.dataSource.getStockPrice("MSFT", date, false)).multiply(
        new BigDecimal(20));

    p = p.buyStocks(this.dataSource, stocks, commissionFees);

    assertEquals(stockVal1.add(stockVal2), p.getValue(this.dataSource, date).getO1());
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Chart Tests">

  @Test
  public void testFlexible_Portfolio_EndBeforeStart_DailyChart() {
    Pair<eChartInterval, List<Date>> intervals;
    ChartService chartService = new ChartService(5, 30, 50);
    try {
      intervals = chartService.getChartInterval(
          new Date(121, 1, 1), new Date(120, 1, 3));
    } catch (IllegalArgumentException e) {
      assertEquals("End date cannot be before start date!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_LessThanFiveDaysDailyChart() {

    Pair<eChartInterval, List<Date>> intervals;
    ChartService chartService = new ChartService(5, 30, 50);
    try {
      intervals = chartService.getChartInterval(
          new Date(120, 1, 1), new Date(120, 1, 3));
    } catch (IllegalArgumentException e) {
      assertEquals("Start date and end date "
          + "need to be apart by at least the minimum interval value: 5.", e.getMessage());
    }
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Dollar Cost Investment Tests">

  @Test
  public void testFlexiblePortfolio_AddDollarCostInvestment_InvalidAmount()
      throws StockDataSourceException {
    Date date = new Date(120, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    var fps1 = new FlexiblePortfolioStock(
        "GOOG", "Alphabet Inc - Class C", "NASDAQ",
        new BigDecimal(20), date, new BigDecimal(1000), this.commissionFees);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);

    try {
      p = p.addDollarCostInvestment(this.dataSource, date, new BigDecimal(0),
          commissionFees, stocksWithPercentage, false, null,
          eRecurringIntervalType.DAILY, new Integer(10));
    } catch (IllegalArgumentException e) {
      assertEquals("Amount has to be a positive number!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_AddDollarCostInvestment_InvalidCommissionFee()
      throws StockDataSourceException {
    Date date = new Date(120, 01, 01);
    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>(
        List.of(new Triplet<>("GOOG", date, new BigDecimal(20))));
    BigDecimal commissionFees = new BigDecimal(5);

    p = p.buyStocks(this.dataSource, stocks, commissionFees);

    try {
      p = p.addDollarCostInvestment(this.dataSource, date, new BigDecimal(1000),
          new BigDecimal(-5), stocksWithPercentage, false, null,
          eRecurringIntervalType.DAILY, new Integer(10));
    } catch (IllegalArgumentException e) {
      assertEquals("Commission Fees has to be a non-negative number!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_AddDollarCostInvestment_NegativePercentage()
      throws StockDataSourceException {

    Date date = new Date(120, 01, 01);

    Pair<String, BigDecimal> microsoft = new Pair<>("MSFT", new BigDecimal(-50));
    Pair<String, BigDecimal> google = new Pair<>("GOOG", new BigDecimal(50));
    Pair<String, BigDecimal> amazon = new Pair<>("AMZN", new BigDecimal(50));

    stocksWithPercentage.clear();
    stocksWithPercentage.add(microsoft);
    stocksWithPercentage.add(google);
    stocksWithPercentage.add(amazon);

    try {
      p = p.addDollarCostInvestment(this.dataSource, date, new BigDecimal(1000),
          new BigDecimal(5), stocksWithPercentage,
          false, null, null, null);
    } catch (IllegalArgumentException e) {
      assertEquals("Percentages have to be positive!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_AddDollarCostInvestment_NonOneHundredPercentages()
      throws StockDataSourceException {

    Date date = new Date(120, 01, 01);

    Pair<String, BigDecimal> google = new Pair<>("GOOG", new BigDecimal(50));
    Pair<String, BigDecimal> microsoft = new Pair<>("MSFT", new BigDecimal(25));

    stocksWithPercentage.clear();
    stocksWithPercentage.add(google);
    stocksWithPercentage.add(microsoft);

    try {
      p = p.addDollarCostInvestment(this.dataSource, date, new BigDecimal(1000),
          new BigDecimal(5), stocksWithPercentage,
          false, null, null, null);
    } catch (IllegalArgumentException e) {
      assertEquals("The percentages of stocks should total up to 100!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_AddDollarCostInvestment_Before1900()
      throws StockDataSourceException {

    try {
      p = p.addDollarCostInvestment(this.dataSource, new Date(-1, 01, 01),
          new BigDecimal(1000), new BigDecimal(5), stocksWithPercentage,
          false, null, null, null);
    } catch (IllegalArgumentException e) {
      assertEquals(
          "The transaction date for stock symbol: GOOG cannot be before it's IPO date!",
          e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_GetDollarCostInvestments_NotRecurring_Past()
      throws StockDataSourceException {

    p = new FlexiblePortfolio(this.dataSource, "test Portfolio", null);
    p = p.addDollarCostInvestment(this.dataSource, new Date(120, 01, 01),
        new BigDecimal(1000), new BigDecimal(50), stocksWithPercentage,
        false, null, null, null);

    recurringEvent = null;

    assertEquals(0, p.getDollarCostInvestments().size());

    assertEquals(new BigDecimal(0),
        p.getCostBasis(this.dataSource, new Date(115, 01, 01)));
    assertEquals(new BigDecimal("1100.00"),
        p.getCostBasis(this.dataSource, new Date(120, 01, 02)));

    assertEquals(new BigDecimal("1000.00"),
        p.getValue(this.dataSource, new Date(120, 01, 02)).getO1());
  }

  @Test
  public void testFlexiblePortfolio_GetDollarCostInvestments_NotRecurring_Future()
      throws StockDataSourceException {

    p = new FlexiblePortfolio(this.dataSource, "test Portfolio", null);
    p = p.addDollarCostInvestment(this.dataSource, new Date(125, 01, 01),
        new BigDecimal(1000), new BigDecimal(50), stocksWithPercentage,
        false, null, null, null);

    recurringEvent = null;

    assertEquals("GOOG",
        p.getDollarCostInvestments().get(0).getO1());
    assertEquals(new Date(125, 01, 01),
        p.getDollarCostInvestments().get(0).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        p.getDollarCostInvestments().get(0).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        p.getDollarCostInvestments().get(0).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        p.getDollarCostInvestments().get(0).getO2().getRecurringEvent());

    assertEquals("MSFT",
        p.getDollarCostInvestments().get(1).getO1());
    assertEquals(new Date(125, 01, 01),
        p.getDollarCostInvestments().get(1).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        p.getDollarCostInvestments().get(1).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        p.getDollarCostInvestments().get(1).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        p.getDollarCostInvestments().get(1).getO2().getRecurringEvent());

    assertEquals(new BigDecimal(0),
        p.getCostBasis(this.dataSource, new Date(120, 01, 01)));
    assertEquals(new BigDecimal("1100.00"),
        p.getCostBasis(this.dataSource, new Date(125, 01, 02)));

    try {
      Pair<BigDecimal, List<IPortfolioStockValue>> value
          = p.getValue(this.dataSource, new Date(125, 01, 02));
    } catch (IllegalArgumentException e)  {
      assertEquals(
          "Provided date: Sun Feb 02 00:00:00 EST 2025 cannot be in the future!",
          e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_GetDollarCostInvestments_Recurring_StartInPast_NoEnd()
      throws StockDataSourceException {

    p = p.addDollarCostInvestment(this.dataSource, new Date(120, 01, 01),
        new BigDecimal(1000), new BigDecimal(50), stocksWithPercentage,
        true, null, eRecurringIntervalType.MONTHLY, 6);

    recurringEvent = new RecurringEvent(
        null, eRecurringIntervalType.MONTHLY, 6);

    List<Pair<String, IDollarCostInvestment>> scheduledInvestments = p.getDollarCostInvestments();

    assertEquals("GOOG",
        scheduledInvestments.get(0).getO1());
    assertEquals(new Date(123, 01, 01),
        scheduledInvestments.get(0).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        scheduledInvestments.get(0).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        scheduledInvestments.get(0).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        scheduledInvestments.get(0).getO2().getRecurringEvent());

    assertEquals("MSFT",
        scheduledInvestments.get(1).getO1());
    assertEquals(new Date(123, 01, 01),
        scheduledInvestments.get(1).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        scheduledInvestments.get(1).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        scheduledInvestments.get(1).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        scheduledInvestments.get(1).getO2().getRecurringEvent());

    assertEquals(new BigDecimal("1100.00"),
        p.getCostBasis(this.dataSource, new Date(120, 01, 01)));
    assertEquals(new BigDecimal("3300.00"),
        p.getCostBasis(this.dataSource, new Date(121, 01, 01)));

    assertEquals(new BigDecimal("1000.00"),
        p.getValue(this.dataSource, new Date(120, 01, 01)).getO1());
    assertEquals(new BigDecimal("3000.00"),
        p.getValue(this.dataSource, new Date(121, 01, 01)).getO1());
  }

  @Test
  public void testFlexiblePortfolio_GetDollarCostInvestments_Recurring_StartInPast_EndInPast()
      throws StockDataSourceException {

    p = p.addDollarCostInvestment(this.dataSource, new Date(120, 01, 01),
        new BigDecimal(1000), new BigDecimal(50), stocksWithPercentage,
        true, new Date(122, 01, 01),
        eRecurringIntervalType.MONTHLY, 6);

    recurringEvent = new RecurringEvent(
        new Date(122, 01, 01), eRecurringIntervalType.MONTHLY, 6);

    assertEquals(0, p.getDollarCostInvestments().size());

    assertEquals(new BigDecimal(0),
        p.getCostBasis(this.dataSource, new Date(119, 01, 01)));
    assertEquals(new BigDecimal("1100.00"),
        p.getCostBasis(this.dataSource, new Date(120, 01, 01)));
    assertEquals(new BigDecimal("3300.00"),
        p.getCostBasis(this.dataSource, new Date(121, 01, 01)));
    assertEquals(new BigDecimal("5500.00"),
        p.getCostBasis(this.dataSource, new Date(122, 01, 01)));

    assertEquals(new BigDecimal(0),
        p.getValue(this.dataSource, new Date(119, 01, 01)).getO1());
    assertEquals(new BigDecimal("1000.00"),
        p.getValue(this.dataSource, new Date(120, 01, 01)).getO1());
    assertEquals(new BigDecimal("3000.00"),
        p.getValue(this.dataSource, new Date(121, 01, 01)).getO1());
    assertEquals(new BigDecimal("5000.00"),
        p.getValue(this.dataSource, new Date(122, 01, 01)).getO1());
  }

  @Test
  public void testFlexiblePortfolio_GetDollarCostInvestments_Recurring_StartInPast_EndInFuture()
      throws StockDataSourceException {

    p = p.addDollarCostInvestment(this.dataSource, new Date(120, 01, 01),
        new BigDecimal(1000), new BigDecimal(50), stocksWithPercentage,
        true, new Date(123, 01, 01),
        eRecurringIntervalType.MONTHLY, 6);

    recurringEvent = new RecurringEvent(
        new Date(123, 01, 01), eRecurringIntervalType.MONTHLY, 6);

    List<Pair<String, IDollarCostInvestment>> scheduledInvestments = p.getDollarCostInvestments();

    assertEquals(2, scheduledInvestments.size());

    assertEquals("GOOG",
        scheduledInvestments.get(0).getO1());
    assertEquals(new Date(123, 01, 01),
        scheduledInvestments.get(0).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        scheduledInvestments.get(0).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        scheduledInvestments.get(0).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        scheduledInvestments.get(0).getO2().getRecurringEvent());

    assertEquals("MSFT",
        scheduledInvestments.get(1).getO1());
    assertEquals(new Date(123, 01, 01),
        scheduledInvestments.get(1).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        scheduledInvestments.get(1).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        scheduledInvestments.get(1).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        scheduledInvestments.get(1).getO2().getRecurringEvent());

    assertEquals(new BigDecimal(0),
        p.getCostBasis(this.dataSource, new Date(115, 01, 01)));
    assertEquals(new BigDecimal("1100.00"),
        p.getCostBasis(this.dataSource, new Date(120, 01, 01)));
    assertEquals(new BigDecimal("3300.00"),
        p.getCostBasis(this.dataSource, new Date(121, 01, 01)));
    assertEquals(new BigDecimal("5500.00"),
        p.getCostBasis(this.dataSource, new Date(122, 01, 01)));
    assertEquals(new BigDecimal("7700.00"),
        p.getCostBasis(this.dataSource, new Date(123, 01, 01)));

    assertEquals(new BigDecimal(0),
        p.getValue(this.dataSource, new Date(115, 01, 01)).getO1());
    assertEquals(new BigDecimal("1000.00"),
        p.getValue(this.dataSource, new Date(120, 01, 01)).getO1());
    assertEquals(new BigDecimal("3000.00"),
        p.getValue(this.dataSource, new Date(121, 01, 01)).getO1());
    assertEquals(new BigDecimal("5000.00"),
        p.getValue(this.dataSource, new Date(122, 01, 01)).getO1());
    try {
      Pair<BigDecimal, List<IPortfolioStockValue>> value
          = p.getValue(this.dataSource, new Date(123, 01, 01));
    } catch (IllegalArgumentException e)  {
      assertEquals(
          "Provided date: Wed Feb 01 00:00:00 EST 2023 cannot be in the future!",
          e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_GetDollarCostInvestments_Recurring_StartInFuture_NoEnd()
      throws StockDataSourceException {

    p = p.addDollarCostInvestment(this.dataSource, new Date(125, 01, 01),
        new BigDecimal(1000), new BigDecimal(50), stocksWithPercentage,
        true, null, eRecurringIntervalType.MONTHLY, 6);

    recurringEvent = new RecurringEvent(
        null, eRecurringIntervalType.MONTHLY, 6);

    List<Pair<String, IDollarCostInvestment>> scheduledInvestments = p.getDollarCostInvestments();

    assertEquals("GOOG",
        scheduledInvestments.get(0).getO1());
    assertEquals(new Date(125, 01, 01),
        scheduledInvestments.get(0).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        scheduledInvestments.get(0).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        scheduledInvestments.get(0).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        scheduledInvestments.get(0).getO2().getRecurringEvent());

    assertEquals("MSFT",
        scheduledInvestments.get(1).getO1());
    assertEquals(new Date(125, 01, 01),
        scheduledInvestments.get(1).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        scheduledInvestments.get(1).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        scheduledInvestments.get(1).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        scheduledInvestments.get(1).getO2().getRecurringEvent());

    assertEquals(new BigDecimal(0),
        p.getCostBasis(this.dataSource, new Date(120, 01, 01)));
    assertEquals(new BigDecimal("5500.00"),
        p.getCostBasis(this.dataSource, new Date(125, 01, 01)));

    try {
      Pair<BigDecimal, List<IPortfolioStockValue>> value
          = p.getValue(this.dataSource, new Date(125, 01, 01));
    } catch (IllegalArgumentException e)  {
      assertEquals(
          "Provided date: Sat Feb 01 00:00:00 EST 2025 cannot be in the future!",
          e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolio_GetDollarCostInvestments_Recurring_StartInFuture_EndInFuture()
      throws StockDataSourceException {

    p = p.addDollarCostInvestment(this.dataSource, new Date(123, 01, 01),
        new BigDecimal(1000), new BigDecimal(50), stocksWithPercentage,
        true, new Date(124, 01, 01),
        eRecurringIntervalType.MONTHLY, 6);

    recurringEvent = new RecurringEvent(
        new Date(124, 01, 01), eRecurringIntervalType.MONTHLY, 6);

    List<Pair<String, IDollarCostInvestment>> scheduledInvestments = p.getDollarCostInvestments();

    assertEquals(2, scheduledInvestments.size());

    assertEquals("GOOG",
        scheduledInvestments.get(0).getO1());
    assertEquals(new Date(123, 01, 01),
        scheduledInvestments.get(0).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        scheduledInvestments.get(0).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        scheduledInvestments.get(0).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        scheduledInvestments.get(0).getO2().getRecurringEvent());

    assertEquals("MSFT",
        scheduledInvestments.get(1).getO1());
    assertEquals(new Date(123, 01, 01),
        scheduledInvestments.get(1).getO2().getDate());
    assertEquals(new BigDecimal("500.00"),
        scheduledInvestments.get(1).getO2().getAmount());
    assertEquals(new BigDecimal(50),
        scheduledInvestments.get(1).getO2().getCommissionFees());
    assertEquals(recurringEvent,
        scheduledInvestments.get(1).getO2().getRecurringEvent());

    assertEquals(new BigDecimal(0),
        p.getCostBasis(this.dataSource, new Date(120, 01, 01)));
    assertEquals(new BigDecimal("1100.00"),
        p.getCostBasis(this.dataSource, new Date(123, 01, 01)));
    assertEquals(new BigDecimal("3300.00"),
        p.getCostBasis(this.dataSource, new Date(124, 01, 01)));

    try {
      Pair<BigDecimal, List<IPortfolioStockValue>> value
          = p.getValue(this.dataSource, new Date(124, 01, 01));
    } catch (IllegalArgumentException e)  {
      assertEquals(
          "Provided date: Thu Feb 01 00:00:00 EST 2024 cannot be in the future!",
          e.getMessage());
    }
  }

  //</editor-fold>

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Store Tests">

  @Test
  public void testFlexiblePortfolioStore_GetCsvRowHeaders() {
    assertEquals("PortfolioName," + "StockSymbol," + "StockName," + "StockExchange,"
        + "StockTransactionDate," + "StockTransactionVolume," + "StockTransactionPurchasePrice,"
        + "StockTransactionCommissionFees\n", flexibleStore.getCsvRowHeaders());
  }

  @Test
  public void testFlexiblePortfolioStore_GetCsvRowCount() {
    assertEquals(8, flexibleStore.getCsvRowCount());
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_Empty()
      throws InstantiationException, IOException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_Empty.csv");
    try {
      flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: The file does not have any valid rows!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_FloatVolume()
      throws IOException, InstantiationException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_FloatVolume.csv");
    assertEquals(7, flexibleRows.size());

    flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    assertEquals(2, flexiblePortfolios.size());
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_NegativeVolume()
      throws InstantiationException, IOException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_NegativeVolume.csv");
    try {
      flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Portfolio stock needs to have valid volume!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_NonNumberVolume()
      throws InstantiationException, IOException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_NonNumberVolume.csv");
    try {
      flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Portfolio stock needs to have valid volume!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_PortfolioNameInvalid()
      throws InstantiationException, IOException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_PortfolioNameInvalid.csv");
    try {
      flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Portfolio name format not valid!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_StockExchangeMismatch()
      throws InstantiationException, IOException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_StockExchangeMismatch.csv");
    try {
      flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Entered data for stock exchange is a mismatch!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_StockNameMismatch()
      throws InstantiationException, IOException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_StockNameMismatch.csv");
    try {
      flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid input: Entered data for stock name is a mismatch!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_StockSymbolDoesntExist()
      throws InstantiationException, IOException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_StockSymbolDoesntExist.csv");
    try {
      flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    } catch (IllegalArgumentException e) {
      assertEquals("Provided stock symbol is not supported by the API!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_Valid()
      throws InstantiationException, IOException {

    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_Valid.csv");
    assertEquals(10, flexibleRows.size());

    flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    assertEquals(3, flexiblePortfolios.size());

    checkPortfolioContents(flexiblePortfolios.get(0).getName());
    checkPortfolioContents(flexiblePortfolios.get(1).getName());
    checkPortfolioContents(flexiblePortfolios.get(2).getName());
  }

  @Test
  public void testFlexiblePortfolioStore_GetPortfoliosFromCsvRows_ZeroVolume()
      throws InstantiationException, IOException {
    flexibleRows = getRowsFromCsv("res/testing_import/export_Flexible_ZeroVolume.csv");
    try {
      flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    } catch (IllegalArgumentException e) {
      assertEquals("One or more stock has invalid volume!", e.getMessage());
    }
  }

  @Test
  public void testFlexiblePortfolioStore_ExportPortfoliosToCsv()
      throws InstantiationException, IOException, NotImplementedException {

    IFlexiblePortfolioStock s1 = createStock("GOOG", "Alphabet Inc - Class C", new BigDecimal(10),
        new BigDecimal(1000));
    IFlexiblePortfolioStock s2 = createStock("MSFT", "Microsoft Corporation", new BigDecimal(20),
        new BigDecimal(2000));
    IFlexiblePortfolioStock s3 = createStock("AMZN", "Amazon.com Inc", new BigDecimal(30),
        new BigDecimal(3000));

    IFlexiblePortfolio p1 = new FlexiblePortfolio(this.dataSource, "Test Portfolio 1",
        new ArrayList<>(List.of(s1, s2, s3)));

    s1 = createStock("NFLX", "Netflix Inc", new BigDecimal(10), new BigDecimal(4000));
    s2 = createStock("TSLA", "Tesla Inc", new BigDecimal(20), new BigDecimal(5000));
    s3 = createStock("META", "Meta Platforms Inc - Class A", new BigDecimal(30),
        new BigDecimal(6000));

    IFlexiblePortfolio p2 = new FlexiblePortfolio(this.dataSource, "Test Portfolio 2",
        new ArrayList<>(List.of(s1, s2, s3)));

    flexibleStore.save(p1);
    flexibleStore.save(p2);

    Path path = Paths.get("res/testing_import/export_flexible_test.csv");
    flexibleStore.exportItemsToCsv(Files.newOutputStream(path));

    flexibleRows = getRowsFromCsv("res/testing_import/export_flexible_test.csv");
    assertEquals(7, flexibleRows.size());

    flexiblePortfolios = flexibleStore.getPortfoliosFromCsvRows(flexibleRows);
    assertEquals(2, flexiblePortfolios.size());
  }

  //</editor-fold>

  //<editor-fold desc="Recurring Event Tests">

  @Test
  public void testRecurringEvent_IntervalTypeNull() {

    try {
      IRecurringEvent event = new RecurringEvent(
          new Date(122, 1, 1), null, 10);
    } catch (IllegalArgumentException e) {
      assertEquals(
          "recurringIntervalType cannot be null if recurring event is set to active!",
          e.getMessage());
    }
  }

  @Test
  public void testRecurringEvent_IntervalDeltaNull() {
    try {
      IRecurringEvent event = new RecurringEvent(
          new Date(122, 1, 1), eRecurringIntervalType.DAILY, null);
    } catch (IllegalArgumentException e) {
      assertEquals(
          "recurringIntervalDelta cannot be null if recurring event is set to active!",
          e.getMessage());
    }
  }

  @Test
  public void testRecurringEvent_GetEndDate() {
    assertEquals(new Date(122, 1, 1), recurringEvent.getEndDate());
  }

  @Test
  public void testRecurringEvent_GetRecurringIntervalType() {
    assertEquals(eRecurringIntervalType.MONTHLY, recurringEvent.getRecurringIntervalType());
  }

  @Test
  public void testRecurringEvent_GetRecurringIntervalDelta() {
    assertEquals(new Integer(6), recurringEvent.getRecurringIntervalDelta());
  }

  //</editor-fold>

  //<editor-fold desc="Dollar-Cost Investment Tests">

  @Test
  public void testDollarCostInvestment_GetDate() {
    assertEquals(new Date(120, 1, 1), dollarCostInvest.getDate());
  }

  @Test
  public void testDollarCostInvestment_GetAmount() {
    assertEquals(new BigDecimal(2500), dollarCostInvest.getAmount());
  }

  @Test
  public void testDollarCostInvestment_GetCommissionFees() {
    assertEquals(new BigDecimal(50), dollarCostInvest.getCommissionFees());
  }

  @Test
  public void testDollarCostInvestment_GetRecurringEvent() {
    assertEquals(recurringEvent, dollarCostInvest.getRecurringEvent());
  }

  //</editor-fold>

}
