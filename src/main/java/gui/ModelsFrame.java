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

import dbapi.DbManager;
import gui.eventlisteners.NewModelListener;

public class ModelsFrame extends JFrame {
  private final DbManager dbManager;
  private final GuiManager guiManager;
  private int modelFrameWidth;
  private int modelFrameHeight;
  private JMenuBar modelMenuBar;
  private JMenu modelMenu;
  private JTable repairPeriodsTable;
  private JPanel repairPeriodsTablePane;
  
  public ModelsFrame(final GuiManager guiManager, final DbManager dbManager) {
    super();
    this.guiManager = guiManager;
    this.dbManager = dbManager;
    createGui();
  }
  
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
    tempItem.addActionListener(new NewModelListener(dbManager));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
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
    repairPeriodsTable = new JTable(new RepairPeriodsTableModel());
    repairPeriodsTable.setPreferredScrollableViewportSize(
        new Dimension(modelFrameWidth, modelFrameHeight));
    repairPeriodsTable.setFillsViewportHeight(true);
    repairPeriodsTable.getTableHeader().setReorderingAllowed(false);
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
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ModelsFrame [dbManager=");
    builder.append(dbManager);
    builder.append(", guiManager=");
    builder.append(guiManager);
    builder.append(", modelFrameWidth=");
    builder.append(modelFrameWidth);
    builder.append(", modelFrameHeight=");
    builder.append(modelFrameHeight);
    builder.append(", modelMenuBar=");
    builder.append(modelMenuBar);
    builder.append(", modelMenu=");
    builder.append(modelMenu);
    builder.append(", repairPeriodsTablePane=");
    builder.append(repairPeriodsTablePane);
    builder.append(", repairPeriodsTable=");
    builder.append(repairPeriodsTable);
    builder.append("]");
    return builder.toString();
  }
}


