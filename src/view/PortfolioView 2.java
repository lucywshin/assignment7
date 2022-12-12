package view;

import common.Utils;
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

/**
 * An implementation of a view to show the output and prompts to the user regarding the
 * application.
 */
public class PortfolioView implements IPortfolioView {

  //<editor-fold desc="Display helper methods">

  private void displayPageHeading(StringBuilder stringToDisplay, String pageHeading) {
    stringToDisplay.append(pageHeading).append("\n");
    stringToDisplay.append("\n");
  }

  private void displayError(StringBuilder stringToDisplay, String errorMessage) {
    if (errorMessage != null && !errorMessage.isEmpty()) {
      stringToDisplay.append("Error: ");
      stringToDisplay.append(errorMessage);
      stringToDisplay.append("\n\n");
    }
  }

  private <T extends IAbstractPortfolio> void displayAbstractPortfolios(
      StringBuilder stringToDisplay,
      List<Pair<Integer, T>> portfolios) {
    for (Pair<Integer, T> p : portfolios) {
      stringToDisplay
          .append("\t")
          .append(p.getO1() + 1).append(". ").append(p.getO2().getName())
          .append("\n");
    }
  }

  private void displayDividerLine(StringBuilder stringToDisplay) {
    stringToDisplay.append("\n");
    stringToDisplay.append("------------------------------------------------------------\n");
  }

  private void displayPromptsAndInput(StringBuilder stringToDisplay, List<String> prompts) {
    for (String p : prompts) {
      stringToDisplay.append(p).append("\n");
    }

    stringToDisplay.append("\n");
    stringToDisplay.append("Your Input:");
  }

  private void displayPageHeader(StringBuilder stringToDisplay,
      String pageHeading, String errorMessage) {
    this.displayPageHeading(stringToDisplay, pageHeading);
    this.displayError(stringToDisplay, errorMessage);
  }

  private void displayPageMenu(StringBuilder stringToDisplay, List<String> prompts) {
    this.displayDividerLine(stringToDisplay);
    this.displayPromptsAndInput(stringToDisplay, prompts);
  }

  //</editor-fold>

  //<editor-fold desc="Normal portfolio pages">

  @Override
  public String displayHomePage(List<Pair<Integer, IPortfolio>> portfolios,
      List<Pair<Integer, IFlexiblePortfolio>> flexiblePortfolios, List<String> prompts,
      String errorMessage) {
    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, "Home Page", errorMessage);

    // content
    stringToDisplay.append("Available Regular Portfolios:\n");
    this.displayAbstractPortfolios(stringToDisplay, portfolios);

    stringToDisplay.append("Available Flexible Portfolios:\n");
    this.displayAbstractPortfolios(stringToDisplay, flexiblePortfolios);

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public <T extends IAbstractPortfolio> String displayGenericPortfolioPromptPage(String pageName,
      List<Pair<Integer, T>> portfolios, List<String> prompts, String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay,
        pageName + " Portfolio Prompt", errorMessage);

    // content
    stringToDisplay.append("Select a portfolio:\n");
    this.displayAbstractPortfolios(stringToDisplay, portfolios);

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayCompositionResultPage(Pair<String, List<String>> portfolioComposition,
      List<String> prompts, String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, "Composition Result", errorMessage);

    // content
    stringToDisplay.append("The portfolio ")
        .append("\"").append(portfolioComposition.getO1()).append("\"")
        .append(" is composed of:\n");

    for (String c : portfolioComposition.getO2()) {
      stringToDisplay.append(c).append("\n");
    }

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayValueDatePromptPage(String portfolioName, List<String> prompts,
      String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, "Value Date Prompt", errorMessage);

    // content
    stringToDisplay.append("Enter the date for which you would like to view the \"")
        .append(portfolioName + "\"")
        .append(" portfolio's value.\n");

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayValueResultPage(String portfolioName,
      Pair<BigDecimal, List<IPortfolioStockValue>> portfolioStockValues, List<String> prompts,
      String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, "Value Result", errorMessage);

    // content
    stringToDisplay
        .append("The portfolio ")
        .append("\"").append(portfolioName).append("\"")
        .append(" contains the following stocks:\n");

    stringToDisplay
        .append("The value of the portfolio: ")
        .append(portfolioStockValues.getO1().equals(new BigDecimal(0)) ? "0" :
            portfolioStockValues.getO1())
        .append("\n");

    for (IPortfolioStockValue psv : portfolioStockValues.getO2()) {
      stringToDisplay
          .append("Symbol: ").append(psv.getSymbol())
          .append(", Name: ").append(psv.getName())
          .append(", Volume: ").append(psv.getVolume())
          .append(", Value: ")
          .append(psv.getValue().equals(new BigDecimal(0)) ? "0" : psv.getValue())
          .append("\n");
    }

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayAddPortfolioNamePromptPage(List<String> prompts, String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay,
        "Add Portfolio - Name Prompt", errorMessage);

    // content
    stringToDisplay.append("Enter the name of your portfolio.\n");

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayAddPortfolioStocksPromptPage(String name,
      List<IPortfolioStock> currentPortfolioStocks,
      List<String> prompts, String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay,
        "Add Portfolio - Stocks Prompt", errorMessage);

    // content
    stringToDisplay.append("Enter in the stocks to be added to the \"")
        .append(name)
        .append("\" portfolio.\n");
    stringToDisplay.append("\n");

    if (!currentPortfolioStocks.isEmpty()) {
      stringToDisplay.append("Below are the stocks currently in the portfolio:\n");
      for (IPortfolioStock ps : currentPortfolioStocks) {
        stringToDisplay.append("Symbol: ").append(ps.getSymbol()).append(", Name: ")
            .append(ps.getName()).append(", Volume: ").append(ps.getVolume()).append("\n");
      }
    }

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayGenericPathPromptPage(String pageName, List<String> prompts,
      String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, pageName + " Prompt", errorMessage);

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayGenericPathPromptResultPage(String pageName, String filePath,
      boolean isSuccessful, List<String> prompts, String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, pageName + " Result", errorMessage);

    // content
    if (pageName.equals("Import")) {
      if (isSuccessful) {
        stringToDisplay.append("File successfully imported from: ")
            .append(filePath)
            .append("\n");
      } else {
        stringToDisplay.append("Provide the file path to the file being imported.\n");
      }
    } else if (pageName.equals("Export")) {
      if (isSuccessful) {
        stringToDisplay
            .append("File successfully exported to: ")
            .append(filePath)
            .append("\n");
      } else {
        stringToDisplay.append("Provide the file path for the file being exported.\n");
      }
    }

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayGenericResultPage(String pageName, boolean isSuccessful,
      List<String> prompts, String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, pageName + " Result", errorMessage);

    // content
    if (isSuccessful) {
      stringToDisplay.append("Operation Successful! \n");
    } else {
      stringToDisplay.append("Operation failed!.\n");
    }

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayGenericDatePromptPage(String pageName, List<String> prompts,
      String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, pageName, errorMessage);

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  //</editor-fold>

  //<editor-fold desc="Flexible portfolio pages">

  @Override
  public String displayFlexiblePortfolioStocksPromptPage(String portfolioName, String actionName,
      List<IObservableFlexiblePortfolioStock> currentPortfolioStocks,
      List<String> prompts, String errorMessage) {

    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay,
        "Add Portfolio - Stocks Prompt", errorMessage);

    // content
    stringToDisplay.append("Enter in the stocks to ")
        .append(actionName)
        .append(" in the \"")
        .append(portfolioName)
        .append("\" portfolio.\n");

    if (!currentPortfolioStocks.isEmpty()) {
      stringToDisplay.append("Below are the stocks currently in the portfolio:\n");
      for (IObservableFlexiblePortfolioStock ps : currentPortfolioStocks) {
        stringToDisplay
            .append("Symbol: ").append(ps.getSymbol())
            .append(", Name: ").append(ps.getName())
            .append(", Volume: ").append(ps.getVolume())
            .append("\n");

        for (var transaction : ps.getTransactions()) {
          stringToDisplay.append("Transaction: ")
              .append("Date: ").append(Utils.convertDateToDefaultStringFormat(transaction.getO1()))
              .append(", Volume: ").append(transaction.getO2().getO1())
              .append(", Commission Fees: ").append(transaction.getO2().getO2())
              .append("\n");
        }
      }
    }

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayFlexiblePortfolioCostBasisPage(String portfolioName, BigDecimal costBasis,
      List<String> prompts, String errorMessage) {
    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay,
        "Cost Basis", errorMessage);

    // content
    stringToDisplay.append("Cost Basis for \"").append(portfolioName).append("\" portfolio: $")
        .append(costBasis)
        .append("\n");

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayFlexiblePortfolioPerformancePage(String portfolioName, Date startDate,
      Date endDate, IChart chart,
      List<String> prompts, String errorMessage) {
    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, "Performance Result", errorMessage);

    // content
    stringToDisplay.append(chart.getTitle())
        .append("\n");

    String asterisk = "*";

    for (int i = 0; i < chart.getDataPoints().size(); i++) {
      stringToDisplay.append(chart.getDataPoints().get(i).getO1())
          .append(": ")
          .append(asterisk.repeat(chart.getDataPoints().get(i).getO2()))
          .append("\n");
    }

    stringToDisplay.append("\n")
        .append("Base: $" + chart.getScaleBase())
        .append("\n")
        .append("Scale: * = $" + chart.getScaleDelta());

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  @Override
  public String displayFlexiblePortfolioCommissionFeePromptPage(BigDecimal currentCommissionFee,
      List<String> prompts,
      String errorMessage) {
    StringBuilder stringToDisplay = new StringBuilder();

    // header
    this.displayPageHeader(stringToDisplay, "Commission Fee Prompt", errorMessage);

    // content
    stringToDisplay.append("Current Commission Fee: $").append(currentCommissionFee).append("\n");
    stringToDisplay.append("Enter the commission fee you would like to set.\n");

    // menu
    this.displayPageMenu(stringToDisplay, prompts);

    return stringToDisplay.toString();
  }

  //</editor-fold>
}
