package dbapi;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides functionality for working with database.
 * <p>
 * Defines methods which could be used by GUI client to read and write actual data.
 */
public abstract interface DbManager {
  
  // Methods for repair records table
  
  /**
   * Returns all data from repair records table.
   * <p>
   * If the value of particular nullable cell is SQL {@code NULL}, it would be replaced with empty 
   * string in resulting list. 
   * <br>
   * Therefore, for any column except {@code loco_model_id}, {@code loco_number} and {@code notes}
   * (indices 0, 1 and 14 in list), there is only two possible values: empty string or string in
   * format {@code "dd.MM.yyyy"}.
   * @return map with rows {@code id} as keys and other columns in list of strings as values from
   * repair records database table.
   */
  public abstract Map<Integer, List<String>> getAllRepairRecords();
  
  /**
   * Create new row in repair records table. 
   * <p>
   * @param rowToInsert represents new repair record for database. 
   * Locomotive model and number (indices 0 and 1) could not be {@code NULL} or empty string.
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean insertNewRepairRecord(List<String> rowToInsert);
  
  /**
   * Set value of particular cell in repair records table.
   * @param rowId as row coordinate
   * @param columnIndex as column coordinate
   * @param value to set
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean setRepairRecordCell(int rowId, int columnIndex, String value);
  
  /**
   * Delete rows from repair record table.
   * @param rowId array of rows that should be deleted
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean deleteRepairRecords(int[] rowId);
  
  /**
   * Converts ordinal number to corresponding id in repair_records database table.
   * <p>
   * This mechanic provides consistent access to repair_records rows id`s. Id`s in database table
   * not necessarily are continuous natural numbers, furthermore, internal data structure for 
   * database data representation is a map, which unordered by it`s nature. Meanwhile, GUI table
   * needs to order rows as if id`s were continuous natural numbers starting with 0.
   * <br>
   * This method allows to convert GUI table row id to actual row id in a consistent way.
   * @param ordinalNumber to convert
   * @return corresponding {@code id} in repair_records database table
   */
  public abstract int getIdByOrdinalNumber(int ordinalNumber);
  
  // Methods for repair periods table
  
/**
 * Returns all data from repair periods table.
 * <p>
 * The table is meant to be used rarely so it should build GUI components and load data only when
 * user opens Model Frame.
 * @param wasInitialized for lazy loading
 * @return map with model names as keys and other columns in list of integers as values from
 * repair periods database table.
 */
  public abstract Map<String, List<Integer>> getAllRepairPeriodData(boolean wasInitialized);
  
  /**
   * Create new row in repair periods table.
   * <p>
   * @param modelName unique identifier of model in repair periods table
   * @param repairPeriods in list for all possible repair types
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean insertNewModelRepairPeriods(
      String modelName, List<Integer> repairPeriods);
  
  /**
   * Set value of particular cell in repair periods table.
   * @param modelName as row coordinate
   * @param columnIndex as column coordinate
   * @param value to set
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean setRepairPeriodCell(String modelName, int columnIndex, int value);
  
  /**
   * Delete rows from repair periods table.
   * @param rowId array of rows that should be deleted
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean deleteRepairPeriods(String[] modelName);
  
  /**
   * Returns all model names from repair periods table.
   * @return unique model names
   */
  public abstract String[] getAllModelNames();
}