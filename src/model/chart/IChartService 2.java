package model.chart;

import common.pair.Pair;
import java.util.Date;
import java.util.List;

/**
 * This interface represents a chart service.
 */
public interface IChartService {

  /**
   * Generates a chart with the given interval, title, and data.
   *
   * @param interval an enum of the chart's interval type.
   * @param title    the title of the chart.
   * @param data     the data points of the chart.
   * @return a chart {@code IChart} generated with all the given attributes.
   */
  IChart generateChart(eChartInterval interval, String title, List<Pair<Date, Integer>> data);

  /**
   * Gets the chart interval based on the given start date and end date.
   *
   * @param start the state date of a time frame.
   * @param end   the end date of a time frame.
   * @return a pair of an enum specifying the type of chart interval, and a list of dates.
   */
  Pair<eChartInterval, List<Date>> getChartInterval(Date start, Date end);
}
