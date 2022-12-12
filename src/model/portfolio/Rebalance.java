package model.portfolio;

import java.math.BigDecimal;
import java.util.Date;

public class Rebalance implements IRebalance{

  private final Date date;
  private final BigDecimal amount;
  private final BigDecimal weight;

  /**
   * Creates a dollar cost investment object.
   *
   * @param date           the date on which the first transaction of the dollar cost investment is
   *                       to take place.
   * @param amount         the amount to be invested into the stock.
   * @param weight         weight percent.
   */
  Rebalance(Date date, BigDecimal amount, BigDecimal weight) {
    this.date = date;
    this.amount = amount;
    this.weight = weight;
  }

  @Override
  public Date getDate() {
    return date;
  }

  @Override
  public BigDecimal getAmount() {
    return amount;
  }

  @Override
  public BigDecimal getWeight() {
    return weight;
  }

}
