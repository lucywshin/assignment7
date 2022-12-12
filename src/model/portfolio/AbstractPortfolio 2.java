package model.portfolio;

import common.Utils;
import common.pair.Pair;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * An abstract portfolio implementing common functionality of all supported portfolios.
 */
abstract class AbstractPortfolio implements IAbstractPortfolio {

  //<editor-fold desc="State variables">

  final String name;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  AbstractPortfolio() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Creates an {@code AbstractPortfolio} with the specified name.
   *
   * @param name the name of the portfolio.
   */
  AbstractPortfolio(String name) {
    Utils.validateWordsInput(name);
    this.name = name;
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public abstract Pair<BigDecimal, List<IPortfolioStockValue>> getValue(IStockDataSource source,
      Date date) throws IllegalArgumentException, StockDataSourceException;

  @Override
  public abstract List<String> toCsvRows();

  //</editor-fold>
}
