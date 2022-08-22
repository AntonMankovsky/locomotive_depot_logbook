package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import dbapi.DbManager;
import gui.GuiManager;
import gui.utility.DialogWindow;

/**
 * Reacts to delete model command from user.
 */
public class DeleteModelListener implements ActionListener {
  private final GuiManager guiManager;
  private final DialogWindow dialogWindow;

  /**
   * Object that reacts to "Delete model" event.
   * @param guiManager to access required application components
   * @param dialogWindow to communicate with user
   */
  public DeleteModelListener(final GuiManager guiManager, final DialogWindow dialogWindow) {
    super();
    this.guiManager = guiManager;
    this.dialogWindow = dialogWindow;
  }

  /**
   * If model is not currently used by {@code repair_records} table, 
   * deletes selected row from {@code repair_periods} table and selects for user another row
   * if present.
   */
  @Override
  public void actionPerformed(final ActionEvent event) {
    final int selectedRow = guiManager.getModelsFrame().getRepairPeriodsTable().getSelectedRow();
    if (selectedRow == -1) {
      return;
    }
    
    final String modelName =
        (String) guiManager.getModelsFrame().getRepairPeriodsTable().getValueAt(selectedRow, 0);
    
    if (isModelInUse(modelName)) {
      dialogWindow.showErrorMessage(guiManager.getModelsFrame(), "Ошибка при удалении записи",
                           "Модель " + modelName + " используется в журнале учёта ремонтов");
      return;
    }
    
    try {
    guiManager.getModelsFrame().getRepairPeriodsTable().getCellEditor().cancelCellEditing();
    } catch (final NullPointerException exception) {
      // This construction is needed to prevent a graphical bug that occurs 
      // when a row is deleted while it`s cell is in editing state.
    }
    
    final boolean wasDeleted = guiManager.getDbManager().deleteRepairPeriods(modelName);
    
    if (wasDeleted) {
      guiManager.rebuildNewRecordSubmenu();
      
      final TableModel tm = guiManager.getModelsFrame().getRepairPeriodsTable().getModel();
      if (tm instanceof AbstractTableModel) {
        final AbstractTableModel recordsModel = (AbstractTableModel) tm;
        recordsModel.fireTableRowsDeleted(selectedRow, selectedRow);
        
        if (recordsModel.getRowCount() > 0) {
          final int rowToSelect = selectedRow != 0 ? selectedRow - 1 : 0;
          guiManager.getModelsFrame()
              .getRepairPeriodsTable()
              .setRowSelectionInterval(rowToSelect, rowToSelect);
        }
      }
    } else {
      dialogWindow.showErrorMessage(guiManager.getModelsFrame(), 
                                    "Ошибка при удалении записи",
                                    "Не удалось удалить выбранную запись");
    }
  }
  
  private boolean isModelInUse(final String modelName) {
    final DbManager dbManager = guiManager.getDbManager();
    final Map<Integer, List<String>> recordsData = dbManager.getAllRepairRecords();
    for (int j = 0; j < dbManager.getRecordsCount(); j++) {
      if (recordsData
                .get(dbManager.getIdByOrdinalNumber(j))
                .get(0)
                .equals(modelName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "DeleteModelListener [guiManager=" + guiManager + "]";
  }
  
}
