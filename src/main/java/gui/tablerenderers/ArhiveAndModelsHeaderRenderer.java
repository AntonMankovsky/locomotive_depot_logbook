package gui.tablerenderers;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import gui.lookandfeel.BorderManager;
import gui.lookandfeel.ColorManager;

/**
 * Custom table header renderer for archive and models tables.
 */
public class ArhiveAndModelsHeaderRenderer implements TableCellRenderer {
  private final DefaultTableCellRenderer renderer;
  private final ColorManager colorManager;
  private final BorderManager borderManager;
  
  /**
   * Object that renders table header cells in application specific way.
   * <p>
   * Object obtains default table renderer and sets up certain properties to desired values.
   * @param table that owns this renderer
   */
  public ArhiveAndModelsHeaderRenderer (final JTable table) {
    renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    colorManager = ColorManager.getColorManager();
    borderManager = BorderManager.getBorderManager();
  }

  /**
   * Obtains JLabel from default table renderer and sets custom border and background color to it.
   */
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
