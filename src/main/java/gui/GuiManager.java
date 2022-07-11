package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dbapi.DbManager;


/**
 * Creates, displays and provides access to application GUI.
 */
@Service
public class GuiManager {
  private DbManager dbManager;
  private JFrame mainFrame;
  private JMenuBar mainMenuBar;
  
  /**
   * Builds all components for GUI and registers listeners for them. Displays GUI on screen.
   * @param dbManager that will be used by listeners that handle events.
   */
  @Autowired
  public GuiManager(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
    createAndShowGUI();
  }
  
  private void createAndShowGUI() {
    buildMainMenuBar();
    buildMainFrame();
  }
  
  private void buildMainFrame() {
    mainFrame = new JFrame();
    final Toolkit kit = Toolkit.getDefaultToolkit();
    final Dimension screenSize = kit.getScreenSize();
    final int screenWidth = screenSize.width;
    final int screenHeight = screenSize.height;
    mainFrame.setSize(screenWidth / 2, screenHeight / 2);
    mainFrame.setLocationByPlatform(true);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setTitle("Журнал учёта ремонтов");
    mainFrame.setJMenuBar(mainMenuBar);
    
    
    mainFrame.setVisible(true);
  }
  
  private void buildMainMenuBar() {
    mainMenuBar = new JMenuBar();
    
    // test
    JMenuItem menuItem;
    menuItem = new JMenuItem("Text");
    mainMenuBar.add(menuItem);
  }

  public DbManager getDbManager() {
    return dbManager;
  }

  @Override
  public String toString() {
    return "GuiManager - the object for setting up and run application GUI.";
  }
  
  
}
