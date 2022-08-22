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
   * <p>
   * If the value of particular nullable cell is SQL {@code NULL}, it would be replaced with empty 
   * string in a resulting list. 
   * @return map with rows {@code id} as keys and other columns in list of strings as values from
   * repair records database table.
   */
  public abstract Map<Integer, List<String>> getAllRepairRecords();
  
  /**
   * Creates new row in repair records table. 
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
   * Deletes row from repair record table.
   * @param rowId to delete
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean deleteRepairRecord(int rowId);
  
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
  
  /**
   * Returns count of rows in {@code repair_records} database table.
   * <p>
   * Although there are other (more expensive) ways to access this value,
   * the need for such a method is due to the huge number of calls to
   * this value during the rendering of GUI table.
   * <br>
   * @return count of rows in {@code repair_record} table with minimal overhead
   */
  public abstract int getRecordsCount();
  
  /**
   * Provides access to information about overdue repairs.
   * <p>
   * The map contains record id integers as keys and isOverdue booleans as values.
   * @return map that allows to get or set information about overdue repairs.
   */
  public abstract Map<Integer, Boolean> getOverdueRepairsMap();
  
  // Methods for repair periods table
  
/**
 * Returns all data from repair periods table.
 * @return map with model names as keys and other columns in list of integers as values from
 * repair periods database table.
 */
  public abstract Map<String, List<Integer>> getAllRepairPeriodData();
  
  /**
   * Creates new row in repair periods table.
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
   * Deletes row from repair periods table.
   * @param modelName to delete
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean deleteRepairPeriods(String modelName);
  
  /**
   * Returns all model names from repair periods table.
   * @return unique model names
   */
  public abstract String[] getAllModelNames();
  
  // Methods for records archive table
  
  /**
   * Returns all data from records archive table.
   * <p>
   * The table is meant to be used rarely so it should build GUI components and load data only when
   * user opens Archive Frame.
   * @return list of rows from records archive database table.
   */
    public abstract List<List<String>> getAllRecordsArchiveData();
  
  /**
   * Creates new row in records archive table. 
   * <p>
   * Method suppose to be called in deleteRepairRecord method, cause this table is meant to
   * contain only repair records that was deleted from repair_records table.
   * @param rowToInsert represents new archive record for database. 
   */
  public abstract void insertNewArchiveRecord(List<String> rowToInsert);
  
  /**
   * Deletes all records from records_archive table.
   * @return {@code false} if operation fails and {@code true} if operation succeeds
   */
  public abstract boolean clearArchive();
  
}