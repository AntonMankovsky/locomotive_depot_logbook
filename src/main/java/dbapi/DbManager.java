package dbapi;

import java.util.List;
import java.util.Map;

/**
 * Provides functionality for working with database.
 * <p>
 * Defines methods which could be used by GUI client to read and write actual data.
 */
public abstract interface DbManager {
  
  // Methods for repair records table
  
  /**
   * Returns all data from repair records table.
   * @return map with rows {@code id} as keys and other columns in list of strings as values from
   * repair records database table.
   * <br>
   * If the value of particular nullable cell is SQL {@code NULL}, it would be replaced with empty 
   * string in resulting list. 
   * <br>
   * Therefore, for any column except {@code loco_model_id} and {@code loco_number} (indices 0 and
   * 1 in list), there is only two possible values: empty string or string in format
   * {@code "dd/MM/yyyy"}.
   */
  public abstract Map<Integer, List<String>> getAllRepairRecords();
  
  /**
   * Create new row in repair records table.
   * <p>
   * @param rowToInsert represents new repair record for database. 
   * Locomotive model and number (indices 0 and 1) could not be {@code NULL} or empty string.
   */
  public abstract void insertNewRepairRecord(List<String> rowToInsert);
  
  /**
   * Set value of particular cell in repair records table.
   * @param rowId as row coordinate
   * @param columnIndex as column coordinate
   */
  public abstract void setRepairRecordCell(int rowId, int columnIndex);
  
  /**
   * Delete rows from repair record table.
   * @param rowId array of rows that should be deleted
   */
  public abstract void deleteRepairRecords(int[] rowId);
  
  // Methods for repair periods table
  
  /**
   * Returns all data from repair periods table.
   * @return map with model names as keys and other columns in list of integers as values from 
   * repair periods database table.
   */
  public abstract Map<String, List<Integer>> getAllRepairPeriodData();
  
  /**
   * Create new row in repair periods table.
   * <p>
   * @param modelName unique identifier of model in repair periods table
   * @param repairPeriods in list for all possible repair types
   */
  public abstract void insertNewModelRepairPeriods(String modelName, List<Integer> repairPeriods);
  
  /**
   * Set value of particular cell in repair periods table.
   * @param rowId as row coordinate
   * @param columnIndex as column coordinate
   */
  public abstract void setRepairPeriodCell(int rowId, int columnIndex);
  
  /**
   * Delete rows from repair period table.
   * @param modelName array of models that should be deleted
   */
  public abstract void deleteRepairPeriodsRows(String[] modelName);
  
}