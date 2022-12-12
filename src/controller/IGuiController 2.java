package controller;

import view.IJFrameView;

/**
 * This interface represents a controller used for the Java Swing GUI.
 */
public interface IGuiController {

  /**
   * Sets the view for the controller as the specified view object.
   *
   * @param view the view object to be used with this controller.
   */
  void setView(IJFrameView view);
}
