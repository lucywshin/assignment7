package view;

import features.IPortfolioManagerFeatures;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import view.JFrameView.eHomePageActionCommand;

/**
 * This class represents an abstract JPanel.
 */
abstract class AbstractPanel extends JPanel implements IPortfolioOperationPanel {

  //<editor-fold desc="State variables">

  int portfolioId;
  final eHomePageActionCommand actionCommand;
  final JLabel errorMessageLabel;

  //</editor-fold>

  //<editor-fold desc="Constructors">

  /**
   * Constructs an AbstractPanel.
   *
   * @param actionCommand the action command from the home page.
   * @param errorMessageLabel the label for the error message.
   */
  AbstractPanel(eHomePageActionCommand actionCommand, JLabel errorMessageLabel) {
    this.actionCommand = actionCommand;

    //<editor-fold desc="Error Message Panel">

    this.errorMessageLabel = errorMessageLabel;

    //</editor-fold>
  }

  //</editor-fold>

  @Override
  public abstract void addFeatures(IPortfolioManagerFeatures features);

  @Override
  public abstract void clearInputFields();

  @Override
  public abstract void clearOutputLabel();

  @Override
  public void displayErrorMessage(String errorMessage) {
    if (errorMessage == null) {
      this.errorMessageLabel.setVisible(false);
    } else {
      this.errorMessageLabel.setForeground(Color.RED);
      this.errorMessageLabel.setVisible(true);
      this.errorMessageLabel.setText("ERROR: " + errorMessage);
    }
  }

  @Override
  public void setPortfolioId(int portfolioId) {
    this.portfolioId = portfolioId;

    this.clearInputFields();
    this.clearOutputLabel();
  }
}
