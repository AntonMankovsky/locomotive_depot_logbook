package datecalculations;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import dbapi.DbManager;

/**
 * Handles values of {@code required_repair} type and date.
 */
public class RequiredRepairHandler {
  private final DbManager dbManager;
  private List<String> recordData;
  private LocalDate today;
  private final DateTimeFormatter formatter;
  private static final String[] REPAIR_NAMES = {"ТО-3", "ТР-1", "ТР-2", "ТР-3", "СР", "КР"};

  /**
   * Object that encapsulates {@code required_repair} column business-logic.
   * @param dbManager to obtain and write data into the database
   */
  public RequiredRepairHandler(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  }
  
  /**
   * Updates database values of {@code required_repair} columns for record with given rowId.
   * <p>
   * Updates DB Manager data structure of overdue repairs with relevant values.
   * @param rowId of database repair record
   * @param todayDate to detect overdue repair and define required repair date 
   */
  public void updateRequiredRepairValues(final int rowId, final LocalDate todayDate) {
    today = todayDate;
    recordData = dbManager.getAllRepairRecords().get(rowId);
    final LocalDate[] nextRepairsDates = getNextRepairsDates();
    
    if (nextRepairsDates == null) {
      dbManager.getOverdueRepairsMap().put(rowId, false);
      return;
    }
    
    if (checkOverdueRepair(nextRepairsDates, rowId)) {
      dbManager.getOverdueRepairsMap().put(rowId, true);
      return;
    }
    dbManager.getOverdueRepairsMap().put(rowId, false);
    
    findNextRequiredRepair(nextRepairsDates, rowId);
  }
  
  /**
   * Returns whether or not repair record has overdue repair.
   * <p>
   * Updates {@code required_repair} values in the database.
   * <p>
   * Looks for given next repair dates and picks the one which is behind today date.
   * If there is more than one overdue repair, the repair of a biggest caliber will be picked.
   * @param nextRepairsDates to choose from
   * @param rowId of repair record to write data into the database
   * @return {@code true} if there is overdue repair
   */
  private boolean checkOverdueRepair(final LocalDate[] nextRepairsDates, final int rowId) {
    LocalDate overdueRepair = null;
    int index = 0;
    for (int j = 0; j < 6; j++) {
     if (nextRepairsDates[j] != null && nextRepairsDates[j].isBefore(today)) {
       overdueRepair = nextRepairsDates[j];
       index = j;
     }
    }
    
    if (overdueRepair != null) {
      final String currentRequiredRepair = recordData.get(17);
      final String requiredRepairDateString = overdueRepair.format(formatter);
      
      if (!currentRequiredRepair.equals(requiredRepairDateString)) {
        dbManager.setRepairRecordCell(rowId, 16, REPAIR_NAMES[index]);
        dbManager.setRepairRecordCell(rowId, 17, requiredRepairDateString);
      }
      
      return true;
    }
    
    return false;
  }
  
  /**
   * Defines next repair date and write corresponding values into the database.
   * <p>
   * Looks for given next repair dates and picks the one which is behind the others on a time scale.
   * <br>
   * {@code checkOverdueRepair} method should be called before this one, cause it is not accounting
   * for the possible overdue repairs.
   * <p>
   * If two or more repairs fall within same month, repair with a bigger caliber will be picked.
   * @param nextRepairsDates to choose from
   * @param rowId of repair record to write data into the database
   */
  private void findNextRequiredRepair(final LocalDate[] nextRepairsDates, final int rowId) {
    // find the nearest next repair
    LocalDate nextRepairDate = today.plusYears(100);
    for (int j = 0; j < 6; j++) {
      if (nextRepairsDates[j] != null && (nextRepairsDates[j].isBefore(nextRepairDate))) {
        nextRepairDate = nextRepairsDates[j];
      }
    }
    // find the largest next repair type within a month of nearest next repair
    int index = 0;
    YearMonth nextRepairMonth = YearMonth.of(nextRepairDate.getYear(), nextRepairDate.getMonth());
    for (int j = 0; j < 6; j++) {
      if (nextRepairsDates[j] != null
          && nextRepairsDates[j].getYear() == nextRepairMonth.getYear()
          && nextRepairsDates[j].getMonth() == nextRepairMonth.getMonth()) {
        nextRepairDate = nextRepairsDates[j];
        index = j;
      }
    }
    // update required repair values if needed
    final String currentRequiredRepair = recordData.get(17);
    final String requiredRepairDateString = nextRepairDate.format(formatter);
    
    if (!currentRequiredRepair.equals(requiredRepairDateString)) {
      dbManager.setRepairRecordCell(rowId, 16, REPAIR_NAMES[index]);
      dbManager.setRepairRecordCell(rowId, 17, requiredRepairDateString);
    }
    
  }
  
  /**
   * Returns array of next repair dates if there is at least one, otherwise returns {@code null}.
   * @return array of next repair dates or {@code null} if record has no next repair dates yet
   */
  private LocalDate[] getNextRepairsDates() {
    final LocalDate[] nextRepairsDates = new LocalDate[6];
    boolean atLeastOneDate = false;
    String nextRepairString;
    for (int j = 3, k = 0; j <= 13; j+=2, k++) {
      nextRepairString = recordData.get(j);
      
      if (nextRepairString == null || nextRepairString.equals("")) {
        continue;
      }
      
      nextRepairsDates[k] = LocalDate.parse(nextRepairString, formatter);
      atLeastOneDate = true;
    }
    return atLeastOneDate ? nextRepairsDates : null;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RequiredRepairHandler to define required repair column values. \n[dbManager=");
    builder.append(dbManager);
    builder.append(", formatter=");
    builder.append(formatter);
    builder.append("]");
    return builder.toString();
  }
  
}
