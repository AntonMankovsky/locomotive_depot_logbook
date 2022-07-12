package dbapi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("SqliteDb")
public class DbManagerSqliteImp implements DbManager {
  private static final Logger logger = LogManager.getLogger();
  private SqliteConnection connection;
  private Map<String, List<Integer>> repairPeriodsTableData;
  
  @Autowired
  public DbManagerSqliteImp(final SqliteConnection connection) {
    this.connection = connection;
    repairPeriodsTableData = loadDataFromRepairPeriodsTable();
  }

  @Override
  public Map<Integer, List<String>> getAllRepairRecords() {
    // TODO Auto-generated method stub
    return null;
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
    // TODO Auto-generated method stub
    return null;
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
  public String[] deleteRepairPeriodsRows() {
    // TODO Auto-generated method stub
    return null;
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
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DbManagerSqliteImp [repairPeriodsTableData=");
    builder.append(repairPeriodsTableData);
    builder.append("]");
    return builder.toString();
  }
  
}
