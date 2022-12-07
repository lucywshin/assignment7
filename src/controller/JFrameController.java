package controller;

import common.pair.Pair;
import common.triplet.Triplet;
import features.IPortfolioManagerFeatures;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import model.IPortfolioModel;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.eRecurringIntervalType;
import view.IJFrameView;

/**
 * A controller to be used with a Java Swing GUI.
 */
public class JFrameController implements IPortfolioManagerFeatures, IGuiController {

  //<editor-fold desc="State variables">

  private final IPortfolioModel model;
  private IJFrameView view;

  private String errorMessage;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Instantiates a controller for the portfolio manager.
   *
   * @param model the model to be used with this controller.
   */
  public JFrameController(IPortfolioModel model) {

    this.model = model;
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public void setView(IJFrameView view) {
    this.view = view;
    this.view.addFeatures(this);
  }

  @Override
  public void loadPortfolioList() {
    List<Pair<Integer, String>> portfolioNames = this.model.getAllFlexiblePortfolios()
        .stream()
        .map(p -> new Pair<>(p.getO1(), p.getO2().getName()))
        .collect(Collectors.toList());

    this.view.loadPortfoliosList(portfolioNames);
  }

  @Override
  public void createPortfolio(String portfolioName) {
    try {
      this.model.createFlexiblePortfolio(portfolioName);
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return;
    }

    this.view.displayErrorMessage(null);
    this.view.createPortfolio(portfolioName);
    this.loadPortfolioList();
  }

  @Override
  public boolean importPortfolios(String filePath) {
    try {
      this.model.importFlexiblePortfolios(filePath);
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    this.loadPortfolioList();
    return true;
  }

  @Override
  public boolean exportPortfolios(String filePath) {
    try {
      this.model.exportFlexiblePortfolios(filePath);
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean loadStocksForPortfolio(int portfolioId) {
    try {
      this.view.loadPortfolioStocksList(this.model.getFlexiblePortfolio(portfolioId).getStocks());
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean buyStocksForPortfolio(int portfolioId, String symbol, BigDecimal volume,
      Date transactionDate, BigDecimal commissionFees) {
    var stock = new Triplet<>(symbol, transactionDate, volume);
    List<Triplet<String, Date, BigDecimal>> stocksParam = new ArrayList<>();

    stocksParam.add(stock);
    try {
      this.model.buyStocksForFlexiblePortfolio(portfolioId, stocksParam, commissionFees);
      if (!this.loadStocksForPortfolio(portfolioId)) {
        return false;
      }
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean sellStocksForPortfolio(int portfolioId, String symbol, BigDecimal volume,
      Date transactionDate, BigDecimal commissionFees) {
    var stock = new Triplet<>(symbol, transactionDate, volume);
    List<Triplet<String, Date, BigDecimal>> stocksParam = new ArrayList<>();

    stocksParam.add(stock);
    try {
      this.model.sellStocksForFlexiblePortfolio(portfolioId, stocksParam, commissionFees);
      if (!this.loadStocksForPortfolio(portfolioId)) {
        return false;
      }
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean getPortfolioComposition(int portfolioId, Date date) {
    try {
      List<Pair<String, BigDecimal>> composition =
          this.model.getFlexiblePortfolioComposition(portfolioId, date).getO2();

      this.view.loadPortfolioComposition(composition);
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean getPortfolioValue(int portfolioId, Date date) {
    try {
      Pair<BigDecimal, List<IPortfolioStockValue>> value =
          this.model.getFlexiblePortfolioValue(portfolioId, date);

      this.view.loadPortfolioValue(value);
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean getPortfolioCostBasis(int portfolioId, Date date) {
    try {
      BigDecimal costBasis = this.model.getCostBasis(portfolioId, date);

      this.view.loadPortfolioCostBasis(costBasis);
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean loadDollarCostInvestmentsForPortfolio(int portfolioId) {
    try {
      this.view.loadPortfolioDollarCostInvestments(
          this.model.getFlexiblePortfolio(portfolioId).getDollarCostInvestments());
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean addDollarCostInvestmentForPortfolio(int portfolioId, Date date, BigDecimal amount,
      List<Pair<String, BigDecimal>> stocksWithPercentage, BigDecimal commissionFee,
      boolean isRecurring, Date endDate, eRecurringIntervalType intervalType,
      Integer intervalDelta) {

    try {
      this.model.addDollarCostInvestment(portfolioId, date, amount, stocksWithPercentage,
          commissionFee, isRecurring, endDate, intervalType, intervalDelta);

      if (!this.loadDollarCostInvestmentsForPortfolio(portfolioId)) {
        return false;
      }
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  @Override
  public boolean loadPerformanceChart(int portfolioId, Date startDate, Date endDate) {
    try {
      this.view.loadPerformanceChart(
          this.model.getPerformanceChart(portfolioId, startDate, endDate));
    } catch (Exception e) {
      this.view.displayErrorMessage(e.getMessage());
      return false;
    }

    this.view.displayErrorMessage(null);
    return true;
  }

  //</editor-fold>
}
