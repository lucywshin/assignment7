package common;

import common.pair.Pair;
import common.triplet.Triplet;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * A class with static methods for utility across the application.
 */
public class Utils {

  //<editor-fold desc="Regex constants">

  private final static Pattern INTEGER_REGEX_PATTERN = Pattern.compile("^\\d+$");
  private final static Pattern FLOAT_REGEX_PATTERN = Pattern.compile("^\\d*\\.?\\d*$");
  private final static Pattern WORDS_REGEX_PATTERN = Pattern.compile("^[a-zA-Z0-9_ ]*$");
  private final static Pattern STOCKS_REGEX_PATTERN = Pattern.compile("([A-Z.]+,(\\d+)(;?))*$");
  private final static Pattern FLEXIBLE_STOCKS_REGEX_PATTERN = Pattern.compile(
      "([A-Z.]+,(\\d+),(\\d{2}-\\d{2}-\\d{4})(;?))*$");
  private final static Pattern DATE_REGEX_PATTERN = Pattern.compile("^\\d{2}-\\d{2}-\\d{4}$");

  //</editor-fold>

  //<editor-fold desc="Date constants">

  private final static ZoneId BOSTON_ZONE_ID = ZoneId.of("America/New_York");

  //</editor-fold>

  //<editor-fold desc="String Input Validator methods and Converter methods">

  //<editor-fold desc="Int number validations">

  /**
   * Validates string input to be an integer.
   *
   * @param input String input to be validated.
   * @return whether the input is an integer.
   */
  public static boolean isValidNumberInput(String input) {
    if (input == null || input.isBlank()) {
      return false;
    }
    return INTEGER_REGEX_PATTERN.matcher(input).matches();
  }

  /**
   * Converts the string input to an integer.
   *
   * @param input String input to be converted.
   * @return the converted value.
   * @throws IllegalArgumentException when a parsing error occurs.
   */
  public static int convertStringToNumber(String input) throws IllegalArgumentException {
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("the provided string could not be parsed!");
    }
  }

  //</editor-fold>

  //<editor-fold desc="Float Number validations">

  /**
   * Validates string input to be a float.
   *
   * @param input String input to be validated.
   * @return whether the value is float.
   */
  public static boolean isValidFloatNumberInput(String input) {
    if (input == null || input.isBlank()) {
      return false;
    }
    return FLOAT_REGEX_PATTERN.matcher(input).matches();
  }

  /**
   * Converts string input to a float.
   *
   * @param input String input to be converted.
   * @return the converted value.
   * @throws IllegalArgumentException when a parsing error occurs.
   */
  public static float convertStringToFloatNumber(String input) throws IllegalArgumentException {
    try {
      return Float.parseFloat(input);
    } catch (NumberFormatException | NullPointerException e) {
      throw new IllegalArgumentException("the provided string could not be parsed!");
    }
  }

  //</editor-fold>

  //<editor-fold desc="Words validations">

  /**
   * Checks whether input string is a string with space seperated words.
   *
   * @param input String input to be validated.
   * @return whether the value are words.
   */
  public static boolean isValidWordsInput(String input) {
    if (input == null || input.isBlank()) {
      return false;
    }

    return WORDS_REGEX_PATTERN.matcher(input).matches();
  }

  /**
   * Validates string input to be words.
   *
   * @param input String input to be validated.
   * @throws IllegalArgumentException when the input string is not a string with space seperated
   *                                  words.
   */
  public static void validateWordsInput(String input) throws IllegalArgumentException {
    if (!isValidWordsInput(input)) {
      throw new IllegalArgumentException("The provided string: " + input + " is not a word!");
    }
  }

  //</editor-fold>

  //<editor-fold desc="Stocks validations">

  /**
   * Checks whether string input is in requested format for stocks.
   *
   * @param input String input to be validated.
   * @return whether the input is in valid format for stocks.
   */
  public static boolean isValidStockInputFormat(String input) {
    if (input == null || input.isBlank()) {
      return false;
    }
    return STOCKS_REGEX_PATTERN.matcher(input).matches();
  }

  /**
   * Converts string input to be requested format for stocks.
   *
   * @param input String input to be converted.
   * @return list of pairs with stock symbol and volume.
   * @throws IllegalArgumentException when a parsing error occurs.
   */
  public static List<Pair<String, Integer>> convertStringToStocksFormat(String input)
      throws IllegalArgumentException {
    List<Pair<String, Integer>> result = new ArrayList<>();
    String[] stocks = input.split(";");

    if (stocks.length == 0) {
      return result;
    }

    try {
      for (String s : stocks) {
        List<String> currentStockSplit = Arrays.asList(s.split(","));
        result.add(
            new Pair<>(currentStockSplit.get(0), Integer.parseInt(currentStockSplit.get(1))));
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("the provided string could not be parsed!");
    }
    return result;
  }

  /**
   * Converts string input to be requested format for stocks.
   *
   * @param input String input to be converted.
   * @return list of pairs with stock symbol and weight.
   * @throws IllegalArgumentException when a parsing error occurs.
   */
  public static List<Pair<String, BigDecimal>> convertStringToListFormat(String input)
          throws IllegalArgumentException {
    List<Pair<String, BigDecimal>> result = new ArrayList<>();
    String[] stocks = input.split(";");

    if (stocks.length == 0) {
      return result;
    }

    try {
      for (String s : stocks) {
        List<String> currentStockSplit = Arrays.asList(s.split(","));
        result.add(
                new Pair<>(currentStockSplit.get(0), new BigDecimal(currentStockSplit.get(1))));
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("the provided string could not be parsed!");
    }
    return result;
  }

  /**
   * Checks whether string input is in requested format for stocks.
   *
   * @param input String input to be validated.
   * @return whether the input is in valid format for stocks.
   */
  public static boolean isValidFlexibleStockInputFormat(String input) {
    if (input == null || input.isBlank()) {
      return false;
    }
    return FLEXIBLE_STOCKS_REGEX_PATTERN.matcher(input).matches();
  }

  /**
   * Converts string input to be requested format for flexible stocks.
   *
   * @param input String input to be converted.
   * @return list of triplets with stock symbol date of transaction, and volume.
   */
  public static List<Triplet<String, Date, BigDecimal>> convertStringToFlexibleStocksFormat(
      String input) {
    List<Triplet<String, Date, BigDecimal>> result = new ArrayList<>();
    String[] stocks = input.split(";");

    if (stocks.length == 0) {
      return result;
    }

    for (String s : stocks) {
      List<String> currentStockSplit = Arrays.asList(s.split(","));
      result.add(
          new Triplet<>(currentStockSplit.get(0),
              convertStringToDate(currentStockSplit.get(2)),
              new BigDecimal(currentStockSplit.get(1))));
    }
    return result;
  }

  //</editor-fold>

  //<editor-fold desc="Date validations">

  /**
   * Checks whether string input is a date.
   *
   * @param input String input to be validated.
   * @return whether the input is in date format.
   */
  public static boolean isValidDateInput(String input) {
    if (input == null || input.isBlank() || !DATE_REGEX_PATTERN.matcher(input).matches()) {
      return false;
    }

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.US);

    try {
      dateFormatter.parse(input);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }

  /**
   * Converts string input to a date.
   *
   * @param input String input to be converted.
   * @return the converted value.
   * @throws IllegalArgumentException when a parsing error occurs.
   */
  public static Date convertStringToDate(String input) throws IllegalArgumentException {
    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    try {
      return formatter.parse(input);
    } catch (ParseException e) {
      throw new IllegalArgumentException("the provided string could not be parsed!");
    }
  }

  /**
   * Converts string input to a dat according to given format.
   *
   * @param input  String input to be converted.
   * @param format the format in which the date is to be converted.
   * @return the converted value.
   * @throws IllegalArgumentException when a parsing error occurs.
   */
  public static Date convertStringToDate(String input, String format)
      throws IllegalArgumentException {
    SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);

    try {
      return formatter.parse(input);
    } catch (ParseException e) {
      throw new IllegalArgumentException("the provided string could not be parsed!");
    }
  }

  /**
   * Validates the date to be in the present or past.
   *
   * @param date the date to be validated.
   * @throws IllegalArgumentException when the date is in the future.
   */
  public static void validateFutureDate(Date date) throws IllegalArgumentException {
    if (Utils.isFutureDate(date)) {
      throw new IllegalArgumentException("Provided date: " + date + " cannot be in the future!");
    }
  }

  /**
   * Converts given date to specified date format.
   *
   * @param date   the date to be converted.
   * @param format the format in which the date is to be converted.
   * @return the converted date in string format.
   */
  public static String convertDateToString(Date date, String format) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.format(date);
  }

  /**
   * Converts given string to application default date format.
   *
   * @param date the date to be converted.
   * @return the converted date in the default string format.
   */
  public static String convertDateToDefaultStringFormat(Date date) {
    return convertDateToString(date, "MM-dd-yyyy");
  }

  //</editor-fold>

  //<editor-fold desc="File path validations">

  /**
   * Validates string input to be a valid file path.
   *
   * @param input String input to be validated.
   * @return whether the input is a valid file path
   */
  public static boolean validateFilePathInput(String input) {
    if (input == null || input.isBlank()) {
      return false;
    }

    String path = input.trim();

    // Validate publish settings path
    File file = new File(path);
    return (file.exists()) && !file.isDirectory();
  }

  //</editor-fold>

  //<editor-fold desc="Date Validators and helpers">

  /**
   * Checks whether the given date is in the future.
   *
   * @param date the date to be checked.
   * @return a boolean value representing whether the date provided is in the future.
   * @throws IllegalArgumentException when the date is not parsable.
   */
  public static boolean isFutureDate(Date date) throws IllegalArgumentException {
    String dateString = date.toString();
    String dateFormat = "EEE MMM dd HH:mm:ss zzz yyyy";

    LocalDate localDate = LocalDate.now(ZoneId.systemDefault());

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);

    try {
      LocalDate inputDate = LocalDate.parse(dateString, dtf);
      return inputDate.isAfter(localDate);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("The date could not be parsed!");
    }
  }

  /**
   * Gets the date of today without the time.
   *
   * @return today's date in {@code Date} data type.
   */
  public static Date getTodayDate() {
    Date now = new Date();
    return new Date(now.getYear(), now.getMonth(), now.getDay());
  }

  //</editor-fold>

  //</editor-fold>

  //<editor-fold desc="Date - LocalDate conversions">

  /**
   * Converts the given date to Local date data type.
   *
   * @param dateToConvert the date in {@code Date} data type.
   * @return the date in {@code LocalDate} data type.
   */
  public static LocalDate convertDateToLocalDate(Date dateToConvert) {
    return dateToConvert.toInstant()
        .atZone(BOSTON_ZONE_ID)
        .toLocalDate();
  }

  /**
   * Convert the given local date to date.
   *
   * @param localDate the date in {@code LocalDate} data type.
   * @return the date in {@code Date} data type.
   */
  public static Date convertLocalDateToDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay(BOSTON_ZONE_ID).toInstant());
  }

  //</editor-fold>

  //<editor-fold desc="Date difference methods">

  /**
   * Gets the difference of the provided dates in milliseconds.
   *
   * @param firstDate  the first date.
   * @param secondDate the second date.
   * @return the difference of the two dates in milliseconds.
   */
  public static long getDateDifferenceInMilliseconds(Date firstDate, Date secondDate) {
    return Math.abs(secondDate.getTime() - firstDate.getTime());
  }

  /**
   * Gets the difference of the provided dates in days.
   *
   * @param firstDate  the first date.
   * @param secondDate the second date.
   * @return the difference of the two dates in days.
   */
  public static long getDateDifferenceInDays(Date firstDate, Date secondDate) {
    long diffInMilliseconds = getDateDifferenceInMilliseconds(firstDate, secondDate);
    return TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS);
  }

  /**
   * Gets the difference of months of the two dates.
   *
   * @param firstDate  the first date.
   * @param secondDate the second date.
   * @return the difference of months of the two dates.
   */
  public static int getDateMonthDifference(Date firstDate, Date secondDate) {
    return ((secondDate.getYear() - firstDate.getYear()) * 12)
        + (secondDate.getMonth() - firstDate.getMonth() + 1);
  }

  /**
   * Gets the difference of years of the two dates.
   *
   * @param firstDate  the first date.
   * @param secondDate the second date.
   * @return the difference of years of the two dates.
   */
  public static int getDateYearDifference(Date firstDate, Date secondDate) {
    return secondDate.getYear() - firstDate.getYear();
  }

  //</editor-fold>

  //<editor-fold desc="Two Date validators">

  /**
   * Validates whether the start date is before the end date.
   *
   * @param startDate the date on which the event starts.
   * @param endDate   the date on which the event ends.
   * @throws IllegalArgumentException when the start date is equal or after the end date.
   */
  public static void validateStartAndEndDate(Date startDate, Date endDate)
      throws IllegalArgumentException {
    if (startDate.equals(endDate)) {
      throw new IllegalArgumentException("The start date cannot be equal to the end date!");
    }
    if (startDate.after(endDate)) {
      throw new IllegalArgumentException("The start date cannot be after the end date!");
    }
  }

  //</editor-fold>

  //<editor-fold desc="BigDecimal validators">

  /**
   * Validates the given {@code BigDecimal} number to be a positive value.
   *
   * @param number the {@code BigDecimal} number to be validated.
   * @param label  the label for this number if an error is to be thrown.
   * @throws IllegalArgumentException when the number is not a positive value.
   */
  public static void validatePositiveValue(BigDecimal number, String label) {
    if (number.compareTo(new BigDecimal(0)) <= 0) {
      throw new IllegalArgumentException(label + " has to be a positive number!");
    }
  }

  /**
   * Validates the given {@code BigDecimal} number to be a non-negative value.
   *
   * @param number the {@code BigDecimal} number to be validated.
   * @param label  the label for this number if an error is to be thrown.
   * @throws IllegalArgumentException when the number is not a non-negative value.
   */
  public static void validateNonNegativeValue(BigDecimal number, String label) {
    if (number.compareTo(new BigDecimal(0)) < 0) {
      throw new IllegalArgumentException(label + " has to be a non-negative number!");
    }
  }

  //</editor-fold>

}
