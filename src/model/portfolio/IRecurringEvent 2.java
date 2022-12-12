package model.portfolio;

import java.util.Date;

/**
 * An interface representing a recurring event.
 */
public interface IRecurringEvent {

  /**
   * Gets the end date of the recurring event.
   *
   * @return the end date of the recurring event.
   */
  Date getEndDate();

  /**
   * Gets the recurring event interval type.
   *
   * @return the interval type for the recurring event.
   */
  eRecurringIntervalType getRecurringIntervalType();

  /**
   * Gets the recurring event interval delta.
   *
   * @return the interval delta for the recurring event.
   */
  Integer getRecurringIntervalDelta();
}
