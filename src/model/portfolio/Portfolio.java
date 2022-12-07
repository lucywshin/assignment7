package model.portfolio;

import common.pair.Pair;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * This class represents a portfolio of stocks.
 */
public class Portfolio extends AbstractPortfolio implements IPortfolio {

  //<editor-fold desc="State variables">

  private final List<IPortfolioStock> stocks;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  private Portfolio() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Creates a {@code Portfolio}.
   *
   * @param name   The name of the portfolio.
   * @param stocks The stocks contained in the portfolio.
   * @throws IllegalArgumentException when the provided name is not a valid string with space
   *                                  seperated words.
   */
  private Portfolio(String name, List<IPortfolioStock> stocks) throws IllegalArgumentException {
    super(name);
    this.stocks = stocks;
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  private static void validateStocksInput(List<Pair<String, BigDecimal>> stocks)
      throws IllegalArgumentException {
    if (stocks == null || stocks.size() == 0) {
      throw new IllegalArgumentException("No stocks provided!");
    }

    for (Pair<String, BigDecimal> stock : stocks) {
      if (stock.getO1().isBlank()) {
        throw new IllegalArgumentException("One or more stock has an empty symbol!");
      }

      if (stock.getO2().floatValue() <= 0) {
        throw new IllegalArgumentException("One or more stock has invalid volume!");
      }
    }
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public List<IPortfolioStock> getStocks() {
    return this.stocks;
  }

  @Override
  public Pair<BigDecimal, List<IPortfolioStockValue>> getValue(IStockDataSource source, Date date)
      throws StockDataSourceException {
    BigDecimal resultPortfolioValue = new BigDecimal("0");
    boolean resultPortfolioValueUntouched = true;

    List<IPortfolioStockValue> result = new ArrayList<>();
    for (IPortfolioStock stock : this.getStocks()) {
      IPortfolioStockValue stockValue = new PortfolioStockValue(source, date, stock);
      result.add(stockValue);

      if (!stockValue.getValue().equals(new BigDecimal("-1"))) {
        resultPortfolioValueUntouched = false;
        resultPortfolioValue = resultPortfolioValue.add(stockValue.getValue());
      }
    }
    if (resultPortfolioValueUntouched) {
      return new Pair<>(new BigDecimal("-1"), result);
    }
    return new Pair<>(resultPortfolioValue, result);
  }

  @Override
  public List<String> getComposition() {
    List<String> result = new ArrayList<>();
    for (IPortfolioStock stock : this.getStocks()) {
      result.add(stock.getName());
    }
    return result;
  }

  @Override
  public List<String> toCsvRows() {
    List<String> result = new ArrayList<>();

    for (IPortfolioStock stock : this.getStocks()) {
      result.add(this.name + ","
          + stock.getSymbol() + ","
          + stock.getName() + ","
          + stock.getExchange() + ","
          + stock.getVolume() + "\n");
    }
    return result;
  }

  //</editor-fold>

  //<editor-fold desc="Builder">

  /**
   * A builder for {@code Portfolio}s.
   *
   * @return A builder class which helps with building a {@code Portfolio}.
   */
  public static PortfolioImplBuilder getBuilder() {
    return new PortfolioImplBuilder();
  }

  /**
   * A builder class for the {@code PortfolioImpl} class.
   */
  public static class PortfolioImplBuilder {

    //<editor-fold desc="State Variables">

    private String name;
    private List<IPortfolioStock> stocks;
    private Set<String> stockSymbols;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    private PortfolioImplBuilder() {
      this.name = "";
      this.stocks = new ArrayList<>();
      this.stockSymbols = new HashSet<>();
    }

    //</editor-fold>

    //<editor-fold desc="Core methods">

    /**
     * Gets the name for the portfolio being built.
     *
     * @return the name of the portfolio being built.
     */
    public String getName() {
      return this.name;
    }

    /**
     * Sets the name for the portfolio being built.
     *
     * @param name the new name of the portfolio.
     * @return the builder with the new name.
     * @throws IllegalArgumentException if the provided string is empty or whitespace.
     */
    public PortfolioImplBuilder setName(String name) throws IllegalArgumentException {
      if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("The new name cannot be empty");
      }
      this.name = name;
      return this;
    }

    /**
     * Clears the name of the portfolio being built.
     *
     * @return the portfolio builder with an empty string for its name.
     */
    public PortfolioImplBuilder clearName() {
      this.name = "";
      return this;
    }

    /**
     * Adds provided stocks to the builder.
     *
     * @param source The date source of stocks to be utilized.
     * @param stocks The stocks to be added.
     * @return The builder with the added stocks.
     * @throws IllegalArgumentException if one or more of the provided stocks exist in the builder's
     *                                  stock list already.
     */
    public PortfolioImplBuilder addStocks(IStockDataSource source,
        List<Pair<String, BigDecimal>> stocks) throws IllegalArgumentException {
      validateStocksInput(stocks);

      for (Pair<String, BigDecimal> s : stocks) {
        String newStockSymbol = s.getO1();
        BigDecimal newStockVolume = s.getO2();

        if (stockSymbols.contains(newStockSymbol)) {
          throw new IllegalArgumentException(
              "One or more stocks provided are already in the portfolio!");
        }

        // get stock details for stock and validates valid symbol and date for transaction
        IStock sourceStock = source.getStock(newStockSymbol);
        IPortfolioStock stock = new PortfolioStock(sourceStock.getSymbol(), sourceStock.getName(),
            sourceStock.getExchange(), newStockVolume);

        this.stocks.add(stock);
        this.stockSymbols.add(stock.getSymbol());
      }
      return this;
    }

    /**
     * Removes the provided stocks from the builder.
     *
     * @param stockSymbols The stocks to be removed.
     * @return The builder with the removed stocks.
     * @throws IllegalArgumentException if one or more of the provided stocks aren't in the
     *                                  builder's stock list.
     */
    public PortfolioImplBuilder removeStocks(String... stockSymbols)
        throws IllegalArgumentException {
      for (String stockSymbol : stockSymbols) {
        if (stockSymbol.isBlank()) {
          throw new IllegalArgumentException("One or more stock has an empty symbol!");
        }
        if (!this.stockSymbols.contains(stockSymbol)) {
          throw new IllegalArgumentException(
              "One or more stocks provided are not in the portfolio!");
        }

        this.stocks.removeIf(s -> s.getSymbol().equals(stockSymbol));
        this.stockSymbols.remove(stockSymbol);
      }
      return this;
    }

    /**
     * Gets the stocks list in the builder.
     *
     * @return The list of stocks in the builder.
     */
    public List<IPortfolioStock> getStocks() {
      return this.stocks;
    }

    /**
     * Clears the list of stocks in the builder.
     *
     * @return the builder with an empty stock list.
     */
    public PortfolioImplBuilder clearStocks() {
      this.stocks = new ArrayList<>();
      this.stockSymbols = new HashSet<>();

      return this;
    }

    /**
     * Creates a {@code PortfolioImpl} using the current state of this builder class. The builder is
     * cleared once the portfolio is created.
     *
     * @return a new {@code PortfolioImpl}
     * @throws InstantiationException when the creation of the portfolio fails.
     */
    public IPortfolio create() throws InstantiationException {
      if (this.name.isBlank()) {
        throw new InstantiationException("The name of the portfolio cannot be empty!");
      }
      if (this.stocks.isEmpty()) {
        throw new InstantiationException("The portfolio needs to have at least one stock!");
      }

      IPortfolio result = new Portfolio(this.name, this.stocks);

      this.clearName();
      this.clearStocks();

      return result;
    }

    //</editor-fold>
  }

  //</editor-fold>
}
