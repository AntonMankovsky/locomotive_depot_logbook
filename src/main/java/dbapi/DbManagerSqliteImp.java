package dbapi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import exceptions.IdAlreadyExistsException;

@Service
@Profile("SqliteDb")
public class DbManagerSqliteImp implements DbManager {
  private static final Logger logger = LogManager.getLogger();
  private Map<String, List<Integer>> repairPeriodsTableData;
  private Map<Integer, List<String>> repairRecordsTableData;
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
    repairRecordsTableData = loadDataFromRepairRecordsTable();
  }
  
//========================== Methods for repair records table ==========================

  @Override
  public Map<Integer, List<String>> getAllRepairRecords() {
    return repairRecordsTableData;
  }

  @Override
  public boolean insertNewRepairRecord(final List<String> rowToInsert) {
    try (final PreparedStatement insertRow =
          connection.getConnection().prepareStatement(SqlCommands.RT_INSERT_ROW);) {
      for (int j = 0; j < rowToInsert.size(); j++) {
        insertRow.setString(j + 1, rowToInsert.get(j));
      }
      insertRow.executeUpdate();
    } catch (final SQLException e) {
      logger.error("Row was not inserted in repair records table: " + rowToInsert
          + "Error: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
    logger.info("Row was succesfully inserted in repair records database table: " + rowToInsert);
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
    final String sqlStatement =
        "UPDATE repair_records SET " 
        + IndexToColumnNameTranslator.translateForRepairRecordsTable(columnIndex)
        + " " + value + " WHERE id = " + rowId;
    try (final PreparedStatement updateCell =
          connection.getConnection().prepareStatement(sqlStatement)) {
      updateCell.executeUpdate();
      repairRecordsTableData.get(rowId).set(columnIndex, value);
      return true;
    } catch (final SQLException e) {
      logger.error("Failed to update " + rowId + " row " + columnIndex + " column with value " 
          + value + ": " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean deleteRepairRecords(int[] rowId) {
    for (int row : rowId) {
      try {
        deleteRepairRecordRow(row);
        repairRecordsTableData.remove(row);
      } catch (final SQLException e) {
        logger.error("Failed to delete row with id " + row + " from repair_records table: "
            + e.getMessage());
        e.printStackTrace();
        return false;
      }
    }
    logger.info("Rows " + Arrays.toString(rowId)
          + " was succesfully deleted from repair_records table");
    return true;
  }
  
  private void deleteRepairRecordRow(final int rowId) throws SQLException {
    final String sqlStatement = "DELETE FROM repair_records WHERE id = " + rowId;
    try (final PreparedStatement deleteRow =
        connection.getConnection().prepareStatement(sqlStatement)) {
      deleteRow.executeUpdate();
    }
  }

  // ========================== Methods for repair periods table ==========================
  
  @Override
  public Map<String, List<Integer>> getAllRepairPeriodData(final boolean wasInitialized) {
    if (wasInitialized == false) {
      repairPeriodsTableData = loadDataFromRepairPeriodsTable();
    }
    return repairPeriodsTableData;
  }

  @Override
  public void insertNewModelRepairPeriods(String modelName, List<Integer> repairPeriods) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setRepairPeriodCell(final String modelName, final int columnIndex, final int value) {
    // TODO Auto-generated method stub

  }

  @Override
  public String[] getAllModelNames() {
    return (String[]) repairPeriodsTableData.keySet().toArray();
  }
  
// ====================================== Utility methods ======================================
  
  private Map<String, List<Integer>> loadDataFromRepairPeriodsTable() {
    final Map<String, List<Integer>> data = new HashMap<>();
    final List<Integer> repairPeriods = new ArrayList<>(6);
    try (final PreparedStatement fetchData =
        connection.getConnection().prepareStatement(SqlCommands.PT_ALL_DATA)) {
      final ResultSet resultSet = fetchData.executeQuery();
      while (resultSet.next()) {
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
    final List<String> repairRecords = new ArrayList<>(15);
    try (final PreparedStatement fetchData =
        connection.getConnection().prepareStatement(SqlCommands.RT_ALL_DATA)) {
      final ResultSet resultSet = fetchData.executeQuery();
      while (resultSet.next()) {
        repairRecords.clear();
        repairRecords.add(resultSet.getString("loco_model_name"));
        repairRecords.add(resultSet.getString("loco_number"));
        repairRecords.add(validateString(resultSet.getString("last_three_maintenance")));
        repairRecords.add(validateString(resultSet.getString("last_three_maintenance")));
        repairRecords.add(validateString(resultSet.getString("next_three_maintenance")));
        repairRecords.add(validateString(resultSet.getString("last_three_current_repair")));
        repairRecords.add(validateString(resultSet.getString("next_three_current_repair")));
        repairRecords.add(validateString(resultSet.getString("last_two_current_repair")));
        repairRecords.add(validateString(resultSet.getString("next_two_current_repair")));
        repairRecords.add(validateString(resultSet.getString("last_one_current_repair")));
        repairRecords.add(validateString(resultSet.getString("next_one_current_repair")));
        repairRecords.add(validateString(resultSet.getString("last_medium_repair")));
        repairRecords.add(validateString(resultSet.getString("next_medium_repair")));
        repairRecords.add(validateString(resultSet.getString("last_overhaul")));
        repairRecords.add(validateString(resultSet.getString("next_overhaul")));
        repairRecords.add(validateString(resultSet.getString("notes")));
        
        data.put(resultSet.getInt("id"), repairRecords);
      }
      
      logger.info("Successfully loaded data from repair records table: " + data);
    } catch (final SQLException e) {
      final String logString = "Unable to establish connection with database.\n"
          + "SQLException was occured at attempt to initialize DbManager instance: \n"
          + "can`t load data from repair records table.";
      logger.fatal(logString);
      e.printStackTrace();
    }
    
    return data;
  }
  
  private String validateString(final String tempString) {
    return tempString != null ? tempString : "";
  }
  
  private void updateRepairRecordsMapWithLastInsertedRow(final List<String> row)
                                                   throws IdAlreadyExistsException, SQLException {
    try (final PreparedStatement getMaxId =
        connection.getConnection().prepareStatement(SqlCommands.RT_MAX_ID)) {
      final int id = getMaxId.executeQuery().getInt("id");
      if (repairPeriodsTableData.containsKey(id)) {
        final String logString = "Error on attempt to update repair records map data structure\n"
            + " with new inserted row: the row with id" + id + "already exists. \n"
            + "This could lead to inconsistent data. Row that was not add to map: " + row;
        logger.error(logString);
        throw new IdAlreadyExistsException("id already exists in internal data structure: " + id);
      } else {
        repairRecordsTableData.put(id, row);
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
