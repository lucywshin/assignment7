package controller;

import common.pair.Pair;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.IPortfolioModel;
import model.portfolio.IFlexiblePortfolio;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.StockDataSourceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import view.IJFrameView;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A test class for the JFrameController class utilizing a Mockito mock model
 * and a Mockito mock view.
 */
@RunWith(MockitoJUnitRunner.class)
public class JFrameControllerTest {

  //<editor-fold desc="State Variables">

  @InjectMocks
  private JFrameController controller;

  @Mock
  private IPortfolioModel model;

  @Mock
  private IJFrameView view;

  //</editor-fold>

  //<editor-fold desc="Setup">

  @Before
  public void setUp() {
    controller.setView(view);
  }

  //</editor-fold>

  @Test
  public void testLoadPortfolioList()  {

    List<Pair<Integer, IFlexiblePortfolio>> portfolioNames = new ArrayList<>();
    when(model.getAllFlexiblePortfolios()).thenReturn(portfolioNames);

    controller.loadPortfolioList();

    verify(model, times(1)).getAllFlexiblePortfolios();
    verify(view, times(1)).loadPortfoliosList(any());
  }

  @Test
  public void testCreatePortfolio() throws InstantiationException {

    when(model.createFlexiblePortfolio("test")).thenReturn(anyInt());

    controller.createPortfolio("test");

    verify(model, times(1)).createFlexiblePortfolio("test");
    verify(view, times(1)).createPortfolio("test");
  }

  @Test
  public void testImportPortfolios() throws IOException, InstantiationException {

    doThrow(new IOException()).when(model).importFlexiblePortfolios("filePath");

    controller.importPortfolios("filePath");

    verify(model, times(1)).importFlexiblePortfolios("filePath");
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testExportPortfolios() throws IOException {

    doThrow(new IOException()).when(model).exportFlexiblePortfolios("filePath");

    controller.importPortfolios("filePath");

    verify(model, times(1)).exportFlexiblePortfolios("filePath");
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testLoadStocksForPortfolio() throws NotImplementedException {

    when(model.getFlexiblePortfolio(any())).thenReturn(any());

    controller.loadStocksForPortfolio(any());

    verify(model, times(1)).getFlexiblePortfolio(any());
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testBuyStocksForPortfolio() throws StockDataSourceException {

    List<IObservableFlexiblePortfolioStock> list = new ArrayList<>();
    Pair<Integer, List<IObservableFlexiblePortfolioStock>> pair
        = new Pair<>(0, list);
        
    when(model.buyStocksForFlexiblePortfolio(any(), any(), any())).thenReturn(pair);

    controller.buyStocksForPortfolio(any(), any(), any(), any(), any());

    verify(model,
        times(1)).buyStocksForFlexiblePortfolio(any(), any(), any());
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testSellStocksForPortfolio() throws StockDataSourceException {

    List<IObservableFlexiblePortfolioStock> list = new ArrayList<>();
    Pair<Integer, List<IObservableFlexiblePortfolioStock>> pair
        = new Pair<>(0, list);

    when(model.sellStocksForFlexiblePortfolio(any(), any(), any())).thenReturn(pair);

    controller.sellStocksForPortfolio(any(), any(), any(), any(), any());

    verify(model,
        times(1)).sellStocksForFlexiblePortfolio(any(), any(), any());
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testGetPortfolioComposition() {

    List<Pair<String, BigDecimal>> list = new ArrayList<>();
    Pair<String, List<Pair<String, BigDecimal>>> composition = new Pair<>("", list);

    when(model.getFlexiblePortfolioComposition(any(), any())).thenReturn(composition);

    controller.getPortfolioComposition(any(), any());

    verify(model,
        times(1)).getFlexiblePortfolioComposition(any(), any());
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testGetPortfolioValue() throws StockDataSourceException {

    List<IPortfolioStockValue> list = new ArrayList<>();
    Pair<BigDecimal, List<IPortfolioStockValue>> pair = new Pair<>(new BigDecimal(0), list);

    when(model.getFlexiblePortfolioValue(any(), any())).thenReturn(pair);

    controller.getPortfolioValue(any(), any());

    verify(model, times(1)).getFlexiblePortfolioValue(any(), any());
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testGetPortfolioCostBasis() throws StockDataSourceException {

    BigDecimal costBasis = new BigDecimal(0);

    when(model.getCostBasis(any(), any())).thenReturn(any());

    controller.getPortfolioCostBasis(any(), any());

    verify(model, times(1)).getCostBasis(any(), any());
    verify(view, times(1)).loadPortfolioCostBasis(any());
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testLoadDollarCostInvestmentsForPortfolio() {

    when(model.getFlexiblePortfolio(any())).thenReturn(any());

    controller.loadDollarCostInvestmentsForPortfolio(any());

    verify(model, times(1)).getFlexiblePortfolio(any());
    verify(view, times(1)).loadPortfolioDollarCostInvestments(any());
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testAddDollarCostInvestmentForPortfolio() throws StockDataSourceException {

    List<IObservableFlexiblePortfolioStock> list = new ArrayList<>();
    Pair<Integer, List<IObservableFlexiblePortfolioStock>> pair = new Pair<>(1, list);

    when(model.addDollarCostInvestment(
        any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(pair);

    controller.addDollarCostInvestmentForPortfolio(
        any(), any(), any(), any(), any(), any(), any(), any(), any());

    verify(model, times(1))
        .addDollarCostInvestment(any(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(view, times(1)).displayErrorMessage(null);
  }

  @Test
  public void testLoadPerformanceChart() throws StockDataSourceException {

    when(model.getPerformanceChart(any(), any(), any())).thenReturn(any());

    controller.loadPerformanceChart(any(), any(), any());

    verify(model, times(1)).getPerformanceChart(any(), any(), any());
    verify(view, times(1)).loadPerformanceChart(any());
    verify(view, times(1)).displayErrorMessage(null);
  }

}
