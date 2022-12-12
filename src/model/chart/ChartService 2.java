package model.chart;

import common.Utils;
import common.pair.Pair;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * This class represents a chart service which computes a chart's intervals based on the given time
 * frame, and generates a chart accordingly.
 */
public class ChartService implements IChartService {

  //<editor-fold desc="State variables">

  private final int intervalMin;
  private final int intervalMax;
  private final int scaleMax;

  private final int dayInMS = 86400000;

  private enum Month {
    JAN(0), FEB(1), MAR(2), APR(3), MAY(4), JUN(5), JUL(6), AUG(7), SEP(8), OCT(9), NOV(10), DEC(
        11);

    public final int label;

    Month(int label) {
      this.label = label;
    }

    public int getNumLabel() {
      return this.label;
    }
  }

  //</editor-fold>

  //<editor-fold desc="Constructors">

  private ChartService() throws NotImplementedException {
    throw new NotImplementedException("This constructor cannot be used!");
  }

  /**
   * Creates a {@code ChartService}.
   *
   * @param intervalMin the minimum number of time intervals allowed for the chart
   * @param intervalMax the maximum number of time intervals allowed for the chart
   * @param scaleMax    the maximum number of scale symbols allowed on one line of the chart
   */
  public ChartService(int intervalMin, int intervalMax, int scaleMax) {
    if (intervalMin < 0 || intervalMax < 0 || scaleMax < 0) {
      throw new IllegalArgumentException("Interval and scale parameters cannot be less than zero.");
    } else if (intervalMin >= intervalMax) {
      throw new IllegalArgumentException(
          "Minimum interval cannot be greater than or equal to maximum interval.");
    } else if (intervalMax - intervalMin < 25) {
      throw new IllegalArgumentException(
          "Difference between interval minimum and interval maximum needs to be at least 25.");
    } else if (scaleMax < 10) {
      throw new IllegalArgumentException(
          "Scale max has to be at least 10.");
    }

    this.intervalMin = intervalMin;
    this.intervalMax = intervalMax;
    this.scaleMax = scaleMax;
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  private boolean isPrime(int number) {
    if (number <= 1) {
      return false;
    }

    for (int i = 2; i <= (number / 2); i++) {
      if ((number % i) == 0) {
        return false;
      }
    }

    return true;
  }

  private int findDivider(int timeVar) {

    int divider = 0;

    for (int i = 2; i < 10; i++) {
      if ((timeVar % i == 0) && ((timeVar / i) >= 5) && ((timeVar / i) <= 30)) {
        divider = i;
        break;
      }
    }

    return divider;
  }

  private Month determineMonth(int month) {
    switch (month % 12) {
      case 0:
        return Month.JAN;
      case 1:
        return Month.FEB;
      case 2:
        return Month.MAR;
      case 3:
        return Month.APR;
      case 4:
        return Month.MAY;
      case 5:
        return Month.JUN;
      case 6:
        return Month.JUL;
      case 7:
        return Month.AUG;
      case 8:
        return Month.SEP;
      case 9:
        return Month.OCT;
      case 10:
        return Month.NOV;
      case 11:
      default:
        return Month.DEC;
    }
  }

  private int determineLastDayOfMonth(Month month, int year) {
    switch (month) {
      case JAN:
      case MAR:
      case MAY:
      case JUL:
      case AUG:
      case OCT:
      case DEC:
        return 31;

      case APR:
      case JUN:
      case SEP:
      case NOV:
        return 30;

      case FEB:
        return year % 4 == 0 ? 29 : 28;
      default:
        return 0;
    }
  }

  private List<Date> createListOfDays(Date start, Date end) {

    List<Date> days = new ArrayList<>();
    int totalDays = (int) ((end.getTime() - start.getTime()) / dayInMS) + 1;

    for (int i = 0; i < totalDays; i++) {
      days.add(new Date(end.getTime() - (long) i * dayInMS));
    }

    Collections.reverse(days);

    return days;
  }

  private List<Date> createListOfMonths(Date start, Date end) {

    List<Date> months = new ArrayList<>();

    int totalMonths = Utils.getDateMonthDifference(start, end);

    int year = start.getYear();
    int startMonth = start.getMonth();

    Month month = determineMonth(startMonth);

    for (int i = 0; i < totalMonths; i++) {

      if (month == Month.DEC) {
        months.add(new Date(year, month.getNumLabel(), determineLastDayOfMonth(month, year)));
        month = Month.JAN;
        year++;
      } else {
        months.add(new Date(year, month.getNumLabel(), determineLastDayOfMonth(month, year)));
        month = determineMonth(month.getNumLabel() + 1);
      }
    }

    return months;
  }

  //<editor-fold desc="Days calculations">

  private Pair<eChartInterval, List<Date>> handleDays(Date start, Date end) {

    int totalDays = (int) Utils.getDateDifferenceInDays(start, end) + 1;

    if (totalDays <= 30) {
      return new Pair<>(eChartInterval.DAILY, createListOfDays(start, end));
    } else {
      if (isPrime(totalDays)) {
        return handlePrimeDays(start, end);
      }
      return handleMultiDailyIntervals(start, end);
    }
  }

  private Pair<eChartInterval, List<Date>> handlePrimeDays(Date start, Date end) {

    int nearest = (int) Utils.getDateDifferenceInDays(start, end);
    int divider = findDivider(nearest);

    List<Date> days = createListOfDays(start, end);

    List<Date> dates = new ArrayList<>();
    for (int i = 0; i < (nearest / divider + 1); i++) {
      dates.add(i, days.get(i * divider));
    }
    dates.add((nearest / divider + 1), end);

    return new Pair<>(eChartInterval.DAILY, dates);
  }

  private Pair<eChartInterval, List<Date>> handleMultiDailyIntervals(Date start, Date end) {

    int totalDays = (int) Utils.getDateDifferenceInDays(start, end) + 1;
    int divider = findDivider(totalDays);

    List<Date> days = createListOfDays(start, end);

    List<Date> dates = new ArrayList<>();
    for (int i = (totalDays / divider); i > 0; i--) {
      dates.add(days.get(i * divider - 1));
    }
    Collections.reverse(dates);

    return new Pair<>(eChartInterval.MULTI_DAILY, dates);
  }

  //</editor-fold>

  //<editor-fold desc="Month calculations">

  private Pair<eChartInterval, List<Date>> handleMonths(Date start, Date end) {

    int totalMonths = Utils.getDateMonthDifference(start, end);

    if (totalMonths < this.intervalMin) {
      return handleDays(start, end);
    } else if (totalMonths <= this.intervalMax) {
      return new Pair<>(eChartInterval.MONTHLY, createListOfMonths(start, end));
    } else {
      // quarter-annually
      if ((totalMonths % 4 == 0) && (totalMonths / 4 >= 5) && (totalMonths / 4 <= 30)) {
        return handleMultiMonthlyIntervals(start, end, eChartInterval.QUARTER_ANNUALLY);
      }
      // semi-annually
      else if ((totalMonths % 2 == 0) && (totalMonths / 2 >= 5) && (totalMonths / 2 <= 30)) {
        return handleMultiMonthlyIntervals(start, end, eChartInterval.SEMI_ANNUALLY);
      }
      // multi-monthly
      else {
        if (isPrime(totalMonths)) {
          return handlePrimeMonths(start, end);
        }
        return handleMultiMonthlyIntervals(start, end, eChartInterval.MULTI_MONTHLY);
      }
    }
  }

  private Pair<eChartInterval, List<Date>> handlePrimeMonths(Date start, Date end) {

    int nearest = Utils.getDateMonthDifference(start, end) - 1;
    int divider = findDivider(nearest);

    List<Date> months = createListOfMonths(start, end);

    List<Date> dates = new ArrayList<>();
    for (int i = 0; i < (nearest / divider + 1); i++) {
      dates.add(i, months.get(i * divider));
    }
    dates.add((nearest / divider + 1), new Date(end.getYear(), end.getMonth(),
        determineLastDayOfMonth(determineMonth(end.getMonth()), end.getYear())));

    return new Pair<>(eChartInterval.MULTI_MONTHLY, dates);
  }

  private Pair<eChartInterval, List<Date>> handleMultiMonthlyIntervals(Date start, Date end,
      eChartInterval interval) {

    List<Date> months = createListOfMonths(start, end);
    int totalMonths = Utils.getDateMonthDifference(start, end);

    int divider = 0;

    if (interval == eChartInterval.QUARTER_ANNUALLY) {
      divider = 3;
    } else if (interval == eChartInterval.SEMI_ANNUALLY) {
      divider = 6;
    } else if (interval == eChartInterval.MULTI_MONTHLY) {
      divider = findDivider(totalMonths);
    }

    int divisions = totalMonths / divider;

    List<Date> dates = new ArrayList<>();

    for (int i = divisions; i > 0; i--) {
      dates.add(months.get((i * divider) - 1));
    }

    Collections.reverse(dates);

    return new Pair<>(interval, dates);
  }

  //</editor-fold>

  //<editor-fold desc="Year calculations">

  private Pair<eChartInterval, List<Date>> handleYearlyIntervals(Date start, Date end) {
    int yearDifference = Utils.getDateYearDifference(start, end);
    List<Date> dates = new ArrayList<>();

    for (int i = 0; i <= yearDifference; i++) {
      dates.add(i, new Date(end.getYear() - i, Calendar.DECEMBER, 31));
    }
    Collections.reverse(dates);

    return new Pair<>(eChartInterval.ANNUALLY, dates);
  }

  private Pair<eChartInterval, List<Date>> handlePrimeYears(Date start, Date end) {

    int nearest = Utils.getDateYearDifference(start, end) - 1;
    int divider = findDivider(nearest);

    List<Date> dates = new ArrayList<>();

    for (int i = 0; i < (nearest / divider + 1); i++) {
      dates.add(i, new Date(end.getYear() - i * divider, Calendar.DECEMBER, 31));
    }

    Collections.reverse(dates);

    return new Pair<>(eChartInterval.MULTI_ANNUALLY, dates);
  }

  private Pair<eChartInterval, List<Date>> handleMultiAnnuallyIntervals(Date start, Date end) {

    int years = Utils.getDateYearDifference(start, end);

    if (isPrime(years)) {
      return handlePrimeYears(start, end);
    }

    List<Date> dates = new ArrayList<>();
    int divider = findDivider(years);

    for (int i = 0; i < (years / divider + 1); i++) {
      dates.add(i, new Date(end.getYear() - i * divider, Calendar.DECEMBER, 31));
    }
    Collections.reverse(dates);

    return new Pair<>(eChartInterval.MULTI_ANNUALLY, dates);
  }

  //</editor-fold>

  private String generateLabel(eChartInterval interval, Date date) {

    switch (interval) {
      case MULTI_ANNUALLY:
      case ANNUALLY:
        return Utils.convertDateToString(date, "yyyy");

      case SEMI_ANNUALLY:
      case QUARTER_ANNUALLY:
      case MULTI_MONTHLY:
      case MONTHLY:
        return Utils.convertDateToString(date, "MMM yyyy");

      case MULTI_DAILY:
      case DAILY:
        return Utils.convertDateToString(date, "MM-dd-yyyy");

      default:
        return "";
    }
  }

  private void validateStartAndEndDates(Date start, Date end) throws IllegalArgumentException {
    if (end.before(start)) {
      throw new IllegalArgumentException("End date cannot be before start date!");
    }

    if ((end.getTime() - start.getTime()) < ((long) this.intervalMin) * dayInMS) {
      throw new IllegalArgumentException(
          "Start date and end date need to be apart by at least the minimum interval value: "
              + this.intervalMin + ".");
    }
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public IChart generateChart(eChartInterval interval, String title,
      List<Pair<Date, Integer>> data) {

    List<Integer> values = new ArrayList<>();
    for (Pair<Date, Integer> dataPoint : data) {
      values.add(dataPoint.getO2());
    }

    List<Integer> sortedlist = new ArrayList<>(values);
    Collections.sort(sortedlist);
    int scaleBase = sortedlist.get(0);
    int maxValue = sortedlist.get(sortedlist.size() - 1);
    int range = maxValue - scaleBase;

    int scaleDelta;
    if (range == 0) {
      scaleDelta = 1;
    } else {
      // determine scale delta
      scaleDelta = range / (this.scaleMax - 1);

      int length = (int) (Math.log10(scaleDelta) + 1);
      int firstDigit = Integer.parseInt(Integer.toString(scaleDelta).substring(0, 1));

      if (scaleDelta > 1 && scaleDelta <= 2) {
        scaleDelta = 2;
      } else if (scaleDelta > 2 && scaleDelta <= 5) {
        scaleDelta = 5;
      } else if (scaleDelta > 5 && scaleDelta <= 10) {
        scaleDelta = 10;
      } else if (scaleDelta > 10) {
        if (firstDigit == 1) {
          scaleDelta = (int) ((firstDigit + 1) * (Math.pow(10, length - 1)));
        } else if (firstDigit == 2 || firstDigit == 3 || firstDigit == 4) {
          scaleDelta = (int) (5 * (Math.pow(10, length - 1)));
        } else {
          scaleDelta = (int) (1 * (Math.pow(10, length)));
        }
      }
    }

    List<Pair<String, Integer>> displayDataPoints = new ArrayList<>();
    for (Pair<Date, Integer> dataPoint : data) {
      // transform data point to chart data point
      int scaleValue = (dataPoint.getO2() - scaleBase) / scaleDelta;

      displayDataPoints.add(
          new Pair<>(this.generateLabel(interval, dataPoint.getO1()),
              dataPoint.getO2() != 0
                  ? scaleValue + 1 : scaleValue));
    }

    return new Chart(title, scaleBase, scaleDelta, displayDataPoints);
  }

  @Override
  public Pair<eChartInterval, List<Date>> getChartInterval(Date start, Date end)
      throws IllegalArgumentException {
    this.validateStartAndEndDates(start, end);

    int yearDifference = end.getYear() - start.getYear();

    if (yearDifference < this.intervalMin) {
      return handleMonths(start, end);
    } else if (yearDifference <= this.intervalMax) {
      return handleYearlyIntervals(start, end);
    } else {
      return handleMultiAnnuallyIntervals(start, end);
    }
  }

  //</editor-fold>
}
