package model;

import java.math.BigDecimal;
import java.util.Date;
import model.portfolio.IStock;
import model.portfolio.IStockDataSource;
import model.portfolio.Stock;
import model.portfolio.StockDataSourceException;

/**
 * A mock data source for the stock data.
 */
public class StockDataSourceMock implements IStockDataSource {

  /**
   * Initializes a mock data source for stocks data.
   *
   * <p>Contains 9 stocks:
   * <ul>
   * <li>GOOG,1000; ipoDate: 01-01-2000 ; delistingDate: null</li>
   * <li>MSFT,2000; ipoDate: 01-01-2001 ; delistingDate: null</li>
   * <li>AMZN,3000; ipoDate: 01-01-2002 ; delistingDate: 01-01-2021</li>
   * <li>NFLX,4000; ipoDate: 01-01-2003 ; delistingDate: 01-01-2021</li>
   * <li>TSLA,5000; ipoDate: 01-01-2004 ; delistingDate: 01-01-2021</li>
   * <li>META,6000; ipoDate: 01-01-2005 ; delistingDate: 01-01-2021</li>
   * <li>A,7000; ipoDate: 01-01-2006 ; delistingDate: null</li>
   * <li>B,8000; ipoDate: 01-01-2007 ; delistingDate: null</li>
   * <li>CA,9000; ipoDate: 01-01-2008 ; delistingDate: null</li>
   * </ul>
   */
  public StockDataSourceMock() {
    // this constructor is empty as we need to override java docs for this constructor.
  }

  @Override
  public IStock getStock(String symbol) {
    if (symbol.equals("GOOG")) {
      return new Stock("GOOG", "Alphabet Inc - Class C", "NASDAQ");
    } else if (symbol.equals("MSFT")) {
      return new Stock("MSFT", "Microsoft Corporation", "NASDAQ");
    } else if (symbol.equals("AMZN")) {
      return new Stock("AMZN", "Amazon.com Inc", "NASDAQ");
    } else if (symbol.equals("NFLX")) {
      return new Stock("NFLX", "Netflix Inc", "NASDAQ");
    } else if (symbol.equals("TSLA")) {
      return new Stock("TSLA", "Tesla Inc", "NASDAQ");
    } else if (symbol.equals("META")) {
      return new Stock("META", "Meta Platforms Inc - Class A", "NASDAQ");
    } else if (symbol.equals("A")) {
      return new Stock("A", "Agilent Technologies Inc", "NYSE");
    } else if (symbol.equals("B")) {
      return new Stock("B", "Barnes Group Inc", "NYSE");
    } else if (symbol.equals("CA")) {
      return new Stock("CA", "CA Inc", "NASDAQ");
    } else {
      throw new IllegalArgumentException("Provided stock symbol is not supported by the API!");
    }
  }

  @Override
  public Date getIPODate(String symbol) throws IllegalArgumentException {
    if (symbol.equals("GOOG")) {
      return new Date(100, 01, 01);
    } else if (symbol.equals("MSFT")) {
      return new Date(101, 01, 01);
    } else if (symbol.equals("AMZN")) {
      return new Date(102, 01, 01);
    } else if (symbol.equals("NFLX")) {
      return new Date(103, 01, 01);
    } else if (symbol.equals("TSLA")) {
      return new Date(104, 01, 01);
    } else if (symbol.equals("META")) {
      return new Date(105, 01, 01);
    } else if (symbol.equals("A")) {
      return new Date(106, 01, 01);
    } else if (symbol.equals("B")) {
      return new Date(107, 01, 01);
    } else if (symbol.equals("CA")) {
      return new Date(108, 01, 01);
    } else {
      throw new IllegalArgumentException("Provided stock symbol is not supported by the API!");
    }
  }

  @Override
  public Date getDelistingDate(String symbol) {
    if (symbol.equals("GOOG")) {
      return null;
    } else if (symbol.equals("MSFT")) {
      return null;
    } else if (symbol.equals("AMZN")) {
      return new Date(121, 01, 01);
    } else if (symbol.equals("NFLX")) {
      return new Date(121, 01, 01);
    } else if (symbol.equals("TSLA")) {
      return new Date(121, 01, 01);
    } else if (symbol.equals("META")) {
      return new Date(121, 01, 01);
    } else if (symbol.equals("A")) {
      return null;
    } else if (symbol.equals("B")) {
      return null;
    } else if (symbol.equals("CA")) {
      return null;
    } else {
      throw new IllegalArgumentException("Provided stock symbol is not supported by the API!");
    }
  }

  @Override
  public BigDecimal getStockPrice(String symbol, Date date, boolean takeFuturePrice)
      throws StockDataSourceException {
    if (date.before(this.getIPODate(symbol))) {
      return new BigDecimal(0);
    }
    if (this.getDelistingDate(symbol) != null && date.after(this.getDelistingDate(symbol))) {
      return new BigDecimal(-1);
    }

    if (symbol.equals("GOOG")) {
      return new BigDecimal(1000);
    } else if (symbol.equals("MSFT")) {
      return new BigDecimal(2000);
    } else if (symbol.equals("AMZN")) {
      return new BigDecimal(3000);
    } else if (symbol.equals("NFLX")) {
      return new BigDecimal(4000);
    } else if (symbol.equals("TSLA")) {
      return new BigDecimal(5000);
    } else if (symbol.equals("META")) {
      return new BigDecimal(6000);
    } else if (symbol.equals("A")) {
      return new BigDecimal(7000);
    } else if (symbol.equals("B")) {
      return new BigDecimal(8000);
    } else if (symbol.equals("CA")) {
      return new BigDecimal(9000);
    } else {
      throw new IllegalArgumentException("Provided stock symbol is not supported by the API!");
    }
  }
}
