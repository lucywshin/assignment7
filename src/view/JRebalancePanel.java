package view;

import common.Utils;
import common.pair.Pair;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import features.IPortfolioManagerFeatures;
import model.portfolio.IPortfolioStockValue;
import model.portfolio.IRebalance;
import model.portfolio.StockDataSourceException;
import view.JFrameView.eHomePageActionCommand;

/**
 * This class represents the rebalance panel of the JFrame GUI for this application.
 */
class JRebalancePanel extends AbstractPanel {

  private JFormattedTextField dateTextField;
  private final JButton enterButton;
  private JButton rebalanceButton;
  private final JPanel resultPanel;
  private final Map<String, BigDecimal> stocksMap;

  private final Map<String, JTextField> stocksWeights;
  private final ArrayList<String> stocksList;
  private final ArrayList<BigDecimal> weightsList;

  /**
   * An enumeration for the action commands of dollar-cost investing.
   */
  enum eRebalanceActionCommand {
    ENTER("Enter"),
    REBALANCE("Rebalance");
    private final String string;

    eRebalanceActionCommand(String functionString) {
      this.string = functionString;
    }

    String getActionCommandVal() {
      return this.string;
    }

    static JRebalancePanel.eRebalanceActionCommand fromValue(String givenName) {
      for (JRebalancePanel.eRebalanceActionCommand actionCommand : values()) {
        if (actionCommand.string.equals(givenName)) {
          return actionCommand;
        }
      }
      return null;
    }
  }

  /**
   * Construct a single date operation JPanel.
   *
   * @param actionCommand     the provided home page action command.
   * @param errorMessageLabel the label for the provided error message.
   */
  JRebalancePanel(eHomePageActionCommand actionCommand, JLabel errorMessageLabel) {
    super(actionCommand, errorMessageLabel);
    stocksMap = new HashMap<>();
    stocksList = new ArrayList<>();
    weightsList = new ArrayList<>();
    stocksWeights = new HashMap<>();

    this.setLayout(new GridLayout(0, 1));

    JLabel dateSelectDisplay = new JLabel();
    this.resultPanel = new JPanel();

    this.setBorder(BorderFactory.createTitledBorder("Rebalance Portfolio"));
    dateSelectDisplay.setText("Date for portfolio rebalance (mm-dd-yyyy):");
    this.resultPanel.setBorder(BorderFactory.createTitledBorder("Stocks and Weights:"));

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(0, 1));

    JPanel datePanel = new JPanel();
    datePanel.setLayout(new GridLayout(0, 2));

    // add label
    dateSelectDisplay.setSize(20, 1);

    datePanel.add(dateSelectDisplay);

    // using FormattedTextField for date field
    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    this.dateTextField = new JFormattedTextField(dateFormat);
    this.dateTextField.setSize(20, 1);
    this.dateTextField.setMaximumSize(new Dimension(10, 10));
    datePanel.add(this.dateTextField);

    this.enterButton = new JButton("Get Portfolio Weights");
    enterButton.setActionCommand(JRebalancePanel.eRebalanceActionCommand.ENTER.getActionCommandVal());
    this.enterButton.setLayout(new FlowLayout());
    this.enterButton.setMaximumSize(new Dimension(10, 10));

    inputPanel.add(datePanel);
    inputPanel.add(enterButton);

    inputPanel.setVisible(true);

    JPanel outputPanel = new JPanel();
    // top to bottom BoxLayout
    outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
    outputPanel.setBorder(BorderFactory.createTitledBorder("Rebalance"));

    JScrollPane currentStocksScrollPane = new JScrollPane(resultPanel);
    outputPanel.add(currentStocksScrollPane);

    this.resultPanel.setLayout(new BoxLayout(this.resultPanel, BoxLayout.PAGE_AXIS));

    this.resultPanel.setVisible(false);

    this.add(inputPanel);
    this.add(outputPanel);
    //this.add(resultPanel);

    this.setVisible(true);
  }

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

  @Override
  public void addFeatures(IPortfolioManagerFeatures features) {
      this.enterButton.addActionListener(e -> {
        if (this.validateInputFields()) {
          features.loadPortfolioRebalanceDate(
                  this.portfolioId,
                  Utils.convertStringToDate(this.dateTextField.getText()));
        }
        this.rebalanceButton.addActionListener(d -> {
          if (this.validateInputFields()) {
            try {
              List<Pair<String, BigDecimal>> list = new ArrayList<>();
              if (validateInputFields()) {
                for (Map.Entry<String,JTextField> entry : stocksWeights.entrySet()) {
                  String symbol = entry.getKey();
                  BigDecimal percentage = new BigDecimal(entry.getValue().getText());
                  list.add(new Pair<String, BigDecimal>(symbol, percentage));
                }
              }
              boolean isSuccessful =
                      features.addPortfolioRebalance(
                      this.portfolioId,
                      Utils.convertStringToDate(this.dateTextField.getText()),
                      list);
              if (isSuccessful) {
                this.clearInputFields();
              }
            } catch (StockDataSourceException ex) {
              throw new RuntimeException(ex);
            }
          }
          stocksMap.clear();
        });
      });
  }

  @Override
  public void clearInputFields() {
    this.dateTextField.setText("");
    this.dateTextField.removeAll();
  }

  @Override
  public void clearOutputLabel() {
    this.resultPanel.removeAll();
  }

  /**
   * Loads the portfolio data.
   *
   * @param value a pair of the total portfolio value and a list of the individual stock values.
   */
  void loadRebalanceForPortfolio(List<Pair<String, IRebalance>> rebalance,
                                 Pair<BigDecimal, List<IPortfolioStockValue>> value) throws Exception {
    this.clearOutputLabel();

    BigDecimal totalValue = value.getO1();

    for (Pair<String, IRebalance> stock : rebalance) {

      IPortfolioStockValue ips = null;
      for (IPortfolioStockValue s : value.getO2()) {
        if (s.getSymbol().equals(stock.getO1())) {
          ips = s;
        }
      }

      // name
      JPanel namePanel = new JPanel();
      namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel nameLabel = new JLabel("Stock Symbol: " + stock.getO1());
      nameLabel.setSize(20, 1);
      namePanel.add(nameLabel);

      // volume
      JPanel volumePanel = new JPanel();
      volumePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel volumeLabel = new JLabel("Volume: " + ips.getVolume()
              .setScale(2, RoundingMode.HALF_DOWN));
      volumeLabel.setSize(20, 1);
      volumePanel.add(volumeLabel);

      // value
      JPanel valuePanel = new JPanel();
      valuePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel valueLabel = new JLabel("Value: $" + stock.getO2().getAmount()
              .setScale(2, RoundingMode.HALF_DOWN));
      valueLabel.setSize(20, 1);
      valuePanel.add(valueLabel);

      // weight
      JPanel weightPanel = new JPanel();
      weightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

      int stockWeight = stock.getO2().getWeight().intValue();

      JLabel currWeightLabel = new JLabel("New Weight: " + stock.getO2().getWeight()
              .setScale(2, RoundingMode.HALF_DOWN) + "%");

      weightPanel.add(currWeightLabel);

      resultPanel.add(namePanel);
      resultPanel.add(volumePanel);
      resultPanel.add(valuePanel);
      resultPanel.add(weightPanel);
    }

    this.resultPanel.setVisible(true);
  }

  /**
   * Loads the portfolio data.
   *
   * @param value a pair of the total portfolio value and a list of the individual stock values.
   */
  void loadRebalanceForPortfolioDate(Pair<BigDecimal, List<IPortfolioStockValue>> value) {

    JPanel outputPanel = new JPanel();
    // top to bottom BoxLayout
    outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
    JScrollPane currentStocksScrollPane = new JScrollPane();
    outputPanel.add(currentStocksScrollPane);

    BigDecimal totalValue = value.getO1();

    for (IPortfolioStockValue stock : value.getO2()) {

      // name
      JPanel namePanel = new JPanel();
      namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel nameLabel = new JLabel("Stock Symbol: " + stock.getSymbol());
      nameLabel.setSize(20, 1);
      namePanel.add(nameLabel);

      // volume
      JPanel volumePanel = new JPanel();
      volumePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel volumeLabel = new JLabel("Volume: " + stock.getVolume()
              .setScale(2, RoundingMode.HALF_DOWN));
      volumeLabel.setSize(20, 1);
      volumePanel.add(volumeLabel);

      // value
      JPanel valuePanel = new JPanel();
      valuePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      JLabel valueLabel = new JLabel("Value: $" + stock.getValue()
              .setScale(2, RoundingMode.HALF_DOWN));
      valueLabel.setSize(20, 1);
      valuePanel.add(valueLabel);

      // weight
      JPanel weightPanel = new JPanel();
      weightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

      float a = stock.getValue().intValue();
      float b = totalValue.intValue();
      int stockWeight = Math.round(a / b * 100);

      JLabel weightLabel = new JLabel("Weight: ");
      JLabel percent = new JLabel("%");
      JTextField weightTextField = new JFormattedTextField(stockWeight);
      weightLabel.setSize(20, 1);
      weightPanel.add(weightLabel);
      weightPanel.add(weightTextField);
      weightPanel.add(percent);

      stocksWeights.put(stock.getSymbol(), weightTextField);

      outputPanel.add(namePanel);
      outputPanel.add(volumePanel);
      outputPanel.add(valuePanel);
      outputPanel.add(weightPanel);
    }

    this.rebalanceButton = new JButton("Rebalance");
    rebalanceButton.setActionCommand(eRebalanceActionCommand.REBALANCE.getActionCommandVal());
    this.rebalanceButton.setLayout(new FlowLayout());

    outputPanel.add(rebalanceButton);

    resultPanel.add(outputPanel);
    this.resultPanel.setVisible(true);
  }

}

