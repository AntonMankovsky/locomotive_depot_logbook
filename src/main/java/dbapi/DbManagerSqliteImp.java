package dbapi;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("SqliteDb")
public class DbManagerSqliteImp implements DbManager {

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
  public void deleteRepairPeriodsRows(String[] modelName) {
    // TODO Auto-generated method stub

  }

}
