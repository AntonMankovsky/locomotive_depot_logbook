package datecalculations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import dbapi.DbManager;
import gui.GuiManager;

public class DateCalculationsHandler {
  private final GuiManager guiManager;
  private final DbManager dbManager;
  private List<String> recordData;
  private List<Integer> periodsData;
  private final DateTimeFormatter formatter;
  
  public DateCalculationsHandler(final GuiManager guiManager, final DbManager dbManager) {
    super();
    this.guiManager = guiManager;
    this.dbManager = dbManager;
    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  }

  public void handleDateCalculations(
      final String lastRepairString, final int rowIndex, final int colIndex ) {
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
    
    dbManager.setRepairRecordCell(rowId, colIndex * 2 - 1, nextOneCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextTreeMaintenanceString);
    
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
    dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextOneCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 2) * 2 - 1, nextTreeMaintenanceString);
    
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
    dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextTwoCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 2) * 2 - 1, nextOneCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 3) * 2 - 1, nextTreeMaintenanceString);
    
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
    dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextThreeCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 2) * 2 - 1, nextTwoCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 3) * 2 - 1, nextOneCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 4) * 2 - 1, nextTreeMaintenanceString);
    
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
    dbManager.setRepairRecordCell(rowId, (colIndex - 1) * 2 - 1, nextMediumRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 2) * 2 - 1, nextThreeCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 3) * 2 - 1, nextTwoCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 4) * 2 - 1, nextOneCurrentRepairString);
    dbManager.setRepairRecordCell(rowId, (colIndex - 5) * 2 - 1, nextTreeMaintenanceString);
    
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

}