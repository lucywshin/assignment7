package model.portfolio;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * This class represents a stock. A portfolio consists of stocks.
 */
public class Stock implements IStock {

  //<editor-fold desc="State variables">

  private final String symbol;
  private final String name;
  private final String exchange;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  Stock() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Creates a {@code StockImpl}.
   *
   * @param symbol   The symbol of the stock with which it is traded on the exchange.
   * @param name     The name of the stock.
   * @param exchange the exchange on which the stock is traded.
   */
  public Stock(String symbol, String name, String exchange) {
    this.symbol = symbol;
    this.name = name;
    this.exchange = exchange;
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public String getSymbol() {
    return this.symbol;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getExchange() {
    return this.exchange;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Stock)) {
      return false;
    }

    IStock other = (Stock) o;
    return other.equalsStock(this);
  }

  @Override
  public boolean equalsStock(Object o) {
    IStock other = (Stock) o;
    return other.getSymbol() == this.getSymbol()
        && other.getName() == this.getName()
        && other.getExchange() == this.getExchange();
  }

  @Override
  public boolean equalsPortfolioStock(Object o) {
    return false;
  }

  @Override
  public boolean equalsPortfolioStockValue(Object o) {
    return false;
  }

  @Override
  public int hashCode() {
    return this.getSymbol().hashCode()
        + this.getExchange().hashCode()
        + this.getName().hashCode();
  }

  //</editor-fold>
}
