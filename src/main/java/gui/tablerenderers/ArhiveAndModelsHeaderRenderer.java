package gui.tablerenderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import gui.lookandfeel.BorderManager;
import gui.lookandfeel.ColorManager;

public class ArhiveAndModelsHeaderRenderer implements TableCellRenderer {
  private final DefaultTableCellRenderer renderer;
  private final ColorManager colorManager;
  private final BorderManager borderManager;
  
  public ArhiveAndModelsHeaderRenderer (final JTable table) {
    renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    colorManager = ColorManager.getColorManager();
    borderManager = BorderManager.getBorderManager();
  }

  @Override
  public Component getTableCellRendererComponent(
      final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column) {
    final JLabel label = (JLabel)
        renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    
    label.setBorder(borderManager.getHeaderBorder());
    label.setBackground(colorManager.getRecordsTableHeaderDefaultColor());
    
    return label;
  }

  @Override
  public String toString() {
    return "ArhiveAndModelsHeaderRenderer - sets up border and background color for archive and"
        + "models table headers."
        + "[colorManager=" + colorManager + ", borderManager=" + borderManager + "]";
  }

}
