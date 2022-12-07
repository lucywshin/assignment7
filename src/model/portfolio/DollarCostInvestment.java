package model.portfolio;

import java.math.BigDecimal;
import java.util.Date;

/**
 * A class representing a dollar cost investment for a stock.
 */
public class DollarCostInvestment implements IDollarCostInvestment {

  //<editor-fold desc="State variables">

  private final Date date;
  private final BigDecimal amount;
  private final BigDecimal commissionFees;
  private final IRecurringEvent recurringEvent;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Creates a dollar cost investment object.
   *
   * @param date           the date on which the first transaction of the dollar cost investment is
   *                       to take place.
   * @param amount         the amount to be invested into the stock for which this object is
   *                       created.
   * @param commissionFees the commission fees charged for the transaction.
   * @param recurringEvent the recurring event object if the dollar cost investment is a recurring
   *                       event.
   */
  DollarCostInvestment(Date date, BigDecimal amount, BigDecimal commissionFees,
      IRecurringEvent recurringEvent) {
    this.date = date;
    this.amount = amount;
    this.commissionFees = commissionFees;
    this.recurringEvent = recurringEvent;
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public Date getDate() {
    return date;
  }

  @Override
  public BigDecimal getAmount() {
    return amount;
  }

  @Override
  public BigDecimal getCommissionFees() {
    return commissionFees;
  }

  @Override
  public IRecurringEvent getRecurringEvent() {
    return recurringEvent;
  }

  //</editor-fold>
}

