package view;

import common.Utils;
import common.pair.Pair;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import features.IPortfolioManagerFeatures;
import model.portfolio.IDollarCostInvestment;
import model.portfolio.eRecurringIntervalType;
import view.JFrameView.eHomePageActionCommand;

/**
 * This class represents the dollar-cost investing panel of the JFrame GUI for this application.
 */
class JDollarCostPanel extends AbstractPanel {

  //<editor-fold desc="Inner enums">

  /**
   * An enumeration for the action commands of dollar-cost investing.
   */
  enum eDollarCostActionCommand {
    RECURRING("Recurring"),
    ADD_STOCK("Add Stock"),
    REMOVE_STOCK("Remove Stock");
    private final String string;

    eDollarCostActionCommand(String functionString) {
      this.string = functionString;
    }

    String getActionCommandVal() {
      return this.string;
    }

    static eDollarCostActionCommand fromValue(String givenName) {
      for (eDollarCostActionCommand actionCommand : values()) {
        if (actionCommand.string.equals(givenName)) {
          return actionCommand;
        }
      }
      return null;
    }
  }

  //</editor-fold>

  //<editor-fold desc="Inner classes">

  private class DollarCostPanelActionListener implements ActionListener {

    private void redrawStocksOutput() {
      currentStocksOutput.removeAll();
      for (var s : stocksMap.entrySet()) {
        currentStocksOutput.add(
            new JLabel("Symbol: " + s.getKey() + " | Percentage : " + s.getValue() + "%"));
      }

      currentStocksOutput.revalidate();
      currentStocksOutput.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      eDollarCostActionCommand actionEvent = eDollarCostActionCommand.fromValue(
          e.getActionCommand());

      switch (actionEvent) {
        case RECURRING:
          boolean isRecurring = recurringCheckBox.isSelected();

          if (isRecurring) {
            startDateLabel.setText("Start Date (mm-dd-yyyy):");
            recurringPanel.setVisible(true);
          } else {
            recurringPanel.setVisible(false);
            recurringPanel.revalidate();
            recurringPanel.repaint();
            startDateLabel.setText("Date (mm-dd-yyyy):");
            clearRecurringInputFields();
          }
          break;

        case ADD_STOCK:
          if (validateStocksInputFields()) {
            String symbol = symbolTextField.getText();
            BigDecimal percentage = new BigDecimal(percentageTextField.getText());

            if (stocksMap.containsKey(symbol)) {
              stocksMap.put(symbol, stocksMap.get(symbol).add(percentage));
            } else {
              stocksMap.put(symbol, percentage);
            }

            this.redrawStocksOutput();
          }
          break;
        case REMOVE_STOCK:
          if (validateStocksInputFields()) {
            String symbol = symbolTextField.getText();
            BigDecimal percentage = new BigDecimal(percentageTextField.getText());

            if (stocksMap.containsKey(symbol)) {
              if (stocksMap.get(symbol).compareTo(percentage) == 0) {
                stocksMap.remove(symbol);
              } else if (stocksMap.get(symbol).compareTo(percentage) > 0) {
                stocksMap.put(symbol, stocksMap.get(symbol).subtract(percentage));
              }
            } else {
              displayErrorMessage("This stock doesn't exist in the active list!");
            }

            this.redrawStocksOutput();
          }
          break;

        default:
          break;
      }
    }
  }

  //</editor-fold>

  //<editor-fold desc="View State variables">

  private final Map<String, BigDecimal> stocksMap;

  //</editor-fold>

  //<editor-fold desc="Frame State variables">

  private final JFormattedTextField symbolTextField;
  private final JFormattedTextField percentageTextField;

  private final JFormattedTextField totalAmountField;
  private final JFormattedTextField commissionFeeField;
  private final JLabel startDateLabel;
  private final JFormattedTextField startDateTextField;
  private final JCheckBox recurringCheckBox;
  private final JPanel recurringPanel;
  private final JFormattedTextField endDateTextField;
  private final JComboBox<String> recurringIntervalTypeCombobox;
  private final JFormattedTextField recurringIntervalDeltaField;

  private final JPanel currentStocksOutput;

  private final JButton enterButton;

  private final JPanel currentDollarCostInvestmentsOutput;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Constructs a dollar-cost investment JPanel.
   *
   * @param actionCommand the provided home page action command.
   * @param errorMessageLabel the label for the provided error message.
   */
  JDollarCostPanel(eHomePageActionCommand actionCommand, JLabel errorMessageLabel) {
    super(actionCommand, errorMessageLabel);

    stocksMap = new HashMap<>();

    this.setBorder(BorderFactory.createTitledBorder("Dollar-Cost Investing"));
    this.setLayout(new GridLayout(0, 1));
    //this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    // size and location
    Dimension d = new Dimension();
    d.setSize(500, 600);
    this.setMinimumSize(d);

    //<editor-fold desc="Strategy Input Panel">

    JPanel strategyInputPanel = new JPanel();
    strategyInputPanel.setLayout(new GridLayout(0, 1));

    //<editor-fold desc="Total Amount">
    JPanel totalAmountPanel = new JPanel();
    totalAmountPanel.setLayout(new GridLayout(0, 2));

    JLabel totalAmountLabel = new JLabel("Total Amount ($):");
    totalAmountPanel.add(totalAmountLabel);

    NumberFormat totalAmountFormat = NumberFormat.getNumberInstance();
    totalAmountFormat.setMaximumFractionDigits(2);
    this.totalAmountField = new JFormattedTextField(totalAmountFormat);
    this.totalAmountField.setColumns(10);

    totalAmountPanel.add(this.totalAmountField);

    strategyInputPanel.add(totalAmountPanel);

    //</editor-fold>

    //<editor-fold desc="Commission Fee">

    JPanel commissionFeePanel = new JPanel();
    commissionFeePanel.setLayout(new GridLayout(0, 2));

    JLabel commissionFeeLabel = new JLabel("Commission Fee ($):");
    commissionFeePanel.add(commissionFeeLabel);

    NumberFormat commissionFormat = NumberFormat.getNumberInstance();
    commissionFormat.setMaximumFractionDigits(2);
    this.commissionFeeField = new JFormattedTextField(commissionFormat);
    this.commissionFeeField.setColumns(10);

    commissionFeePanel.add(this.commissionFeeField);

    strategyInputPanel.add(commissionFeePanel);

    //</editor-fold>

    //<editor-fold desc="Start Date">

    JPanel startDatePanel = new JPanel();
    startDatePanel.setLayout(new GridLayout(0, 2));

    this.startDateLabel = new JLabel("Start Date (mm-dd-yyyy):");
    startDatePanel.add(this.startDateLabel);

    // using FormattedTextField for date field
    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    this.startDateTextField = new JFormattedTextField(dateFormat);
    this.startDateTextField.setColumns(10);

    startDatePanel.add(this.startDateTextField);

    strategyInputPanel.add(startDatePanel);

    //</editor-fold>

    //<editor-fold desc="Recurring Checkbox">

    JPanel recurringCheckBoxPanel = new JPanel();

    this.recurringCheckBox = new JCheckBox("Recurring");
    this.recurringCheckBox.setSelected(false);
    this.recurringCheckBox.setActionCommand(
        eDollarCostActionCommand.RECURRING.getActionCommandVal());

    var dollarCostPanelActionListener = new DollarCostPanelActionListener();
    this.recurringCheckBox.addActionListener(dollarCostPanelActionListener);

    recurringCheckBoxPanel.add(this.recurringCheckBox);

    strategyInputPanel.add(recurringCheckBoxPanel);

    //</editor-fold>

    //<editor-fold desc="Recurring Panel">

    this.recurringPanel = new JPanel();
    this.recurringPanel.setLayout(new GridLayout(0, 1));
    this.recurringPanel.setVisible(false);

    //<editor-fold desc="End Date">

    JPanel endDatePanel = new JPanel();
    endDatePanel.setLayout(new GridLayout(0, 2));

    JLabel endDateLabel = new JLabel("Optional End Date (mm-dd-yyyy):");
    endDatePanel.add(endDateLabel);

    // using FormattedTextField for date field
    this.endDateTextField = new JFormattedTextField(dateFormat);
    this.endDateTextField.setColumns(10);

    endDatePanel.add(this.endDateTextField);

    this.recurringPanel.add(endDatePanel);

    //</editor-fold>

    //<editor-fold desc="Interval Type">

    JPanel intervalTypePanel = new JPanel();
    intervalTypePanel.setLayout(new GridLayout(0, 2));

    JLabel intervalTypeLabel = new JLabel("Interval Type:");
    intervalTypePanel.add(intervalTypeLabel);

    String[] options = {
        eRecurringIntervalType.DAILY.getRecurringIntervalTypeVal(),
        eRecurringIntervalType.MONTHLY.getRecurringIntervalTypeVal(),
        eRecurringIntervalType.YEARLY.getRecurringIntervalTypeVal()
    };

    this.recurringIntervalTypeCombobox = new JComboBox<>();
    for (String option : options) {
      this.recurringIntervalTypeCombobox.addItem(option);
    }

    intervalTypePanel.add(this.recurringIntervalTypeCombobox);

    this.recurringPanel.add(intervalTypePanel);

    //</editor-fold>

    //<editor-fold desc="Interval">

    JPanel intervalDeltaPanel = new JPanel();
    intervalDeltaPanel.setLayout(new GridLayout(0, 2));

    JLabel intervalDeltaLabel = new JLabel("Interval (#):");
    intervalDeltaPanel.add(intervalDeltaLabel);

    NumberFormat intervalDeltaFormat = NumberFormat.getNumberInstance();
    this.recurringIntervalDeltaField = new JFormattedTextField(intervalDeltaFormat);
    this.recurringIntervalDeltaField.setColumns(10);

    intervalDeltaPanel.add(this.recurringIntervalDeltaField);

    this.recurringPanel.add(intervalDeltaPanel);

    //</editor-fold>

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Stock Input Panel">

    JPanel stockInputPanel = new JPanel();
    stockInputPanel.setLayout(new GridLayout(0, 2));
    stockInputPanel.setBorder(BorderFactory.createTitledBorder("Stocks"));

    //<editor-fold desc="Symbol">

    JLabel symbolLabel = new JLabel("Symbol:");
    stockInputPanel.add(symbolLabel);

    this.symbolTextField = new JFormattedTextField();
    this.symbolTextField.setColumns(10);

    stockInputPanel.add(symbolTextField);

    //</editor-fold>

    //<editor-fold desc="Percentage">

    JLabel percentLabel = new JLabel("Percentage:");
    stockInputPanel.add(percentLabel);

    // percentage text field
    NumberFormat percentFormat = NumberFormat.getPercentInstance();
    percentFormat.setMaximumFractionDigits(2);

    this.percentageTextField = new JFormattedTextField(new DecimalFormat("#.##"));
    this.percentageTextField.setColumns(10);

    stockInputPanel.add(this.percentageTextField);

    //</editor-fold>

    //<editor-fold desc="Add Stock Button">

    JButton stocksAddButton = new JButton("Add Stock");
    stocksAddButton.setActionCommand(eDollarCostActionCommand.ADD_STOCK.getActionCommandVal());
    stocksAddButton.addActionListener(dollarCostPanelActionListener);

    stockInputPanel.add(stocksAddButton);

    //</editor-fold>

    //<editor-fold desc="Remove Stock Button">

    JButton stocksRemoveButton = new JButton("Remove Stock");
    stocksRemoveButton.setActionCommand(
        eDollarCostActionCommand.REMOVE_STOCK.getActionCommandVal());
    stocksRemoveButton.addActionListener(dollarCostPanelActionListener);

    stockInputPanel.add(stocksRemoveButton);

    //</editor-fold>

    //<editor-fold desc="Current Stocks">

    this.currentStocksOutput = new JPanel();
    this.currentStocksOutput.setLayout(
        new BoxLayout(this.currentStocksOutput, BoxLayout.PAGE_AXIS));
    this.currentStocksOutput.setBorder(
        BorderFactory.createTitledBorder("Current Stock Percentages"));

    //stockInputPanel.add(this.currentStocksOutput);

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Create Dollar Cost Investment Button">

    this.enterButton = new JButton("Create Dollar Cost Investment");
    this.enterButton.setMaximumSize(new Dimension(10, 10));

    //</editor-fold>

    //<editor-fold desc="Output Panel">

    this.currentDollarCostInvestmentsOutput = new JPanel();
    this.currentDollarCostInvestmentsOutput
        .setLayout(new BoxLayout(this.currentDollarCostInvestmentsOutput, BoxLayout.PAGE_AXIS));
    this.currentDollarCostInvestmentsOutput
        .setBorder(BorderFactory.createTitledBorder("Current Dollar Cost Investments"));

    Dimension outD = new Dimension();
    d.setSize(400, 200);
    this.currentDollarCostInvestmentsOutput.setMinimumSize(outD);

    //</editor-fold>

    this.add(strategyInputPanel);
    this.add(this.recurringPanel);
    this.add(stockInputPanel);

    JScrollPane currentStocksScrollPane = new JScrollPane(this.currentStocksOutput);
    this.add(currentStocksScrollPane);

    this.add(this.enterButton);

    JScrollPane currentDollarCostInvestmentsScrollPane =
        new JScrollPane(this.currentDollarCostInvestmentsOutput);
    this.add(currentDollarCostInvestmentsScrollPane);

    this.setVisible(true);
  }

  //</editor-fold>

  //<editor-fold desc="Helper Methods">

  private void clearStocksInputFields() {
    this.symbolTextField.setText("");
    this.symbolTextField.removeAll();

    this.percentageTextField.setText("");
    this.percentageTextField.removeAll();
  }

  private void clearStrategyInputFields() {
    this.totalAmountField.setText("");
    this.totalAmountField.removeAll();

    this.commissionFeeField.setText("");
    this.commissionFeeField.removeAll();

    this.startDateTextField.setText("");
    this.startDateTextField.removeAll();
  }

  private void clearRecurringInputFields() {
    this.endDateTextField.setText("");
    this.endDateTextField.removeAll();

    this.recurringIntervalDeltaField.setText("");
    this.recurringIntervalDeltaField.removeAll();

    this.recurringIntervalTypeCombobox.setSelectedIndex(0);

    this.recurringCheckBox.setSelected(false);

    this.revalidate();
    this.repaint();
  }

  private void clearCurrentStocksOutput() {
    this.currentStocksOutput.removeAll();
  }

  private void clearDollarCostInvestmentsOutput() {
    this.currentDollarCostInvestmentsOutput.removeAll();
  }

  private boolean validateInputFields() {
    boolean result = true;
    StringBuilder errorMessage = new StringBuilder();

    if (this.totalAmountField.getValue() == null) {
      errorMessage.append("Given amount is not in valid format! \n");
      result = false;
    }
    if (!Utils.isValidDateInput(this.startDateTextField.getText())) {
      errorMessage.append("Given start date is not in valid format! \n");
      result = false;
    }
    if (!Utils.isValidFloatNumberInput(this.commissionFeeField.getText())) {
      errorMessage.append("Given commission fee is not in valid format! \n");
      result = false;
    }

    if (!result) {
      this.displayErrorMessage(errorMessage.toString());
    } else {
      this.displayErrorMessage("");
    }
    return result;
  }

  private boolean validateStocksInputFields() {
    boolean result = true;
    StringBuilder errorMessage = new StringBuilder();

    if (!Utils.isValidWordsInput(this.symbolTextField.getText())) {
      errorMessage.append("Given symbol is not in valid format! \n");
      result = false;
    }
    if (!Utils.isValidFloatNumberInput(this.percentageTextField.getText())) {
      errorMessage.append("Given percentage is not in valid format! \n");
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
  public void clearInputFields() {
    this.clearStocksInputFields();
    this.clearRecurringInputFields();
    this.clearStrategyInputFields();
  }

  @Override
  public void clearOutputLabel() {
    this.clearCurrentStocksOutput();
    this.clearDollarCostInvestmentsOutput();
  }

  @Override
  public void addFeatures(IPortfolioManagerFeatures features) {
    this.enterButton.addActionListener(e -> {
      if (this.validateInputFields()) {
        boolean isRecurring = this.recurringCheckBox.isSelected();

        Date endDate
            = isRecurring ? Utils.convertStringToDate(this.startDateTextField.getText()) :
            null;
        eRecurringIntervalType recurringType
            = isRecurring ? eRecurringIntervalType
                .fromValue(this.recurringIntervalTypeCombobox.getSelectedItem().toString()) :
            null;
        Integer recurringDelta
            = isRecurring ? Utils.convertStringToNumber(
                this.recurringIntervalDeltaField.getText()) : null;

        List<Pair<String, BigDecimal>> stocksList = new ArrayList<>();

        for (var s : this.stocksMap.entrySet()) {
          stocksList.add(new Pair<>(s.getKey(), s.getValue()));
        }

        boolean isSuccessful = features.addDollarCostInvestmentForPortfolio(
            this.portfolioId,
            Utils.convertStringToDate(this.startDateTextField.getText()),
            new BigDecimal(this.totalAmountField.getValue().toString()),
            stocksList,
            new BigDecimal(this.commissionFeeField.getText()),
            isRecurring,
            endDate,
            recurringType,
            recurringDelta);

        if (isSuccessful) {
          this.clearInputFields();
        }
      }
    });
  }

  /**
   * Loads the portfolio's dollar-cost investment strategies.
   *
   * @param dollarCostInvestments a list of the portfolio's dollar-cost investments.
   */
  void loadPortfolioDollarCostInvestments(
      List<Pair<String, IDollarCostInvestment>> dollarCostInvestments) {
    this.clearOutputLabel();

    // build output
    for (Pair<String, IDollarCostInvestment> stock : dollarCostInvestments) {
      JLabel symbolLabel = new JLabel("Stock Symbol: " + stock.getO1());

      JLabel dciDateLabel = new JLabel("Date: " + stock.getO2().getDate());
      JLabel dciAmountLabel = new JLabel("Amount: " + stock.getO2().getAmount());
      JLabel dciCommissionFeesLabel = new JLabel(
          "Commission Fees: " + stock.getO2().getCommissionFees());

      this.currentDollarCostInvestmentsOutput.add(symbolLabel);
      this.currentDollarCostInvestmentsOutput.add(dciDateLabel);
      this.currentDollarCostInvestmentsOutput.add(dciAmountLabel);
      this.currentDollarCostInvestmentsOutput.add(dciCommissionFeesLabel);

      boolean isRecurring = stock.getO2().getRecurringEvent() != null;
      JLabel dciIsRecurringLabel = new JLabel("Is Recurring: " + (isRecurring ? "Yes" : "No"));

      this.currentDollarCostInvestmentsOutput.add(dciIsRecurringLabel);

      if (isRecurring) {
        JLabel dciRecurringEndDateLabel = new JLabel(
            "End Date: " + stock.getO2().getRecurringEvent().getEndDate());
        JLabel dciRecurringIntervalTypeLabel = new JLabel(
            "Interval Type: " + stock.getO2().getRecurringEvent().getRecurringIntervalType());
        JLabel dciRecurringIntervalDeltaLabel = new JLabel(
            "Interval Delta: " + stock.getO2().getRecurringEvent().getRecurringIntervalDelta());

        this.currentDollarCostInvestmentsOutput.add(dciRecurringEndDateLabel);
        this.currentDollarCostInvestmentsOutput.add(dciRecurringIntervalTypeLabel);
        this.currentDollarCostInvestmentsOutput.add(dciRecurringIntervalDeltaLabel);
      }
    }
    this.currentDollarCostInvestmentsOutput.setVisible(true);
  }

  //</editor-fold>
}