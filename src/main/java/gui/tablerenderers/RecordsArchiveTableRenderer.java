package gui.tablerenderers;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import gui.lookandfeel.BorderManager;
import gui.lookandfeel.ColorManager;

/**
 * Custom table renderer for archive table.
 */
public class RecordsArchiveTableRenderer extends DefaultTableCellRenderer {
  private static final long serialVersionUID = 1L;
  private final ColorManager colorManager;
  private final BorderManager borderManager;
  
  /**
   * Object that renders archive table cells in application specific way.
   * <p>
   * Extends DefaultTableCellRenderer and overrides only one method, that returns customized JLabel,
   * which is used by JTable to draw table cells.
   */
  public RecordsArchiveTableRenderer() {
    super();
    super.setHorizontalAlignment(JLabel.CENTER);
    colorManager = ColorManager.getColorManager();
    borderManager = BorderManager.getBorderManager();
  }
  
  /**
   * Sets up JLabel properties accordingly to application visual design so that every cell will
   * have correct colors, borders, etc.
   */
  @Override
  public Component getTableCellRendererComponent(
      final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column) {
    
    final Color backgroundColor = isSelected ? table.getSelectionBackground()
                                             : defineBackgroundColor(row);
    super.setBackground(backgroundColor);
    super.setBorder(defineBorder(row, hasFocus));
    super.setFont(table.getFont());
    super.setValue(value);
    return this;     
  }
  
  private Color defineBackgroundColor(final int row) {
    final Color color;
    color = row % 2 == 0 ? colorManager.getRecordsArchiveTablePrimaryRowColor()
                         : colorManager.getRecordsArchiveTableSecondaryRowColor();
    return color;
  }
  
  private Border defineBorder(final int row, final boolean hasFocus) {
    final Border border;
    if (hasFocus) {
      border = borderManager.getDefaultFocusBorder();
    } else if (row % 2 == 0) {
      border = borderManager.getArchiveTablePrimaryRowBorder();
    } else {
      border = borderManager.getArchiveTableSecondaryRowBorder();
    }
    return border;
  }
  
  @Override
  public String toString() {
    return "RecordsArchiveTableRenderer - sets up JLabel properties to desired values. "
        + "[colorManager=" + colorManager + ", borderManager=" + borderManager + "]";
  }
}
