package view;

import common.Utils;
import common.pair.Pair;
import features.IPortfolioManagerFeatures;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.portfolio.IPortfolioStockValue;
import view.JFrameView.eHomePageActionCommand;

/**
 * This class represents the single date operations panel of the JFrame GUI for this application.
 * Utilized for composition, value, and cost basis.
 */
class JSingleDateOperationPanel extends AbstractPanel {

  //<editor-fold desc="State variables">

  private JFormattedTextField dateTextField;
  private final JButton enterButton;

  private final JPanel outputPanel;
  private final JPanel resultPanel;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Construct a single date operation JPanel.
   *
   * @param actionCommand the provided home page action command.
   * @param errorMessageLabel the label for the provided error message.
   */
  JSingleDateOperationPanel(eHomePageActionCommand actionCommand, JLabel errorMessageLabel) {
    super(actionCommand, errorMessageLabel);

    // layout
    this.setLayout(new GridLayout(0, 1));

    //<editor-fold desc="Set Action Specific Labels">

    JLabel dateSelectDisplay = new JLabel();
    this.resultPanel = new JPanel();

    if (this.actionCommand == eHomePageActionCommand.COST_BASIS) {
      this.setBorder(BorderFactory.createTitledBorder("Portfolio Cost Basis"));
      dateSelectDisplay.setText("Date for portfolio cost basis (mm-dd-yyyy):");
      this.resultPanel.setBorder(BorderFactory.createTitledBorder("Cost Basis Result:"));
    } else if (this.actionCommand == eHomePageActionCommand.VALUE) {
      this.setBorder(BorderFactory.createTitledBorder("Portfolio Value"));
      dateSelectDisplay.setText("Date for portfolio value (mm-dd-yyyy):");
      this.resultPanel.setBorder(BorderFactory.createTitledBorder("Value Result:"));
    } else if (this.actionCommand == eHomePageActionCommand.COMPOSITION) {
      this.setBorder(BorderFactory.createTitledBorder("Portfolio Composition"));
      dateSelectDisplay.setText("Date for portfolio composition (mm-dd-yyyy):");
      this.resultPanel.setBorder(BorderFactory.createTitledBorder("Composition Result:"));
    }

    //</editor-fold>

    //<editor-fold desc="Input Panel">

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(0, 1));

    //<editor-fold desc="Date">

    JPanel datePanel = new JPanel();
    datePanel.setLayout(new GridLayout(0, 2));

    // add label
    dateSelectDisplay.setSize(20, 1);

    datePanel.add(dateSelectDisplay);

    // using FormattedTextField for date field
    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    this.dateTextField = new JFormattedTextField(dateFormat);
    this.dateTextField.setSize(20, 1);
    datePanel.add(this.dateTextField);

    //</editor-fold>

    //<editor-fold desc="Enter">

    String buttonName = "Operation not implemented";
    if (actionCommand == eHomePageActionCommand.COMPOSITION) {
      buttonName = "Get Composition";
    } else if (actionCommand == eHomePageActionCommand.VALUE) {
      buttonName = "Get Value";
    } else if (actionCommand == eHomePageActionCommand.COST_BASIS) {
      buttonName = "Get Cost Basis";
    }

    this.enterButton = new JButton(buttonName);
    this.enterButton.setLayout(new FlowLayout());

    //</editor-fold>

    inputPanel.add(datePanel);
    inputPanel.add(this.enterButton);

    inputPanel.setVisible(true);

    //</editor-fold>

    //<editor-fold desc="Result Panel">

    this.resultPanel.setLayout(new BoxLayout(this.resultPanel, BoxLayout.PAGE_AXIS));

    //<editor-fold desc="Output Panel">

    this.outputPanel = new JPanel();
    // top to bottom BoxLayout
    this.outputPanel.setLayout(new BoxLayout(this.outputPanel, BoxLayout.PAGE_AXIS));

    //</editor-fold>

    JScrollPane outputScrollPane = new JScrollPane(this.outputPanel);
    this.resultPanel.add(outputScrollPane);

    this.resultPanel.setVisible(false);

    //</editor-fold>

    this.add(inputPanel);
    this.add(this.resultPanel);

    this.setVisible(true);
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  private boolean validateInputFields() {
    boolean result = true;
    StringBuilder errorMessage = new StringBuilder();
    if (!Utils.isValidDateInput(this.dateTextField.getText())) {
      errorMessage.append("Given date is not in valid format!\n");
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

  //<editor-fold desc="Core methods">

  @Override
  public void addFeatures(IPortfolioManagerFeatures features) {
    if (this.actionCommand == eHomePageActionCommand.COMPOSITION) {
      this.enterButton.addActionListener(e -> {
        if (this.validateInputFields()) {
          features.getPortfolioComposition(
              this.portfolioId,
              Utils.convertStringToDate(this.dateTextField.getText()));
        }
      });
    } else if (this.actionCommand == eHomePageActionCommand.VALUE) {
      this.enterButton.addActionListener(e -> {
        if (this.validateInputFields()) {
          features.getPortfolioValue(
              this.portfolioId,
              Utils.convertStringToDate(this.dateTextField.getText()));
        }
      });
    } else if (this.actionCommand == eHomePageActionCommand.COST_BASIS) {
      this.enterButton.addActionListener(e -> {
        if (this.validateInputFields()) {
          features.getPortfolioCostBasis(
              this.portfolioId,
              Utils.convertStringToDate(this.dateTextField.getText()));
        }
      });
    }
  }

  @Override
  public void clearInputFields() {
    this.dateTextField.setText("");
    this.dateTextField.removeAll();
  }

  @Override
  public void clearOutputLabel() {
    this.resultPanel.setVisible(false);
    this.outputPanel.removeAll();
  }

  /**
   * Loads the portfolio's composition.
   *
   * @param composition a list of pairs of stock names and volumes.
   */
  void loadPortfolioComposition(List<Pair<String, BigDecimal>> composition) {
    this.clearOutputLabel();

    for (Pair<String, BigDecimal> stock : composition) {

      JPanel singleStockPanel = new JPanel();
      singleStockPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
      singleStockPanel.setBorder(BorderFactory.createTitledBorder(stock.getO1()));

      JLabel symbolLabel = new JLabel("Stock Volume: " + stock.getO2());

      singleStockPanel.add(symbolLabel);
      this.outputPanel.add(singleStockPanel);
    }

    this.resultPanel.setVisible(true);
  }

  /**
   * Loads the portfolio's value.
   *
   * @param value a pair of the total portfolio value and a list of the individual stock values.
   */
  void loadPortfolioValue(Pair<BigDecimal, List<IPortfolioStockValue>> value) {
    this.clearOutputLabel();

    JPanel totalValuePanel = new JPanel();
    totalValuePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

    totalValuePanel.add(new JLabel("Total Value: $" + value.getO1()));

    this.outputPanel.add(totalValuePanel);

    for (IPortfolioStockValue stock : value.getO2()) {

      JPanel singleStockPanel = new JPanel();
      singleStockPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
      singleStockPanel.setBorder(BorderFactory.createTitledBorder(stock.getSymbol()));

      // name
      JPanel namePanel = new JPanel();
      namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel nameLabel = new JLabel("Stock Name: " + stock.getName());
      nameLabel.setSize(20, 1);
      namePanel.add(nameLabel);

      // volume
      JPanel volumePanel = new JPanel();
      volumePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel volumeLabel = new JLabel("Volume: " + stock.getVolume());
      volumeLabel.setSize(20, 1);
      volumePanel.add(volumeLabel);

      // value
      JPanel valuePanel = new JPanel();
      valuePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel valueLabel = new JLabel("Value: $" + stock.getValue());
      valueLabel.setSize(20, 1);
      valuePanel.add(valueLabel);

      singleStockPanel.add(namePanel);
      singleStockPanel.add(volumePanel);
      singleStockPanel.add(valuePanel);

      this.outputPanel.add(singleStockPanel);
    }

    this.resultPanel.setVisible(true);
  }

  /**
   * Loads the portfolio's cost basis.
   *
   * @param costBasis the portfolio's cost basis on a given date.
   */
  void loadPortfolioCostBasis(BigDecimal costBasis) {
    this.clearOutputLabel();

    JPanel costBasisPanel = new JPanel();
    costBasisPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    costBasisPanel.add(new JLabel("Cost Basis: $" + costBasis));

    this.outputPanel.add(costBasisPanel);

    this.resultPanel.setVisible(true);
  }

  //</editor-fold>
}