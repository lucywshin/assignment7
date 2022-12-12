package model.chart;

import common.pair.Pair;
import java.util.List;

/**
 * This class represents a 2-D chart.
 */
public class Chart implements IChart {

  private final String title;
  private final int scaleBase;
  private final int scaleDelta;
  private final List<Pair<String, Integer>> dataPoints;

  /**
   * Creates a {@code Chart}.
   *
   * @param title      The title of the chart.
   * @param scaleBase  The base scale of the chart.
   * @param scaleDelta The relative scale of the chart.
   * @param dataPoints The data points of the chart.
   */
  public Chart(String title, int scaleBase, int scaleDelta,
      List<Pair<String, Integer>> dataPoints) {

    if (scaleBase < 0) {
      throw new IllegalArgumentException("Base scale cannot be negative!");
    } else if (scaleDelta < 0) {
      throw new IllegalArgumentException("Relative scale cannot be negative!");
    } else if (scaleDelta == 0) {
      throw new IllegalArgumentException("Relative scale cannot be zero!");
    }

    this.title = title;
    this.scaleBase = scaleBase;
    this.scaleDelta = scaleDelta;
    this.dataPoints = dataPoints;
  }

  @Override
  public String getTitle() {
    return this.title;
  }

  @Override
  public int getScaleBase() {
    return this.scaleBase;
  }

  @Override
  public int getScaleDelta() {
    return this.scaleDelta;
  }

  @Override
  public List<Pair<String, Integer>> getDataPoints() {
    return this.dataPoints;
  }
}
