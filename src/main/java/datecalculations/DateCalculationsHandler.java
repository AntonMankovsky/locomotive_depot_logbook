package datecalculations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import dbapi.DbManager;
import gui.GuiManager;
import gui.utility.DialogWindow;

public class DateCalculationsHandler {
  private final GuiManager guiManager;
  private final DbManager dbManager;
  private final RequiredRepairHandler requiredRepairHandler;
  private List<String> recordData;
  private List<Integer> periodsData;
  private final DateTimeFormatter formatter;
  private final DialogWindow dialogWindow;
  
  public DateCalculationsHandler(final GuiManager guiManager, final DbManager dbManager,
                                 final RequiredRepairHandler requiredRepairHandler,
                                 final DialogWindow dialogWindow) {
    super();
    this.guiManager = guiManager;
    this.dbManager = dbManager;
    this.requiredRepairHandler = requiredRepairHandler;
    this.dialogWindow = dialogWindow;
    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  }

  public void handleDateCalculations(final String lastRepairString, final int rowIndex,
                                     final int colIndex, final LocalDate today ) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    recordData = dbManager.getAllRepairRecords().get(rowId);
    periodsData = dbManager.getAllRepairPeriodData().get(recordData.get(0));
    final LocalDate lastRepairDate = LocalDate.parse(lastRepairString, formatter);
    switch (colIndex) {
    case 2:
      threeMaintenanceCase(lastRepairDate, rowIndex, colIndex);
      break;
    case 3:
      oneCurrentRepairCase(lastRepairDate, rowIndex, colIndex);
      break;
    case 4:
      twoCurrentRepairCase(lastRepairDate, rowIndex, colIndex);
      break;
    case 5:
      threeCurrentRepairCase(lastRepairDate, rowIndex, colIndex);
      break;
    case 6:
      mediumRepairCase(lastRepairDate, rowIndex, colIndex);
      break;
    case 7:
      overhaulCase(lastRepairDate, rowIndex, colIndex);
      break;
    default:
    }
    
    updateRequiredRepairColumn(rowIndex);
    informUserIfLastRepairDateIsAfterToday(lastRepairDate, today);
  }
  
  private void threeMaintenanceCase(
      final LocalDate lastThreeMaintenanceDate, final int rowIndex, final int colIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    final long threeMaintenancePeriod = periodsData.get(0);
    final LocalDate nextTreeMaintenanceDate =
        lastThreeMaintenanceDate.plusDays(threeMaintenancePeriod);
    final String nextTreeMaintenanceString = nextTreeMaintenanceDate.format(formatter);
    
    dbManager.setRepairRecordCell(rowId, colIndex * 2 - 1, nextTreeMaintenanceString);
    fireCellUpdated(rowIndex + 1, colIndex, colIndex);
  }
  
  private void oneCurrentRepairCase(
      final LocalDate lastOneCurrentRepairDate, final int rowIndex, final int colIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    
    final long threeMaintenancePeriod = periodsData.get(0);
    final long oneCurrentRepairPeriod = periodsData.get(1);
    
    final LocalDate nextTreeMaintenanceDate =
        lastOneCurrentRepairDate.plusDays(threeMaintenancePeriod);
    final LocalDate nextOneCurrentRepairDate =
        lastOneCurrentRepairDate.plusDays(oneCurrentRepairPeriod);
    
    final String nextTreeMaintenanceString = nextTreeMaintenanceDate.format(formatter);
    final String nextOneCurrentRepairString = nextOneCurrentRepairDate.format(formatter);
    
    // TODO: replace multiple database calls for every cell with single call that would
    // insert values in database for all cells of row at once (for all repair cases).
    dbManager.setRepairRecordCell(rowId, colIndex * 2 - 1, nextOneCurrentRepairString);
    if (shouldUpdate((colIndex - 1) * 2 - 1, nextTreeMaintenanceDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextTreeMaintenanceString);
    }
    
    fireCellUpdated(rowIndex + 1, colIndex, colIndex - 1);
  }
  
  private void twoCurrentRepairCase(
      final LocalDate lastTwoCurrentRepairDate, final int rowIndex, final int colIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    
    final long threeMaintenancePeriod = periodsData.get(0);
    final long oneCurrentRepairPeriod = periodsData.get(1);
    final long twoCurrentRepairPeriod = periodsData.get(2);
    
    final LocalDate nextTreeMaintenanceDate =
        lastTwoCurrentRepairDate.plusDays(threeMaintenancePeriod);
    final LocalDate nextOneCurrentRepairDate =
        lastTwoCurrentRepairDate.plusDays(oneCurrentRepairPeriod);
    final LocalDate nextTwoCurrentRepairDate =
        lastTwoCurrentRepairDate.plusDays(twoCurrentRepairPeriod);

    final String nextTreeMaintenanceString = nextTreeMaintenanceDate.format(formatter);
    final String nextOneCurrentRepairString = nextOneCurrentRepairDate.format(formatter);
    final String nextTwoCurrentRepairString = nextTwoCurrentRepairDate.format(formatter);
    
    dbManager.setRepairRecordCell(rowId, colIndex * 2 - 1, nextTwoCurrentRepairString);
    if (shouldUpdate((colIndex - 1) * 2 - 1, nextOneCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextOneCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 2) * 2 - 1, nextTreeMaintenanceDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 2) * 2 - 1, nextTreeMaintenanceString);
    }

    fireCellUpdated(rowIndex + 1, colIndex, colIndex - 2);
  }
  
  private void threeCurrentRepairCase(
      final LocalDate lastThreeCurrentRepairDate, final int rowIndex, final int colIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    
    final long threeMaintenancePeriod = periodsData.get(0);
    final long oneCurrentRepairPeriod = periodsData.get(1);
    final long twoCurrentRepairPeriod = periodsData.get(2);
    final long threeCurrentRepairPeriod = periodsData.get(3);
    
    final LocalDate nextTreeMaintenanceDate =
        lastThreeCurrentRepairDate.plusDays(threeMaintenancePeriod);
    final LocalDate nextOneCurrentRepairDate =
        lastThreeCurrentRepairDate.plusDays(oneCurrentRepairPeriod);
    final LocalDate nextTwoCurrentRepairDate =
        lastThreeCurrentRepairDate.plusDays(twoCurrentRepairPeriod);
    final LocalDate nextThreeCurrentRepairDate =
        lastThreeCurrentRepairDate.plusDays(threeCurrentRepairPeriod);
    
    final String nextTreeMaintenanceString = nextTreeMaintenanceDate.format(formatter);
    final String nextOneCurrentRepairString = nextOneCurrentRepairDate.format(formatter);
    final String nextTwoCurrentRepairString = nextTwoCurrentRepairDate.format(formatter);
    final String nextThreeCurrentRepairString = nextThreeCurrentRepairDate.format(formatter);
    
    dbManager.setRepairRecordCell(rowId, colIndex * 2 - 1, nextThreeCurrentRepairString);
    if (shouldUpdate((colIndex - 1) * 2 - 1, nextTwoCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextTwoCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 2) * 2 - 1, nextOneCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 2) * 2 - 1, nextOneCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 3) * 2 - 1, nextTreeMaintenanceDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 3) * 2 - 1, nextTreeMaintenanceString);
    }
    
    fireCellUpdated(rowIndex + 1, colIndex, colIndex - 3);
  }
  
  private void mediumRepairCase(
      final LocalDate lastMediumRepairDate, final int rowIndex, final int colIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    
    final long threeMaintenancePeriod = periodsData.get(0);
    final long oneCurrentRepairPeriod = periodsData.get(1);
    final long twoCurrentRepairPeriod = periodsData.get(2);
    final long threeCurrentRepairPeriod = periodsData.get(3);
    final long mediumRepairPeriod = periodsData.get(4);
    
    final LocalDate nextTreeMaintenanceDate =
        lastMediumRepairDate.plusDays(threeMaintenancePeriod);
    final LocalDate nextOneCurrentRepairDate =
        lastMediumRepairDate.plusDays(oneCurrentRepairPeriod);
    final LocalDate nextTwoCurrentRepairDate =
        lastMediumRepairDate.plusDays(twoCurrentRepairPeriod);
    final LocalDate nextThreeCurrentRepairDate =
        lastMediumRepairDate.plusDays(threeCurrentRepairPeriod);
    final LocalDate nextMediumRepairDate =
        lastMediumRepairDate.plusDays(mediumRepairPeriod);
    
    final String nextTreeMaintenanceString = nextTreeMaintenanceDate.format(formatter);
    final String nextOneCurrentRepairString = nextOneCurrentRepairDate.format(formatter);
    final String nextTwoCurrentRepairString = nextTwoCurrentRepairDate.format(formatter);
    final String nextThreeCurrentRepairString = nextThreeCurrentRepairDate.format(formatter);
    final String nextMediumRepairString = nextMediumRepairDate.format(formatter);
    
    dbManager.setRepairRecordCell(rowId, colIndex * 2 - 1, nextMediumRepairString);
    if (shouldUpdate((colIndex - 1) * 2 - 1, nextThreeCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextThreeCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 2) * 2 - 1, nextTwoCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 2) * 2 - 1, nextTwoCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 3) * 2 - 1, nextOneCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 3) * 2 - 1, nextOneCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 4) * 2 - 1, nextTreeMaintenanceDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 4) * 2 - 1, nextTreeMaintenanceString);
    }
    
    fireCellUpdated(rowIndex + 1, colIndex, colIndex - 4);
  }
  
  private void overhaulCase(
      final LocalDate lastOverhaulDate, final int rowIndex, final int colIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    
    final long threeMaintenancePeriod = periodsData.get(0);
    final long oneCurrentRepairPeriod = periodsData.get(1);
    final long twoCurrentRepairPeriod = periodsData.get(2);
    final long threeCurrentRepairPeriod = periodsData.get(3);
    final long mediumRepairPeriod = periodsData.get(4);
    final long overhaulPeriod = periodsData.get(5);
    
    final LocalDate nextTreeMaintenanceDate =
        lastOverhaulDate.plusDays(threeMaintenancePeriod);
    final LocalDate nextOneCurrentRepairDate =
        lastOverhaulDate.plusDays(oneCurrentRepairPeriod);
    final LocalDate nextTwoCurrentRepairDate =
        lastOverhaulDate.plusDays(twoCurrentRepairPeriod);
    final LocalDate nextThreeCurrentRepairDate =
        lastOverhaulDate.plusDays(threeCurrentRepairPeriod);
    final LocalDate nextMediumRepairDate =
        lastOverhaulDate.plusDays(mediumRepairPeriod);
    final LocalDate nextOverhaulDate =
        lastOverhaulDate.plusDays(overhaulPeriod);
    
    final String nextTreeMaintenanceString = nextTreeMaintenanceDate.format(formatter);
    final String nextOneCurrentRepairString = nextOneCurrentRepairDate.format(formatter);
    final String nextTwoCurrentRepairString = nextTwoCurrentRepairDate.format(formatter);
    final String nextThreeCurrentRepairString = nextThreeCurrentRepairDate.format(formatter);
    final String nextMediumRepairString = nextMediumRepairDate.format(formatter);
    final String nextOverhaulString = nextOverhaulDate.format(formatter);
    
    dbManager.setRepairRecordCell(rowId, colIndex * 2 - 1, nextOverhaulString);
    if (shouldUpdate((colIndex - 1) * 2 - 1, nextMediumRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextMediumRepairString);
    }
    if (shouldUpdate((colIndex - 2) * 2 - 1, nextThreeCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 2) * 2 - 1, nextThreeCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 3) * 2 - 1, nextTwoCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 3) * 2 - 1, nextTwoCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 4) * 2 - 1, nextOneCurrentRepairDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 4) * 2 - 1, nextOneCurrentRepairString);
    }
    if (shouldUpdate((colIndex - 5) * 2 - 1, nextTreeMaintenanceDate)) {
      dbManager.setRepairRecordCell(rowId, (colIndex - 5) * 2 - 1, nextTreeMaintenanceString);
    }
    
    fireCellUpdated(rowIndex + 1, colIndex, colIndex - 5);
  }
  
  private void fireCellUpdated(
      final int rowIndex, final int firstColIndex, final int lastColIndex) {
    final TableModel tm = guiManager.getRepairRecordsTable().getModel();
    if (tm instanceof AbstractTableModel) {
      final AbstractTableModel recordsModel = (AbstractTableModel) tm;
      for (int j = firstColIndex; j >= lastColIndex; j--) {
        recordsModel.fireTableCellUpdated(rowIndex, j);
      }
    } 
  }
  
  /**
   * Defines whether or not date value should be updated.
   * @param colId for repair type
   * @param newDate to compare with old date
   * @return {@code true} if new date is after old date
   */
  private boolean shouldUpdate(final int colId, final LocalDate newDate) {
    final String oldDateString = recordData.get(colId);
    if (oldDateString == null|| oldDateString.equals("")) {
      return true;
    }
    final LocalDate oldDate = LocalDate.parse(oldDateString, formatter);
    if (newDate.isAfter(oldDate)) {
      return true;
    }
    return false;
  }
  
  private void updateRequiredRepairColumn(final int rowIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    requiredRepairHandler.updateRequiredRepairValues(rowId, LocalDate.now());
    fireCellUpdated(rowIndex, 9, 9);
    fireCellUpdated(rowIndex + 1, 9, 9);
  }
  
  private void informUserIfLastRepairDateIsAfterToday(
      final LocalDate lastRepairDate, final LocalDate today) {
    if (lastRepairDate.isAfter(today)) {
      dialogWindow.showInfoMessage(guiManager.getMainFrame(),
            "Потенциальная опечатка", "Введённая дата ремонта больше сегодняшней");
    }
  }

  @Override
  public String toString() {
    return "DateCalculationsHandler [guiManager=" + guiManager + ", dbManager=" + dbManager
            + ", requiredRepairHandler=" + requiredRepairHandler + "]";
  }
  
}
