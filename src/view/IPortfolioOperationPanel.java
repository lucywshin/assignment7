package view;

import features.IPortfolioManagerFeatures;

/**
 * This interface represents the portfolio operation panel of the JFrame GUI application.
 */
interface IPortfolioOperationPanel {

  /**
   * Grants access to the features of the application to the view via a features object.
   *
   * @param features the features available in the application.
   */
  void addFeatures(IPortfolioManagerFeatures features);

  /**
   * Clears the input fields.
   */
  void clearInputFields();

  /**
   * Clears the output label.
   */
  void clearOutputLabel();

  /**
   * Displays an error message.
   *
   * @param errorMessage the provided error message to display.
   */
  void displayErrorMessage(String errorMessage);

  /**
   * Sets the portfolio id.
   *
   * @param portfolioId the provided portfolio id to be set.
   */
  void setPortfolioId(int portfolioId);
}
