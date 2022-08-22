package gui.tablerenderers;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import gui.lookandfeel.BorderManager;
import gui.lookandfeel.ColorManager;

/**
 * Custom table header renderer for repair records table.
 */
public class RepairRecordsHeaderRenderer implements TableCellRenderer {
  private final DefaultTableCellRenderer renderer;
  private final ColorManager colorManager;
  private final BorderManager borderManager;
  private static final String[] HEADER_TOOLTIPS = {
                                     "Модель тепловоза",
                                     "Номер тепловоза",
                                     "Техническое обслуживание третьего объёма",
                                     "Текущий ремонт первого объёма",
                                     "Текущий ремонт второго объёма",
                                     "Текущий ремонт третьего объёма",
                                     "Средний ремонт",
                                     "Капитальный ремонт",
                                     "Самый поздний из последних ремонтов",
                                     "Наибольший из просроченных или ближайший предстоящий ремонт",
                                     "Поле для заметок"
                                     };
  
  /**
   * Object that renders table header cells in application specific way.
   * <p>
   * Object obtains default table renderer and sets up certain properties to desired values.
   * @param table that owns this renderer
   */
  public RepairRecordsHeaderRenderer(final JTable table) {
    renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    colorManager = ColorManager.getColorManager();
    borderManager = BorderManager.getBorderManager();
  }

  /**
   * Obtains JLabel from default table renderer and sets custom tooltips, border
   * and background color to it.
   */
  @Override
  public Component getTableCellRendererComponent(
      final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column) {
    final JLabel label = (JLabel)
        renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    
    label.setToolTipText(HEADER_TOOLTIPS[column]);
    label.setBorder(borderManager.getHeaderBorder());
    
    if (column == 6) {
      label.setBackground(colorManager.getRecordsTableHeaderMediumRepairColor());
    } else if (column == 7) {
      label.setBackground(colorManager.getRecordsTableHeaderOverhaulColor());
    } else {
      label.setBackground(colorManager.getRecordsTableHeaderDefaultColor());
    }
    
    return label;
  }
  
  @Override
  public String toString() {
    return "RepairRecordsHeaderRenderer - sets up tooltips, border and background color for"
        + "repair records table header."
        + "[colorManager=" + colorManager + ", borderManager=" + borderManager + "]";
  }
  
}
