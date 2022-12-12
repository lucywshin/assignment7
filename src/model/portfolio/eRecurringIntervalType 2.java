package model.portfolio;

/**
 * An enumerator containing the supported recurring event interval types.
 */
public enum eRecurringIntervalType {
  YEARLY("Yearly"),

  MONTHLY("Monthly"),

  DAILY("Daily");

  private final String string;

  eRecurringIntervalType(String functionString) {
    this.string = functionString;
  }

  /**
   * Get the value of the recurring interval type.
   *
   * @return the {@code String} value of this recurring interval type.
   */
  public String getRecurringIntervalTypeVal() {
    return this.string;
  }

  /**
   * Get the type of interval from the {@code String} value of the recurring interval type.
   *
   * @param givenName the value of the recurring interval type.
   * @return the {@code eRecurringIntervalType} type of recurring interval.
   */
  public static eRecurringIntervalType fromValue(String givenName) {
    for (eRecurringIntervalType recurringType : values()) {
      if (recurringType.string.equals(givenName)) {
        return recurringType;
      }
    }
    return null;
  }
}
