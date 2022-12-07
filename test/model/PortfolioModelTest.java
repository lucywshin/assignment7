package model;

import static org.junit.Assert.assertEquals;

import common.pair.Pair;
import common.triplet.Triplet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolio;
import model.portfolio.IPortfolioStock;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.PortfolioStock;
import model.portfolio.StockDataSourceException;
import org.junit.Before;
import org.junit.Test;

/**
 * A test class to test the functionality of the Model for this application.
 */
public class PortfolioModelTest {

  //<editor-fold desc="State Variables">

  IPortfolioStock ps1;
  IPortfolioStock ps2;
  IPortfolioStock ps3;
  Pair<String, BigDecimal> stock1;
  Pair<String, BigDecimal> stock2;
  Pair<String, BigDecimal> stock3;
  BigDecimal commissionFees;
  private IPortfolioModel pm;
  private final List<Pair<Date, Pair<BigDecimal, BigDecimal>>> transactions = new ArrayList<>();

  //</editor-fold>

  //<editor-fold desc="Setup">

  @Before
  public void setUp() throws InstantiationException {
    ps1 = new PortfolioStock("GOOG", "Google",
        "NASDAQ", new BigDecimal(100));
    ps2 = new PortfolioStock("MSFT", "Microsoft",
        "NASDAQ", new BigDecimal(100));
    ps3 = new PortfolioStock("AMZN", "Amazon",
        "NASDAQ", new BigDecimal(100));

    stock1 = new Pair<>(ps1.getSymbol(), ps1.getVolume());
    stock2 = new Pair<>(ps2.getSymbol(), ps2.getVolume());
    stock3 = new Pair<>(ps3.getSymbol(), ps3.getVolume());

    pm = new PortfolioModel();

    /* Flexible Portfolio Setup */
    commissionFees = new BigDecimal(5);

    Date date = new Date(120, 01, 01);
    transactions.add(new Pair<>(date, new Pair<>(new BigDecimal(10), new BigDecimal(5))));

    date = new Date(120, 02, 01);
    transactions.add(new Pair<>(date, new Pair<>(new BigDecimal(20), new BigDecimal(10))));
  }

  //</editor-fold>

  //<editor-fold desc="Regular Portfolio Tests">

  @Test
  public void testRegular_PortfolioName()
      throws InstantiationException {
    pm.setName("myPortfolio");
    List<IPortfolioStock> psList = pm.addStocksToPortfolioBuilder(List.of(stock1, stock2, stock3));
    assertEquals(3, psList.size());

    IPortfolio p = pm.getPortfolio(pm.buildPortfolio());
    assertEquals("myPortfolio", p.getName());
  }

  @Test
  public void testRegular_AddStocksToPortfolioBuilder() {

    List<IPortfolioStock> list = pm.addStocksToPortfolioBuilder(List.of(stock1));
    assertEquals(1, list.size());

    list = pm.addStocksToPortfolioBuilder(List.of(stock2));
    assertEquals(2, list.size());

    list = pm.addStocksToPortfolioBuilder(List.of(stock3));
    assertEquals(3, list.size());

    validatePortfolioStocksHelper(list);
  }

  @Test
  public void testRegular_GetPortfolioBuilderStocks() {

    List<IPortfolioStock> list = pm.getPortfolioBuilderStocks();
    assertEquals(0, list.size());

    list = pm.addStocksToPortfolioBuilder(List.of(stock1, stock2, stock3));
    assertEquals(3, list.size());

    validatePortfolioStocksHelper(list);
  }

  @Test
  public void testRegular_GetAllPortfolios()
      throws InstantiationException {

    pm.setName("myPortfolio");

    List<IPortfolioStock> psList = pm.addStocksToPortfolioBuilder(List.of(stock1, stock2, stock3));
    assertEquals(3, psList.size());

    IPortfolio p = pm.getPortfolio(pm.buildPortfolio());

    List<Pair<Integer, IPortfolio>> retrieved = pm.getAllPortfolios();
    assertEquals(1, retrieved.size());

    Pair<Integer, IPortfolio> firstPortfolio = retrieved.get(0);
    Pair<Integer, IPortfolio> other = new Pair<Integer, IPortfolio>(0, p);

    assertEquals(firstPortfolio.getO1(), other.getO1());
    assertEquals("myPortfolio", firstPortfolio.getO2().getName());

    List<IPortfolioStock> stocks = p.getStocks();
    validatePortfolioStocksHelper(stocks);
  }

  @Test
  public void testRegular_BuildPortfolio()
      throws InstantiationException {

    pm.setName("myPortfolio");
    List<IPortfolioStock> psList = pm.addStocksToPortfolioBuilder(List.of(stock1, stock2, stock3));
    assertEquals(3, psList.size());

    IPortfolio p = pm.getPortfolio(pm.buildPortfolio());
    assertEquals("myPortfolio", p.getName());

    List<IPortfolioStock> stocks = p.getStocks();
    validatePortfolioStocksHelper(stocks);
  }

  @Test
  public void testRegular_GetPortfolioComposition()
      throws InstantiationException {

    pm.setName("myPortfolio");

    List<IPortfolioStock> psList = pm.addStocksToPortfolioBuilder(List.of(stock1, stock2, stock3));
    assertEquals(3, psList.size());

    IPortfolio p = pm.getPortfolio(pm.buildPortfolio());

    Pair<String, List<String>> composition = pm.getPortfolioComposition(0);
    List<String> portfolioComposition = composition.getO2();

    assertEquals("myPortfolio", composition.getO1());
    assertEquals("Alphabet Inc - Class C", portfolioComposition.get(0));
    assertEquals("Microsoft Corporation", portfolioComposition.get(1));
    assertEquals("Amazon.com Inc", portfolioComposition.get(2));
  }

  @Test
  public void testRegular_GetPortfolioValue()
      throws InstantiationException, StockDataSourceException {
    pm.setName("myPortfolio");

    List<IPortfolioStock> psList = pm.addStocksToPortfolioBuilder(List.of(stock1, stock2, stock3));
    assertEquals(3, psList.size());

    IPortfolio p = pm.getPortfolio(pm.buildPortfolio());

    // values for November 1, 2022
    Pair<BigDecimal, List<IPortfolioStockValue>> psvList = pm.getPortfolioValue(0,
        new Date(122, 10, 1));

    assertEquals("9050.0000", psvList.getO2().get(0).getValue().toString());
    assertEquals("22817.0000", psvList.getO2().get(1).getValue().toString());
    assertEquals("9679.0000", psvList.getO2().get(2).getValue().toString());

    assertEquals("41546.0000", psvList.getO1().toString());
  }

  //</editor-fold>

  //<editor-fold desc="Flexible Portfolio Tests">


  @Test
  public void testFlexible_getPortfolioCount_EmptyStore() {
    assertEquals(0, this.pm.getFlexiblePortfolioCount());
  }

  @Test
  public void testFlexible_getPortfolioCount()
      throws InstantiationException {
    int id = this.pm.createFlexiblePortfolio("testPortfolio");
    assertEquals(1, this.pm.getFlexiblePortfolioCount());
  }

  @Test
  public void testFlexible_BuyStocks()
      throws InstantiationException, StockDataSourceException {

    int id = this.pm.createFlexiblePortfolio("testPortfolio");

    Date date = new Date(120, 0, 1);

    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>();
    stocks.add(new Triplet<>("GOOG", date, new BigDecimal(10)));
    stocks.add(new Triplet<>("MSFT", date, new BigDecimal(20)));
    stocks.add(new Triplet<>("AMZN", date, new BigDecimal(30)));

    Pair<Integer, List<IObservableFlexiblePortfolioStock>>
        newStocks = this.pm.buyStocksForFlexiblePortfolio(id, stocks, new BigDecimal(5));

    assertEquals("GOOG", newStocks.getO2().get(0).getSymbol());
    assertEquals("MSFT", newStocks.getO2().get(1).getSymbol());
    assertEquals("AMZN", newStocks.getO2().get(2).getSymbol());

    assertEquals(new BigDecimal(10), newStocks.getO2().get(0).getVolume());
    assertEquals(new BigDecimal(20), newStocks.getO2().get(1).getVolume());
    assertEquals(new BigDecimal(30), newStocks.getO2().get(2).getVolume());
  }

  @Test
  public void testFlexible_SellStocks()
      throws InstantiationException, StockDataSourceException {

    int id = this.pm.createFlexiblePortfolio("testPortfolio");

    Date date = new Date(120, 0, 1);

    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>();
    stocks.add(new Triplet<>("GOOG", date, new BigDecimal(10)));
    stocks.add(new Triplet<>("MSFT", date, new BigDecimal(20)));
    stocks.add(new Triplet<>("AMZN", date, new BigDecimal(30)));

    Pair<Integer, List<IObservableFlexiblePortfolioStock>> newStocks
        = this.pm.buyStocksForFlexiblePortfolio(id, stocks, new BigDecimal(5));

    List<Triplet<String, Date, BigDecimal>> stocksToSell = new ArrayList<>();
    stocksToSell.add(new Triplet<>("GOOG", date, new BigDecimal(10)));
    Pair<Integer, List<IObservableFlexiblePortfolioStock>> newStocksAfterSale
        = this.pm.sellStocksForFlexiblePortfolio(id, stocksToSell, new BigDecimal(5));

    assertEquals("GOOG", newStocksAfterSale.getO2().get(0).getSymbol());
    assertEquals("MSFT", newStocksAfterSale.getO2().get(1).getSymbol());
    assertEquals("AMZN", newStocksAfterSale.getO2().get(2).getSymbol());

    assertEquals(new BigDecimal(0), newStocksAfterSale.getO2().get(0).getVolume());
    assertEquals(new BigDecimal(20), newStocksAfterSale.getO2().get(1).getVolume());
    assertEquals(new BigDecimal(30), newStocksAfterSale.getO2().get(2).getVolume());
  }

  @Test
  public void testFlexible_GetComposition()
      throws InstantiationException, StockDataSourceException {

    int id = this.pm.createFlexiblePortfolio("testPortfolio");

    Date date = new Date(120, 0, 1);

    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>();
    stocks.add(new Triplet<>("GOOG", date, new BigDecimal(10)));
    stocks.add(new Triplet<>("MSFT", date, new BigDecimal(20)));
    stocks.add(new Triplet<>("AMZN", date, new BigDecimal(30)));

    Pair<Integer, List<IObservableFlexiblePortfolioStock>> newStocks
        = this.pm.buyStocksForFlexiblePortfolio(id, stocks, new BigDecimal(5));

    Pair<String, List<Pair<String, BigDecimal>>> composition =
        this.pm.getFlexiblePortfolioComposition(id, date);

    assertEquals("testPortfolio", composition.getO1());

    assertEquals("Alphabet Inc - Class C", composition.getO2().get(0).getO1());
    assertEquals(new BigDecimal(10), composition.getO2().get(0).getO2());

    assertEquals("Microsoft Corporation", composition.getO2().get(1).getO1());
    assertEquals(new BigDecimal(20), composition.getO2().get(1).getO2());

    assertEquals("Amazon.com Inc", composition.getO2().get(2).getO1());
    assertEquals(new BigDecimal(30), composition.getO2().get(2).getO2());
  }

  @Test
  public void testFlexible_GetValue()
      throws InstantiationException, StockDataSourceException {

    int id = this.pm.createFlexiblePortfolio("testPortfolio");

    Date date = new Date(120, 0, 1);

    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>();
    stocks.add(new Triplet<>("GOOG", date, new BigDecimal(10)));
    stocks.add(new Triplet<>("MSFT", date, new BigDecimal(20)));
    stocks.add(new Triplet<>("AMZN", date, new BigDecimal(30)));

    Pair<Integer, List<IObservableFlexiblePortfolioStock>> newStocks
        = this.pm.buyStocksForFlexiblePortfolio(id, stocks, new BigDecimal(5));

    Pair<BigDecimal, List<IPortfolioStockValue>> values
        = this.pm.getFlexiblePortfolioValue(id, date);

    assertEquals("71959.4000", values.getO1().toString());
    assertEquals("13370.2000", values.getO2().get(0).getValue().toString());
    assertEquals("3154.0000", values.getO2().get(1).getValue().toString());
    assertEquals("55435.2000", values.getO2().get(2).getValue().toString());
  }

  @Test
  public void testFlexible_GetCostBasis()
      throws InstantiationException, StockDataSourceException {

    int id = this.pm.createFlexiblePortfolio("testPortfolio");

    Date date = new Date(120, 0, 1);

    List<Triplet<String, Date, BigDecimal>> stocks = new ArrayList<>();
    stocks.add(new Triplet<>("GOOG", date, new BigDecimal(10)));
    stocks.add(new Triplet<>("MSFT", date, new BigDecimal(20)));
    stocks.add(new Triplet<>("AMZN", date, new BigDecimal(30)));

    Pair<Integer, List<IObservableFlexiblePortfolioStock>> newStocks
        = this.pm.buyStocksForFlexiblePortfolio(id, stocks, new BigDecimal(5));

    BigDecimal costBasis = this.pm.getCostBasis(id, date);

    assertEquals("71974.4000", costBasis.toString());
  }

  //</editor-fold>

  //<editor-fold desc="Helper Methods">

  private void validatePortfolioStocksHelper(List<IPortfolioStock> list) {
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