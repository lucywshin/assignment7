package model.store;

import static org.junit.Assert.assertEquals;

import common.pair.Pair;
import java.math.BigDecimal;
import java.util.List;
import model.StockDataSourceMock;
import model.portfolio.IPortfolio;
import model.portfolio.IPortfolioStock;
import model.portfolio.Portfolio;
import model.portfolio.PortfolioStock;
import org.junit.Before;
import org.junit.Test;

/**
 * A test class to test implementation of store.
 */
public class StoreTest {

  //<editor-fold desc="State Variables">

  private String string1;
  private String string2;
  private String string3;
  private IStore<String> stringStore;

  private int num1;
  private int num2;
  private int num3;
  private IStore<Integer> intStore;

  private Pair<String, BigDecimal> stock1;
  private Pair<String, BigDecimal> stock2;
  private Pair<String, BigDecimal> stock3;
  private IPortfolio p;
  private IStore<IPortfolio> portfolioStore;
  private StockDataSourceMock stockDataSource;

  //</editor-fold>

  //<editor-fold desc="Setup">

  @Before
  public void setUp() throws InstantiationException {
    string1 = "Google";
    string2 = "Microsoft";
    string3 = "Amazon";
    stringStore = new Store<>();

    num1 = 100;
    num2 = 200;
    num3 = 300;
    intStore = new Store<>();

    IPortfolioStock portfolioStock1 = new PortfolioStock("GOOG", "Google",
        "NASDAQ", new BigDecimal(100));

    IPortfolioStock portfolioStock2 = new PortfolioStock("MSFT", "Microsoft",
        "NASDAQ", new BigDecimal(100));

    IPortfolioStock portfolioStock3 = new PortfolioStock("AMZN", "Amazon",
        "NASDAQ", new BigDecimal(100));

    stock1 = new Pair<>(portfolioStock1.getSymbol(), portfolioStock1.getVolume());
    stock2 = new Pair<>(portfolioStock2.getSymbol(), portfolioStock2.getVolume());
    stock3 = new Pair<>(portfolioStock3.getSymbol(), portfolioStock3.getVolume());

    this.stockDataSource = new StockDataSourceMock();

    p = Portfolio.getBuilder()
        .setName("myFirstPortfolio")
        .addStocks(this.stockDataSource, List.of(stock1, stock2, stock3))
        .create();

    portfolioStore = new Store<>();
  }

  //</editor-fold>

  //<editor-fold desc="Store Immutable Tests">

  /**
   * Testing saving to the store.
   */
  @Test
  public void testImmutable_Save() {
    saveToStringStoreHelper();

    List<Pair<Integer, String>> storeList;
    storeList = stringStore.getAll();

    assertEquals(3, storeList.size());
    checkContentsStringStoreHelper(storeList);
  }

  /**
   * Testing retrieving items from the store.
   */
  @Test
  public void testImmutable_Retrieve() {
    saveToStringStoreHelper();

    String retrieved = stringStore.retrieve(0);
    assertEquals("Google", retrieved);

    retrieved = stringStore.retrieve(1);
    assertEquals("Microsoft", retrieved);

    retrieved = stringStore.retrieve(2);
    assertEquals("Amazon", retrieved);
  }

  /**
   * Testing retrieving an item from the store with an invalid id.
   */
  @Test
  public void testImmutable_RetrieveInvalidId() {
    saveToStringStoreHelper();

    try {
      stringStore.retrieve(5);
    } catch (Exception e) {
      assertEquals("Provided id is not valid!", e.getMessage());
    }
  }

  /**
   * Testing getting all the items from the store.
   */
  @Test
  public void testImmutable_GetAll() {
    saveToStringStoreHelper();

    List<Pair<Integer, String>> storeList;
    storeList = stringStore.getAll();

    assertEquals(3, storeList.size());
    checkContentsStringStoreHelper(storeList);
  }

  //</editor-fold>

  //<editor-fold desc="Store Mutable Tests">

  /**
   * Testing saving to the store.
   */
  @Test
  public void testMutable_Save() {
    saveToIntegerStoreHelper();

    List<Pair<Integer, Integer>> storeList;
    storeList = intStore.getAll();

    assertEquals(3, storeList.size());
    checkContentsIntegerStoreHelper(storeList);
  }

  /**
   * Testing retrieving from the store.
   */
  @Test
  public void testMutable_Retrieve() {
    saveToIntegerStoreHelper();

    int retrieved = intStore.retrieve(0);
    assertEquals(100, retrieved);

    retrieved = intStore.retrieve(1);
    assertEquals(200, retrieved);

    retrieved = intStore.retrieve(2);
    assertEquals(300, retrieved);
  }

  /**
   * Testing retrieving an item from the store with an invalid id.
   */
  @Test
  public void testMutable_RetrieveInvalidId() {
    saveToIntegerStoreHelper();

    try {
      intStore.retrieve(5);
    } catch (Exception e) {
      assertEquals("Provided id is not valid!", e.getMessage());
    }
  }

  /**
   * Testing getting all the items from the store.
   */
  @Test
  public void testMutable_GetAll() {
    saveToIntegerStoreHelper();

    List<Pair<Integer, Integer>> storeList;
    storeList = intStore.getAll();

    assertEquals(3, storeList.size());
    checkContentsIntegerStoreHelper(storeList);
  }

  //</editor-fold>

  //<editor-fold desc="Store Portfolio Tests">

  /**
   * Testing saving a portfolio to the store.
   *
   * @throws InstantiationException when the creation of the portfolio fails.
   */
  @Test
  public void testPortfolio_Save() throws InstantiationException {

    IPortfolio retrieved = portfolioStore.retrieve(portfolioStore.save(p));

    List<Pair<Integer, IPortfolio>> storeList;
    storeList = portfolioStore.getAll();

    assertEquals(1, storeList.size());
    checkContentsPortfolioStoreHelper(storeList);
  }

  /**
   * Testing retrieving a portfolio from the store.
   */
  @Test
  public void testPortfolio_Retrieve() {
    IPortfolio retrieved = portfolioStore.retrieve(portfolioStore.save(p));

    assertEquals("myFirstPortfolio", retrieved.getName());
  }

  /**
   * Testing retrieving a portfolio from the store with an invalid id.
   */
  @Test
  public void testPortfolio_RetrieveInvalidId() {
    IPortfolio retrieved = portfolioStore.retrieve(portfolioStore.save(p));

    try {
      portfolioStore.retrieve(1);
    } catch (Exception e) {
      assertEquals("Provided id is not valid!", e.getMessage());
    }
  }

  /**
   * Testing getting all the portfolios from the store.
   *
   * @throws InstantiationException when the creation of the portfolio fails.
   */
  @Test
  public void testPortfolio_GetAll() throws InstantiationException {
    IPortfolio retrieved = portfolioStore.retrieve(portfolioStore.save(p));
    List<Pair<Integer, IPortfolio>> storeList;
    storeList = portfolioStore.getAll();

    assertEquals(1, storeList.size());
    checkContentsPortfolioStoreHelper(storeList);
  }

  //</editor-fold>

  //<editor-fold desc="Helper Methods">

  private void saveToStringStoreHelper() {
    int id = stringStore.save(string1);
    id = stringStore.save(string2);
    id = stringStore.save(string3);
  }

  private void checkContentsStringStoreHelper(List<Pair<Integer, String>> list) {
    assertEquals(new Pair<Integer, String>(0, "Google"), list.get(0));
    assertEquals(new Pair<Integer, String>(1, "Microsoft"), list.get(1));
    assertEquals(new Pair<Integer, String>(2, "Amazon"), list.get(2));
  }

  private void saveToIntegerStoreHelper() {
    int id = intStore.save(num1);
    id = intStore.save(num2);
    id = intStore.save(num3);
  }

  private void checkContentsIntegerStoreHelper(List<Pair<Integer, Integer>> list) {
    assertEquals(new Pair<Integer, Integer>(0, 100), list.get(0));
    assertEquals(new Pair<Integer, Integer>(1, 200), list.get(1));
    assertEquals(new Pair<Integer, Integer>(2, 300), list.get(2));
  }

  private void checkContentsPortfolioStoreHelper(List<Pair<Integer, IPortfolio>> list)
      throws InstantiationException {

    p = Portfolio.getBuilder()
        .setName("myFirstPortfolio")
        .addStocks(this.stockDataSource, List.of(stock1, stock2, stock3))
        .create();

    Pair<Integer, IPortfolio> fromStore = list.get(0);
    Pair<Integer, IPortfolio> other = new Pair<Integer, IPortfolio>(0, p);

    assertEquals(fromStore.getO1(), other.getO1());
    assertEquals("myFirstPortfolio", fromStore.getO2().getName());

    List<IPortfolioStock> stockList = fromStore.getO2().getStocks();
    checkPortfolioStocksHelper(stockList);
  }

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

  //</editor-fold>


}
