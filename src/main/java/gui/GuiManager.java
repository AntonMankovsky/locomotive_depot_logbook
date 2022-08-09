package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dbapi.DbManager;
import gui.eventlisteners.DeleteRecordListener;
import gui.eventlisteners.ShowNextRepairsDatesListener;
import gui.eventlisteners.NewRecordAction;
import gui.tablemodels.RepairRecordsTableModel;

/**
 * Creates, displays and provides access to application GUI.
 */
@Service
public class GuiManager {
  private DbManager dbManager;
  private int mainFrameWidth;
  private int mainFrameHeight;
  private JFrame mainFrame;
  private ModelsFrame modelsFrame;
  private ArchiveFrame archiveFrame;
  private JMenuBar mainMenuBar;
  private JMenu mainMenu;
  private JMenu newRecordSubmenu;
  private JTable repairRecordsTable;
  private JPanel repairRecordsTablePane;
  private boolean showNextRepairsDates;
  
  /**
   * Builds all components for GUI and registers listeners for them. Displays GUI on screen.
   * @param dbManager that contains database API, that will be used by table models and listeners
   * that handle events.
   */
  @Autowired
  public GuiManager(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
    showNextRepairsDates = false;
    createAndShowGui();
  }
  
  /**
   * Defines correct order of variables initialization and GUI build methods.
   */
  private void createAndShowGui() {
    final Toolkit kit = Toolkit.getDefaultToolkit();
    final Dimension screenSize = kit.getScreenSize();
    mainFrameWidth = (int) (screenSize.width * 0.60);
    mainFrameHeight = screenSize.height / 2;
    modelsFrame = new ModelsFrame(this, dbManager);
    archiveFrame = new ArchiveFrame(this, dbManager);
    
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
    for (String model : modelNames) {
      newRecordSubmenu.add(new JMenuItem(new NewRecordAction(model, this)));
    }
  }
  
  private void buildMainMenu() {
    mainMenu = new JMenu("Меню");
    mainMenu.add(newRecordSubmenu);
    
    mainMenu.addSeparator();
    
    JMenuItem tempItem = new JMenuItem("Архив");
    tempItem.addActionListener(e -> archiveFrame.setVisible(true));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    mainMenu.add(tempItem);
    
    tempItem = new JMenuItem("Таблица моделей");
    tempItem.addActionListener(e -> modelsFrame.setVisible(true));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
    mainMenu.add(tempItem);
    
    mainMenu.addSeparator();
    
    tempItem = new JCheckBoxMenuItem("Отображать рассчитанные даты");
    tempItem.addItemListener(new ShowNextRepairsDatesListener(this));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
    mainMenu.add(tempItem);
    
    tempItem = new JMenuItem("Удалить выбранную запись");
    tempItem.addActionListener(new DeleteRecordListener(this));
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
    repairRecordsTable = new JTable(new RepairRecordsTableModel(dbManager, this));
    repairRecordsTable.setPreferredScrollableViewportSize(
        new Dimension(mainFrameWidth, mainFrameHeight));
    repairRecordsTable.setFillsViewportHeight(true);
    repairRecordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    repairRecordsTable.getTableHeader().setReorderingAllowed(false);
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
    mainFrame.setMinimumSize(new Dimension((int) (mainFrameWidth * 0.40), 0));
    
    mainFrame.setVisible(true);
  }
  
  /**
   * Updates submenu items by rebuilding it from scratch.
   * <p>
   * Method suppose to be used when model was deleted form models table
   * so submenu will not contain wrong option.
   */
  public void rebuildNewRecordSubmenu() {
    newRecordSubmenu.removeAll();
    String[] modelNames = dbManager.getAllModelNames();
    for (String model : modelNames) {
      newRecordSubmenu.add(new JMenuItem(new NewRecordAction(model, this)));
    }
  }
  
  public boolean isShowNextRepairsDates() {
    return showNextRepairsDates;
  }

  public void setShowNextRepairsDates(final boolean showNextRepairsDates) {
    this.showNextRepairsDates = showNextRepairsDates;
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
  
  public ModelsFrame getModelsFrame() {
    return modelsFrame;
  }
  
  public ArchiveFrame getArchiveFrame() {
    return archiveFrame;
  }
  
  public JMenu getNewRecordSubmenu() {
    return newRecordSubmenu;
  }
  
  @Override
  public String toString() {
    return "GuiManager - the object for setting up and run application GUI.";
  }
  
  
}
