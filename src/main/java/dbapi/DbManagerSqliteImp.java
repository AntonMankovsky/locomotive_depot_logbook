package dbapi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import exceptions.IdAlreadyExistsException;
import gui.utility.ModelNamesComparator;

@Service
@Profile("SqliteDb")
public class DbManagerSqliteImp implements DbManager {
  private static final Logger logger = LogManager.getLogger();
  private SqliteConnection connection;
  private Map<String, List<Integer>> repairPeriodsTableData;
  private Map<Integer, List<String>> repairRecordsTableData;
  private Map<Integer, Integer> orderedId;
  private int maxId;
  private List<List<String>> recordsArchiveTableData;
  private boolean archiveInitialized;
  
  /**
   * Gives methods for working with database.
   * <br>
   * Any data manipulation must be done only through methods of this object.
   * @param connection to the database
   */
  @Autowired
  public DbManagerSqliteImp(final SqliteConnection connection) {
    this.connection = connection;
    clearUnusedDiskSpace();
    
    orderedId = new HashMap<>();
    repairRecordsTableData = loadDataFromRepairRecordsTable();
    repairPeriodsTableData = loadDataFromRepairPeriodsTable();
    recordsArchiveTableData = new ArrayList<>(0);
    archiveInitialized = false;
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
    } catch (final SQLException err) {
      logger.error("Row was not inserted in repair records table: " + rowToInsert
          + " Error: " + err.getMessage());
      err.printStackTrace();
      return false;
    }
    try {
      updateRepairRecordsMapWithLastInsertedRow(rowToInsert);
    } catch (SQLException | IdAlreadyExistsException err) {
      rebuildRepairRecordsTableData();
      // TODO: update GUI representation of data (although this is very unrealistic case)
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
    } catch (final IllegalArgumentException err) {
      logger.error("Failed to prepare update cell SQL statement: " 
          + "cannot convert given index to column name: " 
          + err.getMessage());
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
    } catch (final SQLException err) {
      logger.error("Failed to update " + rowId + " row " + columnIndex + " column with value " 
          + value + ": " + err.getMessage());
      err.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean deleteRepairRecord(final int rowId) {
    final String sqlStatement = "DELETE FROM repair_records WHERE id = " + rowId;
    try (final PreparedStatement deleteRow =
        connection.getConnection().prepareStatement(sqlStatement)) {
      deleteRow.executeUpdate();
      insertNewArchiveRecord(repairRecordsTableData.get(rowId));
      repairRecordsTableData.remove(rowId);
      reorderIdOnRowDeletion(rowId);
      logger.info("Row with id=" + rowId + " was succesfully deleted from repair_records table");
      return true;
    } catch (final SQLException err) {
      logger.error("Failed to delete row with id=" + rowId + " from repair_records table: "
          + err.getMessage());
      err.printStackTrace();
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
    } catch (final SQLException err) {
      logger.error("Row was not inserted in repair periods table: " + repairPeriods
          + " Error: " + err.getMessage());
      err.printStackTrace();
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
              + " = " + value + " WHERE loco_model_name = " + "'" + modelName + "'";
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
    } catch (final Exception err) {
      logger.error("Failed to update " + modelName + " row " + columnIndex + " column with value " 
          + value + ": " + err.getMessage());
      err.printStackTrace();
      return false;
    }
  }
  
  @Override
  public boolean deleteRepairPeriods(final String modelName) {
    final String sqlStatement =
        "DELETE FROM repair_periods WHERE loco_model_name = " + "'" + modelName + "'";
      try (final PreparedStatement deleteRow =
          connection.getConnection().prepareStatement(sqlStatement)) {
        deleteRow.executeUpdate();
        repairPeriodsTableData.remove(modelName);
        logger.info(
            "Row with model " + modelName + " was succesfully deleted from repair_periods table");
        return true;
      } catch (final SQLException err) {
        logger.error(
                "Failed to delete row with model " + modelName + " from repair_periods table: "
                + err.getMessage());
        err.printStackTrace();
        return false;
      }
  }

  @Override
  public String[] getAllModelNames() {
    final Set<String> namesSet = repairPeriodsTableData.keySet();
    final String[] names = namesSet.toArray(new String[namesSet.size()]);
    Arrays.sort(names, new ModelNamesComparator());
    return names;
  }
  
  //========================== Methods for records archive table ==========================
  
  public List<List<String>> getAllRecordsArchiveData() {
    if (!archiveInitialized) {
      recordsArchiveTableData = loadDataFromRecordsArchiveTable();
      archiveInitialized = true;
    }
    return recordsArchiveTableData;
  }
  
  public void insertNewArchiveRecord(final List<String> rowToInsert) {
    try (final PreparedStatement insertRow =
          connection.getConnection().prepareStatement(SqlCommands.AT_INSERT_ROW)) {
      for (int j = 0; j < rowToInsert.size(); j++) {
        // After adding 4 new columns before 'notes' in repair_records table, archive insertion
        // loop adjusted to skip those columns and jump from last_overhaul to notes
        if (j == 14) {
          insertRow.setString(15, rowToInsert.get(18));
          break;
        }
        insertRow.setString(j + 1, rowToInsert.get(j));
      }
      insertRow.executeUpdate();
      logger.info(
          "Row was succesfully inserted in records archive database table: " + rowToInsert);
      if (archiveInitialized) {
        recordsArchiveTableData.add(rowToInsert);
      }
    } catch (final SQLException err) {
      logger.error("Row was not inserted in records_archive table: " + rowToInsert
          + " Error: " + err.getMessage());
      err.printStackTrace();
    }
  }
  
  public boolean clearArchive() {
    try (final PreparedStatement clearTable =
          connection.getConnection().prepareStatement("DELETE FROM records_archive;")) {
      clearTable.executeUpdate();
      recordsArchiveTableData.clear();
      logger.info("All data was deleted from archive table");
      return true;
    } catch (final SQLException err) {
      logger.error("Failed to delete all data from repair_periods table: " + err.getMessage());
      err.printStackTrace();
      return false;
    }
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
      
      logger.info(
          "Successfully loaded data from repair periods table (" + data.size() + " rows total).");
    } catch (final SQLException err) {
      final String logString = "Unable to establish connection with database.\n"
          + "SQLException was occured at attempt to load data from repair periods table: "
          + err.getMessage();
      logger.fatal(logString);
      err.printStackTrace();
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
        final List<String> repairRecords = new ArrayList<>(19);
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
        repairRecords.add(validateString(resultSet.getString("last_repair_type")));
        repairRecords.add(validateString(resultSet.getString("last_repair_date")));
        repairRecords.add(validateString(resultSet.getString("required_repair_type")));
        repairRecords.add(validateString(resultSet.getString("required_repair_date")));
        repairRecords.add(validateString(resultSet.getString("notes")));
        data.put(resultSet.getInt("id"), repairRecords);
        
        orderedId.put(maxId++, resultSet.getInt("id"));
      }
      logger.info(
          "Successfully loaded data from repair records table (" + data.size() + " rows total).");
    } catch (final SQLException err) {
      final String logString = "Unable to establish connection with database.\n"
          + "SQLException was occured at attempt to initialize DbManager instance: \n"
          + "can`t load data from repair records table.";
      logger.fatal(logString);
      err.printStackTrace();
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
    } catch (final SQLException err) {
      final String logString = "SQLException was occured at attempt to update repair records map: "
          + err.getMessage();
      logger.warn(logString);
      throw err;
    }
  }
  
  private List<List<String>> loadDataFromRecordsArchiveTable() {
    final List<List<String>> data = new ArrayList<>();
    try (final PreparedStatement fetchData =
        connection.getConnection().prepareStatement(SqlCommands.AT_ALL_DATA)) {
      final ResultSet resultSet = fetchData.executeQuery();
      while (resultSet.next()) {
        final List<String> archiveRecords = new ArrayList<>(15);
        archiveRecords.add(resultSet.getString("loco_model_name"));
        archiveRecords.add(resultSet.getString("loco_number"));
        archiveRecords.add(resultSet.getString("last_three_maintenance"));
        archiveRecords.add(resultSet.getString("next_three_maintenance"));
        archiveRecords.add(resultSet.getString("last_one_current_repair"));
        archiveRecords.add(resultSet.getString("next_one_current_repair"));
        archiveRecords.add(resultSet.getString("last_two_current_repair"));
        archiveRecords.add(resultSet.getString("next_two_current_repair"));
        archiveRecords.add(resultSet.getString("last_three_current_repair"));
        archiveRecords.add(resultSet.getString("next_three_current_repair"));
        archiveRecords.add(resultSet.getString("last_medium_repair"));
        archiveRecords.add(resultSet.getString("next_medium_repair"));
        archiveRecords.add(resultSet.getString("last_overhaul"));
        archiveRecords.add(resultSet.getString("next_overhaul"));
        archiveRecords.add(resultSet.getString("notes"));
        data.add(archiveRecords);
      }
      logger.info(
          "Successfully loaded data from records archive table (" + data.size() + " rows total).");
    } catch (final SQLException err) {
      final String logString = "Unable to establish connection with database.\n"
          + "SQLException was occured at attempt to initialize DbManager instance: \n"
          + "can`t load data from records archive table.";
      logger.error(logString);
      err.printStackTrace();
    }
    
    return data;
  }
  
  /**
   * GUI representation of {@code repairRecordsTableData} must be updated after performing this
   * operation.
   */
  private void rebuildRepairRecordsTableData() {
    repairRecordsTableData = loadDataFromRepairRecordsTable();
    logger.info("Data structure with repair records data was updated with latest database data.");
  }
  
  /**
   * Cleans up unused disk space that remains after previous application sessions.
   * <p>
   * Running VACUUM to rebuild the database reclaims unused space and
   * reduces the size of the database file. 
   * @see <a href="https://www.sqlite.org/lang_vacuum.html">SQLite VACUUM</a>
   */
  private void clearUnusedDiskSpace() {
    try (final PreparedStatement vacuum =
        connection.getConnection().prepareStatement("VACUUM;")) {
      vacuum.executeUpdate();
      logger.info("Database was successfully vacuumed.");
    } catch (final SQLException err) {
      logger.error("Failed to vacuum database: " + err.getMessage());
      err.printStackTrace();
    }
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
