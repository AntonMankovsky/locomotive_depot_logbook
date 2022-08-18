package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import gui.GuiManager;
import gui.utility.DialogWindow;

public class DeleteRecordListener implements ActionListener {
  private final GuiManager guiManager;
  private final DialogWindow dialogWindow;
  
  public DeleteRecordListener(final GuiManager guiManager, final DialogWindow dialogWindow) {
    super();
    this.guiManager = guiManager;
    this.dialogWindow = dialogWindow;
  }
  
  @Override
  public void actionPerformed(final ActionEvent event) {
    int selectedRecord = guiManager.getRepairRecordsTable().getSelectedRow();
    if (selectedRecord == -1) {
      return;
    }
    
    try {
    guiManager.getRepairRecordsTable().getCellEditor().cancelCellEditing();
    } catch (final NullPointerException npe) {
      // This construction is needed to prevent a graphical bug that occurs 
      // when a row is deleted while it`s cell is in editing state.
    }
    
    final int[] deletedRows = new int[2];
    if (selectedRecord % 2 == 0) {
      deletedRows[0] = selectedRecord;
      deletedRows[1] = selectedRecord + 1;
      selectedRecord /= 2;
    } else {
      deletedRows[0] = selectedRecord - 1;
      deletedRows[1] = selectedRecord;
      selectedRecord = --selectedRecord / 2;
    }
    
    final int actualRowId = guiManager.getDbManager().getIdByOrdinalNumber(selectedRecord);
    final boolean wasDeleted = guiManager.getDbManager().deleteRepairRecord(actualRowId);
    
    if (wasDeleted) {
      
      if (guiManager.getArchiveFrame().isInitialized()) {
        fireArchiveTableRowInserted();
      }
      
      final TableModel tm = guiManager.getRepairRecordsTable().getModel();
      if (tm instanceof AbstractTableModel) {
        final AbstractTableModel recordsModel = (AbstractTableModel) tm;
        recordsModel.fireTableRowsDeleted(deletedRows[0], deletedRows[1]);
        
        if (recordsModel.getRowCount() > 0) {
          final int rowToSelect = deletedRows[0] != 0 ? deletedRows[0] - 2 : 0;
          guiManager.getRepairRecordsTable().setRowSelectionInterval(rowToSelect, rowToSelect);
        }
      }

    } else {
      dialogWindow.showErrorMessage(guiManager.getMainFrame(), 
          "Ошибка при удалении записи",
          "Не удалось удалить выбранную запись");
    }
  }
  
  private void fireArchiveTableRowInserted() {
    final TableModel tm = guiManager.getArchiveFrame().getRecordsArchiveTable().getModel();
    if (tm instanceof AbstractTableModel) {
      final AbstractTableModel archiveModel = (AbstractTableModel) tm;
      final int rows = archiveModel.getRowCount();
      archiveModel.fireTableRowsInserted(rows - 2, rows - 1);
    }
  }

  @Override
  public String toString() {
    return "DeleteRecordListener [guiManager=" + guiManager + "]";
  }
}
