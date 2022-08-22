package gui.eventlisteners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import gui.GuiManager;

/**
 * Reacts to changes in "Show next repairs dates" checkbox value.
 */
public class ShowNextRepairsDatesListener implements ItemListener {
  private final GuiManager guiManager;

  /**
   * Object that reacts to changes in "Show next repairs dates" checkbox value
   * @param guiManager to access required application components
   */
  public ShowNextRepairsDatesListener(final GuiManager guiManager) {
    super();
    this.guiManager = guiManager;
  }

  /**
   * Inverts GUI Manager {@code showNextRepairsDates} boolean value, fires table to redraw
   * every cell with next_repair_date values.
   */
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
      } catch (final NullPointerException npe) {
        // This construction is needed to override default behavior that automatically enters
        // cell editing mode on current focused cell after table cell updated has fired.
      }
  }
  
  @Override
  public String toString() {
    return "ShowNextRepairsDatesListener [guiManager=" + guiManager + "]";
  }
}
