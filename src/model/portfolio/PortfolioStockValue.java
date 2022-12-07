package model.portfolio;

import java.math.BigDecimal;
import java.util.Date;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * This class contains all the values of a Stock, along with the value of the stock on the day
 * specified at the time of creation of this object.
 */
public class PortfolioStockValue extends PortfolioStock implements IPortfolioStockValue {

  //<editor-fold desc="State variables">

  private final BigDecimal value;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  private PortfolioStockValue() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Creates a {@code PortfolioStockValue} object which contains all properties of a
   * {@code PortfolioStock}: symbol of the stock, name of the stock, exchange of the stock, and
   * volume of the stock, along with the value of the stock on the date provided at time of calling
   * this method.
   *
   * @param source         The source of Stock data.
   * @param date           The date on which the value of the stock is to be fetched.
   * @param portfolioStock The stock for which the value is to be fetched.
   */
  public PortfolioStockValue(IStockDataSource source, Date date, IPortfolioStock portfolioStock) {
    super(portfolioStock.getSymbol(), portfolioStock.getName(), portfolioStock.getExchange(),
        portfolioStock.getVolume());
    try {
      this.value = source.getStockPrice(portfolioStock.getSymbol(), date, false)
          .multiply(this.volume);
    } catch (StockDataSourceException e) {
      throw new RuntimeException(
          "Stock price could not be fetched because of an unexpected error in the API!");
    }
  }

  /**
   * Creates a {@code PortfolioStockValue} object with the provided properties.
   *
   * @param symbol   The symbol of the stock.
   * @param name     The name of the stock.
   * @param exchange The exchange the stock is traded in.
   * @param volume   The volume of the stock.
   * @param value    The value of the stock.
   */
  public PortfolioStockValue(String symbol, String name, String exchange, BigDecimal volume,
      BigDecimal value) {
    super(symbol, name, exchange, volume);
    this.value = value;
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public BigDecimal getValue() {
    return this.value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof PortfolioStockValue)) {
      return false;
    }

    IPortfolioStockValue other = (PortfolioStockValue) o;
    return other.equalsPortfolioStockValue(this);
  }

  @Override
  public boolean equalsStock(Object o) {
    return false;
  }

  @Override
  public boolean equalsPortfolioStock(Object o) {
    return false;
  }

  @Override
  public boolean equalsPortfolioStockValue(Object o) {
    IPortfolioStockValue other = (PortfolioStockValue) o;
    return other.getSymbol().equals(this.getSymbol())
        && other.getName().equals(this.getName())
        && other.getExchange().equals(this.getExchange())
        && other.getVolume().equals(this.getVolume())
        && other.getValue().equals(this.getValue());
  }

  @Override
  public int hashCode() {
    return this.getSymbol().hashCode()
        + this.getName().hashCode()
        + this.getExchange().hashCode()
        + this.getVolume().hashCode()
        + this.getValue().hashCode();
  }

  @Override
  public String toString() {
    return this.getSymbol() + ", "
        + this.getName() + ", "
        + this.getExchange() + ", "
        + this.getVolume().toString() + ", "
        + this.getValue().toString();
  }

  //</editor-fold>
}
