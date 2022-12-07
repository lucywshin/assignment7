package controller;

import common.pair.Pair;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import model.chart.IChart;
import model.portfolio.IAbstractPortfolio;
import model.portfolio.IFlexiblePortfolio;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolio;
import model.portfolio.IPortfolioStock;
import model.portfolio.IPortfolioStockValue;
import view.IPortfolioView;

class PortfolioViewMock implements IPortfolioView {

  //<editor-fold desc="State variables">

  private final StringBuilder log;
  private final String expectedOutput;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  PortfolioViewMock(StringBuilder log, String expectedOutput) {
    this.log = log;
    this.expectedOutput = expectedOutput;
  }

  //</editor-fold>

  //<editor-fold desc="Static helper methods">

  public static void receivedPortfolioStocksList(StringBuilder log,
      List<IPortfolioStock> portfolioStocks) {
    if (portfolioStocks == null || portfolioStocks.isEmpty()) {
      return;
    }
    for (IPortfolioStock ps : portfolioStocks) {
      log.append("Received portfolio stock: ")
          .append("symbol: ").append(ps.getSymbol()).append(", ")
          .append("name: ").append(ps.getName()).append(", ")
          .append("exchange: ").append(ps.getExchange()).append(", ")
          .append("volume: ").append(ps.getVolume()).append("\n");
    }
  }

  public static void receivedFlexiblePortfolioStocksList(StringBuilder log,
      List<IObservableFlexiblePortfolioStock> portfolioStocks) {
    if (portfolioStocks == null || portfolioStocks.isEmpty()) {
      return;
    }
    // TODO: implement changes for flexible
    for (IObservableFlexiblePortfolioStock ps : portfolioStocks) {
      log.append("Received portfolio stock: ")
          .append("symbol: ").append(ps.getSymbol()).append(", ")
          .append("name: ").append(ps.getName()).append(", ")
          .append("exchange: ").append(ps.getExchange()).append(", ")
          .append("volume: ").append(ps.getVolume()).append("\n");
    }
  }

  public static void receivedPortfolioStockValuesList(StringBuilder log,
      Pair<BigDecimal, List<IPortfolioStockValue>> portfolioStockValues) {
    if (portfolioStockValues == null
        || portfolioStockValues.getO2() == null
        || portfolioStockValues.getO2().isEmpty()) {
      return;
    }
    log.append("Received portfolio value: ").append(portfolioStockValues.getO1()).append("\n");

    for (IPortfolioStockValue ps : portfolioStockValues.getO2()) {
      log.append("Received portfolio stock: ")
          .append("symbol: ").append(ps.getSymbol()).append(", ")
          .append("name: ").append(ps.getName()).append(", ")
          .append("exchange: ").append(ps.getExchange()).append(", ")
          .append("volume: ").append(ps.getVolume())
          .append("value: ").append(ps.getValue())
          .append("\n");
    }
  }

  public static void receivedPortfolioStocks(StringBuilder log, IPortfolio portfolio) {
    if (portfolio == null) {
      return;
    }
    receivedPortfolioStocksList(log, portfolio.getStocks());
  }

  public static void receivedFlexiblePortfolioStocks(StringBuilder log,
      IFlexiblePortfolio portfolio) {
    if (portfolio == null) {
      return;
    }
    receivedFlexiblePortfolioStocksList(log, portfolio.getStocks());
  }

  public static void receivedPortfolios(StringBuilder log,
      List<Pair<Integer, IPortfolio>> portfolios) {
    if (portfolios == null || portfolios.isEmpty()) {
      return;
    }
    for (Pair<Integer, IPortfolio> p : portfolios) {
      log.append("Received portfolio: ")
          .append(p.getO1()).append(" ")
          .append(p.getO2().getName())
          .append("\n");

      receivedPortfolioStocks(log, p.getO2());
    }
  }

  public static void receivedFlexiblePortfolios(StringBuilder log,
      List<Pair<Integer, IFlexiblePortfolio>> portfolios) {
    if (portfolios == null || portfolios.isEmpty()) {
      return;
    }
    for (Pair<Integer, IFlexiblePortfolio> p : portfolios) {
      log.append("Received portfolio: ")
          .append(p.getO1()).append(" ")
          .append(p.getO2().getName())
          .append("\n");

      receivedFlexiblePortfolioStocks(log, p.getO2());
    }
  }

  public static void receivedPortfolio(StringBuilder log, IPortfolio portfolio) {
    if (portfolio == null) {
      return;
    }
    log.append("Received portfolio: ")
        .append(portfolio.getName())
        .append("\n");

    receivedPortfolioStocks(log, portfolio);
  }

  public static void receivedPortfolioName(StringBuilder log, String portfolioName) {
    if (portfolioName == null) {
      return;
    }
    log.append("Received portfolio name: ")
        .append(portfolioName)
        .append("\n");
  }

  public static void receivedPortfolioComposition(StringBuilder log,
      Pair<String, List<String>> portfolioComposition) {
    if (portfolioComposition == null) {
      return;
    }
    log.append("Received portfolio: ")
        .append(portfolioComposition.getO1())
        .append("\n");

    for (String pc : portfolioComposition.getO2()) {
      log.append("Received portfolio stock composition: ")
          .append(pc).append("\n");
    }
  }

  public static void receivedPrompts(StringBuilder log, List<String> prompts) {
    for (String p : prompts) {
      log.append("Received prompt: ").append(p).append("\n");
    }
  }

  public static void receivedErrorMessage(StringBuilder log, String errorMessage) {
    if (errorMessage == null) {
      log.append("Received no error message\n");
    } else {
      log.append("Received error message: ").append(errorMessage).append("\n");
    }
  }

  //</editor-fold>\

  //<editor-fold desc="Core mock methods">

  @Override
  public String displayHomePage(List<Pair<Integer, IPortfolio>> portfolios,
      List<Pair<Integer, IFlexiblePortfolio>> flexiblePortfolios,
      List<String> prompts,
      String errorMessage) {
    log.append("displayed home page\n");
    receivedPortfolios(this.log, portfolios);
    receivedFlexiblePortfolios(this.log, flexiblePortfolios);
    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);

    return this.expectedOutput;
  }

  @Override
  public <T extends IAbstractPortfolio> String displayGenericPortfolioPromptPage(String pageName,
      List<Pair<Integer, T>> portfolios, List<String> prompts, String errorMessage) {
    log.append("displayed ").append(pageName).append(" prompt page\n");
    this.log.append("Received page name: ").append(pageName).append("\n");

    for (var p : portfolios) {
      receivedPortfolioName(this.log, p.getO2().getName());
    }

    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);
    return this.expectedOutput;
  }

  @Override
  public String displayCompositionResultPage(Pair<String, List<String>> portfolioComposition,
      List<String> prompts,
      String errorMessage) {
    log.append("displayed composition result page\n");
    receivedPortfolioComposition(this.log, portfolioComposition);
    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);
    return this.expectedOutput;
  }

  @Override
  public String displayValueDatePromptPage(String portfolioName, List<String> prompts,
      String errorMessage) {
    log.append("displayed value date prompt page\n");
    log.append("displayed value date prompt page\n");
    receivedPortfolioName(this.log, portfolioName);
    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);
    return this.expectedOutput;
  }

  @Override
  public String displayValueResultPage(String portfolioName,
      Pair<BigDecimal, List<IPortfolioStockValue>> portfolioStockValues,
      List<String> prompts,
      String errorMessage) {
    log.append("displayed value result page\n");
    this.log.append("Received portfolio name: ").append(portfolioName).append("\n");
    receivedPortfolioStockValuesList(this.log, portfolioStockValues);
    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);
    return this.expectedOutput;
  }

  @Override
  public String displayAddPortfolioNamePromptPage(List<String> prompts, String errorMessage) {
    log.append("displayed add portfolio name prompt page\n");
    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);
    return this.expectedOutput;
  }

  @Override
  public String displayAddPortfolioStocksPromptPage(String name,
      List<IPortfolioStock> currentPortfolioStocks, List<String> prompts, String errorMessage) {
    log.append("displayed add portfolio stocks prompt page\n");
    this.log.append("Received name: ").append(name).append("\n");
    receivedPortfolioStocksList(this.log, currentPortfolioStocks);
    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);
    return this.expectedOutput;
  }

  @Override
  public String displayGenericResultPage(String pageName, boolean isSuccessful,
      List<String> prompts, String errorMessage) {
    // TODO: implementation pending
    return null;
  }

  @Override
  public String displayGenericDatePromptPage(String pageHeading, List<String> prompts,
      String errorMessage) {
    // TODO: implementation pending
    return null;
  }

  @Override
  public String displayFlexiblePortfolioStocksPromptPage(String portfolioName, String actionName,
      List<IObservableFlexiblePortfolioStock> currentPortfolioStocks, List<String> prompts,
      String errorMessage) {
    // TODO: implementation pending
    return null;
  }

  @Override
  public String displayGenericPathPromptPage(String pageName, List<String> prompts,
      String errorMessage) {
    log.append("displayed ").append(pageName).append(" prompt page\n");
    this.log.append("Received pageName: ").append(pageName).append("\n");
    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);
    return this.expectedOutput;
  }

  @Override
  public String displayGenericPathPromptResultPage(String pageName, String filePath,
      boolean isSuccessful, List<String> prompts, String errorMessage) {
    log.append("displayed ").append(pageName).append(" result page\n");
    this.log.append("Received pageName: ").append(pageName).append("\n");
    this.log.append("Received filePath: ").append(filePath).append("\n");
    this.log.append("Received isSuccessful: ").append(isSuccessful).append("\n");
    receivedPrompts(this.log, prompts);
    receivedErrorMessage(this.log, errorMessage);

    return this.expectedOutput;
  }

  @Override
  public String displayFlexiblePortfolioCostBasisPage(String portfolioName, BigDecimal costBasis,
      List<String> prompts, String errorMessage) {
    // TODO: implementation pending
    return null;
  }

  @Override
  public String displayFlexiblePortfolioPerformancePage(String portfolioName, Date startDate,
      Date endDate, IChart chart, List<String> prompts, String errorMessage) {
    // TODO: implementation pending
    return null;
  }

  @Override
  public String displayFlexiblePortfolioCommissionFeePromptPage(BigDecimal currentCommissionFee,
      List<String> prompts, String errorMessage) {
    // TODO: implementation pending
    return null;
  }

  //</editor-fold>
}
