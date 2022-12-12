package view;

import common.Utils;
import features.IPortfolioManagerFeatures;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.chart.IChart;
import view.JFrameView.eHomePageActionCommand;

/**
 * This class represents the chart panel of the JFrame GUI for this application.
 */
public class JChartPanel extends AbstractPanel {

  //<editor-fold desc="State variables">

  private final JFormattedTextField startDateTextField;
  private final JFormattedTextField endDateTextField;
  private final JButton enterButton;
  private final JPanel outputPanel;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Construct a chart JPanel.
   *
   * @param actionCommand the provided home page action command.
   * @param errorMessageLabel the label for the provided error message.
   */
  JChartPanel(eHomePageActionCommand actionCommand, JLabel errorMessageLabel) {
    super(actionCommand, errorMessageLabel);

    this.setBorder(BorderFactory.createTitledBorder("Performance Chart"));
    this.setLayout(new GridLayout(0, 1));

    //<editor-fold desc="Input Panel">

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new GridLayout(0, 1));

    //<editor-fold desc="Start Date">

    JPanel startDatePanel = new JPanel();
    startDatePanel.setLayout(new GridLayout(0, 2));

    JLabel startDateLabel = new JLabel("Start Date (mm-dd-yyy):");
    startDateLabel.setSize(20, 1);
    startDatePanel.add(startDateLabel);

    // using FormattedTextField for date field
    DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    this.startDateTextField = new JFormattedTextField(dateFormat);
    this.startDateTextField.setSize(20, 1);
    startDatePanel.add(this.startDateTextField);

    //</editor-fold>

    //<editor-fold desc="End Date">

    JPanel endDatePanel = new JPanel();
    endDatePanel.setLayout(new GridLayout(0, 2));

    JLabel endDateLabel = new JLabel("End Date (mm-dd-yyy):");
    endDateLabel.setSize(20, 1);
    endDatePanel.add(endDateLabel);

    // using FormattedTextField for date field
    this.endDateTextField = new JFormattedTextField(dateFormat);
    this.endDateTextField.setSize(20, 1);
    endDatePanel.add(this.endDateTextField);

    //</editor-fold>

    //<editor-fold desc="Enter">

    this.enterButton = new JButton("Generate Chart");
    this.enterButton.setLayout(new FlowLayout());

    //</editor-fold>

    inputPanel.add(startDatePanel);
    inputPanel.add(endDatePanel);
    inputPanel.add(this.enterButton);

    //</editor-fold>

    //<editor-fold desc="Output Panel">

    this.outputPanel = new JPanel();
    outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
    outputPanel.setBorder(BorderFactory.createTitledBorder("Performance Chart:"));

    this.outputPanel.setVisible(false);

    //</editor-fold>

    this.add(inputPanel);
    this.add(this.outputPanel);

    setVisible(true);
  }

  //</editor-fold>

  //<editor-fold desc="Helper Methods">

  private boolean validateInputFields() {
    boolean result = true;
    StringBuilder errorMessage = new StringBuilder();

    if (!Utils.isValidDateInput(this.startDateTextField.getText())) {
      errorMessage.append("Given start date is not in valid format! \n");
      result = false;
    }
    if (!Utils.isValidDateInput(this.endDateTextField.getText())) {
      errorMessage.append("Given end date is not in valid format! \n");
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

  //<editor-fold desc="Chart Helper Methods">

  //</editor-fold>

  //<editor-fold desc="Core Methods">

  @Override
  public void clearInputFields() {
    this.startDateTextField.setText("");
    this.startDateTextField.removeAll();
    this.endDateTextField.setText("");
    this.endDateTextField.removeAll();
  }

  @Override
  public void clearOutputLabel() {
    this.outputPanel.removeAll();
  }

  @Override
  public void addFeatures(IPortfolioManagerFeatures features) {
    this.enterButton.addActionListener(e -> {
      if (this.validateInputFields()) {
        features.loadPerformanceChart(
            this.portfolioId,
            Utils.convertStringToDate(this.startDateTextField.getText()),
            Utils.convertStringToDate(this.endDateTextField.getText()));
      }
    });
  }

  /**
   * Loads the performance chart of a portfolio.
   *
   * @param chart the performance chart of a portfolio for a user provided time range.
   */
  void loadPortfolioPerformanceChart(IChart chart) {
    this.outputPanel.removeAll();
    this.outputPanel.add(new BarChart(chart));
    this.outputPanel.setVisible(true);
  }

  //</editor-fold>
}