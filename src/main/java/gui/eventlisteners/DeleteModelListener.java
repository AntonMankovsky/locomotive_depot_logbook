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
    final int selectedRow = guiManager.getModelsFrame().getRepairPeriodsTable().getSelectedRow();
    if (selectedRow == -1) {
      return;
    }
    
    try {
    guiManager.getRepairRecordsTable().getCellEditor().cancelCellEditing();
    } catch (final NullPointerException e) {
      // This construction is needed to prevent a graphical bug that occurs 
      // when a row is deleted while it`s cell is in editing state.
    }
    
    final String modelName =
        (String) guiManager.getModelsFrame().getRepairPeriodsTable().getValueAt(selectedRow, 0);
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
          guiManager.getMainFrame(),
          "Не удалось удалить выбранную запись",
          "Ошибка при удалении записи",
          JOptionPane.ERROR_MESSAGE
          );
    }
  }
}
