package gui;

import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import dbapi.DbManager;

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
                                                  "Примечания"
                                                  };
  private final DbManager dbManager;
  
  /**
   * Provides methods for core operations with repair records table.
   * <p>
   * Actively interacts with database API and data validation classes. 
   * @param dbManager that provides API for working with database
   */
  public RepairRecordsTableModel(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
  }
  
  @Override
  public String getColumnName(final int columnIndex) {
    return COLUMN_NAMES[columnIndex];
  }

  @Override
  public int getRowCount() {
    return dbManager.getAllRepairRecords().size() * 2;
  }

  @Override
  public int getColumnCount() {
    return 9;
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
      result = getCellValueForPrimaryRow(rowIndex, columnIndex);
    } else {
      result = getCellValueForSecondaryRow(rowIndex - 1, columnIndex);
    }
    return result;
  }
  
  private String getCellValueForPrimaryRow(final int rowIndex, final int columnIndex) {
    final String result;
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex);
    final Map<Integer, List<String>> data = dbManager.getAllRepairRecords();
    if (columnIndex <= 1) {                                     // loco_model_name or loco_number
      result = data.get(rowId).get(columnIndex);
    } else if (columnIndex == 8) {                              // notes
      result = data.get(rowId).get(14);
    } else {                                                    // last repairs
      final int requiredIndex = columnIndex * 2 - 2;
      result = data.get(rowId).get(requiredIndex);
    }
    return result;
  }
  
  private String getCellValueForSecondaryRow(final int rowIndex, final int columnIndex) {
    if (columnIndex <= 1 || columnIndex == 8) {
      return "";
    }
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex);
    return dbManager.getAllRepairRecords().get(rowId).get(columnIndex * 2 - 1);
  }

  @Override
  public String toString() {
    return "Model for repair records table, performs core operations on data in the table.";
  }

}
