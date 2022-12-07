package view;

import common.Utils;
import features.IPortfolioManagerFeatures;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DateFormatter;
import model.portfolio.IObservableFlexiblePortfolioStock;
import view.JFrameView.eHomePageActionCommand;

/**
 * This class represents the buy and sell panel of the JFrame GUI for this application.
 */
class JBuySellPanel extends AbstractPanel {

  //<editor-fold desc="State variables">

  private final JFormattedTextField symbolTextField;
  private final JFormattedTextField volumeTextField;
  private final JFormattedTextField dateTextField;
  private final JFormattedTextField commissionFeeField;

  private final JButton enterButton;

  private final JPanel currentStocksPanel;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Construct a buy/sell JPanel.
   *
   * @param actionCommand the provided home page action command.
   * @param errorMessageLabel the label for the provided error message.
   */
  JBuySellPanel(eHomePageActionCommand actionCommand, JLabel errorMessageLabel) {
    super(actionCommand, errorMessageLabel);

    if (this.actionCommand == eHomePageActionCommand.BUY) {
      this.setBorder(BorderFactory.createTitledBorder("Buy Stocks"));
    } else if (this.actionCommand == eHomePageActionCommand.SELL) {
      this.setBorder(BorderFactory.createTitledBorder("Sell Stocks"));
    }

    // layout
    this.setLayout(new GridLayout(0, 1));

    //<editor-fold desc="Input Panel">

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(0, 1));

    //<editor-fold desc="Symbol">

    JPanel symbolPanel = new JPanel();
    symbolPanel.setLayout(new GridLayout(0, 2));

    // stock symbol label
    JLabel symbolLabel = new JLabel("Symbol:");
    symbolLabel.setSize(20, 1);
    symbolPanel.add(symbolLabel);

    // stock symbol text box
    this.symbolTextField = new JFormattedTextField();
    this.symbolTextField.setSize(20, 1);
    symbolPanel.add(symbolTextField);

    //</editor-fold>

    //<editor-fold desc="Volume">

    JPanel volumePanel = new JPanel();
    volumePanel.setLayout(new GridLayout(0, 2));

    // stock volume label
    JLabel volumeDisplay = new JLabel("Volume:");
    volumeDisplay.setSize(20, 1);
    volumePanel.add(volumeDisplay);

    // stock volume text field
    NumberFormat volumeFormat = NumberFormat.getNumberInstance();

    if (this.actionCommand == eHomePageActionCommand.BUY) {
      // allowing user to only buy non-fractional stocks
      volumeFormat.setMaximumFractionDigits(0);
    } else if (this.actionCommand == eHomePageActionCommand.SELL) {
      // allowing user to sell fractional stocks
      volumeFormat.setMaximumFractionDigits(2);
    }

    this.volumeTextField = new JFormattedTextField(volumeFormat);
    this.volumeTextField.setSize(20, 1);
    volumePanel.add(this.volumeTextField);

    //</editor-fold>

    //<editor-fold desc="Date">

    JPanel datePanel = new JPanel();
    datePanel.setLayout(new GridLayout(0, 2));

    JLabel dateSelectDisplay = new JLabel("Date (mm-dd-yyyy):");
    dateSelectDisplay.setSize(20, 1);
    datePanel.add(dateSelectDisplay);

    // using FormattedTextField for date field
    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    DateFormatter dateFormatter = new DateFormatter(dateFormat);

    this.dateTextField = new JFormattedTextField(dateFormatter);
    this.dateTextField.setSize(20, 1);
    datePanel.add(this.dateTextField);

    //</editor-fold>

    //<editor-fold desc="Commission Fee">

    JPanel commissionFeePanel = new JPanel();
    commissionFeePanel.setLayout(new GridLayout(0, 2));

    JLabel commissionFeeLabel = new JLabel("Commission Fee ($):");
    commissionFeeLabel.setSize(20, 1);
    commissionFeePanel.add(commissionFeeLabel);

    NumberFormat commissionFormat = NumberFormat.getNumberInstance();
    this.commissionFeeField = new JFormattedTextField(commissionFormat);
    this.commissionFeeField.setSize(20, 1);
    commissionFeePanel.add(this.commissionFeeField);

    //</editor-fold>

    //<editor-fold desc="Enter">

    String buttonName = "Operation not implemented";
    if (actionCommand == eHomePageActionCommand.BUY) {
      buttonName = "Buy Stock";
    } else if (actionCommand == eHomePageActionCommand.SELL) {
      buttonName = "Sell Stock";
    }

    this.enterButton = new JButton(buttonName);
    this.enterButton.setLayout(new FlowLayout());

    //</editor-fold>

    inputPanel.add(symbolPanel);
    inputPanel.add(volumePanel);
    inputPanel.add(datePanel);
    inputPanel.add(commissionFeePanel);
    inputPanel.add(this.enterButton);

    //</editor-fold>

    //<editor-fold desc="Output Panel">

    JPanel outputPanel = new JPanel();
    // top to bottom BoxLayout
    outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
    outputPanel.setBorder(BorderFactory.createTitledBorder("Current Stocks in the Portfolio:"));

    //<editor-fold desc="Current Stocks">

    this.currentStocksPanel = new JPanel();
    this.currentStocksPanel.setLayout(new BoxLayout(this.currentStocksPanel, BoxLayout.PAGE_AXIS));

    //</editor-fold>

    JScrollPane currentStocksScrollPane = new JScrollPane(this.currentStocksPanel);
    outputPanel.add(currentStocksScrollPane);

    //</editor-fold>

    this.add(inputPanel);
    this.add(outputPanel);

    setVisible(true);
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  private boolean validateInputFields() {
    boolean result = true;
    StringBuilder errorMessage = new StringBuilder();
    if (!Utils.isValidWordsInput(this.symbolTextField.getText())) {
      errorMessage.append("Given stock is not in valid format! \n");
      result = false;
    }
    if (this.actionCommand == eHomePageActionCommand.BUY
        && !Utils.isValidNumberInput(this.volumeTextField.getText())) {
      errorMessage.append("Given volume is not in valid format! \n");
      result = false;
    }
    if (this.actionCommand == eHomePageActionCommand.SELL
        && !Utils.isValidFloatNumberInput(this.volumeTextField.getText())) {
      errorMessage.append("Given volume is not in valid format! \n");
      result = false;
    }
    if (!Utils.isValidDateInput(this.dateTextField.getText())) {
      errorMessage.append("Given date is not in valid format! \n");
      result = false;
    }
    if (!Utils.isValidFloatNumberInput(this.commissionFeeField.getText())) {
      errorMessage.append("Given Commission Fee is not in valid format! \n");
      result = false;
    }

    if (!result) {
      this.displayErrorMessage(errorMessage.toString());
    } else {
      this.displayErrorMessage("");
    }
    return result;
  }

  //</editor-fold>

  //<editor-fold desc="Core Methods">

  @Override
  public void addFeatures(IPortfolioManagerFeatures features) {
    if (this.actionCommand == eHomePageActionCommand.BUY) {
      this.enterButton.addActionListener(e -> {
        if (this.validateInputFields()) {
          boolean isSuccessful = features.buyStocksForPortfolio(
              this.portfolioId,
              this.symbolTextField.getText(),
              new BigDecimal(this.volumeTextField.getValue().toString()),
              Utils.convertStringToDate(this.dateTextField.getText()),
              new BigDecimal(this.commissionFeeField.getText())
          );

          if (isSuccessful) {
            this.clearInputFields();
          }
        }
      });
    } else if (this.actionCommand == eHomePageActionCommand.SELL) {
      this.enterButton.addActionListener(e -> {
        if (this.validateInputFields()) {
          boolean isSuccessful = features.sellStocksForPortfolio(
              this.portfolioId,
              this.symbolTextField.getText(),
              new BigDecimal(this.volumeTextField.getText()),
              Utils.convertStringToDate(this.dateTextField.getText()),
              new BigDecimal(this.commissionFeeField.getText())
          );

          if (isSuccessful) {
            this.clearInputFields();
          }
        }
      });
    }
  }

  @Override
  public void clearInputFields() {
    this.symbolTextField.removeAll();
    this.symbolTextField.setText("");

    this.volumeTextField.removeAll();
    this.volumeTextField.setText("");

    this.dateTextField.removeAll();
    this.dateTextField.setText("");

    this.commissionFeeField.removeAll();
    this.commissionFeeField.setText("");
  }

  @Override
  public void clearOutputLabel() {
    this.currentStocksPanel.removeAll();
  }

  /**
   * Loads the list of stocks of a portfolio.
   *
   * @param portfolioStocks a list of flexible portfolio stocks.
   */
  void loadPortfolioStocksList(List<IObservableFlexiblePortfolioStock> portfolioStocks) {
    this.clearOutputLabel();

    // build output
    for (IObservableFlexiblePortfolioStock stock : portfolioStocks) {

      JPanel singleStockPanel = new JPanel();
      singleStockPanel.setLayout(new BoxLayout(singleStockPanel, BoxLayout.PAGE_AXIS));
      singleStockPanel.setBorder(BorderFactory.createTitledBorder(stock.getSymbol()));

      // name
      JPanel namePanel = new JPanel();
      namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel nameLabel = new JLabel("Stock Name: " + stock.getName());
      nameLabel.setSize(20, 1);
      namePanel.add(nameLabel);

      // exchange
      JPanel exchangePanel = new JPanel();
      exchangePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel exchangeLabel = new JLabel("Stock Exchange: " + stock.getExchange());
      exchangeLabel.setSize(20, 1);
      exchangePanel.add(exchangeLabel);

      singleStockPanel.add(namePanel);
      singleStockPanel.add(exchangePanel);

      // transactions
      JPanel transactionsPanel = new JPanel();
      transactionsPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));
      transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.PAGE_AXIS));

      StringBuilder transactionsLabelContent = new StringBuilder();

      for (var transaction : stock.getTransactions()) {

        JPanel singleTransactionPanel = new JPanel();
        singleTransactionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        boolean isBuyTransaction = transaction.getO2().getO1().compareTo(new BigDecimal(0)) > 0;
        BigDecimal displayedVolume = isBuyTransaction ? transaction.getO2().getO1() :
            transaction.getO2().getO1().multiply(new BigDecimal(-1));

        transactionsLabelContent
            .append("Date: ").append(Utils.convertDateToDefaultStringFormat(transaction.getO1()))
            .append(", Transaction Type: ").append(isBuyTransaction ? "Buy" : "Sell")
            .append(", Volume: ").append(displayedVolume)
            .append(", Stock Price: ").append(transaction.getO2().getO2())
            .append(", Commission Fees: $").append(transaction.getO2().getO3());
        JLabel transactionsLabel = new JLabel(transactionsLabelContent.toString());
        singleTransactionPanel.add(transactionsLabel);
        transactionsPanel.add(singleTransactionPanel);

        transactionsLabelContent = new StringBuilder();
      }

      singleStockPanel.add(transactionsPanel);

      this.currentStocksPanel.add(singleStockPanel);
    }
  }

  //</editor-fold>
}
