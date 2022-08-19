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
    JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
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
    JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
  }
  
  /**
   * Shows to a user dialog window with OK and CANCEL options.
   * <p>
   * Replace a direct {@code JOptionPane.showConfirmDialog} method call with this method for
   * tests convenience.
   * @param frame in which the dialog window is displayed
   * @param title of dialog window
   * @param message of dialog window
   * @param messageType primarily used to determine the icon from the pluggable Look and Feel:
   *        ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE, QUESTION_MESSAGE, or PLAIN_MESSAGE
   * @return an integer indicating the option selected by the user (0 if user has selected OK)
   * @throws HeadlessException if GraphicsEnvironment.isHeadless returns true
   */
  public int showConfirmDialog(
      final Component frame, final String title, final String message, final int messageType) 
      throws HeadlessException {
    
    return JOptionPane.showConfirmDialog(
        frame,
        message,
        title,
        JOptionPane.OK_CANCEL_OPTION,
        messageType
        );
  }
  
  /**
   * Shows to a user dialog window with line to write text.
   * <p>
   * Replace a direct {@code JOptionPane.showInputDialog} method call with this method for
   * tests convenience.
   * @param frame in which the dialog window is displayed
   * @param title of dialog window
   * @param message of dialog window
   * @return a string with user`s input
   * @throws HeadlessException if GraphicsEnvironment.isHeadless returns true
   */
  public String showInputDialog(
      final Component frame, final String title, final String message) 
      throws HeadlessException {
    return JOptionPane.showInputDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
  }
}
