package model.portfolio;

import java.math.BigDecimal;
import java.util.Date;

/**
 * An interface representing a dollar cost investment.
 */
public interface IDollarCostInvestment {

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
   * Gets the commission fee charged per transaction in this dollar cost investment.
   *
   * @return the commission fee charged per transaction.
   */
  BigDecimal getCommissionFees();

  /**
   * Gets the recurring event object if the dollar cost investment is set as recurring. Can be
   * null.
   *
   * @return the recurring event object.
   */
  IRecurringEvent getRecurringEvent();
}
