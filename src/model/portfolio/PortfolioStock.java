package model.portfolio;

import java.math.BigDecimal;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * This class represents a stock in a portfolio with the name, symbol, exchange it is traded in, and
 * the volume of the stock.
 */
public class PortfolioStock extends Stock implements IPortfolioStock {

  //<editor-fold desc="State variables">

  BigDecimal volume;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  PortfolioStock() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Creates a {@code PortfolioStock} which contains stock data and volume.
   *
   * @param symbol   The symbol of the stock with which it is traded on the exchange.
   * @param name     The name of the stock.
   * @param exchange the exchange on which the stock is traded.
   * @param volume   The volume of the stock in the portfolio.
   */
  public PortfolioStock(String symbol, String name, String exchange, BigDecimal volume) {
    super(symbol, name, exchange);
    this.volume = volume;
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public BigDecimal getVolume() {
    return this.volume;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof PortfolioStock)) {
      return false;
    }

    IPortfolioStock other = (PortfolioStock) o;
    return other.equalsPortfolioStock(this);
  }

  @Override
  public boolean equalsStock(Object o) {
    return false;
  }

  @Override
  public boolean equalsPortfolioStock(Object o) {
    IPortfolioStock other = (PortfolioStock) o;
    return other.getSymbol().equals(this.getSymbol())
        && other.getName().equals(this.getName())
        && other.getExchange().equals(this.getExchange())
        && other.getVolume().equals(this.getVolume());
  }

  @Override
  public boolean equalsPortfolioStockValue(Object o) {
    return false;
  }

  @Override
  public int hashCode() {
    return this.getSymbol().hashCode()
        + this.getName().hashCode()
        + this.getExchange().hashCode()
        + this.getVolume().hashCode();
  }

  //</editor-fold>
}
