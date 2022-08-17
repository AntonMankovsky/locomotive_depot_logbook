package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import anton.mankovsky.locomotivedepotlogbook.LocomotiveDepotLogbookApplication;
import gui.GuiManager;
import gui.utility.DialogWindow;

public class ChooseThemeAction extends AbstractAction {
  private static final Logger logger = LogManager.getLogger();
  private final GuiManager guiManager;
  private final DialogWindow dialogWindow;
  
  /**
   * Overrides color theme name in UI configuration file.
   * @param name of the theme
   * @param guiManager to provide access to application main frame
   * @throws IllegalArgumentException if theme with given name is not supported
   */
  public ChooseThemeAction(
      final String name, final GuiManager guiManager, final DialogWindow dialogWindow)
      throws IllegalArgumentException {
    
    super();
    this.guiManager = guiManager;
    this.dialogWindow = dialogWindow;
    
    if (name.equals("Светлая") || name.equals("Тёмная")) {
      putValue(Action.NAME, name);
    } else {
      throw new IllegalArgumentException("Название темы \"" + name + "\" не поддерживается.");
    }
  }

  @Override
  public void actionPerformed(final ActionEvent event) {
    final String name = (String) getValue(Action.NAME);
    final String theme = name.equals("Светлая") ? "uitheme=light" : "uitheme=dark";
    try (final BufferedWriter writer = Files.newBufferedWriter(
        LocomotiveDepotLogbookApplication.getUiConfigPath(), Charset.forName("UTF-8"))) {
      writer.write(theme);
      notifyOnSuccess();
    } catch (final IOException exception) {
      logger.warn(
          "Failed to write in \"UITheme.txt\" file."
          + exception.getMessage());
      notifyOnFailure();
    }
  }
  
  private void notifyOnSuccess() {
    dialogWindow.showInfoMessage(guiManager.getMainFrame(),
        "Выбор темы", "Изменения вступят в силу при следующем запуске программы");
  }
  
  private void notifyOnFailure() {
    dialogWindow.showErrorMessage(
        guiManager.getMainFrame(), "Ошибка записи в файл",
                                   "Не удалось изменить файл с названием темы.");
  }

  @Override
  public String toString() {
    return "ChooseThemeAction=" + getValue(Action.NAME) + "[guiManager=" + guiManager + "]";
  }
}
