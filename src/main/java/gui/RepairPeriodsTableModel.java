package gui;


import javax.swing.table.AbstractTableModel;

import dbapi.DbManager;

public class RepairPeriodsTableModel extends AbstractTableModel {
  private static final String[] COLUMN_NAMES = {
                                                  "Модель",
                                                  "ТО-3",
                                                  "ТР-1",
                                                  "ТР-2",
                                                  "ТР-3",
                                                  "СР",
                                                  "КР",
                                                  };
  private final DbManager dbManager;

  /**
   * Provides methods for core operations with repair records table.
   * <p>
   * Actively interacts with database API and data validation classes. 
   * @param dbManager that provides API for working with database
   */
  public RepairPeriodsTableModel(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
  }
  
  @Override
  public String getColumnName(final int columnIndex) {
    return COLUMN_NAMES[columnIndex];
  }

  @Override
  public int getRowCount() {
    return dbManager.getAllRepairPeriodData().size();
  }

  @Override
  public int getColumnCount() {
    return 7;
  }
  
  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {
    if (columnIndex == 0) {
      return false;
    }
    return true;
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    final String[] models = dbManager.getAllModelNames();
    if (columnIndex == 0) {
      return models[rowIndex];
    } else {
      return dbManager.getAllRepairPeriodData().get(models[rowIndex]).get(columnIndex - 1);
    }
  }
  
  @Override
  public String toString() {
    return "Model for repair periods table, performs core operations on data in the table.";
  }

}
