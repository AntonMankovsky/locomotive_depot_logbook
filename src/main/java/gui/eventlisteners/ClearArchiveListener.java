package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import gui.GuiManager;

/**
 * Reacts to clear command from user.
 */
public class ClearArchiveListener implements ActionListener {
  private final GuiManager guiManager;

  public ClearArchiveListener(final GuiManager guiManager) {
    super();
    this.guiManager = guiManager;
  }

  @Override
  public void actionPerformed(final ActionEvent event) {
    if (guiManager.getArchiveFrame().getRecordsArchiveTable().getRowCount() == 0) {
      return;
    }
    
    if (getUserConfirmation() != 0) {
      return;
    }
    
    final boolean archiveCleared = guiManager.getDbManager().clearArchive();
    if (archiveCleared) {
      fireArchiveWasCleared();
    } else {
      JOptionPane.showMessageDialog(
          guiManager.getArchiveFrame(),
          "Не удалось очистить архив",
          "Ошибка при очистке архива",
          JOptionPane.ERROR_MESSAGE
          );
    }
  }
  
  private int getUserConfirmation() {
    return JOptionPane.showConfirmDialog(
        guiManager.getArchiveFrame(),
        "Это действие удалит все записи из архива",
        "Очистить архив",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.WARNING_MESSAGE
        );
  }
  
  private void fireArchiveWasCleared() {
    final TableModel tm = guiManager.getArchiveFrame().getRecordsArchiveTable().getModel();
    if (tm instanceof AbstractTableModel) {
      final AbstractTableModel archiveModel = (AbstractTableModel) tm;
      archiveModel.fireTableDataChanged();
    }
  }

}
