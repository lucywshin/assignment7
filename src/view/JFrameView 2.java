package view;

import common.pair.Pair;
import features.IPortfolioManagerFeatures;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.chart.IChart;
import model.portfolio.IDollarCostInvestment;
import model.portfolio.IObservableFlexiblePortfolioStock;
import model.portfolio.IPortfolioStockValue;

/**
 * An implementation of the JFrame view to show the GUI version of this application.
 */
public class JFrameView extends JFrame implements IJFrameView {

  //<editor-fold desc="Inner enums">

  /**
   * An enumeration for the various home page action commands.
   */
  enum eHomePageActionCommand {
    PORTFOLIO_SELECTION("Portfolio Selection"),
    BUY("Buy"),
    SELL("Sell"),
    COMPOSITION("Composition"),
    VALUE("Value"),
    COST_BASIS("Cost Basis"),
    DOLLAR_COST("Dollar Cost"),
    CHART("Chart"),
    CREATE_PORTFOLIO("Create Portfolio"),
    IMPORT("Import"),
    EXPORT("Export");

    private final String string;

    eHomePageActionCommand(String functionString) {
      this.string = functionString;
    }

    String getActionCommandVal() {
      return this.string;
    }

    static eHomePageActionCommand fromValue(String givenName) {
      for (eHomePageActionCommand actionCommand : values()) {
        if (actionCommand.string.equals(givenName)) {
          return actionCommand;
        }
      }
      return null;
    }
  }

  /**
   * An enumeration for the portfolio creation or operations card.
   */
  enum ePortfolioCard {
    CREATE("Create"),

    OPERATIONS("Operations");

    private final String string;

    ePortfolioCard(String functionString) {
      this.string = functionString;
    }

    String getPortfolioCardVal() {
      return this.string;
    }
  }

  /**
   * An enumeration for the various portfolio card selections.
   */
  enum eSelectedPortfolioCard {
    BUY("Selected Portfolio Buy"),
    SELL("Selected Portfolio Sell"),
    COMPOSITION("Selected Portfolio Composition"),
    VALUE("Selected Portfolio Value"),
    COST_BASIS("Selected Portfolio Cost Basis"),
    DOLLAR_COST("Selected Portfolio Dollar Cost"),
    CHART("Selected Portfolio Chart");

    private final String string;

    eSelectedPortfolioCard(String functionString) {
      this.string = functionString;
    }

    String getSelectedPortfolioCardVal() {
      return this.string;
    }
  }

  //</editor-fold>

  //<editor-fold desc="Inner classes">

  private class JPortfolioOperationsRedirector implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      eHomePageActionCommand actionEvent = eHomePageActionCommand.fromValue(e.getActionCommand());
      selectedPortfolioOperationsCardPanel.setVisible(true);

      CardLayout cl = (CardLayout) (selectedPortfolioOperationsCardPanel.getLayout());
      int portfolioId = portfoliosComboBox.getSelectedIndex();

      switch (actionEvent) {
        case COMPOSITION:
          selectedPortfolioCompositionPanel.setPortfolioId(portfolioId);
          cl.show(selectedPortfolioOperationsCardPanel,
              eSelectedPortfolioCard.COMPOSITION.getSelectedPortfolioCardVal());
          break;
        case VALUE:
          selectedPortfolioValuePanel.setPortfolioId(portfolioId);
          cl.show(selectedPortfolioOperationsCardPanel,
              eSelectedPortfolioCard.VALUE.getSelectedPortfolioCardVal());
          break;
        case COST_BASIS:
          selectedPortfolioCostBasisPanel.setPortfolioId(portfolioId);
          cl.show(selectedPortfolioOperationsCardPanel,
              eSelectedPortfolioCard.COST_BASIS.getSelectedPortfolioCardVal());
          break;
        case DOLLAR_COST:
          selectedPortfolioDollarCostPanel.setPortfolioId(portfolioId);
          cl.show(selectedPortfolioOperationsCardPanel,
              eSelectedPortfolioCard.DOLLAR_COST.getSelectedPortfolioCardVal());
          break;
        case CHART:
          selectedPortfolioChartPanel.setPortfolioId(portfolioId);
          cl.show(selectedPortfolioOperationsCardPanel,
              eSelectedPortfolioCard.CHART.getSelectedPortfolioCardVal());
          break;
        default:
          break;
      }
    }
  }

  private class JPortfolioOptionsCard implements ItemListener {

    // needed to switch cards based on option selected
    @Override
    public void itemStateChanged(ItemEvent e) {
      // only interpret the event if the event is thrown from selected item
      selectedPortfolioOperationsCardPanel.setVisible(false);
      if (e.getStateChange() == ItemEvent.SELECTED) {
        CardLayout cl = (CardLayout) (portfolioOperationsCardPanel.getLayout());

        ePortfolioCard portfolioCard;
        if (e.getItem().equals(CREATE_PORTFOLIO_ITEM_STRING)) {
          portfolioCard = ePortfolioCard.CREATE;
        } else {
          portfolioCard = ePortfolioCard.OPERATIONS;
        }
        cl.show(portfolioOperationsCardPanel, portfolioCard.getPortfolioCardVal());
      }
    }
  }

  //</editor-fold>

  //<editor-fold desc="Constants">

  private final static String CREATE_PORTFOLIO_ITEM_STRING = "+ Add New Portfolio";

  //</editor-fold>

  //<editor-fold desc="Frame State variables">

  private final JLabel errorMessageLabel;

  private final JComboBox<String> portfoliosComboBox;

  private final JPanel portfolioOperationsCardPanel;

  private final JTextArea portfolioNameTextArea;
  private final JButton portfolioCreateButton;
  private final JButton importButton;
  private final JButton exportButton;
  private final JButton buyButton;
  private final JButton sellButton;
  private final JButton dollarCostButton;

  private final JLabel fileImportDisplay;
  private final JLabel fileExportDisplay;

  private final JPanel selectedPortfolioOperationsCardPanel;
  private final JBuySellPanel selectedPortfolioBuyPanel;
  private final JBuySellPanel selectedPortfolioSellPanel;
  private final JSingleDateOperationPanel selectedPortfolioCompositionPanel;
  private final JSingleDateOperationPanel selectedPortfolioValuePanel;
  private final JSingleDateOperationPanel selectedPortfolioCostBasisPanel;
  private final JDollarCostPanel selectedPortfolioDollarCostPanel;
  private final JChartPanel selectedPortfolioChartPanel;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Construct a JFrame view.
   */
  public JFrameView() {

    //<editor-fold desc="Home page JFrame">

    super("Portfolio Manager");

    // size and location
    Dimension d = new Dimension();
    d.setSize(800, 900);
    this.setMinimumSize(d);

    this.setLocation(200, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // layout
    this.setLayout(new BorderLayout());

    //</editor-fold>

    //<editor-fold desc="Error Message Panel">

    this.errorMessageLabel = new JLabel();
    this.add(this.errorMessageLabel, BorderLayout.PAGE_START);

    //</editor-fold>

    //<editor-fold desc="Content Panel">

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    //<editor-fold desc="Portfolio Operations Panel">

    JPanel portfolioOperationsPanel = new JPanel();

    portfolioOperationsPanel.setLayout(new BoxLayout(portfolioOperationsPanel, BoxLayout.Y_AXIS));

    //<editor-fold desc="Portfolio Selection Panel">

    JPanel portfolioOperationsSelectPanel = new JPanel();

    // layout
    portfolioOperationsSelectPanel.setLayout(new FlowLayout());

    // add label
    JLabel portfolioSelectDisplay =
        new JLabel("Select the portfolio you would like to work with:");
    portfolioOperationsSelectPanel.add(portfolioSelectDisplay);

    // add dropdown
    this.portfoliosComboBox = new JComboBox<>();
    this.portfoliosComboBox.setSize(50, 0);
    this.refreshPortfolioComboBoxItems(new ArrayList<>());

    this.portfoliosComboBox.setEditable(false);
    this.portfoliosComboBox.addItemListener(new JPortfolioOptionsCard());

    portfolioOperationsSelectPanel.add(this.portfoliosComboBox);

    // add dropdown to operations panel
    portfolioOperationsPanel.add(portfolioOperationsSelectPanel);

    //</editor-fold>

    //<editor-fold desc="Portfolio Operations Card Panel">

    // cards for switching between text field and buttons based on selected option in dropdown
    this.portfolioOperationsCardPanel = new JPanel();

    this.portfolioOperationsCardPanel.setLayout(new CardLayout());

    //<editor-fold desc="Portfolio Create Panel">

    JPanel portfolioOperationsCreateCardPanel = new JPanel();

    // layout
    portfolioOperationsCreateCardPanel.setLayout(new FlowLayout());

    // portfolio Name label
    JLabel portfolioNameLabel = new JLabel("Portfolio Name: ");
    portfolioOperationsCreateCardPanel.add(portfolioNameLabel);

    // portfolio Name text box
    this.portfolioNameTextArea = new JTextArea(1, 20);
    portfolioOperationsCreateCardPanel.add(this.portfolioNameTextArea);

    // create button
    this.portfolioCreateButton = new JButton("Create Portfolio");
    this.portfolioCreateButton.setActionCommand(
        eHomePageActionCommand.CREATE_PORTFOLIO.getActionCommandVal());
    // action listener is a feature
    portfolioOperationsCreateCardPanel.add(this.portfolioCreateButton);

    this.portfolioOperationsCardPanel
        .add(portfolioOperationsCreateCardPanel, ePortfolioCard.CREATE.getPortfolioCardVal());

    //</editor-fold>

    //<editor-fold desc="Portfolio Operations Panel">

    JPanel portfolioOperationsOptionsCardPanel = new JPanel();

    // layout
    portfolioOperationsOptionsCardPanel.setLayout(new FlowLayout());

    var portfolioOperationsRedirector = new JPortfolioOperationsRedirector();

    this.buyButton = new JButton("Buy");
    this.buyButton.setActionCommand(eHomePageActionCommand.BUY.getActionCommandVal());
    portfolioOperationsOptionsCardPanel.add(this.buyButton);

    this.sellButton = new JButton("Sell");
    this.sellButton.setActionCommand(eHomePageActionCommand.SELL.getActionCommandVal());
    portfolioOperationsOptionsCardPanel.add(this.sellButton);

    JButton compositionButton = new JButton("Composition");
    compositionButton.setActionCommand(
        eHomePageActionCommand.COMPOSITION.getActionCommandVal());
    compositionButton.addActionListener(portfolioOperationsRedirector);
    portfolioOperationsOptionsCardPanel.add(compositionButton);

    JButton valueButton = new JButton("Value");
    valueButton.setActionCommand(eHomePageActionCommand.VALUE.getActionCommandVal());
    valueButton.addActionListener(portfolioOperationsRedirector);
    portfolioOperationsOptionsCardPanel.add(valueButton);

    JButton costBasisButton = new JButton("Cost Basis");
    costBasisButton.setActionCommand(eHomePageActionCommand.COST_BASIS.getActionCommandVal());
    costBasisButton.addActionListener(portfolioOperationsRedirector);
    portfolioOperationsOptionsCardPanel.add(costBasisButton);

    this.dollarCostButton = new JButton("Dollar-Cost Investment");
    this.dollarCostButton.setActionCommand(
        eHomePageActionCommand.DOLLAR_COST.getActionCommandVal());
    this.dollarCostButton.addActionListener(portfolioOperationsRedirector);
    portfolioOperationsOptionsCardPanel.add(this.dollarCostButton);

    JButton chartButton = new JButton("Chart");
    chartButton.setActionCommand(eHomePageActionCommand.CHART.getActionCommandVal());
    chartButton.addActionListener(portfolioOperationsRedirector);
    portfolioOperationsOptionsCardPanel.add(chartButton);

    this.portfolioOperationsCardPanel
        .add(portfolioOperationsOptionsCardPanel, ePortfolioCard.OPERATIONS.getPortfolioCardVal());

    //</editor-fold>

    portfolioOperationsPanel.add(this.portfolioOperationsCardPanel);

    //</editor-fold>

    //<editor-fold desc="Import Panel">

    JPanel fileImportPanel = new JPanel();

    // layout
    fileImportPanel.setLayout(new FlowLayout());

    // import button
    this.importButton = new JButton("Import");
    this.importButton.setActionCommand(eHomePageActionCommand.IMPORT.getActionCommandVal());
    // action listener is a feature
    fileImportPanel.add(this.importButton);

    // status label
    this.fileImportDisplay = new JLabel("File path will appear here.");
    fileImportPanel.add(this.fileImportDisplay);

    portfolioOperationsPanel.add(fileImportPanel);

    //</editor-fold>

    //<editor-fold desc="Export Panel">

    // export
    JPanel fileExportPanel = new JPanel();

    // layout
    fileExportPanel.setLayout(new FlowLayout());

    // export button
    this.exportButton = new JButton("Export");
    this.exportButton.setActionCommand(eHomePageActionCommand.EXPORT.getActionCommandVal());
    // action listener is a feature
    fileExportPanel.add(this.exportButton);

    // status label
    this.fileExportDisplay = new JLabel("File path will appear here.");
    fileExportPanel.add(this.fileExportDisplay);

    portfolioOperationsPanel.add(fileExportPanel);

    //</editor-fold>

    contentPanel.add(portfolioOperationsPanel, BorderLayout.CENTER);

    //</editor-fold>

    //<editor-fold desc="Selected Portfolio Dedicated Panel">

    this.selectedPortfolioOperationsCardPanel = new JPanel();

    this.selectedPortfolioOperationsCardPanel.setLayout(new CardLayout());
    this.selectedPortfolioOperationsCardPanel.setVisible(false);

    Dimension cd = new Dimension();
    cd.setSize(800, 600);
    this.selectedPortfolioOperationsCardPanel.setMinimumSize(cd);

    // buy
    this.selectedPortfolioBuyPanel = new JBuySellPanel(eHomePageActionCommand.BUY,
        this.errorMessageLabel);
    this.selectedPortfolioOperationsCardPanel
        .add(selectedPortfolioBuyPanel, eSelectedPortfolioCard.BUY.getSelectedPortfolioCardVal());

    // sell
    this.selectedPortfolioSellPanel = new JBuySellPanel(eHomePageActionCommand.SELL,
        this.errorMessageLabel);
    this.selectedPortfolioOperationsCardPanel
        .add(selectedPortfolioSellPanel, eSelectedPortfolioCard.SELL.getSelectedPortfolioCardVal());

    // cost basis
    this.selectedPortfolioCostBasisPanel
        = new JSingleDateOperationPanel(eHomePageActionCommand.COST_BASIS, this.errorMessageLabel);
    this.selectedPortfolioOperationsCardPanel
        .add(selectedPortfolioCostBasisPanel,
            eSelectedPortfolioCard.COST_BASIS.getSelectedPortfolioCardVal());

    // value
    this.selectedPortfolioValuePanel
        = new JSingleDateOperationPanel(eHomePageActionCommand.VALUE, this.errorMessageLabel);
    this.selectedPortfolioOperationsCardPanel
        .add(selectedPortfolioValuePanel,
            eSelectedPortfolioCard.VALUE.getSelectedPortfolioCardVal());

    // composition
    this.selectedPortfolioCompositionPanel
        = new JSingleDateOperationPanel(eHomePageActionCommand.COMPOSITION, this.errorMessageLabel);
    this.selectedPortfolioOperationsCardPanel
        .add(selectedPortfolioCompositionPanel,
            eSelectedPortfolioCard.COMPOSITION.getSelectedPortfolioCardVal());

    // dollar-cost investing
    this.selectedPortfolioDollarCostPanel
        = new JDollarCostPanel(eHomePageActionCommand.DOLLAR_COST, this.errorMessageLabel);
    this.selectedPortfolioOperationsCardPanel
        .add(selectedPortfolioDollarCostPanel,
            eSelectedPortfolioCard.DOLLAR_COST.getSelectedPortfolioCardVal());

    // chart
    this.selectedPortfolioChartPanel
        = new JChartPanel(eHomePageActionCommand.CHART, this.errorMessageLabel);
    this.selectedPortfolioOperationsCardPanel
        .add(selectedPortfolioChartPanel,
            eSelectedPortfolioCard.CHART.getSelectedPortfolioCardVal());

    contentPanel.add(selectedPortfolioOperationsCardPanel);

    //</editor-fold>

    this.add(contentPanel, BorderLayout.CENTER);

    //</editor-fold>

    setVisible(true);
  }

  //</editor-fold>

  //<editor-fold desc="Helper methods">

  private void refreshPortfolioComboBoxItems(List<Pair<Integer, String>> portfolioList) {
    this.portfoliosComboBox.removeAllItems();

    for (var portfolio : portfolioList) {
      this.portfoliosComboBox.addItem(portfolio.getO2());
    }

    this.portfoliosComboBox.addItem(CREATE_PORTFOLIO_ITEM_STRING);
  }

  //</editor-fold>

  //<editor-fold desc="Core methods">

  @Override
  public void addFeatures(IPortfolioManagerFeatures features) {
    // portfolio create button
    this.portfolioCreateButton.addActionListener(e ->
        features.createPortfolio(this.portfolioNameTextArea.getText()));

    final JFileChooser fileChooser = new JFileChooser(".");
    FileNameExtensionFilter filter =
        new FileNameExtensionFilter("CSV Files", "csv");
    fileChooser.setFileFilter(filter);

    // import button
    this.importButton.addActionListener(e -> {
      int chosenFile = fileChooser.showOpenDialog(JFrameView.this);
      if (chosenFile == JFileChooser.APPROVE_OPTION) {
        File f = fileChooser.getSelectedFile();
        if (features.importPortfolios(f.getAbsolutePath())) {
          this.fileImportDisplay.setText("Import Successful!");
        } else {
          this.fileImportDisplay.setText("Import Failed! Check Error message.");
        }
      }
    });

    // export button
    this.exportButton.addActionListener(e -> {
      int chosenFile = fileChooser.showOpenDialog(JFrameView.this);
      if (chosenFile == JFileChooser.APPROVE_OPTION) {
        File f = fileChooser.getSelectedFile();
        if (features.exportPortfolios(f.getAbsolutePath())) {
          this.fileExportDisplay.setText("Export Successful!");
        } else {
          this.fileImportDisplay.setText("Export Failed! Check Error message.");
        }
      }
    });

    // buy button
    this.buyButton.addActionListener(e -> {
      int portfolioId = portfoliosComboBox.getSelectedIndex();
      this.selectedPortfolioBuyPanel.setPortfolioId(portfolioId);

      if (features.loadStocksForPortfolio(portfolioId)) {
        CardLayout cl = (CardLayout) (selectedPortfolioOperationsCardPanel.getLayout());
        cl.show(selectedPortfolioOperationsCardPanel,
            eSelectedPortfolioCard.BUY.getSelectedPortfolioCardVal());

        this.selectedPortfolioOperationsCardPanel.setVisible(true);
      }
    });

    // sell button
    this.sellButton.addActionListener(e -> {
      int portfolioId = portfoliosComboBox.getSelectedIndex();
      this.selectedPortfolioSellPanel.setPortfolioId(portfolioId);

      if (features.loadStocksForPortfolio(portfolioId)) {
        CardLayout cl = (CardLayout) (selectedPortfolioOperationsCardPanel.getLayout());
        cl.show(selectedPortfolioOperationsCardPanel,
            eSelectedPortfolioCard.SELL.getSelectedPortfolioCardVal());

        this.selectedPortfolioOperationsCardPanel.setVisible(true);
      }
    });

    // dollar cost button
    this.dollarCostButton.addActionListener(e -> {
      int portfolioId = portfoliosComboBox.getSelectedIndex();
      this.selectedPortfolioDollarCostPanel.setPortfolioId(portfolioId);

      if (features.loadDollarCostInvestmentsForPortfolio(portfolioId)) {
        CardLayout cl = (CardLayout) (selectedPortfolioOperationsCardPanel.getLayout());
        cl.show(selectedPortfolioOperationsCardPanel,
            eSelectedPortfolioCard.DOLLAR_COST.getSelectedPortfolioCardVal());

        this.selectedPortfolioOperationsCardPanel.setVisible(true);
      }
    });

    this.selectedPortfolioBuyPanel.addFeatures(features);
    this.selectedPortfolioSellPanel.addFeatures(features);
    this.selectedPortfolioCompositionPanel.addFeatures(features);
    this.selectedPortfolioValuePanel.addFeatures(features);
    this.selectedPortfolioCostBasisPanel.addFeatures(features);
    this.selectedPortfolioDollarCostPanel.addFeatures(features);
    this.selectedPortfolioChartPanel.addFeatures(features);
  }

  @Override
  public void displayErrorMessage(String errorMessage) {
    if (errorMessage == null) {
      this.errorMessageLabel.setVisible(false);
    } else {
      this.errorMessageLabel.setVisible(true);
      this.errorMessageLabel.setText(errorMessage);
    }
  }

  @Override
  public void loadPortfoliosList(List<Pair<Integer, String>> portfolioList) {
    this.refreshPortfolioComboBoxItems(portfolioList);
  }

  @Override
  public void createPortfolio(String portfolioName) {
    this.portfolioNameTextArea.setText("");
  }

  @Override
  public void loadPortfolioStocksList(List<IObservableFlexiblePortfolioStock> portfolioStocks) {
    this.selectedPortfolioBuyPanel.loadPortfolioStocksList(portfolioStocks);
    this.selectedPortfolioSellPanel.loadPortfolioStocksList(portfolioStocks);
  }

  @Override
  public void loadPortfolioComposition(List<Pair<String, BigDecimal>> composition) {
    this.selectedPortfolioCompositionPanel.loadPortfolioComposition(composition);
  }

  @Override
  public void loadPortfolioValue(Pair<BigDecimal, List<IPortfolioStockValue>> value) {
    this.selectedPortfolioValuePanel.loadPortfolioValue(value);
  }

  @Override
  public void loadPortfolioCostBasis(BigDecimal costBasis) {
    this.selectedPortfolioCostBasisPanel.loadPortfolioCostBasis(costBasis);
  }

  @Override
  public void loadPortfolioDollarCostInvestments(
      List<Pair<String, IDollarCostInvestment>> dollarCostInvestments) {
    this.selectedPortfolioDollarCostPanel.loadPortfolioDollarCostInvestments(dollarCostInvestments);
  }

  @Override
  public void loadPerformanceChart(IChart chart) {
    this.selectedPortfolioChartPanel.loadPortfolioPerformanceChart(chart);
  }

  //</editor-fold>
}
