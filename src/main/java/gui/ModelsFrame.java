package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import anton.mankovsky.locomotivedepotlogbook.LocomotiveDepotLogbookApplication;
import datavalidation.InputValidator;
import dbapi.DbManager;
import gui.eventlisteners.DeleteModelListener;
import gui.eventlisteners.NewModelListener;
import gui.tablemodels.RepairPeriodsTableModel;
import gui.tablerenderers.RepairPeriodsTableRenderer;
import gui.utility.DialogWindow;

/**
 * Manages frame with repair periods table.
 */
public class ModelsFrame extends JFrame {
  private final DbManager dbManager;
  private final GuiManager guiManager;
  private final DialogWindow dialogWindow;
  private final InputValidator validator;
  private int modelFrameWidth;
  private int modelFrameHeight;
  private JMenuBar modelMenuBar;
  private JMenu modelMenu;
  private JTable repairPeriodsTable;
  private JPanel repairPeriodsTablePane;
  
  /**
   * Builds components for JFrame for models table and registers listeners for them.
   * @param guiManager the core GUI class of application with important GUI fields and methods
   * @param dbManager class that contains database API
   * @param dialogWindow for interactions with user
   * @param validator for user`s input validation
   */
  public ModelsFrame(
      final GuiManager guiManager, final DbManager dbManager,
      final DialogWindow dialogWindow, final InputValidator validator) {
    super();
    this.guiManager = guiManager;
    this.dbManager = dbManager;
    this.dialogWindow = dialogWindow;
    this.validator = validator;
    createGui();
  }
  
  /**
   * Defines correct order of variables initialization and frame build methods.
   */
  private void createGui() {
    modelFrameWidth = (int) (guiManager.getMainFrameWidth() * 0.75);
    modelFrameHeight = guiManager.getMainFrameHeight();
    buildModelMenu();
    buildModelMenuBar();
    buildRepairPeriodsTable();
    buildRepairPeriodsTablePane();
    buildModelsFrame();
  }
  
  private void buildModelMenu() {
    modelMenu = new JMenu("Действия");
    
    JMenuItem tempItem = new JMenuItem("Новая модель");
    tempItem.addActionListener(new NewModelListener(guiManager, dialogWindow, validator));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    modelMenu.add(tempItem);
    
    modelMenu.addSeparator();
    
    tempItem = new JMenuItem("Удалить выбранную модель");
    tempItem.addActionListener(new DeleteModelListener(guiManager, dialogWindow));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    modelMenu.add(tempItem);
    
    modelMenu.addSeparator();
    
    tempItem = new JMenuItem("Закрыть");
    tempItem.addActionListener(e -> setVisible(false));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    modelMenu.add(tempItem);
  }
  
  private void buildModelMenuBar() {
    modelMenuBar = new JMenuBar();
    modelMenuBar.add(modelMenu);
  }
  
  private void buildRepairPeriodsTable() {
    repairPeriodsTable = new JTable(
        new RepairPeriodsTableModel(dbManager, guiManager, dialogWindow, validator));
    repairPeriodsTable.setPreferredScrollableViewportSize(
        new Dimension(modelFrameWidth, modelFrameHeight));
    repairPeriodsTable.setFillsViewportHeight(true);
    repairPeriodsTable.getTableHeader().setReorderingAllowed(false);
    repairPeriodsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    repairPeriodsTable.setDefaultRenderer(Object.class, new RepairPeriodsTableRenderer());
    repairPeriodsTable.setDefaultRenderer(Integer.class, new RepairPeriodsTableRenderer());
  }
  
  private void buildRepairPeriodsTablePane() {
    repairPeriodsTablePane = new JPanel(new GridLayout(1, 0));
    repairPeriodsTablePane.add(new JScrollPane(repairPeriodsTable));
  }
  
  private void buildModelsFrame() {
    setSize(modelFrameWidth, modelFrameHeight);
    setLocationByPlatform(true);
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    setTitle("Таблица моделей");
    setJMenuBar(modelMenuBar);
    setContentPane(repairPeriodsTablePane);
    setMinimumSize(new Dimension((int) (modelFrameWidth * 0.25), 0));
    setIconImage(LocomotiveDepotLogbookApplication.getAppIcon().getImage());
  }
  
  /**
   * Provides access to JTable representing database table with repair periods.
   * @return JTable that represents database table with models and their repair periods
   */
  public JTable getRepairPeriodsTable() {
    return repairPeriodsTable;
  }
  
  @Override
  public String toString() {
    return "The object that represents models frame of the applciation";
  }
}
