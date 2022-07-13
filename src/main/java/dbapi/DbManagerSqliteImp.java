package dbapi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("SqliteDb")
public class DbManagerSqliteImp implements DbManager {
  private static final Logger logger = LogManager.getLogger();
  private final Map<String, List<Integer>> repairPeriodsTableData;
  private final Map<Integer, List<String>> repairRecordsTableData;
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
    repairPeriodsTableData = loadDataFromRepairPeriodsTable();
    repairRecordsTableData = loadDataFromRepairRecordsTable();
  }
  
//========================== Methods for repair records table ==========================

  @Override
  public Map<Integer, List<String>> getAllRepairRecords() {
    return repairRecordsTableData;
  }

  @Override
  public void insertNewRepairRecord(List<String> rowToInsert) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setRepairRecordCell(int rowId, int columnIndex) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteRepairRecords(int[] rowId) {
    // TODO Auto-generated method stub

  }

  // ========================== Methods for repair periods table ==========================
  
  @Override
  public Map<String, List<Integer>> getAllRepairPeriodData() {
    return repairPeriodsTableData;
  }

  @Override
  public void insertNewModelRepairPeriods(String modelName, List<Integer> repairPeriods) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setRepairPeriodCell(int rowId, int columnIndex) {
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
        connection.getConnection().prepareStatement(SqlCommands.PT_ALL_DATA);) {
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
      
      logger.info("Successfully loaded data from repair periods table at instantiating DbManager: "
          + data);
    } catch (final SQLException e) {
      final String logString = "Unable to establish connection with database.\n"
          + "SQLException was occured at attempt to initialize DbManager instance: \n"
          + "can`t load data from repair periods table.";
      logger.fatal(logString);
      e.printStackTrace();
    }
    return data;
  }
  
  private Map<Integer, List<String>> loadDataFromRepairRecordsTable() {
    final Map<Integer, List<String>> data = new HashMap<>();
    final List<String> repairRecords = new ArrayList<>(15);
    try (final PreparedStatement fetchData =
        connection.getConnection().prepareStatement(SqlCommands.RT_ALL_DATA);) {
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
      
      logger.info("Successfully loaded data from repair records table at instantiating DbManager: "
          + data);
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
