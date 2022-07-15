package gui;

import javax.swing.table.AbstractTableModel;

import datavalidation.InputValidator;
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
  public RepairRecordsTableModel(DbManager dbManager) {
    super();
    this.dbManager = dbManager;
  }
  
  @Override
  public String getColumnName(final int columnIndex) {
    return COLUMN_NAMES[columnIndex];
  }

  @Override
  public int getRowCount() {
    // TODO Auto-generated method stub
    return 6;
  }

  @Override
  public int getColumnCount() {
    // TODO Auto-generated method stub
    return 9;
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    // TODO Auto-generated method stub
    return rowIndex + "" + columnIndex;
  }

  @Override
  public String toString() {
    return "Model for repair records table, performs core operations on data in the table.";
  }
  
  

}
