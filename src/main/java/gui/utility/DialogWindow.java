package gui.utility;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JOptionPane;

/**
 * This class is nothing but wrapper for JOptionPane.
 * <p>
 * Allows to mock dialog windows in unit tests.
 */
public class DialogWindow {
  
  /**
   * Provides methods to show different dialog windows.
   */
  public DialogWindow() {
    super();
  }
  
  /**
   * Shows to a user dialog window with error message.
   * <p>
   * Replace a direct {@code JOptionPane.showMessageDialog} method call with this method for
   * tests convenience.
   * @param frame in which the dialog window is displayed
   * @param title of dialog window
   * @param message of dialog window
   * @throws HeadlessException if GraphicsEnvironment.isHeadless returns true
   */
  public void showErrorMessage(final Component frame, final String title, final String message) 
      throws HeadlessException {
    JOptionPane.showMessageDialog(frame, title, message, JOptionPane.ERROR_MESSAGE);
  }
  
  /**
   * Shows to a user dialog window with informative message.
   * <p>
   * Replace a direct {@code JOptionPane.showMessageDialog} method call with this method for
   * tests convenience.
   * @param frame in which the dialog window is displayed
   * @param title of dialog window
   * @param message of dialog window
   * @throws HeadlessException if GraphicsEnvironment.isHeadless returns true
   */
  public void showInfoMessage(final Component frame, final String title, final String message) 
      throws HeadlessException {
    JOptionPane.showMessageDialog(frame, title, message, JOptionPane.INFORMATION_MESSAGE);
  }

}
