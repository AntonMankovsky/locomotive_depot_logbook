package gui.tablemodels;

import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import dbapi.DbManager;

public class RecordsArchiveTableModel extends AbstractTableModel {
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
     * Provides methods for core operations with records archive table.
     * @param dbManager that provides API for working with database
     */
    public RecordsArchiveTableModel(final DbManager dbManager) {
      super();
      this.dbManager = dbManager;
    }
    
    /**
     * Enables editing mode to allow user to copy value of particular cell.
     * <p>
     * Without overriding setValue method it provides read-only mode.
     * Changing archive values is not allowed by design.
     */
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
      return true;
    }
    
    @Override
    public String getColumnName(final int columnIndex) {
      return COLUMN_NAMES[columnIndex];
    }
    
    @Override
    public int getRowCount() {
      return dbManager.getAllRecordsArchiveData().size() * 2;
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
        result = getCellValueForPrimaryRow(rowIndex / 2, columnIndex);
      } else {
        result = getCellValueForSecondaryRow((rowIndex - 1) / 2, columnIndex);
      }
      return result;
    }
    
    private String getCellValueForPrimaryRow(final int rowIndex, final int columnIndex) {
      final String result;
      final List<List<String>> data = dbManager.getAllRecordsArchiveData();
      if (columnIndex <= 1) {                                     // loco_model_name or loco_number
        result = data.get(rowIndex).get(columnIndex);
      } else if (columnIndex == 8) {                              // notes
        result = data.get(rowIndex).get(14);
      } else {                                                    // last repairs
        final int requiredIndex = columnIndex * 2 - 2;
        result = data.get(rowIndex).get(requiredIndex);
      }
      return result;
    }
    
    private String getCellValueForSecondaryRow(final int rowIndex, final int columnIndex) {
      if (columnIndex <= 1 || columnIndex == 8) {
        return "";
      }
      return dbManager.getAllRecordsArchiveData().get(rowIndex).get(columnIndex * 2 - 1);
    }
    
    @Override
    public String toString() {
      return "Model for records archive table, performs core operations on data in the table.";
    }
}
