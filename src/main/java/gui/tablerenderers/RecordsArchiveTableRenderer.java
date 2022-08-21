package gui.tablerenderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import gui.lookandfeel.BorderManager;
import gui.lookandfeel.ColorManager;

public class RecordsArchiveTableRenderer extends DefaultTableCellRenderer {
  private static final long serialVersionUID = 1L;
  private final ColorManager colorManager;
  private final BorderManager borderManager;
  
  public RecordsArchiveTableRenderer() {
    super();
    super.setHorizontalAlignment(JLabel.CENTER);
    colorManager = ColorManager.getColorManager();
    borderManager = BorderManager.getBorderManager();
  }
  
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
}
