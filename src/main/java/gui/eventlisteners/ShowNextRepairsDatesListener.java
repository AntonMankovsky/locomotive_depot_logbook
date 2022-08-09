package gui.eventlisteners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import gui.GuiManager;

public class ShowNextRepairsDatesListener implements ItemListener {
  private final GuiManager guiManager;

  public ShowNextRepairsDatesListener(final GuiManager guiManager) {
    super();
    this.guiManager = guiManager;
  }

  @Override
  public void itemStateChanged(final ItemEvent event) {
    guiManager.setShowNextRepairsDates(!guiManager.isShowNextRepairsDates());
    
    final TableModel tm = guiManager.getRepairRecordsTable().getModel();
    final int rowCount = tm.getRowCount();
    if (tm instanceof AbstractTableModel) {
      final AbstractTableModel recordsModel = (AbstractTableModel) tm;
      for (int j = 1; j < rowCount; j+=2) {
        for (int k = 2; k < 8; k++) {
        recordsModel.fireTableCellUpdated(j, k);
        }
      }
    }
    
    try {
      guiManager.getRepairRecordsTable().getCellEditor().cancelCellEditing();
      } catch (final NullPointerException exception) {
        // This construction is needed to prevent default behavior that automatically enters
        // cell editing mode on current focused cell after table cell updated has fired.
      }
  }
}
