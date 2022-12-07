package model.chart;

import common.pair.Pair;
import java.util.List;

/**
 * This interface represents a 2-D chart.
 */
public interface IChart {

  /**
   * Gets the title of this chart.
   *
   * @return the title of the chart in {@code String} format.
   */
  String getTitle();

  /**
   * Gets the base scale of this chart.
   *
   * @return the base scale of the chart in {@code int} format.
   */
  int getScaleBase();

  /**
   * Gets the relative scale of this chart.
   *
   * @return the relative scale of the chart in {@code int} format.
   */
  int getScaleDelta();

  /**
   * Get the data points of this chart.
   *
   * @return the data points of the chart as a list of pairs of the time stamp and the value at that
   *     particular time stamp.
   */
  List<Pair<String, Integer>> getDataPoints();
}
