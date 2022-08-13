package gui.tablemodels;

import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import dbapi.DbManager;
import gui.GuiManager;
import gui.utility.RecordUpdateHandler;

/**
 * Provides methods for core operations with repair records table.
 */
public class RepairRecordsTableModel extends AbstractTableModel {
  private static final String[] COLUMN_NAMES = {
                                                  "Модель",
                                                  "Номер",
                                                  "ТО-3",
                                                  "ТР-1",
                                                  "ТР-2",
                                                  "ТР-3",
                                                  "СР",
                                                  "КР",
                                                  "Последний ремонт",
                                                  "Требуемый ремонт",
                                                  "Примечания"
                                                  };
  private final DbManager dbManager;
  final GuiManager guiManager;
  private RecordUpdateHandler updateHandler;
  
  /**
   * Provides methods for core operations with repair records table.
   * <p>
   * Actively interacts with database API and data validation classes. 
   * @param dbManager that provides API for working with database
   */
  public RepairRecordsTableModel(final DbManager dbManager, final GuiManager guiManager, 
                                 final RecordUpdateHandler updateHandler) {
    super();
    this.dbManager = dbManager;
    this.guiManager = guiManager;
    this.updateHandler = updateHandler;
  }
  
  @Override
  public String getColumnName(final int columnIndex) {
    return COLUMN_NAMES[columnIndex];
  }

  @Override
  public int getRowCount() {
    return dbManager.getRecordsCount() * 2;
  }

  @Override
  public int getColumnCount() {
    return 11;
  }
  
  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {
    if (rowIndex % 2 != 0 || columnIndex == 0 || columnIndex == 8 || columnIndex == 9) {
      return false;
    }
    return true;
  }

  /*
   * One row in database table is presented as two rows in GUI table.
   * It leads to the need of determination which row to access: every row with even index 
   * (index 0 included) is a separate row in database table; every row with odd index is the same
   * row as a previous one in database table.
   */
  @Override
  public String getValueAt(final int rowIndex, final int columnIndex) {
    final String result;
    if (rowIndex % 2 == 0) {
      result = getCellValueForPrimaryRow(rowIndex / 2, columnIndex);
    } else {
      result = getCellValueForSecondaryRow((rowIndex - 1) / 2, columnIndex);
    }
    return result;
  }
  
  private String getCellValueForPrimaryRow(final int rowIndex, final int columnIndex) {
    final String result;
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex);
    final Map<Integer, List<String>> data = dbManager.getAllRepairRecords();
    if (columnIndex <= 1) {                                     // loco_model_name or loco_number
      result = data.get(rowId).get(columnIndex);
    } else if (columnIndex == 10) {                              // notes
      result = data.get(rowId).get(18);
    } else {                                                    // last repairs and repair types
      final int requiredIndex = columnIndex * 2 - 2;
      result = data.get(rowId).get(requiredIndex);
    }
    return result;
  }
  
  private String getCellValueForSecondaryRow(final int rowIndex, final int columnIndex) {
    if (columnIndex <= 1 || columnIndex == 10) {
      return "";
    }
    
    if (columnIndex > 1 && columnIndex < 8 && !guiManager.isShowNextRepairsDates()) {
      return "";
    }
    
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex);
      return dbManager.getAllRepairRecords().get(rowId).get(columnIndex * 2 - 1);
  }
  
  @Override
  public void setValueAt(final Object value, final int rowIndex, final int colIndex) {
    String valueString = (String) value;
    valueString = valueString != null ? valueString.trim() : null;
    
    if (rowIndex % 2 == 0 && !valueString.equals(getValueAt(rowIndex, colIndex))) {
      updateHandler.handleCellNewValue(valueString, rowIndex, colIndex);
    }
  }

  @Override
  public String toString() {
    return "Model for repair records table, performs core operations on data in the table.";
  }

}
