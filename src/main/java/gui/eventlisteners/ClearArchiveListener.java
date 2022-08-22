package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import gui.GuiManager;
import gui.utility.DialogWindow;

/**
 * Reacts to "Clear archive" command from user.
 */
public class ClearArchiveListener implements ActionListener {
  private final GuiManager guiManager;
  private final DialogWindow dialogWindow;

  /**
   * Object that reacts to "Clear archive" event.
   * @param guiManager to access required application components
   * @param dialogWindow to communicate with user
   */
  public ClearArchiveListener(final GuiManager guiManager, final DialogWindow dialogWindow) {
    super();
    this.guiManager = guiManager;
    this.dialogWindow = dialogWindow;
  }

  /**
   * If user confirms, deletes all {@code records_archive} table rows.
   * @param
   */
  @Override
  public void actionPerformed(final ActionEvent event) {
    if (guiManager.getArchiveFrame().getRecordsArchiveTable().getRowCount() == 0) {
      return;
    }
    
    if (getUserConfirmation() != 0) {
      return;
    }
    
    try {
    guiManager.getArchiveFrame().getRecordsArchiveTable().getCellEditor().cancelCellEditing();
    } catch (final NullPointerException npe) {
      // This construction is needed to prevent a graphical bug that occurs 
      // when a row is deleted while it`s cell is in editing state.
    }
    
    final boolean archiveCleared = guiManager.getDbManager().clearArchive();
    if (archiveCleared) {
      fireArchiveWasCleared();
    } else {
      dialogWindow.showErrorMessage(
          guiManager.getArchiveFrame(), "Ошибка при очистке архива", "Не удалось очистить архив");
    }
  }
  
  private int getUserConfirmation() {
    return dialogWindow.showConfirmDialog(
           guiManager.getArchiveFrame(),
           "Очистить архив",
           "Это действие удалит все записи из архива",
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

  @Override
  public String toString() {
    return "ClearArchiveListener [guiManager=" + guiManager + "]";
  }

}
