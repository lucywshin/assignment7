package model.alphavantage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.alphavantage.AlphaVantageAPI.IAVListingStatusResult;
import model.alphavantage.AlphaVantageAPI.IAVTimeSeriesDailyResult;
import model.portfolio.IStock;
import model.portfolio.StockDataSourceException;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class tests the functionality of {@code AlphaVantageAPI}.
 */
public class AlphaVantageTest {

  //<editor-fold desc="Setup and internal variables">

  private AlphaVantageAPI alphaVantageAPI;

  @Before
  public void setup() throws StockDataSourceException {
    this.alphaVantageAPI = new AlphaVantageAPI();
  }

  //</editor-fold>

  @Test
  public void testTimeSeriesDaily() {
    List<IAVTimeSeriesDailyResult> result = new ArrayList<>();
    try {
      result = this.alphaVantageAPI.alphaVantageEndpointTimeSeriesDaily("GOOG");
    } catch (StockDataSourceException e) {
      fail("Error in Stock Data Source: " + e.getMessage());
    }

    var last = result.get(result.size() - 1);

    // values from March 27, 2014
    assertEquals(last.getOpenValue().toString(), "568.0000");
    assertEquals(last.getHighValue().toString(), "568.0000");
    assertEquals(last.getLowValue().toString(), "552.9200");
    assertEquals(last.getCloseValue().toString(), "558.4600");
  }

  @Test
  public void testTimeSeriesDailyUnsupportedSymbol() {
    try {
      List<IAVTimeSeriesDailyResult> result =
          this.alphaVantageAPI.alphaVantageEndpointTimeSeriesDaily("GOOGS");
    } catch (IllegalArgumentException e) {
      assertEquals("Provided stock symbol: GOOGS is not supported by the API!", e.getMessage());
      return;
    } catch (StockDataSourceException ae) {
      fail(ae.getMessage());
    }
    fail("The symbol provided is unsupported and the method should have thrown an error!");
  }

  @Test
  public void testListingStatusActive() {
    List<IAVListingStatusResult> result = new ArrayList<>();
    try {
      result = this.alphaVantageAPI.alphaVantageEndpointListingStatus(true);
    } catch (StockDataSourceException e) {
      fail("Error in Stock Data Source: " + e.getMessage());
    }

    var first = result.get(0);
    var last = result.get(result.size() - 1);

    assertEquals(first.getSymbol(), "A");
    assertEquals(first.getName(), "Agilent Technologies Inc");
    assertEquals(first.getExchange(), "NYSE");

    assertEquals(last.getSymbol(), "ZZZ");
    assertEquals(last.getName(), "TEST TICKER FOR UTP");
    assertEquals(last.getExchange(), "NYSE ARCA");
  }

  @Test
  public void testListingStatusDelisted() {
    List<IAVListingStatusResult> result = new ArrayList<>();
    try {
      result = this.alphaVantageAPI.alphaVantageEndpointListingStatus(false);
    } catch (StockDataSourceException e) {
      fail("Error in Stock Data Source: " + e.getMessage());
    }

    var first = result.get(0);
    var last = result.get(result.size() - 1);

    assertEquals(first.getSymbol(), "AA-W");
    assertEquals(first.getName(), "Alcoa Corporation When Issued");
    assertEquals(first.getExchange(), "NYSE");
    assertEquals(first.getStatus(), "Delisted");

    assertEquals(last.getSymbol(), "ZY");
    assertEquals(last.getName(), "Zymergen Inc");
    assertEquals(last.getExchange(), "NASDAQ");
    assertEquals(last.getStatus(), "Delisted");
  }

  /**
   * Testing getting stock for a symbol unsupported by the API.
   */
  @Test
  public void testGetStock_UnsupportedSymbol() {
    try {
      alphaVantageAPI.getStock("QWERTYUIOP");
    } catch (IllegalArgumentException e) {
      assertEquals("Provided stock symbol: QWERTYUIOP is not supported by the API!",
          e.getMessage());
    }
  }

  /**
   * Testing getting stock.
   */
  @Test
  public void testGetStock() {
    IStock s = alphaVantageAPI.getStock("MSFT");

    assertEquals(s.getSymbol(), "MSFT");
    assertEquals(s.getName(), "Microsoft Corporation");
    assertEquals(s.getExchange(), "NASDAQ");
  }

  /**
   * Testing getting stock price for a future date.
   */
  @Test
  public void testGetStockPrice_FutureDate() {
    Date date = new Date(200, 0, 1);
    try {
      BigDecimal price = alphaVantageAPI.getStockPrice("MSFT", date, false);
    } catch (Exception e) {
      assertEquals("Provided date: " + date + " cannot be in the future!", e.getMessage());
    }
  }

  /**
   * Testing getting stock price for a symbol unsupported by the API.
   */
  @Test
  public void testGetStockPrice_UnsupportedSymbol() {
    try {
      alphaVantageAPI.getStock("QWERTYUIOP");
    } catch (Exception e) {
      assertEquals("Provided stock symbol: QWERTYUIOP is not supported by the API!",
          e.getMessage());
    }
  }

  /**
   * Testing getting stock price.
   *
   * @throws StockDataSourceException if stock symbol is not supported by the API.
   */
  @Test
  public void testGetStockPrice() throws StockDataSourceException {
    BigDecimal price = alphaVantageAPI.getStockPrice("MSFT",
        new Date(122, 10, 1), false);

    assertEquals(price.toString(), "228.1700");
  }

  /**
   * Testing getting stock price for a Saturday and Sunday.
   *
   * @throws StockDataSourceException if stock symbol is not supported by the API.
   */
  @Test
  public void testGetStockPrice_WeekendDays() throws StockDataSourceException {
    BigDecimal price = alphaVantageAPI.getStockPrice("MSFT",
        new Date(122, 9, 29), false);

    assertEquals(price.toString(), "235.8700");

    price = alphaVantageAPI.getStockPrice("MSFT",
        new Date(122, 9, 30), false);

    assertEquals(price.toString(), "235.8700");
  }

}
