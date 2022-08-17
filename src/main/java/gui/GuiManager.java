package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.table.TableColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datavalidation.InputValidator;
import datecalculations.DateCalculationsHandler;
import datecalculations.LastRepairHandler;
import datecalculations.RequiredRepairHandler;
import dbapi.DbManager;
import gui.eventlisteners.ChooseThemeAction;
import gui.eventlisteners.DeleteRecordListener;
import gui.eventlisteners.ShowNextRepairsDatesListener;
import gui.eventlisteners.NewRecordAction;
import gui.tablemodels.RepairRecordsTableModel;
import gui.tablerenderers.RepairRecordsHeaderRenderer;
import gui.tablerenderers.RepairRecordsTableRenderer;
import gui.utility.DialogWindow;
import gui.utility.RecordUpdateHandler;

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
  private JMenu chooseThemeSubmenu;
  private JTable repairRecordsTable;
  private JPanel repairRecordsTablePane;
  private InputValidator validator;
  private DialogWindow dialogWindow;
  private boolean showNextRepairsDates;
  private int[] columnsWidth;
  
  /**
   * Builds all components for GUI and registers listeners for them. Displays GUI on screen.
   * @param dbManager that contains database API, that will be used by table models and listeners
   * that handle events.
   */
  @Autowired
  public GuiManager(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
    dialogWindow = new DialogWindow();
    validator = new InputValidator(dbManager);
    showNextRepairsDates = false;
    createAndShowGui();
  }
  
  /**
   * Defines correct order of variables initialization and GUI build methods.
   */
  private void createAndShowGui() {
    final Toolkit kit = Toolkit.getDefaultToolkit();
    final Dimension screenSize = kit.getScreenSize();
    mainFrameWidth = (int) (screenSize.width * 0.90);
    mainFrameHeight = (int) (screenSize.height * 0.80);
    modelsFrame = new ModelsFrame(this, dbManager);
    archiveFrame = new ArchiveFrame(this, dbManager);
    columnsWidth = new int[11];
    
    buildNewRecordSubmenu();
    buildChooseThemeSubmenu();
    buildMainMenu();
    buildMainMenuBar();
    buildRepairRecordsTable();
    buildRepairRecordsTablePane();
    setСolumnsWidth();
    buildMainFrame();
  }
  
  private void buildNewRecordSubmenu() {
    newRecordSubmenu = new JMenu("Новая запись");
    String[] modelNames = dbManager.getAllModelNames();
    for (String model : modelNames) {
      newRecordSubmenu.add(new JMenuItem(new NewRecordAction(model, this)));
    }
  }
  
  private void buildChooseThemeSubmenu() {
    chooseThemeSubmenu = new JMenu("Выбрать тему");
    chooseThemeSubmenu.add(new JMenuItem(new ChooseThemeAction("Светлая", this)));
    chooseThemeSubmenu.add(new JMenuItem(new ChooseThemeAction("Тёмная", this)));
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
    
    mainMenu.add(chooseThemeSubmenu);
    
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
    repairRecordsTable =
        new JTable(new RepairRecordsTableModel(dbManager, this, initRecordUpdateHandler()));
    repairRecordsTable.setPreferredScrollableViewportSize(
        new Dimension(mainFrameWidth, mainFrameHeight));
    repairRecordsTable.setFillsViewportHeight(true);
    repairRecordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    repairRecordsTable.getTableHeader().setReorderingAllowed(false);
    repairRecordsTable.getTableHeader()
        .setDefaultRenderer(new RepairRecordsHeaderRenderer(repairRecordsTable));
    repairRecordsTable.setDefaultRenderer(Object.class, new RepairRecordsTableRenderer(dbManager));
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
  
  private void setСolumnsWidth() {
    defineColumnsWidth();
    TableColumn column;
    for (int j = 0; j < columnsWidth.length; j++) {
      column = repairRecordsTable.getColumnModel().getColumn(j);
      column.setPreferredWidth(columnsWidth[j]);
    }
  }
  
  private void defineColumnsWidth() {
    for (int j = 0; j < columnsWidth.length; j++) {
      switch (j) {
      case 0:
        columnsWidth[j] = (int) (mainFrameWidth * 0.06);
        break;
      case 1:
        columnsWidth[j] = (int) (mainFrameWidth * 0.06);
        break;
      case 8:
        columnsWidth[j] = (int) (mainFrameWidth * 0.12);
        break;
      case 9:
        columnsWidth[j] = (int) (mainFrameWidth * 0.12);
        break;
      case 10:
        columnsWidth[j] = (int) (mainFrameWidth * 0.15);
        break;
      default:
        columnsWidth[j] = (int) (mainFrameWidth * 0.08);
        break;
      }
    }
  }
  
  /**
   * Returns new Record Update Handler.
   * <p>
   * Creates chain of all classes that update handler depends on in order to achieve less coupling
   * and make tests easy to perform.
   * @return Record Update Handler that should handle changes in repair records table model.
   */
  private RecordUpdateHandler initRecordUpdateHandler() {
    final RequiredRepairHandler requiredRepairHandler = new RequiredRepairHandler(dbManager);
    final DateCalculationsHandler dateCalculationsHandler =
        new DateCalculationsHandler(this, dbManager, requiredRepairHandler);
    final LastRepairHandler lastRepairHandler = new LastRepairHandler(this, dbManager);
    
    final RecordUpdateHandler recordUpdateHandler = new RecordUpdateHandler(
            dbManager, this, dateCalculationsHandler, lastRepairHandler, validator, dialogWindow);
    return recordUpdateHandler;
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
