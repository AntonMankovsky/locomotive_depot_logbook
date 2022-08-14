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
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    final LocalDate[] lastRepairsDates = getLastRepairsDates(rowId);
    LocalDate lastRepairDate = null;
    int index = -1;
    
    if (lastRepairsDates != null) {
    lastRepairDate = LocalDate.ofEpochDay(0);
    for (int j = 0; j < 6; j++) {
      if (lastRepairsDates[j] != null && lastRepairDate.isBefore(lastRepairsDates[j])) {
        lastRepairDate = lastRepairsDates[j];
        index = j;
      }
    }
    }
    
    final String currentLastRepair = dbManager.getAllRepairRecords().get(rowId).get(15);
    final String lastRepairDateString =
          lastRepairDate != null ? lastRepairDate.format(formatter) : "";
    if (currentLastRepair.equals(lastRepairDateString)) {
      return;
    }
    
    dbManager.setRepairRecordCell(rowId, 14, index != -1 ? REPAIR_NAMES[index] : "");
    dbManager.setRepairRecordCell(rowId, 15, lastRepairDateString);
    
    final AbstractTableModel recordsModel =
        (AbstractTableModel) guiManager.getRepairRecordsTable().getModel();
    recordsModel.fireTableCellUpdated(rowIndex, 8);
    recordsModel.fireTableCellUpdated(rowIndex + 1, 8);
    
  }
  
  /**
   * Returns array of last repair dates if there is at least one, otherwise returns {@code null}.
   * @param rowId of repair record in database table
   * @return array of last repair dates or {@code null} if record has no last repair dates yet
   */
  private LocalDate[] getLastRepairsDates(final int rowId) {
    recordData = dbManager.getAllRepairRecords().get(rowId);
    final LocalDate[] lastRepairsDates = new LocalDate[6];
    boolean atLeastOneDate = false;
    String tempString;
    // j for indices of last_repair values, k for natural order elements in array
    for (int j = 2, k = 0; j <= 12; j+=2, k++) {
      tempString = recordData.get(j);
      if (tempString == null || tempString.equals("")) {
        continue;
      }
      lastRepairsDates[k] = LocalDate.parse(tempString, formatter);
      atLeastOneDate = true;
    }
    return atLeastOneDate ? lastRepairsDates : null;
  }
}
