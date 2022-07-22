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

import dbapi.DbManager;
import gui.eventlisteners.ClearArchiveListener;
import gui.tablemodels.RecordsArchiveTableModel;

/**
 * Manages frame with records archive table.
 */
public class ArchiveFrame extends JFrame {
  private final DbManager dbManager;
  private final GuiManager guiManager;
  private int archiveFrameWidth;
  private int archiveFrameHeight;
  private JMenuBar archiveMenuBar;
  private JMenu archiveMenu;
  private JTable recordsArchiveTable;
  private JPanel recordsArchiveTablePane;
  private boolean initialized;
  
  /**
   * Builds components for JFrame for archive table and registers listeners for them.
   * <p>
   * Although this object can be created on application start for other GUI needs,
   * it will use lazy GUI and data initialization.
   * @param guiManager the core GUI class of application with important GUI fields and methods
   * @param dbManager class that contains database API
   */
  public ArchiveFrame(final GuiManager guiManager, final DbManager dbManager) {
    super();
    this.guiManager = guiManager;
    this.dbManager = dbManager;
    initialized = false;
  }
  
  /**
   * Initializes GUI and data on the first call.
   */
  @Override
  public void setVisible(final boolean bool) {
    if (!initialized) {
      createGui();
      initialized = true;
    }
    super.setVisible(bool);
  }
  
  private void createGui() {
    archiveFrameWidth = guiManager.getMainFrameWidth();
    archiveFrameHeight = guiManager.getMainFrameHeight();
    buildArchiveMenu();
    buildArchiveMenuBar();
    buildRecordsArchiveTable();
    buildRecordsArchiveTablePane();
    buildArchiveFrame();
  }
  
  private void buildArchiveMenu() {
    archiveMenu = new JMenu("Действия");
    
    JMenuItem tempItem = new JMenuItem("Очистить архив"); 
    tempItem.addActionListener(new ClearArchiveListener(guiManager));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
    archiveMenu.add(tempItem);
    
    archiveMenu.addSeparator();
    
    tempItem = new JMenuItem("Закрыть");
    tempItem.addActionListener(e -> setVisible(false));
    tempItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    archiveMenu.add(tempItem);
  }
  
  private void buildArchiveMenuBar() {
    archiveMenuBar = new JMenuBar();
    archiveMenuBar.add(archiveMenu);
  }
  
  private void buildRecordsArchiveTable() {
    recordsArchiveTable = new JTable(new RecordsArchiveTableModel(dbManager));
    recordsArchiveTable.setPreferredScrollableViewportSize(
          new Dimension(archiveFrameWidth, archiveFrameHeight));
    recordsArchiveTable.setFillsViewportHeight(true);
    recordsArchiveTable.getTableHeader().setReorderingAllowed(false);
    recordsArchiveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }
  
  private void buildRecordsArchiveTablePane() {
    recordsArchiveTablePane = new JPanel(new GridLayout(1, 0));
    recordsArchiveTablePane.add(new JScrollPane(recordsArchiveTable));
  }
  
  private void buildArchiveFrame() {
    setSize(archiveFrameWidth, archiveFrameHeight);
    setLocationByPlatform(true);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setTitle("Архив");
    setJMenuBar(archiveMenuBar);
    setContentPane(recordsArchiveTablePane);
    setMinimumSize(new Dimension((int) (archiveFrameWidth * 0.40), 0));
  }
  
  /**
   * Provides access to JTable representing database table with archive repair records.
   * @return JTable that represents database table with archived records
   */
  public JTable getRecordsArchiveTable() {
    return recordsArchiveTable;
  }
  
  /**
   * Tells whether or not frame was initialized
   * @return {@code true} if frame already has been initialized (i.e. has created it`s own GUI)
   */
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public String toString() {
    return "The object represents archive frame of applciation";
  }
  
}
