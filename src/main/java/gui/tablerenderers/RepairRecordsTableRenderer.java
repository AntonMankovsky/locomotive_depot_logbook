package gui.tablerenderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import dbapi.DbManager;
import gui.lookandfeel.BorderManager;
import gui.lookandfeel.ColorManager;

public class RepairRecordsTableRenderer extends DefaultTableCellRenderer {
  private final DbManager dbManager;
  private final ColorManager colorManager;
  private final BorderManager borderManager;

  public RepairRecordsTableRenderer(final DbManager dbManager) {
    super();
    super.setHorizontalAlignment(JLabel.CENTER);
    this.dbManager = dbManager;
    colorManager = ColorManager.getColorManager();
    borderManager = BorderManager.getBorderManager();
  }

  @Override
  public Component getTableCellRendererComponent(
      final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column) {
    
    final Color backgroundColor = isSelected ? table.getSelectionBackground()
                                             : defineBackgroundColor(row, column, value);
    super.setBackground(backgroundColor);
    super.setBorder(defineBorder(row, column, hasFocus));
    
    Font font = table.getFont();
    if (column == 8 || column == 9) {
      font = font.deriveFont(font.getStyle() | Font.BOLD);
    }
    super.setFont(font);
    
    super.setText((String) value);
    return this;     
  }
  
  private Color defineBackgroundColor(final int row, final int column, final Object value) {
    final Color color;
    if (row % 2 == 0) {
      switch (column) {
      case 0:
        color = colorManager.getRecordsTableModelNameColor();
        break;
      case 1:
        color = colorManager.getRecordsTableLocoNumberColor();
        break;
      case 8:
        color = colorManager.getRecordsTableLastRepairColor();
        break;
      case 9:
        color = dbManager.getOverdueRepairsMap().get(dbManager.getIdByOrdinalNumber(row / 2))
              ? colorManager.getRecordsTableOverdueRequiredRepairTypeColor()
              : colorManager.getRecordsTableRequiredRepairColor();
        break;
      case 10:
        color = colorManager.getRecordsTableNotesColor();
        break;
      default:
        color = colorManager.getRecordsTablePrimaryRowColor();
        break;
      }
    } else {
      switch (column) {
      case 0:
        color = colorManager.getRecordsTableModelNameColor();
        break;
      case 1:
        color = colorManager.getRecordsTableLocoNumberColor();
        break;
      case 7:
        color = value.equals("") ? colorManager.getRecordsTableSecondaryRowColor()
                                 : colorManager.getRecordsTableNextOverhaulWithValueColor();
        break;
      case 8:
        color = colorManager.getRecordsTableLastRepairColor();
        break;
      case 9:
        color = colorManager.getRecordsTableRequiredRepairColor();
        break;
      case 10:
        color = colorManager.getRecordsTableNotesColor();
        break;
      default:
        color = colorManager.getRecordsTableSecondaryRowColor();
        break;
      }
    }
    return color;
  }
  
  private Border defineBorder(final int row, final int column, final boolean hasFocus) {
    final Border border;
    if (hasFocus) {
      border = borderManager.getDefaultFocusBorder();
    } else {
      if (row % 2 == 0) {
        switch (column) {
        case 0:
          border = borderManager.getNoBottomLineBorder();
          break;
        case 1:
          border = borderManager.getNoBottomLineBorder();
          break;
        case 8:
          border = borderManager.getNoBottomLineBorder();
          break;
        case 9:
          border = borderManager.getNoBottomLineBorder();
          break;
        default:
          border = borderManager.getDefaultNoFocusBorder();
          break;
        }
      } else {
        switch (column) {
        case 0:
          border = borderManager.getNoTopLineBorder();
          break;
        case 1:
          border = borderManager.getNoTopLineBorder();
          break;
        case 8:
          border = borderManager.getNoTopLineBorder();
          break;
        case 9:
          border = borderManager.getNoTopLineBorder();
          break;
        default:
          border = borderManager.getDefaultNoFocusBorder();
          break;
        }
      }
    }
    return border;
  }
}
