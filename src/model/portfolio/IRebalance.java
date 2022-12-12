package model.portfolio;

import java.math.BigDecimal;
import java.util.Date;

public interface IRebalance {

  /**
   * Gets the first date on which the dollar cost investment strategy's first transaction takes
   * place.
   *
   * @return the first transaction date for the dollar cost investment strategy.
   */
  Date getDate();

  /**
   * Gets the amount to be invested in the stock for which this dollar cost investment is being
   * created.
   *
   * @return the amount to be invested in the stock.
   */
  BigDecimal getAmount();

  /**
   * Gets the weight percent of the stock.
   *
   * @return numeric percentage of weight.
   */
  BigDecimal getWeight();

}
