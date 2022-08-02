package datecalculations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dbapi.DbManager;
import gui.GuiManager;

/**
 * Controls correctness of last_repair column.
 */
public class LastRepairHandler {
  private final GuiManager guiManager;
  private final DbManager dbManager;
  private List<String> recordData;
  private final DateTimeFormatter formatter;
  private static final String[] REPAIR_NAMES = {"ТО-3", "ТР-1", "ТР-2", "ТР-3", "СР", "КР"};
  
  public LastRepairHandler(final GuiManager guiManager, final DbManager dbManager) {
    super();
    this.guiManager = guiManager;
    this.dbManager = dbManager;
    formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  }
  
  /**
   * Guarantees that last repair column will always have the newest last repair date. 
   * @param rowIndex where new last repair date was inserted
   */
  public void updateLastRepairColumn(final int rowIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex);
    final LocalDate[] lastRepairsDates = getLastRepairsDates(rowId);
    LocalDate lastRepairDate = lastRepairsDates[0];
    int index = 0;
    for (int j = 0; j < 6; j++) {
      if (lastRepairDate.isBefore(lastRepairsDates[j])) {
        lastRepairDate = lastRepairsDates[j];
        index = j;
      }
    }
    
    final String currentLastRepair = dbManager.getAllRepairRecords().get(rowId).get(15);
    final String lastRepairDateString = lastRepairDate.format(formatter);
    if (currentLastRepair.equals(lastRepairDateString)) {
      return;
    }
    
    dbManager.setRepairRecordCell(rowId, 15, lastRepairDateString);
    dbManager.setRepairRecordCell(rowId, 16, REPAIR_NAMES[index]);
    
    final AbstractTableModel recordsModel =
        (AbstractTableModel) guiManager.getRepairRecordsTable().getModel();
    recordsModel.fireTableCellUpdated(rowIndex, 8);
    recordsModel.fireTableCellUpdated(rowIndex + 1, 8);
    
  }
  
  /*
   * int j for indices of last_repair values, int k for simple ordered elements in array
   */
  private LocalDate[] getLastRepairsDates(final int rowId) {
    recordData = dbManager.getAllRepairRecords().get(rowId);
    final LocalDate[] lastRepairsDates = new LocalDate[6];
    for (int j = 2, k = 0; j <= 12; j+=2, k++) {
      lastRepairsDates[k] = LocalDate.parse(recordData.get(j), formatter);
    }
    return lastRepairsDates;
  }
}
