package datecalculations;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dbapi.DbManager;

/**
 * Handles values of required repair type and date
 */
public class RequiredRepairHandler {
  private final DbManager dbManager;
  private List<String> recordData;
  private LocalDate today;
  private final DateTimeFormatter formatter;
  private static final String[] REPAIR_NAMES = {"ТО-3", "ТР-1", "ТР-2", "ТР-3", "СР", "КР"};

  public RequiredRepairHandler(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  }
  
  /**
   * Updates database values of 'required repair' columns for record with given rowId.
   * @param rowId of repair record
   */
  public void updateRequiredRepairValues(final int rowId) {
    recordData = dbManager.getAllRepairRecords().get(rowId);
    today = LocalDate.now();
    final LocalDate[] nextRepairsDates = getNextRepairsDates();
    
    if (nextRepairsDates == null) {
      return;
    }
    
    if (checkOverdueRepair(nextRepairsDates, rowId)) {
      return;
    }
    
    findNextRequiredRepair(nextRepairsDates, rowId);
  }
  
  /**
   * Returns whether or not repair record has overdue repair.
   * @param nextRepairsDates - array of next repair dates for particular repairs record
   * @param rowId of particular repairs record
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
  
  private void findNextRequiredRepair(final LocalDate[] nextRepairsDates, final int rowId) {
    // find the nearest next repair
    LocalDate nextRepairDate = today.plusYears(50);
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
}