package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dbapi.DbManager;
import gui.eventlisteners.DeleteModelListener;
import gui.eventlisteners.NewRecordAction;


/**
 * Creates, displays and provides access to application GUI.
 */
@Service
public class GuiManager {
  private DbManager dbManager;
  private int mainFrameWidth;
  private int mainFrameHeight;
  private JFrame mainFrame;
  private JFrame modelsFrame;
  private JMenuBar mainMenuBar;
  private JMenu mainMenu;
  private JMenu newRecordSubmenu;
  private JTable repairRecordsTable;
  private JPanel repairRecordsTablePane;
  
  /**
   * Builds all components for GUI and registers listeners for them. Displays GUI on screen.
   * @param dbManager that will be used by listeners that handle events.
   */
  @Autowired
  public GuiManager(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
    createAndShowGui();
  }
  
  private void createAndShowGui() {
    final Toolkit kit = Toolkit.getDefaultToolkit();
    final Dimension screenSize = kit.getScreenSize();
    mainFrameWidth = (int) (screenSize.width * 0.60);
    mainFrameHeight = screenSize.height / 2;
    modelsFrame = new ModelsFrame(this, dbManager);
    
    buildNewRecordSubmenu();
    buildMainMenu();
    buildMainMenuBar();
    buildRepairRecordsTable();
    buildRepairRecordsTablePane();
    buildMainFrame();
  }
  
  private void buildNewRecordSubmenu() {
    newRecordSubmenu = new JMenu("Новая запись");
    String[] modelNames = dbManager.getAllModelNames();
    JMenuItem subMenuItem;
    for (String model : modelNames) {
      subMenuItem = new JMenuItem(new NewRecordAction(model, this));
      newRecordSubmenu.add(subMenuItem);
    }
    
  }
  
  private void buildMainMenu() {
    mainMenu = new JMenu("Меню");
    mainMenu.add(newRecordSubmenu);
    
    JMenuItem tempItem = new JMenuItem("Открыть таблицу моделей");
    tempItem.addActionListener(e -> modelsFrame.setVisible(true));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    mainMenu.add(tempItem);
    
    mainMenu.addSeparator();
    
    tempItem = new JMenuItem("Удалить выбранную запись");
    tempItem.addActionListener(new DeleteModelListener(this));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    mainMenu.add(tempItem);
    
    mainMenu.addSeparator();
    
    tempItem = new JMenuItem("Выход");
    tempItem.addActionListener(e -> System.exit(0));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    mainMenu.add(tempItem);
  }
  
  private void buildMainMenuBar() {
    mainMenuBar = new JMenuBar();
    mainMenuBar.add(mainMenu);
  }
  
  private void buildRepairRecordsTable() {
    repairRecordsTable = new JTable(new RepairRecordsTableModel(dbManager));
    repairRecordsTable.setPreferredScrollableViewportSize(
        new Dimension(mainFrameWidth, mainFrameHeight));
    repairRecordsTable.setFillsViewportHeight(true);
    repairRecordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }
  
  private void buildRepairRecordsTablePane() {
    repairRecordsTablePane = new JPanel(new GridLayout(1, 0));
    repairRecordsTablePane.add(new JScrollPane(repairRecordsTable));
  }
  
  private void buildMainFrame() {
    mainFrame = new JFrame();
    mainFrame.setSize(mainFrameWidth, mainFrameHeight);
    
    mainFrame.setLocationByPlatform(true);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setTitle("Журнал учёта ремонтов");
    mainFrame.setJMenuBar(mainMenuBar);
    mainFrame.setContentPane(repairRecordsTablePane);
    
    mainFrame.setVisible(true);
  }
  
  public DbManager getDbManager() {
    return dbManager;
  }
  
  public JTable getRepairRecordsTable() {
    return repairRecordsTable;
  }
  
  public JFrame getMainFrame() {
    return mainFrame;
  }
  
  public int getMainFrameWidth() {
    return mainFrameWidth;
  }

  public int getMainFrameHeight() {
    return mainFrameHeight;
  }

  @Override
  public String toString() {
    return "GuiManager - the object for setting up and run application GUI.";
  }
  
  
}
