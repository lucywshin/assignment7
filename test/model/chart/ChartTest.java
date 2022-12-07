package model.chart;

import static org.junit.Assert.assertEquals;

import common.pair.Pair;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * A JUnit test class for the Chart class.
 */
public class ChartTest {

  //<editor-fold desc="State variables">

  private IChart chart;
  private String title;
  private int scaleBase;
  private int scaleDelta;
  private final List<Pair<String, Integer>> data = new ArrayList<>();

  private IChartService chartService;
  private Date start;
  private Date end;
  private Pair<eChartInterval, List<Date>> intervals;

  //</editor-fold>

  //<editor-fold desc="Chart Service Setup">

  @Before
  public void setup() {
    title = "The Chart Title";
    scaleBase = 1000;
    scaleDelta = 50;
    data.add(new Pair<>("Apples", 1100));
    data.add(new Pair<>("Bananas", 1250));
    data.add(new Pair<>("Oranges", 1500));
    chart = new Chart(title, scaleBase, scaleDelta, data);

    chartService = new ChartService(5, 30, 50);
    start = new Date(2000, 0, 1);
    end = new Date(2020, 0, 1);
  }

  //</editor-fold>

  //<editor-fold desc="Chart Service init tests">

  @Test(expected = IllegalArgumentException.class)
  public void chartServiceInitTestInvalidParams1() {
    new ChartService(0, 1, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void chartServiceInitTestInvalidParams2() {
    new ChartService(0, 0, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void chartServiceInitTestInvalidParams3() {
    new ChartService(5, 30, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void chartServiceInitTestInvalidParams4() {
    new ChartService(5, 30, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void chartServiceInitTestInvalidParams5() {
    new ChartService(-1, 30, 50);
  }

  @Test(expected = IllegalArgumentException.class)
  public void chartServiceInitTestInvalidParams6() {
    new ChartService(1, -1, 50);
  }

  //</editor-fold>

  //<editor-fold desc="Chart - Core Methods">

  @Test
  public void testChart_Construction() {

    scaleBase = -1;
    try {
      chart = new Chart(title, scaleBase, scaleDelta, data);
    } catch (IllegalArgumentException e) {
      assertEquals("Base scale cannot be negative!", e.getMessage());
    }

    scaleBase = 0;
    scaleDelta = -1;
    try {
      chart = new Chart(title, scaleBase, scaleDelta, data);
    } catch (IllegalArgumentException e) {
      assertEquals("Relative scale cannot be negative!", e.getMessage());
    }

    scaleDelta = 0;
    try {
      chart = new Chart(title, scaleBase, scaleDelta, data);
    } catch (IllegalArgumentException e) {
      assertEquals("Relative scale cannot be zero!", e.getMessage());
    }
  }

  @Test
  public void testChart_GetTitle() {
    assertEquals(title, chart.getTitle());
  }

  @Test
  public void testChart_GetScaleBase() {
    assertEquals(scaleBase, chart.getScaleBase());
  }

  @Test
  public void testChart_GetScaleDelta() {
    assertEquals(scaleDelta, chart.getScaleDelta());
  }

  @Test
  public void testChart_GetDataPoints() {
    assertEquals(data.get(0), chart.getDataPoints().get(0));
    assertEquals(data.get(1), chart.getDataPoints().get(1));
    assertEquals(data.get(2), chart.getDataPoints().get(2));
  }

  //</editor-fold>

  //<editor-fold desc="ChartService - Testing Generate Chart">

  @Test
  public void testChartService_GenerateChart_Daily() {

    eChartInterval interval = eChartInterval.DAILY;
    String title = "This Chart Title";
    List<Pair<Date, Integer>> data = new ArrayList<>();

    data.add(new Pair<>(new Date(121, 0, 1), 150));
    data.add(new Pair<>(new Date(121, 0, 2), 250));
    data.add(new Pair<>(new Date(121, 0, 3), 500));
    data.add(new Pair<>(new Date(121, 0, 4), 350));
    data.add(new Pair<>(new Date(121, 0, 5), 400));

    IChart c = chartService.generateChart(interval, title, data);

    assertEquals(title, c.getTitle());
    assertEquals(150, c.getScaleBase());
    assertEquals(10, c.getScaleDelta());
    assertEquals("01-01-2021: ", c.getDataPoints().get(0).getO1());
    assertEquals("01-02-2021: ", c.getDataPoints().get(1).getO1());
    assertEquals("01-03-2021: ", c.getDataPoints().get(2).getO1());
    assertEquals("01-04-2021: ", c.getDataPoints().get(3).getO1());
    assertEquals("01-05-2021: ", c.getDataPoints().get(4).getO1());
  }

  @Test
  public void testChartService_GenerateChart_Monthly() {

    eChartInterval interval = eChartInterval.MONTHLY;
    String title = "This Chart Title";
    List<Pair<Date, Integer>> data = new ArrayList<>();

    data.add(new Pair<>(new Date(121, 0, 1), 150));
    data.add(new Pair<>(new Date(121, 1, 1), 250));
    data.add(new Pair<>(new Date(121, 2, 1), 500));
    data.add(new Pair<>(new Date(121, 3, 1), 350));
    data.add(new Pair<>(new Date(121, 4, 1), 400));

    IChart c = chartService.generateChart(interval, title, data);

    assertEquals(title, c.getTitle());
    assertEquals(150, c.getScaleBase());
    assertEquals(10, c.getScaleDelta());
    assertEquals("Jan 2021: ", c.getDataPoints().get(0).getO1());
    assertEquals("Feb 2021: ", c.getDataPoints().get(1).getO1());
    assertEquals("Mar 2021: ", c.getDataPoints().get(2).getO1());
    assertEquals("Apr 2021: ", c.getDataPoints().get(3).getO1());
    assertEquals("May 2021: ", c.getDataPoints().get(4).getO1());
  }

  @Test
  public void testChartService_GenerateChart_Annually() {

    eChartInterval interval = eChartInterval.ANNUALLY;
    String title = "This Chart Title";
    List<Pair<Date, Integer>> data = new ArrayList<>();

    data.add(new Pair<>(new Date(110, 0, 1), 150));
    data.add(new Pair<>(new Date(111, 0, 1), 250));
    data.add(new Pair<>(new Date(112, 0, 1), 500));
    data.add(new Pair<>(new Date(113, 0, 1), 350));
    data.add(new Pair<>(new Date(114, 0, 1), 400));

    IChart c = chartService.generateChart(interval, title, data);

    assertEquals(title, c.getTitle());
    assertEquals(150, c.getScaleBase());
    assertEquals(10, c.getScaleDelta());
    assertEquals("2010: ", c.getDataPoints().get(0).getO1());
    assertEquals("2011: ", c.getDataPoints().get(1).getO1());
    assertEquals("2012: ", c.getDataPoints().get(2).getO1());
    assertEquals("2013: ", c.getDataPoints().get(3).getO1());
    assertEquals("2014: ", c.getDataPoints().get(4).getO1());
  }

  //</editor-fold>

  //<editor-fold desc="ChartService - Testing Intervals - years">

  @Test
  public void testChartService_GetChartInterval_Multi_Annually() {

    start = new Date(2000, 11, 1);
    end = new Date(2045, 0, 1);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.MULTI_ANNUALLY, intervals.getO1());
    assertEquals(16, intervals.getO2().size());

    assertEquals(new Date(2000, 11, 31), intervals.getO2().get(0));
    assertEquals(new Date(2045, 11, 31), intervals.getO2().get(15));

    end = new Date(2045, 11, 1);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.MULTI_ANNUALLY, intervals.getO1());
    assertEquals(16, intervals.getO2().size());

    assertEquals(new Date(2000, 11, 31), intervals.getO2().get(0));
    assertEquals(new Date(2045, 11, 31), intervals.getO2().get(15));
  }

  @Test
  public void testChartService_GetChartInterval_Multi_Annually_Prime() {

    // check when last month is not December
    start = new Date(2000, 11, 1);
    end = new Date(2031, 0, 1);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.MULTI_ANNUALLY, intervals.getO1());
    assertEquals(16, intervals.getO2().size());
    assertEquals(new Date(2001, 11, 31), intervals.getO2().get(0));
    assertEquals(new Date(2031, 11, 31), intervals.getO2().get(15));

    // check when last month is December
    end = new Date(2031, 11, 1);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.MULTI_ANNUALLY, intervals.getO1());
    assertEquals(16, intervals.getO2().size());
    assertEquals(new Date(2001, 11, 31), intervals.getO2().get(0));
    assertEquals(new Date(2031, 11, 31), intervals.getO2().get(15));
  }

  @Test
  public void testChartService_GetChartInterval_Annually() {
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.ANNUALLY, intervals.getO1());
    assertEquals(21, intervals.getO2().size());
    for (int i = 0; i < (end.getYear() - start.getYear() - 1); i++) {
      assertEquals(new Date(2000 + i, 11, 31), intervals.getO2().get(i));
    }
  }

  @Test
  public void testChartService_GetChartInterval_Annually_Minimum() {
    start = new Date(2000, 11, 1);
    end = new Date(2005, 11, 1);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.ANNUALLY, intervals.getO1());
    assertEquals(6, intervals.getO2().size());
    for (int i = 0; i < end.getYear() - start.getYear(); i++) {
      assertEquals(new Date(2000 + i, 11, 31), intervals.getO2().get(i));
    }
  }

  @Test
  public void testChartService_GetChartInterval_Annually_Maximum() {
    start = new Date(2000, 11, 1);
    end = new Date(2030, 11, 1);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.ANNUALLY, intervals.getO1());
    assertEquals(31, intervals.getO2().size());
    for (int i = 0; i < end.getYear() - start.getYear(); i++) {
      assertEquals(new Date(2000 + i, 11, 31), intervals.getO2().get(i));
    }
  }

  //</editor-fold>

  //<editor-fold desc="ChartService - Testing Intervals - months">

  @Test
  public void testChartService_GetChartInterval_Quarter_Annually() {
    end = new Date(2002, 11, 1);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.QUARTER_ANNUALLY, intervals.getO1());
    assertEquals(12, intervals.getO2().size());
    assertEquals(new Date(2000, 2, 31), intervals.getO2().get(0));
    assertEquals(new Date(2002, 11, 31), intervals.getO2().get(11));
  }

  @Test
  public void testChartService_GetChartInterval_Monthly() {
    start = new Date(2000, 0, 1);
    end = new Date(2000, 11, 1);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.MONTHLY, intervals.getO1());
    assertEquals(12, intervals.getO2().size());

    checkMonthsOfYear(intervals.getO2(), 2000);
  }

  //</editor-fold>

  //<editor-fold desc="ChartService - Testing Intervals - days">

  @Test
  public void testChartService_GetChartInterval_Daily() {
    start = new Date(2000, 0, 1);
    end = new Date(2000, 0, 10);
    intervals = chartService.getChartInterval(start, end);

    assertEquals(eChartInterval.DAILY, intervals.getO1());
    assertEquals(10, intervals.getO2().size());
    assertEquals(new Date(2000, 0, 1), intervals.getO2().get(0));
    assertEquals(new Date(2000, 0, 2), intervals.getO2().get(1));
    assertEquals(new Date(2000, 0, 3), intervals.getO2().get(2));
    assertEquals(new Date(2000, 0, 4), intervals.getO2().get(3));
    assertEquals(new Date(2000, 0, 5), intervals.getO2().get(4));
    assertEquals(new Date(2000, 0, 6), intervals.getO2().get(5));
    assertEquals(new Date(2000, 0, 7), intervals.getO2().get(6));
    assertEquals(new Date(2000, 0, 8), intervals.getO2().get(7));
    assertEquals(new Date(2000, 0, 9), intervals.getO2().get(8));
    assertEquals(new Date(2000, 0, 10), intervals.getO2().get(9));
  }

  @Test
  public void testChartService_GetChartInterval_LessThanMinimumInterval()
      throws IllegalArgumentException {
    start = new Date(2000, 0, 1);
    end = new Date(2000, 0, 3);

    try {
      intervals = chartService.getChartInterval(start, end);
    } catch (IllegalArgumentException e) {
      assertEquals("Start date and end date need to be apart by "
          + "at least the minimum interval value: 5.", e.getMessage());
    }
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  private void checkMonthsOfYear(List<Date> dates, int year) {
    assertEquals(new Date(year, 0, 31), dates.get(0));

    if (year % 4 == 0) {
      assertEquals(new Date(year, 1, 29), dates.get(1));
    } else {
      assertEquals(new Date(year, 1, 28), dates.get(1));
    }

    assertEquals(new Date(year, 2, 31), dates.get(2));
    assertEquals(new Date(year, 3, 30), dates.get(3));
    assertEquals(new Date(year, 4, 31), dates.get(4));
    assertEquals(new Date(year, 5, 30), dates.get(5));
    assertEquals(new Date(year, 6, 31), dates.get(6));
    assertEquals(new Date(year, 7, 31), dates.get(7));
    assertEquals(new Date(year, 8, 30), dates.get(8));
    assertEquals(new Date(year, 9, 31), dates.get(9));
    assertEquals(new Date(year, 10, 30), dates.get(10));
    assertEquals(new Date(year, 11, 31), dates.get(11));
  }

  //</editor-fold>


}

