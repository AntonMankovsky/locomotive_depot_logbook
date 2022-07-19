package dbapi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;

import exceptions.IdAlreadyExistsException;

@Service
@Profile("SqliteDb")
public class DbManagerSqliteImp implements DbManager {
  private static final Logger logger = LogManager.getLogger();
  private Map<String, List<Integer>> repairPeriodsTableData;
  private Map<Integer, List<String>> repairRecordsTableData;
  private Map<Integer, Integer> orderedId;
  private int maxId;
  private SqliteConnection connection;
  
  /**
   * Gives methods for working with database.
   * <br>
   * Any data manipulation must be done only through methods of this object.
   * @param connection to the database
   */
  @Autowired
  public DbManagerSqliteImp(final SqliteConnection connection) {
    this.connection = connection;
    orderedId = new HashMap<>();
    repairRecordsTableData = loadDataFromRepairRecordsTable();
    repairPeriodsTableData = loadDataFromRepairPeriodsTable();
  }
  
//========================== Methods for repair records table ==========================

  /**
   * The method provides convenient access to up-to-date information about the data and should not 
   * be used by other classes other than to read the data.
   */
  @Override
  public Map<Integer, List<String>> getAllRepairRecords() {
    return repairRecordsTableData;
  }

  @Override
  public boolean insertNewRepairRecord(final List<String> rowToInsert) {
    try (final PreparedStatement insertRow =
          connection.getConnection().prepareStatement(SqlCommands.RT_INSERT_ROW)) {
      for (int j = 0; j < rowToInsert.size(); j++) {
        insertRow.setString(j + 1, rowToInsert.get(j));
      }
      insertRow.executeUpdate();
      logger.info("Row was succesfully inserted in repair records database table: " + rowToInsert);
    } catch (final SQLException e) {
      logger.error("Row was not inserted in repair records table: " + rowToInsert
          + " Error: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
    try {
      updateRepairRecordsMapWithLastInsertedRow(rowToInsert);
    } catch (SQLException | IdAlreadyExistsException e) {
      rebuildRepairRecordsTableData();
      // TODO: update GUI representation of data (although this is extremely rare case)
    }
    return true;
  }

  @Override
  public boolean setRepairRecordCell(final int rowId, final int columnIndex, final String value) {
    final String sqlStatement;
    try {
      sqlStatement =
        "UPDATE repair_records SET " 
        + IndexToColumnNameTranslator.translateForRepairRecordsTable(columnIndex)
        + " = '" + value + "' WHERE id = " + rowId;
    } catch (final IllegalArgumentException e) {
      logger.error("Failed to prepare update cell SQL statement: " 
          + "cannot convert given index to column name: " 
          + e.getMessage());
      return false;
    }
    
    try (final PreparedStatement updateCell =
          connection.getConnection().prepareStatement(sqlStatement)) {
      updateCell.executeUpdate();
      repairRecordsTableData.get(rowId).set(columnIndex, value);
      final String logString = "Repair records table was succesfully updated: row="
          + rowId + "; column index=" + columnIndex + "; new value=" + value;
      logger.info(logString);
      return true;
    } catch (final SQLException e) {
      logger.error("Failed to update " + rowId + " row " + columnIndex + " column with value " 
          + value + ": " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean deleteRepairRecord(final int rowId) {
    final String sqlStatement = "DELETE FROM repair_records WHERE id = " + rowId;
    try (final PreparedStatement deleteRow =
        connection.getConnection().prepareStatement(sqlStatement)) {
      deleteRow.executeUpdate();
      repairRecordsTableData.remove(rowId);
      reorderIdOnRowDeletion(rowId);
      logger.info("Row with id=" + rowId + " was succesfully deleted from repair_records table");
      return true;
    } catch (final SQLException e) {
      logger.error("Failed to delete row with id=" + rowId + " from repair_records table: "
          + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
  
  private void reorderIdOnRowDeletion(final int rowId) {
    // Find ordered id that points to deleted row id
    final int idToDelete = orderedId.keySet().stream()
                     .filter(key -> orderedId.get(key) == rowId)
                     .limit(1)
                     .reduce((a, b) -> a).get();
    // Shift ordered id`s by one to the left in order to fill the hole
    for (int j = idToDelete; j < maxId; j++) {
      orderedId.put(j, orderedId.get(j + 1));
    }
    maxId--;
  }
  
  @Override
  public int getIdByOrdinalNumber(final int ordinalNumber) {
    return orderedId.get(ordinalNumber);
  }
  
  @Override
  public int getRecordsCount() {
    return maxId;
  }

  // ========================== Methods for repair periods table ==========================
  
  @Override
  public Map<String, List<Integer>> getAllRepairPeriodData() {
    return repairPeriodsTableData;
  }

  @Override
  public boolean insertNewModelRepairPeriods(
                  final String modelName, final List<Integer> repairPeriods) {
    
    try (final PreparedStatement insertRow =
          connection.getConnection().prepareStatement(SqlCommands.PT_INSERT_ROW)) {
      insertRow.setString(1, modelName);
      for (int j = 0; j < repairPeriods.size(); j++) {
        insertRow.setInt(j + 2, repairPeriods.get(j));
      }
      insertRow.executeUpdate();
      repairPeriodsTableData.put(modelName, repairPeriods);
      final String logString = "Row was succesfully inserted in repair periods database table: "
            + modelName + ":" + repairPeriods;
      logger.info(logString);
      return true;
    } catch (final SQLException e) {
      logger.error("Row was not inserted in repair periods table: " + repairPeriods
          + " Error: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean setRepairPeriodCell(
                  final String modelName, final int columnIndex, final int value) {
    final String sqlStatement;
    try {
      sqlStatement =
              "UPDATE repair_periods SET "
              + IndexToColumnNameTranslator.translateForRepairPeriodsTable(columnIndex)
              + " " + value + " WHERE loco_model_name = " + modelName;
    } catch (final IllegalArgumentException e) {
      logger.error("Failed to prepare update cell SQL statement: " 
          + "cannot convert given index to column name: " 
          + e.getMessage());
      return false;
    }

    try (final PreparedStatement updateCell =
          connection.getConnection().prepareStatement(sqlStatement)) {
      updateCell.executeUpdate();
      repairPeriodsTableData.get(modelName).set(columnIndex, value);
      final String logString = "Repair periods table was succesfully updated: row="
          + modelName + "; repair period index=" + columnIndex + "; new value=" + value;
      logger.info(logString);
      return true;
    } catch (final Exception e) {
      logger.error("Failed to update " + modelName + " row " + columnIndex + " column with value " 
          + value + ": " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
  
  @Override
  public boolean deleteRepairPeriods(String[] modelName) {
    for (String row: modelName) {
      try {
        deleteRepairPeriodsRow(row);
        repairPeriodsTableData.remove(row);
      } catch (final SQLException e) {
        logger.error("Failed to delete row with model " + row + " from repair_periods table: "
            + e.getMessage());
        e.printStackTrace();
        return false;
      }
    }
    logger.info("Rows with models " + Arrays.toString(modelName)
    + " was succesfully deleted from repair_periods table");
    return true;
  }
  
  private void deleteRepairPeriodsRow(final String modelName) throws SQLException {
    final String sqlStatement = "DELETE FROM repair_periods WHERE loco_model_name = " + modelName;
    try (final PreparedStatement deleteRow =
        connection.getConnection().prepareStatement(sqlStatement)) {
      deleteRow.executeUpdate();
    }
  }

  @Override
  public String[] getAllModelNames() {
    final Set<String> names = repairPeriodsTableData.keySet();
    return names.toArray(new String[names.size()]);
  }
  
// ====================================== Utility methods ======================================
  
  private Map<String, List<Integer>> loadDataFromRepairPeriodsTable() {
    final Map<String, List<Integer>> data = new HashMap<>();
    try (final PreparedStatement fetchData =
        connection.getConnection().prepareStatement(SqlCommands.PT_ALL_DATA)) {
      final ResultSet resultSet = fetchData.executeQuery();
      while (resultSet.next()) {
        final List<Integer> repairPeriods = new ArrayList<>(6);
        repairPeriods.clear();
        repairPeriods.add(resultSet.getInt("three_maintenance"));
        repairPeriods.add(resultSet.getInt("one_current_repair"));
        repairPeriods.add(resultSet.getInt("two_current_repair"));
        repairPeriods.add(resultSet.getInt("three_current_repair"));
        repairPeriods.add(resultSet.getInt("medium_repair"));
        repairPeriods.add(resultSet.getInt("overhaul"));
        
        data.put(resultSet.getString("loco_model_name"), repairPeriods);
      }
      
      logger.info("Successfully loaded data from repair periods table: " + data);
    } catch (final SQLException e) {
      final String logString = "Unable to establish connection with database.\n"
          + "SQLException was occured at attempt to load data from repair periods table: "
          + e.getMessage();
      logger.fatal(logString);
      e.printStackTrace();
    }
    return data;
  }
  
  private Map<Integer, List<String>> loadDataFromRepairRecordsTable() {
    final Map<Integer, List<String>> data = new HashMap<>();
    try (final PreparedStatement fetchData =
        connection.getConnection().prepareStatement(SqlCommands.RT_ALL_DATA)) {
      final ResultSet resultSet = fetchData.executeQuery();
      maxId = 0;
      while (resultSet.next()) {
        final List<String> repairRecords = new ArrayList<>(15);
        repairRecords.add(resultSet.getString("loco_model_name"));
        repairRecords.add(resultSet.getString("loco_number"));
        repairRecords.add(validateString(resultSet.getString("last_three_maintenance")));
        repairRecords.add(validateString(resultSet.getString("next_three_maintenance")));
        repairRecords.add(validateString(resultSet.getString("last_one_current_repair")));
        repairRecords.add(validateString(resultSet.getString("next_one_current_repair")));
        repairRecords.add(validateString(resultSet.getString("last_two_current_repair")));
        repairRecords.add(validateString(resultSet.getString("next_two_current_repair")));
        repairRecords.add(validateString(resultSet.getString("last_three_current_repair")));
        repairRecords.add(validateString(resultSet.getString("next_three_current_repair")));
        repairRecords.add(validateString(resultSet.getString("last_medium_repair")));
        repairRecords.add(validateString(resultSet.getString("next_medium_repair")));
        repairRecords.add(validateString(resultSet.getString("last_overhaul")));
        repairRecords.add(validateString(resultSet.getString("next_overhaul")));
        repairRecords.add(validateString(resultSet.getString("notes")));
        data.put(resultSet.getInt("id"), repairRecords);
        
        orderedId.put(maxId++, resultSet.getInt("id"));
      }
      logger.info(
          "Successfully loaded data from repair records table (" + data.size() + " rows total).");
    } catch (final SQLException e) {
      final String logString = "Unable to establish connection with database.\n"
          + "SQLException was occured at attempt to initialize DbManager instance: \n"
          + "can`t load data from repair records table.";
      logger.fatal(logString);
      e.printStackTrace();
    }
    
    return data;
  }
  
  /**
   * Converts any null value to empty string.
   * @param tempString to convert if null
   * @return original string or empty string, if original string was NULL
   */
  private String validateString(final String tempString) {
    return tempString != null ? tempString : "";
  }
  
  private void updateRepairRecordsMapWithLastInsertedRow(final List<String> row)
                                                   throws IdAlreadyExistsException, SQLException {
    try (final PreparedStatement getMaxId =
        connection.getConnection().prepareStatement(SqlCommands.RT_MAX_ID)) {
      final int id = getMaxId.executeQuery().getInt("id");
      if (repairRecordsTableData.containsKey(id)) {
        final String logString = "Error on attempt to update repair records map data structure"
            + " with new inserted row: the row with id " + id + " already exists. "
            + "This could lead to inconsistent data. Row that was not add to map: " + row;
        logger.error(logString);
        throw new IdAlreadyExistsException("id already exists in internal data structure: " + id);
      } else {
        repairRecordsTableData.put(id, row);
        orderedId.put(maxId++, id);
        logger.info("Internal data structure was succesfully updated: " + id + ":" + row);
      }
    } catch (final SQLException e) {
      final String logString = "SQLException was occured at attempt to update repair records map: "
          + e.getMessage();
      logger.warn(logString);
      throw e;
    }
  }
  
  /**
   * GUI representation of {@code repairRecordsTableData} must be updated after performing this
   * operation.
   */
  private void rebuildRepairRecordsTableData() {
    repairRecordsTableData = loadDataFromRepairRecordsTable();
    logger.info("Data structure with repair records data was updated with latest database data.");
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DbManagerSqliteImp [connection=");
    builder.append(connection);
    builder.append(", repairPeriodsTableData=");
    builder.append(repairPeriodsTableData);
    builder.append(", repairRecordsTableData=");
    builder.append(repairRecordsTableData);
    builder.append("]");
    return builder.toString();
  }

}
