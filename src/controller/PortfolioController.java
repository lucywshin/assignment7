package controller;

import common.Utils;
import common.pair.Pair;
import common.triplet.Triplet;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import model.IPortfolioModel;
import model.chart.IChart;
import model.portfolio.IFlexiblePortfolio;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.StockDataSourceException;
import view.IPortfolioView;

/**
 * A controller class which controls the application and responds to requests from the user or test
 * application.
 */
public class PortfolioController implements IPortfolioController {

  //<editor-fold desc="Inner enums">

  /**
   * An enum containing all supported pages.
   */
  enum ePage {
    // common pages
    HOMEPAGE,

    // regular portfolio pages
    COMPOSITION_PORTFOLIO_PROMPT,
    COMPOSITION_RESULT,

    VALUE_PORTFOLIO_PROMPT,
    VALUE_DATE_PROMPT,
    VALUE_RESULT,

    ADD_PORTFOLIO_NAME_PROMPT,
    ADD_PORTFOLIO_STOCKS_PROMPT,

    EXPORT_PROMPT,
    EXPORT_RESULT,

    IMPORT_PROMPT,
    IMPORT_RESULT,

    // flexible portfolio pages
    FLEXIBLE_COMPOSITION_PORTFOLIO_PROMPT,
    FLEXIBLE_COMPOSITION_DATE_PROMPT,
    FLEXIBLE_COMPOSITION_RESULT,

    FLEXIBLE_VALUE_PORTFOLIO_PROMPT,
    FLEXIBLE_VALUE_DATE_PROMPT,
    FLEXIBLE_VALUE_RESULT,

    FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT,
    FLEXIBLE_ADD_PORTFOLIO_RESULT,

    FLEXIBLE_EXPORT_PROMPT,
    FLEXIBLE_EXPORT_RESULT,

    FLEXIBLE_IMPORT_PROMPT,
    FLEXIBLE_IMPORT_RESULT,

    FLEXIBLE_COMMISSION_FEE_PROMPT,
    FLEXIBLE_COMMISSION_FEE_RESULT,

    FLEXIBLE_BUY_PORTFOLIO_PROMPT,
    FLEXIBLE_BUY_STOCKS_PROMPT,

    FLEXIBLE_SELL_PORTFOLIO_PROMPT,
    FLEXIBLE_SELL_STOCKS_PROMPT,

    FLEXIBLE_COST_BASIS_PORTFOLIO_PROMPT,
    FLEXIBLE_COST_BASIS_DATE_PROMPT,
    FLEXIBLE_COST_BASIS_RESULT,

    FLEXIBLE_PERFORMANCE_PORTFOLIO_PROMPT,
    FLEXIBLE_PERFORMANCE_START_DATE_PROMPT,
    FLEXIBLE_PERFORMANCE_END_DATE_PROMPT,
    FLEXIBLE_PERFORMANCE_RESULT
  }

  //</editor-fold>

  //<editor-fold desc="Inner interfaces">

  /**
   * This interface represents a page in the application.
   */
  interface IPage {

    /**
     * Displays the page.
     */
    void displayPage() throws NotImplementedException;

    /**
     * Gets the prompt for the page.
     *
     * @return a list of strings to be displayed for the page.
     */
    List<String> getPrompts() throws NotImplementedException;

    /**
     * Returns the next state of the application after processing the input.
     *
     * @param currentInput the current input to be processed.
     * @return a triplet with the next page, the input to be interpreted, and the error message for
     *     the next page.
     */
    ePage processInputAndGetNextPage(String currentInput) throws NotImplementedException;

    /**
     * Gets the state of the page.
     *
     * @return a {@code IPageState} object.
     */
    IPageState getPageState();
  }

  /**
   * This interface represents the state of the page.
   */
  interface IPageState {

    /**
     * Gets the date being stored.
     *
     * @return the date stored.
     */
    Date getDate();

    /**
     * Sets the date to the provided date.
     *
     * @param date the new date.
     */
    void setDate(Date date);

    /**
     * Gets the portfolioId being stored.
     *
     * @return the portfolioId stored.
     */
    int getPortfolioId();

    /**
     * Sets the portfolio id.
     *
     * @param portfolioId the new portfolio id.
     */
    void setPortfolioId(int portfolioId);

    /**
     * Gets the status of the application.
     *
     * @return the isApplicationRunning stored.
     */
    boolean isApplicationRunning();

    /**
     * Sets the application is running status.
     *
     * @param applicationRunning the new application is running status.
     */
    void setApplicationRunning(boolean applicationRunning);

    /**
     * Gets the error message.
     *
     * @return the error message.
     */
    String getErrorMessage();

    /**
     * Sets the error message.
     *
     * @param errorMessage the new error message.
     */
    void setErrorMessage(String errorMessage);

    /**
     * Gets the date being stored.
     *
     * @return the date stored.
     */
    String getToInterpretInput();

    /**
     * Sets the string input to be interpreted.
     *
     * @param toInterpretInput the string input to be interpreted.
     */
    void setToInterpretInput(String toInterpretInput);

    /**
     * Gets the current page enum.
     *
     * @return the current page enum.
     */
    ePage getCurrentPage();

    /**
     * Sets the current page.
     *
     * @param currentPage the new current page.
     */
    void setCurrentPage(ePage currentPage);
  }

  //</editor-fold>

  //<editor-fold desc="Inner classes">

  /**
   * The page state of the application.
   */
  class PageState implements IPageState {

    private String toInterpretInput;
    private String errorMessage;
    private boolean isApplicationRunning;
    private int portfolioId;
    private Date date;
    private ePage currentPage;

    /**
     * Instantiates a page state object.
     */
    PageState() {
      this.toInterpretInput = null;
      this.errorMessage = null;
      this.isApplicationRunning = true;
      this.portfolioId = -1;
      this.date = null;
      this.currentPage = ePage.HOMEPAGE;
    }

    /**
     * Instantiates an object which contains the state of the page.
     *
     * @param toInterpretInput     the input to be interpreted.
     * @param errorMessage         the error message.
     * @param isApplicationRunning whether the application is running.
     * @param portfolioId          the portfolio id being stored.
     * @param date                 the date being stored.
     * @param currentPage          the current page.
     */
    PageState(String toInterpretInput, String errorMessage, boolean isApplicationRunning,
        int portfolioId, Date date, ePage currentPage) {
      this.toInterpretInput = toInterpretInput;
      this.errorMessage = errorMessage;
      this.isApplicationRunning = isApplicationRunning;
      this.portfolioId = portfolioId;
      this.date = date;
      this.currentPage = currentPage;
    }

    @Override
    public Date getDate() {
      return date;
    }

    @Override
    public void setDate(Date date) {
      this.date = date;
    }

    @Override
    public int getPortfolioId() {
      return portfolioId;
    }

    @Override
    public void setPortfolioId(int portfolioId) {
      this.portfolioId = portfolioId;
    }

    @Override
    public boolean isApplicationRunning() {
      return isApplicationRunning;
    }

    @Override
    public void setApplicationRunning(boolean applicationRunning) {
      isApplicationRunning = applicationRunning;
    }

    @Override
    public String getErrorMessage() {
      return errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
    }

    @Override
    public String getToInterpretInput() {
      return toInterpretInput;
    }

    @Override
    public void setToInterpretInput(String toInterpretInput) {
      this.toInterpretInput = toInterpretInput;
    }

    @Override
    public ePage getCurrentPage() {
      return currentPage;
    }

    @Override
    public void setCurrentPage(ePage currentPage) {
      this.currentPage = currentPage;
    }
  }

  /**
   * An abstract page which contains the common methods and validation methods.
   */
  abstract class AbstractPage implements IPage {

    //<editor-fold desc="State variables">

    final Appendable out;
    IPortfolioModel model;
    IPortfolioView view;
    IPageState pageState;
    final String toInterpretInput;
    final boolean isApplicationRunning;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    /**
     * An abstract constructor to be implemented in each of the pages.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    AbstractPage(IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      this.model = model;
      this.view = view;
      this.out = out;
      this.pageState = pageState;
      this.toInterpretInput = this.pageState.getToInterpretInput();
      this.isApplicationRunning = this.pageState.isApplicationRunning();
    }

    //</editor-fold>

    //<editor-fold desc="Helper methods">

    /**
     * Posts the provided output to the output stream provided and sets the error message to
     * {@code null}.
     *
     * @param output the output to be displayed in the {@code OutputStream}.
     */
    void processOutput(String output) {
      try {
        this.out.append(output);
      } catch (IOException e) {
        System.out.println("An error occurred with processing output!");
      }
      this.pageState.setErrorMessage(null);
    }

    /**
     * Checks whether the provided string is a valid portfolio id.
     *
     * @param currentInput the string to be validated.
     * @return a boolean indicating whether the provided string is a portfolio id.
     */
    boolean isValidIdRequested(String currentInput) {
      boolean validIdRequested = false;
      if (Utils.isValidNumberInput(currentInput)) {
        int numberInput = Utils.convertStringToNumber(currentInput);
        if (numberInput > 0 & this.model.getPortfolioCount() >= numberInput) {
          validIdRequested = true;
        }
      }
      return validIdRequested;
    }

    /**
     * Checks whether the provided string is a valid flexible portfolio id.
     *
     * @param currentInput the string to be validated.
     * @return a boolean indicating whether the provided string is a flexible portfolio id.
     */
    boolean isValidFlexibleIdRequested(String currentInput) {
      boolean validIdRequested = false;
      if (Utils.isValidNumberInput(currentInput)) {
        int numberInput = Utils.convertStringToNumber(currentInput);
        if (numberInput > 0 & this.model.getFlexiblePortfolioCount() >= numberInput) {
          validIdRequested = true;
        }
      }
      return validIdRequested;
    }

    //</editor-fold>

    //<editor-fold desc="Core methods">

    @Override
    public IPageState getPageState() {
      return this.pageState;
    }

    //</editor-fold>
  }

  /**
   * The home page of the application.
   */
  class HomePage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    HomePage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() {
      this.pageState.setToInterpretInput(null);
      List<String> prompts = getPrompts();
      this.processOutput(
          this.view.displayHomePage(
              this.model.getAllPortfolios(),
              this.model.getAllFlexiblePortfolios(),
              prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Regular Portfolio options:");
      prompts.add("(A) Add a portfolio.");
      prompts.add("(C) Request Composition for a portfolio.");
      prompts.add("(V) Request Value for a portfolio.");
      prompts.add("(E) Export all portfolios.");
      prompts.add("(I) Import portfolios.");
      prompts.add("");
      prompts.add("Flexible Portfolio options:");
      prompts.add("(FA) Add a flexible portfolio.");
      prompts.add("(FC) Request Composition for a flexible portfolio.");
      prompts.add("(FV) Request Value for a flexible portfolio.");
      prompts.add("(FE) Export all flexible portfolios.");
      prompts.add("(FI) Import flexible portfolios.");
      prompts.add("(FB) Buy stocks for a flexible portfolio.");
      prompts.add("(FS) Sell stocks for a flexible portfolio.");
      prompts.add("(FCB) Request Cost Basis for a flexible portfolio.");
      prompts.add("(FP) Request Performance chart for a flexible portfolio.");
      prompts.add("(FCF) Change Commission Fees.");
      prompts.add("");

      prompts.add("(X) Exit Application.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      switch (currentInput) {
        // regular portfolio options
        case "A":
          return ePage.ADD_PORTFOLIO_NAME_PROMPT;
        case "C":
          if (this.model.getPortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no portfolios in the application to fetch data for!");
            return ePage.HOMEPAGE;
          }
          return ePage.COMPOSITION_PORTFOLIO_PROMPT;
        case "V":
          if (this.model.getPortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no portfolios in the application to fetch data for!");
            return ePage.HOMEPAGE;
          }
          return ePage.VALUE_PORTFOLIO_PROMPT;
        case "E":
          if (this.model.getPortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no portfolios in the application to fetch data for!");
            return ePage.HOMEPAGE;
          }
          return ePage.EXPORT_PROMPT;
        case "I":
          return ePage.IMPORT_PROMPT;

        // flexible portfolio options
        case "FA":
          return ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT;
        case "FC":
          if (this.model.getFlexiblePortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no flexible portfolios in the application to "
                    + "fetch data for!");
            return ePage.HOMEPAGE;
          }
          return ePage.FLEXIBLE_COMPOSITION_PORTFOLIO_PROMPT;
        case "FV":
          if (this.model.getFlexiblePortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no flexible portfolios in the application to "
                    + "fetch data for!");
            return ePage.HOMEPAGE;
          }
          return ePage.FLEXIBLE_VALUE_PORTFOLIO_PROMPT;
        case "FE":
          if (this.model.getFlexiblePortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no flexible portfolios in the application to "
                    + "fetch data for!");
            return ePage.HOMEPAGE;
          }
          return ePage.FLEXIBLE_EXPORT_PROMPT;
        case "FI":
          return ePage.FLEXIBLE_IMPORT_PROMPT;
        case "FB":
          if (this.model.getFlexiblePortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no flexible portfolios in the application to "
                    + "buy stocks for!");
            return ePage.HOMEPAGE;
          }
          return ePage.FLEXIBLE_BUY_PORTFOLIO_PROMPT;
        case "FS":
          if (this.model.getFlexiblePortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no flexible portfolios in the application to "
                    + "sell stocks for!");
            return ePage.HOMEPAGE;
          }
          return ePage.FLEXIBLE_SELL_PORTFOLIO_PROMPT;

        case "FCB":
          if (this.model.getFlexiblePortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no flexible portfolios in the application to "
                    + "fetch cost basis for!");
            return ePage.HOMEPAGE;
          }
          return ePage.FLEXIBLE_COST_BASIS_PORTFOLIO_PROMPT;

        case "FP":
          if (this.model.getFlexiblePortfolioCount() == 0) {
            this.pageState.setErrorMessage(
                "State error: There are no flexible portfolios in the application to "
                    + "fetch performance chart for!");
            return ePage.HOMEPAGE;
          }
          return ePage.FLEXIBLE_PERFORMANCE_PORTFOLIO_PROMPT;

        case "FCF":
          return ePage.FLEXIBLE_COMMISSION_FEE_PROMPT;

        case "X":
          this.pageState.setApplicationRunning(false);
          return ePage.HOMEPAGE;

        default:
          this.pageState.setErrorMessage("Input error: Invalid input provided!");
          return ePage.HOMEPAGE;
      }
    }
  }

  /**
   * The add portfolio Name prompt page.
   */
  class AddPortfolioNamePromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    AddPortfolioNamePromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      this.pageState.setToInterpretInput(null);
      List<String> prompts = getPrompts();
      if (this.pageState.getCurrentPage() == ePage.ADD_PORTFOLIO_NAME_PROMPT ||
          this.pageState.getCurrentPage() == ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT) {
        this.processOutput(
            this.view.displayAddPortfolioNamePromptPage(prompts, this.pageState.getErrorMessage()));
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() throws NotImplementedException {
      List<String> prompts = new ArrayList<>();
      if (this.pageState.getCurrentPage() == ePage.ADD_PORTFOLIO_NAME_PROMPT) {
        prompts.add("Enter the name for the regular portfolio you would like to create.");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT) {
        prompts.add("Enter the name for the flexible portfolio you would like to create.");
      } else {
        throw new NotImplementedException("Prompt for page is not implemented!");
      }
      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) throws NotImplementedException {
      if ("H".equals(currentInput)) {
        return ePage.HOMEPAGE;
      }
      if (Utils.isValidWordsInput(currentInput)) {
        this.pageState.setToInterpretInput(currentInput);

        if (this.pageState.getCurrentPage() == ePage.ADD_PORTFOLIO_NAME_PROMPT) {
          return ePage.ADD_PORTFOLIO_STOCKS_PROMPT;
        } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT) {
          return ePage.FLEXIBLE_ADD_PORTFOLIO_RESULT;
        } else {
          throw new NotImplementedException("Action for page is not implemented!");
        }
      } else {
        this.pageState.setErrorMessage("Input error: Invalid name entered!");

        if (this.pageState.getCurrentPage() == ePage.ADD_PORTFOLIO_NAME_PROMPT) {
          return ePage.ADD_PORTFOLIO_NAME_PROMPT;
        } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT) {
          return ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT;
        } else {
          throw new NotImplementedException("Action for page is not implemented!");
        }
      }
    }
  }

  /**
   * The add portfolio stocks prompt page.
   */
  class AddPortfolioStocksPromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    AddPortfolioStocksPromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    private void displayRegularPortfolioPage() {
      // previous state was Add portfolio Name prompt
      if (Utils.isValidWordsInput(this.toInterpretInput)) {
        String name = this.toInterpretInput.trim();
        this.model.setPortfolioBuilderName(name);
      }
      // previous state was Add portfolio stocks prompt
      else if (Utils.isValidStockInputFormat(this.toInterpretInput)) {
        var rawStocks = Utils.convertStringToStocksFormat(this.toInterpretInput);
        for (var rawStock : rawStocks) {
          // get stock details from model and add the stock
          // to the list of stocks in the portfolio being built.

          try {
            this.model.addStocksToPortfolioBuilder(
                List.of(new Pair<>(rawStock.getO1(), new BigDecimal(rawStock.getO2()))));
          } catch (IllegalArgumentException e) {
            this.pageState.setErrorMessage(e.getMessage());
          }
        }
      }

      this.pageState.setToInterpretInput(null);

      List<String> prompts = getPrompts();
      this.processOutput(
          this.view.displayAddPortfolioStocksPromptPage(
              this.model.getPortfolioBuilderName(),
              this.model.getPortfolioBuilderStocks(),
              prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public void displayPage() throws NotImplementedException {
      if (this.pageState.getCurrentPage() == ePage.ADD_PORTFOLIO_STOCKS_PROMPT) {
        this.displayRegularPortfolioPage();
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Enter the stocks for the portfolio you would like to create "
          + "(enter in <Stock1 Symbol>,<Stock1 Volume>;<Stock2 Symbol>,<Stock2 Volume>;... "
          + "format).");
      prompts.add("(D) done adding stocks.");
      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      if ("H".equals(currentInput)) {
        return ePage.HOMEPAGE;
      }
      if ("D".equals(currentInput)) {
        try {
          this.pageState.setToInterpretInput(Integer.toString(this.model.buildPortfolio() + 1));
        } catch (InstantiationException e) {
          this.pageState.setErrorMessage("Input error: The portfolio needs at least one stock!");
          return ePage.ADD_PORTFOLIO_STOCKS_PROMPT;
        }
        return ePage.COMPOSITION_RESULT;
      }
      if (Utils.isValidStockInputFormat(currentInput)) {
        this.pageState.setToInterpretInput(currentInput);
      } else {
        this.pageState.setErrorMessage("Input error: Invalid stocks entered!");
      }
      return ePage.ADD_PORTFOLIO_STOCKS_PROMPT;
    }
  }

  /**
   * The commission fee prompt page.
   */
  class CommissionFeePromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    CommissionFeePromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      this.pageState.setToInterpretInput(null);
      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMMISSION_FEE_PROMPT) {
        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayFlexiblePortfolioCommissionFeePromptPage(commissionFee,
                prompts, this.pageState.getErrorMessage()));
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Enter the new Commission fees:");
      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      if ("H".equals(currentInput)) {
        return ePage.HOMEPAGE;
      }
      if (Utils.isValidFloatNumberInput(currentInput)) {
        this.pageState.setToInterpretInput(currentInput);
        return ePage.FLEXIBLE_COMMISSION_FEE_RESULT;
      } else {
        this.pageState.setErrorMessage("Input error: Invalid fees entered!");
        return ePage.FLEXIBLE_COMMISSION_FEE_PROMPT;
      }
    }
  }

  /**
   * The composition portfolio prompt page.
   */
  class CompositionPortfolioPromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    CompositionPortfolioPromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() {
      this.pageState.setToInterpretInput(null);
      List<String> prompts = getPrompts();
      this.processOutput(
          this.view.displayGenericPortfolioPromptPage(
              "Composition",
              this.model.getAllPortfolios(),
              prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Enter the id of the portfolio you would like to see the composition for: ");
      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      if ("H".equals(currentInput)) {
        return ePage.HOMEPAGE;
      }
      if (this.isValidIdRequested(currentInput)) {
        this.pageState.setToInterpretInput(currentInput);
        return ePage.COMPOSITION_RESULT;
      } else {
        this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
        return ePage.COMPOSITION_PORTFOLIO_PROMPT;
      }
    }
  }

  /**
   * The composition result page.
   */
  class CompositionResultPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    CompositionResultPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      if (this.pageState.getCurrentPage() == ePage.COMPOSITION_RESULT) {
        int id = Utils.convertStringToNumber(this.toInterpretInput);
        this.pageState.setToInterpretInput(null);
        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayCompositionResultPage(
                this.model.getPortfolioComposition(id - 1),
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMPOSITION_RESULT) {
        Date date = Utils.convertStringToDate(this.toInterpretInput);
        this.pageState.setToInterpretInput(null);
        List<String> prompts = getPrompts();

        Pair<String, List<Pair<String, BigDecimal>>> compositionList =
            this.model.getFlexiblePortfolioComposition(this.pageState.getPortfolioId(), date);

        List<String> compositionStocks = new ArrayList<>();
        for (var compositionStock : compositionList.getO2()) {
          compositionStocks.add(compositionStock.toString());
        }

        Pair<String, List<String>> compositionResult = new Pair<>(compositionList.getO1(),
            compositionStocks);

        this.processOutput(
            this.view.displayCompositionResultPage(compositionResult,
                prompts,
                this.pageState.getErrorMessage()));
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Press any key to go to the Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      return ePage.HOMEPAGE;
    }
  }

  /**
   * The generic date prompt page.
   */
  class DatePromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    DatePromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMPOSITION_DATE_PROMPT) {
        if (Utils.isValidNumberInput(this.toInterpretInput)) {
          int id = Utils.convertStringToNumber(this.toInterpretInput);
          this.pageState.setPortfolioId(id - 1);
        }

        this.pageState.setToInterpretInput(null);
        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayGenericDatePromptPage(
                "Flexible Portfolio Composition Date Prompt",
                prompts,
                this.pageState.getErrorMessage()));

      } else if (this.pageState.getCurrentPage() == ePage.VALUE_DATE_PROMPT) {
        if (Utils.isValidNumberInput(this.toInterpretInput)) {
          int id = Utils.convertStringToNumber(this.toInterpretInput);
          this.pageState.setPortfolioId(id - 1);
        }

        this.pageState.setToInterpretInput(null);
        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayGenericDatePromptPage(
                "Portfolio Value Date Prompt",
                prompts,
                this.pageState.getErrorMessage()));

      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_VALUE_DATE_PROMPT) {
        if (Utils.isValidNumberInput(this.toInterpretInput)) {
          int id = Utils.convertStringToNumber(this.toInterpretInput);
          this.pageState.setPortfolioId(id - 1);
        }

        this.pageState.setToInterpretInput(null);
        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayGenericDatePromptPage(
                "Flexible Portfolio Value Date Prompt",
                prompts,
                this.pageState.getErrorMessage()));

      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COST_BASIS_DATE_PROMPT) {
        if (Utils.isValidNumberInput(this.toInterpretInput)) {
          int id = Utils.convertStringToNumber(this.toInterpretInput);
          this.pageState.setPortfolioId(id - 1);
        }

        this.pageState.setToInterpretInput(null);
        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayGenericDatePromptPage(
                "Flexible Portfolio Cost Basis Date Prompt",
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_START_DATE_PROMPT) {
        if (Utils.isValidNumberInput(this.toInterpretInput)) {
          int id = Utils.convertStringToNumber(this.toInterpretInput);
          this.pageState.setPortfolioId(id - 1);
        }

        this.pageState.setToInterpretInput(null);
        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayGenericDatePromptPage(
                "Flexible Portfolio Performance Start Date Prompt",
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_END_DATE_PROMPT) {
        if (Utils.isValidDateInput(this.toInterpretInput)) {
          Date date = Utils.convertStringToDate(this.toInterpretInput);
          this.pageState.setDate(date);
        }

        this.pageState.setToInterpretInput(null);
        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayGenericDatePromptPage(
                "Flexible Portfolio Performance End Date Prompt",
                prompts,
                this.pageState.getErrorMessage()));
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() throws NotImplementedException {
      List<String> prompts = new ArrayList<>();
      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMPOSITION_DATE_PROMPT) {
        prompts.add(
            "Enter the date on which you would like to "
                + "see the composition for the flexible portfolio (use mm-dd-yyyy format): ");
      } else if (this.pageState.getCurrentPage() == ePage.VALUE_DATE_PROMPT
          || this.pageState.getCurrentPage() == ePage.FLEXIBLE_VALUE_DATE_PROMPT) {
        prompts.add(
            "Enter the date on which you would like to see the value for the portfolio "
                + "(use mm-dd-yyyy format): ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COST_BASIS_DATE_PROMPT) {
        prompts.add(
            "Enter the date on which you would like to see the cost basis for the portfolio "
                + "(use mm-dd-yyyy format): ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_START_DATE_PROMPT) {
        prompts.add(
            "Enter the start date for the performance chart (use mm-dd-yyyy format): ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_END_DATE_PROMPT) {
        prompts.add(
            "Enter the end date for the performance chart (use mm-dd-yyyy format): ");
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }

      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) throws NotImplementedException {
      if ("H".equals(currentInput)) {
        this.pageState.setPortfolioId(-1);
        this.pageState.setDate(null);
        return ePage.HOMEPAGE;
      }

      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMPOSITION_DATE_PROMPT) {
        if (Utils.isValidDateInput(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_COMPOSITION_RESULT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid date entered!");
          return ePage.FLEXIBLE_COMPOSITION_DATE_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.VALUE_DATE_PROMPT) {
        if (Utils.isValidDateInput(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.VALUE_RESULT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid date entered!");
          return ePage.VALUE_DATE_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_VALUE_DATE_PROMPT) {
        if (Utils.isValidDateInput(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_VALUE_RESULT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid date entered!");
          return ePage.FLEXIBLE_VALUE_DATE_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COST_BASIS_DATE_PROMPT) {
        if (Utils.isValidDateInput(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_COST_BASIS_RESULT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid date entered!");
          return ePage.FLEXIBLE_COST_BASIS_DATE_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_START_DATE_PROMPT) {
        if (Utils.isValidDateInput(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_PERFORMANCE_END_DATE_PROMPT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid start date entered!");
          return ePage.FLEXIBLE_PERFORMANCE_START_DATE_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_END_DATE_PROMPT) {
        if (Utils.isValidDateInput(currentInput)
            && Utils.convertStringToDate(currentInput).after(this.pageState.getDate())) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_PERFORMANCE_RESULT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid end date entered!");
          return ePage.FLEXIBLE_PERFORMANCE_END_DATE_PROMPT;
        }
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }
  }

  /**
   * The cost basis result page.
   */
  class CostBasisResultPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    CostBasisResultPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() {
      Date date = Utils.convertStringToDate(this.toInterpretInput);
      this.pageState.setToInterpretInput(null);

      String pName = this.model.getFlexiblePortfolio(this.pageState.getPortfolioId()).getName();
      BigDecimal pCostBasis;
      try {
        pCostBasis = this.model.getCostBasis(this.pageState.getPortfolioId(), date);
      } catch (StockDataSourceException e) {
        this.pageState.setErrorMessage(e.getMessage());
        pCostBasis = new BigDecimal(0);
      }

      List<String> prompts = getPrompts();
      this.processOutput(
          this.view.displayFlexiblePortfolioCostBasisPage(pName, pCostBasis, prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Press any key to go to the Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      this.pageState.setPortfolioId(-1);
      return ePage.HOMEPAGE;
    }
  }

  /**
   * The flexible portfolio stocks prompt page.
   */
  class FlexiblePortfolioStocksPromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    FlexiblePortfolioStocksPromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      if (Utils.isValidNumberInput(this.toInterpretInput)) {
        int id = Utils.convertStringToNumber(this.toInterpretInput);
        this.pageState.setPortfolioId(id - 1);
      } else if (Utils.isValidFlexibleStockInputFormat(this.toInterpretInput)) {
        List<Triplet<String, Date, BigDecimal>> stocks = Utils.convertStringToFlexibleStocksFormat(
            this.toInterpretInput);
        try {
          if (this.pageState.getPortfolioId() == -1) {
            this.pageState.setErrorMessage(
                "Input error: Reached Buy/Sell operation with Invalid Portfolio Id!");
          } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_BUY_STOCKS_PROMPT) {
            this.model.buyStocksForFlexiblePortfolio(this.pageState.getPortfolioId(), stocks,
                commissionFee);
          } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_SELL_STOCKS_PROMPT) {
            this.model.sellStocksForFlexiblePortfolio(this.pageState.getPortfolioId(), stocks,
                commissionFee);
          } else {
            throw new NotImplementedException("Action for page is not implemented!");
          }
        } catch (StockDataSourceException | IllegalArgumentException | IllegalStateException e) {
          this.pageState.setErrorMessage(e.getMessage());
        }
      }

      this.pageState.setToInterpretInput(null);
      List<String> prompts = getPrompts();

      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_BUY_STOCKS_PROMPT) {
        IFlexiblePortfolio p = this.model.getFlexiblePortfolio(this.pageState.getPortfolioId());
        this.processOutput(
            this.view.displayFlexiblePortfolioStocksPromptPage(
                p.getName(),
                "buy",
                p.getStocks(),
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_SELL_STOCKS_PROMPT) {
        IFlexiblePortfolio p = this.model.getFlexiblePortfolio(this.pageState.getPortfolioId());
        this.processOutput(
            this.view.displayFlexiblePortfolioStocksPromptPage(
                p.getName(),
                "sell",
                p.getStocks(),
                prompts,
                this.pageState.getErrorMessage()));
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() throws NotImplementedException {
      List<String> prompts = new ArrayList<>();

      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_BUY_STOCKS_PROMPT) {
        prompts.add("Enter the stocks for the portfolio you would like to create \n"
            + "(enter in <Stock1 Symbol>,<Stock1 Volume>,<Stock1 Purchase Date>;"
            + "<Stock2 Symbol>,<Stock2 Volume>,<Stock2 Purchase Date>;... format).");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_SELL_STOCKS_PROMPT) {
        prompts.add("Enter the stocks for the portfolio you would like to create \n"
            + "(enter in <Stock1 Symbol>,<Stock1 Volume>,<Stock1 Sale Date>;"
            + "<Stock2 Symbol>,<Stock2 Volume>,<Stock2 Sale Date>;... format).");
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }

      prompts.add("The format for the date is MM-dd-yyyy.");
      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) throws NotImplementedException {
      if ("H".equals(currentInput)) {
        this.pageState.setPortfolioId(-1);
        return ePage.HOMEPAGE;
      }

      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_BUY_STOCKS_PROMPT) {
        if (Utils.isValidFlexibleStockInputFormat(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
        } else {
          this.pageState.setErrorMessage("Input error: Invalid stocks entered!");
        }
        return ePage.FLEXIBLE_BUY_STOCKS_PROMPT;
      }
      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_SELL_STOCKS_PROMPT) {
        if (Utils.isValidFlexibleStockInputFormat(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
        } else {
          this.pageState.setErrorMessage("Input error: Invalid stocks entered!");
        }
        return ePage.FLEXIBLE_SELL_STOCKS_PROMPT;
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }
  }

  /**
   * The generic page for import and export which prompts the filePath.
   */
  class ImportExportPromptPage extends AbstractPage {

    private final boolean isExport;

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     * @param isExport  whether this page is being initialized for an export operation.
     */
    ImportExportPromptPage(IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState,
        boolean isExport) {
      super(model, view, out, pageState);
      this.isExport = isExport;
    }

    private String getPageLabel() {
      return this.isExport ? "Export" : "Import";
    }

    @Override
    public void displayPage() {
      this.pageState.setToInterpretInput(null);
      List<String> prompts = getPrompts();
      this.processOutput(
          this.view.displayGenericPathPromptPage(
              this.getPageLabel(),
              prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      if (this.isExport) {
        prompts.add("Enter the path of file to which the portfolios are to be exported.");
        prompts.add(
            "Do note that the .csv file should have the following headers in the given sequence:");
        prompts.add("PortfolioName,"
            + "StockSymbol,"
            + "StockName,"
            + "StockExchange,"
            + "StockVolume");
      } else {
        prompts.add("Enter the path of file from which the portfolios are to be imported.");
        prompts.add(
            "Do note that the .csv file should have the following headers in the given sequence:");
        prompts.add("PortfolioName,"
            + "StockSymbol,"
            + "StockName,"
            + "StockExchange,"
            + "StockTransactionDate,"
            + "StockTransactionVolume,"
            + "StockTransactionCommissionFees");
      }
      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) throws NotImplementedException {
      if ("H".equals(currentInput)) {
        return ePage.HOMEPAGE;
      } else if (Utils.validateFilePathInput(currentInput)) {
        this.pageState.setToInterpretInput(currentInput);

        if (this.pageState.getCurrentPage() == ePage.EXPORT_PROMPT
            || this.pageState.getCurrentPage() == ePage.IMPORT_PROMPT) {

          return this.isExport ? ePage.EXPORT_RESULT : ePage.IMPORT_RESULT;
        } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_EXPORT_PROMPT
            || this.pageState.getCurrentPage() == ePage.FLEXIBLE_IMPORT_PROMPT) {

          return this.isExport ? ePage.FLEXIBLE_EXPORT_RESULT : ePage.FLEXIBLE_IMPORT_RESULT;
        } else {
          throw new NotImplementedException("Action for page is not implemented!");
        }
      } else {
        this.pageState.setErrorMessage("Input error: Invalid file path entered!");

        if (this.pageState.getCurrentPage() == ePage.EXPORT_PROMPT
            || this.pageState.getCurrentPage() == ePage.IMPORT_PROMPT) {

          return this.isExport ? ePage.EXPORT_PROMPT : ePage.IMPORT_PROMPT;
        } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_EXPORT_PROMPT
            || this.pageState.getCurrentPage() == ePage.FLEXIBLE_IMPORT_PROMPT) {

          return this.isExport ? ePage.FLEXIBLE_EXPORT_PROMPT : ePage.FLEXIBLE_IMPORT_PROMPT;
        } else {
          throw new NotImplementedException("Action for page is not implemented!");
        }
      }
    }
  }

  /**
   * The generic page for import and export which displays the result of the operation.
   */
  class ImportExportResultPage extends AbstractPage {

    private boolean isExport;

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     * @param isExport  whether this page is being initialized for an export operation.
     */
    ImportExportResultPage(IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState,
        boolean isExport) {
      super(model, view, out, pageState);
      this.isExport = isExport;
    }

    private String getPageLabel() {
      return this.isExport ? "Export" : "Import";
    }

    @Override
    public void displayPage() throws NotImplementedException {
      String filePath = this.toInterpretInput;
      this.pageState.setToInterpretInput(null);

      boolean operationResult = true;

      if (this.pageState.getCurrentPage() == ePage.IMPORT_RESULT
          || this.pageState.getCurrentPage() == ePage.EXPORT_RESULT) {
        if (this.isExport) {
          try {
            this.model.exportPortfolios(filePath);
          } catch (IOException e) {
            operationResult = false;
            this.pageState.setErrorMessage(e.getMessage());
          }
        } else {
          try {
            this.model.importPortfolios(filePath);
          } catch (IOException | InstantiationException | IllegalArgumentException e) {
            operationResult = false;
            this.pageState.setErrorMessage(e.getMessage());
          }
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_IMPORT_RESULT
          || this.pageState.getCurrentPage() == ePage.FLEXIBLE_EXPORT_RESULT) {
        if (this.isExport) {
          try {
            this.model.exportFlexiblePortfolios(filePath);
          } catch (IOException e) {
            operationResult = false;
            this.pageState.setErrorMessage(e.getMessage());
          }
        } else {
          try {
            this.model.importFlexiblePortfolios(filePath);
          } catch (IOException | InstantiationException | IllegalArgumentException e) {
            operationResult = false;
            this.pageState.setErrorMessage(e.getMessage());
          }
        }
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }

      List<String> prompts = this.getPrompts();
      this.processOutput(
          this.view.displayGenericPathPromptResultPage(
              this.getPageLabel(),
              filePath,
              operationResult,
              prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Press any key to go to the Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      return ePage.HOMEPAGE;
    }
  }

  /**
   * The performance chart result page.
   */
  class PerformanceResultPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    PerformanceResultPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      Date endDate = Utils.convertStringToDate(this.toInterpretInput);
      this.pageState.setToInterpretInput(null);

      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_RESULT) {
        String pName = this.model.getFlexiblePortfolio(this.pageState.getPortfolioId()).getName();

        List<String> prompts = getPrompts();
        try {
          IChart pChart = this.model.getPerformanceChart(this.pageState.getPortfolioId(),
              this.pageState.getDate(), endDate);

          this.processOutput(
              this.view.displayFlexiblePortfolioPerformancePage(pName, this.pageState.getDate(),
                  endDate, pChart, prompts, this.pageState.getErrorMessage()));

        } catch (StockDataSourceException e) {
          this.pageState.setErrorMessage(e.getMessage());
          this.processOutput(
              this.view.displayGenericResultPage("Performance chart error", false, prompts,
                  this.pageState.getErrorMessage()));
        }
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Press any key to go to the Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      this.pageState.setPortfolioId(-1);
      return ePage.HOMEPAGE;
    }
  }

  /**
   * The generic select portfolio prompt page.
   */
  class SelectPortfolioPromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    SelectPortfolioPromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      this.pageState.setToInterpretInput(null);
      List<String> prompts = getPrompts();
      if (this.pageState.getCurrentPage() == ePage.COMPOSITION_PORTFOLIO_PROMPT) {
        this.processOutput(
            this.view.displayGenericPortfolioPromptPage(
                "Regular Portfolio Composition",
                this.model.getAllPortfolios(),
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMPOSITION_PORTFOLIO_PROMPT) {
        this.processOutput(
            this.view.displayGenericPortfolioPromptPage(
                "Flexible Portfolio Composition",
                this.model.getAllFlexiblePortfolios(),
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_BUY_PORTFOLIO_PROMPT) {
        this.processOutput(
            this.view.displayGenericPortfolioPromptPage(
                "Buy Stocks",
                this.model.getAllFlexiblePortfolios(),
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_SELL_PORTFOLIO_PROMPT) {
        this.processOutput(
            this.view.displayGenericPortfolioPromptPage(
                "Sell Stocks",
                this.model.getAllFlexiblePortfolios(),
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_VALUE_PORTFOLIO_PROMPT) {
        this.processOutput(
            this.view.displayGenericPortfolioPromptPage(
                "Flexible Portfolio Value",
                this.model.getAllFlexiblePortfolios(),
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COST_BASIS_PORTFOLIO_PROMPT) {
        this.processOutput(
            this.view.displayGenericPortfolioPromptPage(
                "Flexible Portfolio Cost Basis",
                this.model.getAllFlexiblePortfolios(),
                prompts,
                this.pageState.getErrorMessage()));
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_PORTFOLIO_PROMPT) {
        this.processOutput(
            this.view.displayGenericPortfolioPromptPage(
                "Flexible Portfolio Performance",
                this.model.getAllFlexiblePortfolios(),
                prompts,
                this.pageState.getErrorMessage()));
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() throws NotImplementedException {
      List<String> prompts = new ArrayList<>();

      if (this.pageState.getCurrentPage() == ePage.COMPOSITION_PORTFOLIO_PROMPT) {
        prompts.add("Enter the id of the portfolio you would like to see the composition for: ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMPOSITION_PORTFOLIO_PROMPT) {
        prompts.add(
            "Enter the id of the flexible portfolio you would like to see the composition for: ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_BUY_PORTFOLIO_PROMPT) {
        prompts.add("Enter the id of the flexible portfolio you would like to buy stocks for: ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_SELL_PORTFOLIO_PROMPT) {
        prompts.add("Enter the id of the flexible portfolio you would like to sell stocks for: ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_VALUE_PORTFOLIO_PROMPT) {
        prompts.add("Enter the id of the flexible portfolio you would like to get value for: ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COST_BASIS_PORTFOLIO_PROMPT) {
        prompts.add(
            "Enter the id of the flexible portfolio you would like to get cost basis for: ");
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_PORTFOLIO_PROMPT) {
        prompts.add(
            "Enter the id of the flexible portfolio you would like to get performance chart for: ");
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }

      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) throws NotImplementedException {
      if ("H".equals(currentInput)) {
        return ePage.HOMEPAGE;
      }
      if (this.pageState.getCurrentPage() == ePage.COMPOSITION_PORTFOLIO_PROMPT) {
        if (this.isValidIdRequested(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.COMPOSITION_RESULT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
          return ePage.COMPOSITION_PORTFOLIO_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMPOSITION_PORTFOLIO_PROMPT) {
        if (this.isValidFlexibleIdRequested(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_COMPOSITION_DATE_PROMPT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
          return ePage.COMPOSITION_PORTFOLIO_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_BUY_PORTFOLIO_PROMPT) {
        if (this.isValidFlexibleIdRequested(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_BUY_STOCKS_PROMPT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
          return ePage.FLEXIBLE_BUY_PORTFOLIO_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_SELL_PORTFOLIO_PROMPT) {
        if (this.isValidFlexibleIdRequested(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_SELL_STOCKS_PROMPT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
          return ePage.FLEXIBLE_SELL_PORTFOLIO_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_VALUE_PORTFOLIO_PROMPT) {
        if (this.isValidFlexibleIdRequested(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_VALUE_DATE_PROMPT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
          return ePage.FLEXIBLE_VALUE_PORTFOLIO_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COST_BASIS_PORTFOLIO_PROMPT) {
        if (this.isValidFlexibleIdRequested(currentInput)) {
          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_COST_BASIS_DATE_PROMPT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
          return ePage.FLEXIBLE_COST_BASIS_PORTFOLIO_PROMPT;
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_PERFORMANCE_PORTFOLIO_PROMPT) {
        if (this.isValidFlexibleIdRequested(currentInput)) {

          IFlexiblePortfolio p = this.model.getFlexiblePortfolio(
              Utils.convertStringToNumber(currentInput) - 1);

          if (p.getStocks() == null || p.getStocks().size() == 0) {
            this.pageState.setErrorMessage("The selected portfolio doesn't have any stocks!");
            return ePage.FLEXIBLE_PERFORMANCE_PORTFOLIO_PROMPT;
          }

          this.pageState.setToInterpretInput(currentInput);
          return ePage.FLEXIBLE_PERFORMANCE_START_DATE_PROMPT;
        } else {
          this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
          return ePage.FLEXIBLE_PERFORMANCE_PORTFOLIO_PROMPT;
        }
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }
  }

  /**
   * The value date prompt page.
   */
  class ValueDatePromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    ValueDatePromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() {
      // previous state was Value Portfolio Prompt
      if (Utils.isValidNumberInput(this.toInterpretInput)) {
        int id = Utils.convertStringToNumber(this.toInterpretInput);
        this.pageState.setPortfolioId(id - 1);
      }

      this.pageState.setToInterpretInput(null);
      List<String> prompts = getPrompts();
      this.processOutput(
          this.view.displayValueDatePromptPage(
              this.model.getPortfolio(this.pageState.getPortfolioId()).getName(), prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add(
          "Enter the date on which you would like to see the value for the portfolio "
              + "(use mm-dd-yyyy format): ");
      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      if ("H".equals(currentInput)) {
        this.pageState.setPortfolioId(-1);
        return ePage.HOMEPAGE;
      }
      if (Utils.isValidDateInput(currentInput)) {
        this.pageState.setToInterpretInput(currentInput);
        return ePage.VALUE_RESULT;
      } else {
        this.pageState.setErrorMessage("Input error: Invalid date entered!");
        return ePage.VALUE_DATE_PROMPT;
      }
    }
  }

  /**
   * The value portfolio prompt page.
   */
  class ValuePortfolioPromptPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    ValuePortfolioPromptPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() {
      this.pageState.setToInterpretInput(null);
      List<String> prompts = getPrompts();
      this.processOutput(
          this.view.displayGenericPortfolioPromptPage(
              "Value",
              this.model.getAllPortfolios(),
              prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Enter the id of the portfolio you would like to see the value for: ");
      prompts.add("(H) Go back to Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      if ("H".equals(currentInput)) {
        return ePage.HOMEPAGE;
      }
      if (this.isValidIdRequested(currentInput)) {
        this.pageState.setToInterpretInput(currentInput);
        return ePage.VALUE_DATE_PROMPT;
      } else {
        this.pageState.setErrorMessage("Input error: Invalid Portfolio Id entered!");
        return ePage.VALUE_PORTFOLIO_PROMPT;
      }
    }
  }

  /**
   * The value result page.
   */
  class ValueResultPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    ValueResultPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      Date date = Utils.convertStringToDate(this.toInterpretInput);
      this.pageState.setToInterpretInput(null);

      if (this.pageState.getCurrentPage() == ePage.VALUE_RESULT) {
        String pName = this.model.getPortfolio(this.pageState.getPortfolioId()).getName();
        Pair<BigDecimal, List<IPortfolioStockValue>> pValues;
        try {
          pValues = this.model.getPortfolioValue(this.pageState.getPortfolioId(), date);
        } catch (StockDataSourceException e) {
          this.pageState.setErrorMessage(e.getMessage());
          pValues = new Pair<>(new BigDecimal(0), new ArrayList<>());
        }

        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayValueResultPage(pName, pValues, prompts,
                this.pageState.getErrorMessage()));

      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_VALUE_RESULT) {
        String pName = this.model.getFlexiblePortfolio(this.pageState.getPortfolioId()).getName();
        Pair<BigDecimal, List<IPortfolioStockValue>> pValues;
        try {
          pValues = this.model.getFlexiblePortfolioValue(this.pageState.getPortfolioId(), date);
        } catch (StockDataSourceException e) {
          this.pageState.setErrorMessage(e.getMessage());
          pValues = new Pair<>(new BigDecimal(0), new ArrayList<>());
        }

        List<String> prompts = getPrompts();
        this.processOutput(
            this.view.displayValueResultPage(pName, pValues, prompts,
                this.pageState.getErrorMessage()));
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Press any key to go to the Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      this.pageState.setPortfolioId(-1);
      return ePage.HOMEPAGE;
    }
  }

  /**
   * The generic results page.
   */
  class GenericResultPage extends AbstractPage {

    /**
     * A constructor to create the page.
     *
     * @param model     the model of the application.
     * @param view      the view of the application.
     * @param out       the output stream of the application.
     * @param pageState the current state of the page.
     */
    GenericResultPage(
        IPortfolioModel model,
        IPortfolioView view,
        Appendable out,
        IPageState pageState) {
      super(model, view, out, pageState);
    }

    @Override
    public void displayPage() throws NotImplementedException {
      String pageLabel = "<NO LABEL>";
      if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_ADD_PORTFOLIO_RESULT) {
        if (Utils.isValidWordsInput(this.toInterpretInput)) {
          String name = this.toInterpretInput.trim();
          try {
            this.model.createFlexiblePortfolio(name);
            pageLabel = "Add Flexible Portfolio Result";
          } catch (InstantiationException e) {
            this.pageState.setErrorMessage(e.getMessage());
          }
        }
      } else if (this.pageState.getCurrentPage() == ePage.FLEXIBLE_COMMISSION_FEE_RESULT) {
        if (Utils.isValidFloatNumberInput(this.toInterpretInput)) {
          BigDecimal fee = new BigDecimal(this.toInterpretInput);
          if (commissionFee.compareTo(new BigDecimal(0)) < 0) {
            this.pageState.setErrorMessage("Commission fees cannot be negative!");
          } else {
            commissionFee = fee;
          }

          pageLabel = "Commission Fees Result";
        }
      } else {
        throw new NotImplementedException("Action for page is not implemented!");
      }

      this.pageState.setToInterpretInput(null);

      List<String> prompts = this.getPrompts();
      this.processOutput(
          this.view.displayGenericResultPage(
              pageLabel,
              this.pageState.getErrorMessage() == null || this.pageState.getErrorMessage()
                  .isBlank(),
              prompts,
              this.pageState.getErrorMessage()));
    }

    @Override
    public List<String> getPrompts() {
      List<String> prompts = new ArrayList<>();
      prompts.add("Press any key to go to the Home page.");

      return prompts;
    }

    @Override
    public ePage processInputAndGetNextPage(String currentInput) {
      return ePage.HOMEPAGE;
    }
  }

  //</editor-fold>

  // <editor-fold desc="State variables">

  private final IPortfolioModel model;
  private final IPortfolioView view;
  private final Scanner scanner;
  private final Appendable out;

  IPageState pageState;

  IPage currentPageObject;
  private Map<ePage, Function<ePage, IPage>> knownPages;
  private BigDecimal commissionFee;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Instantiates the controller for the application.
   *
   * @param model the model which implements the logic for the application.
   * @param view  the view for the application which displays the instructions and data to the user
   *              or test application.
   * @param in    the mode in which the user or test application will feed input to the controller.
   * @param out   the mode in which the user or test application will read output from the
   *              controller.
   */
  public PortfolioController(
      IPortfolioModel model,
      IPortfolioView view,
      Readable in,
      Appendable out) {
    this.model = model;
    this.view = view;
    this.out = out;

    this.scanner = new Scanner(in);

    this.setupPageMap();
    this.pageState = new PageState(null, null, true, -1, null, ePage.HOMEPAGE);
    this.setHomePage();

    this.commissionFee = new BigDecimal(5);
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  private void setHomePage() {
    this.pageState.setCurrentPage(ePage.HOMEPAGE);
    this.currentPageObject = knownPages
        .get(this.pageState.getCurrentPage())
        .apply(this.pageState.getCurrentPage());
  }

  private void setupPageMap() {
    knownPages = new HashMap<>();
    knownPages.put(ePage.HOMEPAGE,
        p -> new HomePage(this.model, this.view, this.out, this.pageState));

    //</editor-fold desc="regular portfolio pages">

    // Composition
    knownPages.put(ePage.COMPOSITION_PORTFOLIO_PROMPT,
        p -> new CompositionPortfolioPromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.COMPOSITION_RESULT,
        p -> new CompositionResultPage(this.model, this.view, this.out, this.pageState));

    // Value
    knownPages.put(ePage.VALUE_PORTFOLIO_PROMPT,
        p -> new ValuePortfolioPromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.VALUE_DATE_PROMPT,
        p -> new ValueDatePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.VALUE_RESULT,
        p -> new ValueResultPage(this.model, this.view, this.out, this.pageState));

    // Add portfolio
    knownPages.put(ePage.ADD_PORTFOLIO_NAME_PROMPT,
        p -> new AddPortfolioNamePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.ADD_PORTFOLIO_STOCKS_PROMPT,
        p -> new AddPortfolioStocksPromptPage(this.model, this.view, this.out, this.pageState));

    // Export
    knownPages.put(ePage.EXPORT_PROMPT,
        p -> new ImportExportPromptPage(this.model, this.view, this.out, this.pageState,
            true));
    knownPages.put(ePage.EXPORT_RESULT,
        p -> new ImportExportResultPage(this.model, this.view, this.out, this.pageState,
            true));

    // Import
    knownPages.put(ePage.IMPORT_PROMPT,
        p -> new ImportExportPromptPage(this.model, this.view, this.out, this.pageState,
            false));
    knownPages.put(ePage.IMPORT_RESULT,
        p -> new ImportExportResultPage(this.model, this.view, this.out, this.pageState,
            false));

    //</editor-fold>

    //<editor-fold desc="Flexible portfolio pages">

    // Flexible Composition
    knownPages.put(ePage.FLEXIBLE_COMPOSITION_PORTFOLIO_PROMPT,
        p -> new SelectPortfolioPromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_COMPOSITION_DATE_PROMPT,
        p -> new DatePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_COMPOSITION_RESULT,
        p -> new CompositionResultPage(this.model, this.view, this.out, this.pageState));

    // Flexible Value
    knownPages.put(ePage.FLEXIBLE_VALUE_PORTFOLIO_PROMPT,
        p -> new SelectPortfolioPromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_VALUE_DATE_PROMPT,
        p -> new DatePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_VALUE_RESULT,
        p -> new ValueResultPage(this.model, this.view, this.out, this.pageState));

    // Flexible Add portfolio
    knownPages.put(ePage.FLEXIBLE_ADD_PORTFOLIO_NAME_PROMPT,
        p -> new AddPortfolioNamePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_ADD_PORTFOLIO_RESULT,
        p -> new GenericResultPage(this.model, this.view, this.out, this.pageState));

    // Flexible Export
    knownPages.put(ePage.FLEXIBLE_EXPORT_PROMPT,
        p -> new ImportExportPromptPage(this.model, this.view, this.out, this.pageState, true));
    knownPages.put(ePage.FLEXIBLE_EXPORT_RESULT,
        p -> new ImportExportResultPage(this.model, this.view, this.out, this.pageState, true));

    // Flexible Import
    knownPages.put(ePage.FLEXIBLE_IMPORT_PROMPT,
        p -> new ImportExportPromptPage(this.model, this.view, this.out, this.pageState, false));
    knownPages.put(ePage.FLEXIBLE_IMPORT_RESULT,
        p -> new ImportExportResultPage(this.model, this.view, this.out, this.pageState, false));

    // Flexible Commission Fee
    knownPages.put(ePage.FLEXIBLE_COMMISSION_FEE_PROMPT,
        p -> new CommissionFeePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_COMMISSION_FEE_RESULT,
        p -> new GenericResultPage(this.model, this.view, this.out, this.pageState));

    // Flexible Buy
    knownPages.put(ePage.FLEXIBLE_BUY_PORTFOLIO_PROMPT,
        p -> new SelectPortfolioPromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_BUY_STOCKS_PROMPT,
        p -> new FlexiblePortfolioStocksPromptPage(this.model, this.view, this.out,
            this.pageState));

    // Flexible Sell
    knownPages.put(ePage.FLEXIBLE_SELL_PORTFOLIO_PROMPT,
        p -> new SelectPortfolioPromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_SELL_STOCKS_PROMPT,
        p -> new FlexiblePortfolioStocksPromptPage(this.model, this.view, this.out,
            this.pageState));

    // Flexible Cost Basis
    knownPages.put(ePage.FLEXIBLE_COST_BASIS_PORTFOLIO_PROMPT,
        p -> new SelectPortfolioPromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_COST_BASIS_DATE_PROMPT,
        p -> new DatePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_COST_BASIS_RESULT,
        p -> new CostBasisResultPage(this.model, this.view, this.out, this.pageState));

    // Flexible Performance
    knownPages.put(ePage.FLEXIBLE_PERFORMANCE_PORTFOLIO_PROMPT,
        p -> new SelectPortfolioPromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_PERFORMANCE_START_DATE_PROMPT,
        p -> new DatePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_PERFORMANCE_END_DATE_PROMPT,
        p -> new DatePromptPage(this.model, this.view, this.out, this.pageState));
    knownPages.put(ePage.FLEXIBLE_PERFORMANCE_RESULT,
        p -> new PerformanceResultPage(this.model, this.view, this.out, this.pageState));

    //</editor-fold>
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public void runApplication() {
    while (this.pageState.isApplicationRunning()) {
      try {
        // show current page
        this.currentPageObject.displayPage();

        String currentInput = this.processInput();
        // take input for current page and let next iteration of loop deal with next page
        this.pageState.setCurrentPage(
            this.currentPageObject.processInputAndGetNextPage(currentInput));

        this.pageState = this.currentPageObject.getPageState();

        this.currentPageObject = knownPages
            .get(this.pageState.getCurrentPage())
            .apply(this.pageState.getCurrentPage());
      } catch (Exception e) {
        // exception middleware to catch all exceptions without breaking application
        this.pageState.setErrorMessage("Unhandled exception: " + e.getMessage() + "\n"
            + "Stack Trace: " + Arrays.toString(e.getStackTrace()) + "\n");
        this.pageState.setToInterpretInput(null);
        this.setHomePage();
      }
    }
  }

  //</editor-fold>

  //<editor-fold desc="Package Private Methods">

  /**
   * Gets the known pages map for this controller.
   *
   * @return the map with the pages of this controller.
   */
  Map<ePage, Function<ePage, IPage>> getKnownPages() {
    return this.knownPages;
  }

  //</editor-fold>

  //<editor-fold desc="Process input and output">

  private String processInput() {
    return this.scanner.nextLine();
  }

  //</editor-fold>
}
