package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dbapi.DbManager;
import gui.eventlisteners.DeleteRepairRecordsListener;
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
    String[] modelNames = {"ТГМ", "ТГМ4Б", "ТГМ4Бл"};
    JMenuItem subMenuItem;
    for (String model : modelNames) {
      subMenuItem = new JMenuItem(new NewRecordAction(model));
      newRecordSubmenu.add(subMenuItem);
    }
    
  }
  
  private void buildMainMenu() {
    mainMenu = new JMenu("Меню");
    mainMenu.add(newRecordSubmenu);
    mainMenu.add(new JMenuItem("Открыть таблицу моделей"))
      .addActionListener(e -> modelsFrame.setVisible(true));
    
    mainMenu.addSeparator();
    mainMenu.add(new JMenuItem("Удалить выбранные строки"))
      .addActionListener(new DeleteRepairRecordsListener(this, dbManager));
    
    mainMenu.addSeparator();
    mainMenu.add(new JMenuItem("Выход")).addActionListener(e -> System.exit(0));

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
