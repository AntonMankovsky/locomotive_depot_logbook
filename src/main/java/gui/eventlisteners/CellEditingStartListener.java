package gui.eventlisteners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;

/**
 *  Provides reaction on user starts editing cell event.
 */
public class CellEditingStartListener implements PropertyChangeListener  {
  private final JTable table;
  
  /**
   * Creates new cell start editing listener. 
   * @param table to listen to
   */
  public CellEditingStartListener(final JTable table) {
    super();
    this.table = table;
  }

  /**
   * Deletes visual representation of cell old value when user starts to edit the cell.
   */
  @Override
  public void propertyChange(final PropertyChangeEvent evt) {
    if ("tableCellEditor".equals(evt.getPropertyName()) && table.isEditing()) {
      table.getCellEditor().getTableCellEditorComponent(
                              table, "", true, -1, -1);       // row and column values to match
                                                              // signature, don't affect anything
    }
  }

  @Override
  public String toString() {
    return "CellEditingStartListener [table=" + table + "]";
  }

}
