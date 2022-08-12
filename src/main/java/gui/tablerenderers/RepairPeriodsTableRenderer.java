package gui.tablerenderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import gui.lookandfeel.BorderManager;
import gui.lookandfeel.ColorManager;

public class RepairPeriodsTableRenderer extends DefaultTableCellRenderer {
  private final ColorManager colorManager;
  private final BorderManager borderManager;
  
  public RepairPeriodsTableRenderer() {
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
    super.setBorder(hasFocus ? borderManager.getDefaultFocusBorder() 
                             : borderManager.getDefaultNoFocusBorder());
    super.setFont(table.getFont());
    super.setValue(value);
    return this;     
  }
  
  private Color defineBackgroundColor(final int row) {
    final Color color;
    color = row % 2 == 0 ? colorManager.getModelsTablePrimaryRowColor()
                         : colorManager.getModelsTableSecondaryRowColor();
    return color;
  }
  
}
