package view;

import common.pair.Pair;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import model.chart.IChart;

/**
 * This class represents a bar chart and all its attributes.
 */
class BarChart extends JPanel {

  //<editor-fold desc="State variables">

  private final String title;
  private final List<Integer> bars;
  private final List<String> xLabels;
  private final int scaleYBase;

  //</editor-fold>

  //<editor-fold desc="Chart variables">

  private final int padding = 12;
  private final int labelPadding = 60;
  private final int barPadding = 4;
  private final int axisHatchWidth = 4;


  private final Color barColor = new Color(44, 102, 230, 180);
  private final Color gridColor = new Color(200, 200, 200, 200);

  private final int maxBarVal;
  private final int minDataVal;
  private final int maxDataVal;
  private final int numberYDivisions;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Constructs a bar chart to be displayed.
   *
   * @param chart the performance chart of a portfolio for a user provided time range.
   */
  BarChart(IChart chart) {
    this.title = chart.getTitle();
    this.scaleYBase = chart.getScaleBase();
    int scaleYDelta = chart.getScaleDelta();

    this.xLabels = new ArrayList<>();
    this.bars = new ArrayList<>();

    var dataPoints = chart.getDataPoints();
    float maxBarVal = Integer.MIN_VALUE;

    for (Pair<String, Integer> dataPoint : dataPoints) {
      int dataPointVal = dataPoint.getO2();

      this.xLabels.add(dataPoint.getO1());
      this.bars.add(dataPointVal);

      if (dataPointVal > maxBarVal) {
        maxBarVal = dataPointVal;
      }
    }

    this.maxBarVal = (int) maxBarVal;

    this.minDataVal = this.scaleYBase;
    this.maxDataVal = this.scaleYBase + (dataPoints.size() * scaleYDelta);

    this.numberYDivisions = 10;
  }

  //</editor-fold>

  //<editor-fold desc="Core Methods">

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // setting scale for x-axis and y-axis
    double availableXWidth = ((double) getWidth() - (2 * padding) - labelPadding);

    // draw white background
    g2.setColor(Color.WHITE);
    g2.fillRect(padding + labelPadding,
        padding,
        getWidth() - (2 * padding) - labelPadding,
        getHeight() - 2 * padding - labelPadding);
    g2.setColor(Color.BLACK);

    // create x and y axes
    g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding,
        padding + labelPadding, padding);
    g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding,
        getHeight() - padding - labelPadding);

    // create hatch marks and grid lines for y-axis.
    for (int i = 0; i < numberYDivisions + 1; i++) {
      int x0 = padding + labelPadding;
      int x1 = axisHatchWidth + padding + labelPadding;
      int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions
          + padding + labelPadding);
      int y1 = y0;
      if (bars.size() > 0) {
        g2.setColor(gridColor);
        g2.drawLine(padding + labelPadding + 1 + axisHatchWidth, y0, getWidth() - padding, y1);
        g2.setColor(Color.BLACK);
        String yLabel = "$" + ((int) (
            (this.scaleYBase + (this.maxDataVal - this.minDataVal) * ((i * 1.0) / numberYDivisions))
                * 100)) / 100.0 + "";
        FontMetrics metrics = g2.getFontMetrics();
        int labelWidth = metrics.stringWidth(yLabel);
        g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
      }
      g2.drawLine(x0, y0, x1, y1);
    }

    FontMetrics metrics = g2.getFontMetrics();

    // create hatch marks and grid lines for x-axis.
    for (int i = 0; i < bars.size(); i++) {
      if (bars.size() > 1) {
        int x0 = (int) (i * (availableXWidth / (bars.size())) + padding + labelPadding);
        int x1 = x0;
        int y0 = getHeight() - padding - labelPadding;
        int y1 = y0 - axisHatchWidth;
        if ((i % ((int) ((bars.size() / 20.0)) + 1)) == 0) {
          g2.setColor(gridColor);
          g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - axisHatchWidth, x1, padding);
          g2.setColor(Color.BLACK);
          String xLabel = this.xLabels.get(i);

          g2.drawString(xLabel, x0, y0 + metrics.getHeight() + 3);
        }
        g2.drawLine(x0, y0, x1, y1);
      }
    }

    // chart title
    int titleLabelWidth = metrics.stringWidth(this.title);
    g2.drawString(title,
        (int) (availableXWidth / 2 - titleLabelWidth / 2 + padding + labelPadding),
        getHeight() - padding);

    // paint bars
    int barWidth = (int) (availableXWidth / bars.size()) - 2 * barPadding;

    int barXOffset = barPadding;
    for (Integer value : bars) {
      int height = (int) ((getHeight() - 5) * ((double) value / this.maxBarVal));

      g.setColor(this.barColor);
      g.fillRect(barXOffset + padding + labelPadding, getHeight() + padding - height, barWidth,
          height - 2 * padding - labelPadding);
      g.setColor(Color.black);
      g.drawRect(barXOffset + padding + labelPadding, getHeight() + padding - height, barWidth,
          height - 2 * padding - labelPadding);
      barXOffset += barWidth + 2 * barPadding;
    }
  }

  /**
   * Gets the preferred size of the bar chart.
   *
   * @return a {@code Dimension} object of the preferred size for this bar chart.
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(bars.size() * 20 + 2, 300);
  }

  //</editor-fold>

}
