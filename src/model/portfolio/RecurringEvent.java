package model.portfolio;

import common.Utils;
import java.time.LocalDate;
import java.util.Date;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * A class representing a recurring event.
 */
public class RecurringEvent implements IRecurringEvent {

  //<editor-fold desc="State variables">

  private final Date endDate;
  private final eRecurringIntervalType recurringIntervalType;
  private final Integer recurringIntervalDelta;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Creates an instance of a recurring event.
   *
   * @param endDate                the end date of the recurring event. Can be null.
   * @param recurringIntervalType  the type of recurring interval.
   * @param recurringIntervalDelta the delta of the recurring interval.
   * @throws IllegalArgumentException when the provided parameters are invalid.
   */
  RecurringEvent(Date endDate, eRecurringIntervalType recurringIntervalType,
      Integer recurringIntervalDelta) throws IllegalArgumentException {
    if (recurringIntervalType == null) {
      throw new IllegalArgumentException(
          "recurringIntervalType cannot be null if recurring event is set to active!");
    }

    if (recurringIntervalDelta == null) {
      throw new IllegalArgumentException(
          "recurringIntervalDelta cannot be null if recurring event is set to active!");
    }

    this.endDate = endDate;
    this.recurringIntervalType = recurringIntervalType;
    this.recurringIntervalDelta = recurringIntervalDelta;
  }

  //</editor-fold>

  //<editor-fold desc="Static helper methods">

  /**
   * Gets the next date according to the current date and the interval options.
   *
   * @param currentDate   the current date.
   * @param intervalType  the interval type to be used to calculate the next date.
   * @param intervalDelta the interval delta to be used to calculate the next date.
   * @return the next date calculated according to the provided parameters.
   * @throws NotImplementedException if an interval type is provided which is not supported.
   *                                 Supported interval types include yearly, monthly, daily.
   */
  static Date getNextDate(Date currentDate, eRecurringIntervalType intervalType,
      Integer intervalDelta) throws NotImplementedException {
    LocalDate resultLocalDate = Utils.convertDateToLocalDate(currentDate);

    switch (intervalType) {
      case YEARLY:
        resultLocalDate = resultLocalDate.plusYears(intervalDelta);
        break;
      case MONTHLY:
        resultLocalDate = resultLocalDate.plusMonths(intervalDelta);
        break;
      case DAILY:
        resultLocalDate = resultLocalDate.plusDays(intervalDelta);
        break;
      default:
        throw new NotImplementedException("This interval Type is not implemented!");
    }

    return Utils.convertLocalDateToDate(resultLocalDate);
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public Date getEndDate() {
    return endDate;
  }

  @Override
  public eRecurringIntervalType getRecurringIntervalType() {
    return recurringIntervalType;
  }

  @Override
  public Integer getRecurringIntervalDelta() {
    return recurringIntervalDelta;
  }

  //</editor-fold>

  //<editor-fold desc="Object methods">


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof IRecurringEvent)) {
      return false;
    }

    IRecurringEvent o = (IRecurringEvent) obj;
    boolean endDatesEqual = !(o.getEndDate() == null ^ this.endDate == null);

    if (endDatesEqual && this.endDate != null) {
      endDatesEqual = o.getEndDate().equals(this.endDate);
    }

    return endDatesEqual
        && o.getRecurringIntervalDelta().equals(this.recurringIntervalDelta)
        && o.getRecurringIntervalType().equals(this.recurringIntervalType);
  }

  @Override
  public int hashCode() {
    return this.endDate.hashCode()
        + this.recurringIntervalDelta.hashCode()
        + this.recurringIntervalType.hashCode();
  }

  //</editor-fold>
}

