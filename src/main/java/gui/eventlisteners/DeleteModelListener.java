package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import gui.GuiManager;

public class DeleteModelListener implements ActionListener {
  private final GuiManager guiManager;
  
  public DeleteModelListener(final GuiManager guiManager) {
    super();
    this.guiManager = guiManager;
  }
  
  @Override
  public void actionPerformed(final ActionEvent event) {
    int selectedRecord = guiManager.getRepairRecordsTable().getSelectedRow();
    if (selectedRecord == -1) {
      return;
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
      final TableModel tm = guiManager.getRepairRecordsTable().getModel();
      if (tm instanceof AbstractTableModel) {
        final AbstractTableModel recordsModel = (AbstractTableModel) tm;
        recordsModel.fireTableRowsDeleted(deletedRows[0], deletedRows[1]);
      }
    } else {
      JOptionPane.showMessageDialog(
          guiManager.getMainFrame(),
          "Не удалось удалить выбранную запись",
          "Ошибка при удалении записи",
          JOptionPane.ERROR_MESSAGE
          );
    }
  }

}
