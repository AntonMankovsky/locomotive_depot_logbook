package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import dbapi.DbManager;
import gui.GuiManager;

public class DeleteModelListener implements ActionListener {
  private final GuiManager guiManager;

  public DeleteModelListener(final GuiManager guiManager) {
    super();
    this.guiManager = guiManager;
  }

  @Override
  public void actionPerformed(final ActionEvent event) {
    final int selectedRow = guiManager.getModelsFrame().getRepairPeriodsTable().getSelectedRow();
    if (selectedRow == -1) {
      return;
    }
    
    final String modelName =
        (String) guiManager.getModelsFrame().getRepairPeriodsTable().getValueAt(selectedRow, 0);
    
    if (isModelInUse(modelName)) {
      JOptionPane.showMessageDialog(
          guiManager.getMainFrame(),
          "Модель " + modelName + " используется в журнале учёта ремонтов",
          "Ошибка при удалении записи",
          JOptionPane.ERROR_MESSAGE
          );
      return;
    }
    
    try {
    guiManager.getModelsFrame().getRepairPeriodsTable().getCellEditor().cancelCellEditing();
    } catch (final NullPointerException e) {
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
          guiManager.getModelsFrame()
              .getRepairPeriodsTable()
              .setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        }
      }
    } else {
      JOptionPane.showMessageDialog(
          guiManager.getModelsFrame(),
          "Не удалось удалить выбранную запись",
          "Ошибка при удалении записи",
          JOptionPane.ERROR_MESSAGE
          );
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
}
