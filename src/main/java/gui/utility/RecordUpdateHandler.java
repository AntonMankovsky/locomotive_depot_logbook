package gui.utility;

import datavalidation.InputValidator;
import datecalculations.DateCalculationsHandler;
import datecalculations.LastRepairHandler;
import dbapi.DbManager;
import gui.GuiManager;

/**
 * Provides appropriate reaction to changes in repair records table model. 
 */
public class RecordUpdateHandler {
  private final DbManager dbManager;
  private final GuiManager guiManager;
  private final DateCalculationsHandler dateCalculationsHandler;
  private final LastRepairHandler lastRepairHandler;
  private final InputValidator validator;
  private final DialogWindow dialogWindow;
  
  public RecordUpdateHandler(final DbManager dbManager, final GuiManager guiManager,
                             final DateCalculationsHandler dateCalculationsHandler,
                             final LastRepairHandler lastRepairHandler,
                             final InputValidator validator, final DialogWindow dialogWindow) {
    super();
    this.dbManager = dbManager;
    this.guiManager = guiManager;
    this.dateCalculationsHandler = dateCalculationsHandler;
    this.lastRepairHandler = lastRepairHandler;
    this.validator = validator;
    this.dialogWindow = dialogWindow;
  }
  
  public void handleCellNewValue(final String value, final int rowIndex, final int colIndex) {
    if (colIndex > 1 && colIndex < 8) {
      editRepairDateCase(value, rowIndex, colIndex);
    } else if (colIndex == 1) {
      editNumberCase(value, rowIndex);
    } else if (colIndex == 10) {
      editNotesCase(value, rowIndex);
    } 
    
  }
  
  private void editRepairDateCase(final String value, final int rowIndex, final int colIndex) {
    try {
      validator.validateRepairDate(value);
    } catch (final IllegalArgumentException err) {
      dialogWindow.showErrorMessage(guiManager.getMainFrame(), "Некорректный ввод",
          "Запись должна быть в формате дд.ММ.гггг и указывать на существующую дату");
      return;
    }
    
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    if (!dbManager.setRepairRecordCell(rowId, colIndex * 2 - 2, value)) {
      notifyUserOnOperationFailure();
    } else {
      lastRepairHandler.updateLastRepairColumn(rowIndex);
      if (value != null && !value.equals("")){
      dateCalculationsHandler.handleDateCalculations(value, rowIndex, colIndex);
      }
    }
  }

  private void editNumberCase(final String value, final int rowIndex) {
    try {
      validator.validateLocoNumber(value);
    } catch (final IllegalArgumentException err) {
      dialogWindow.showErrorMessage(
          guiManager.getMainFrame(), "Операция отменена", "Номер должен состоять только из цифр");
      return;
    }
    
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    if (!dbManager.setRepairRecordCell(rowId, 1, value)) {
      notifyUserOnOperationFailure();
    }
  }
  
  private void editNotesCase(final String value, final int rowIndex) {
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    if (!dbManager.setRepairRecordCell(rowId, 18, value)) {
      notifyUserOnOperationFailure();
    }
  }
  
  private void notifyUserOnOperationFailure() {
    dialogWindow.showErrorMessage(
        guiManager.getMainFrame(), "Ошибка при изменении ячейки", "Не удалось обновить значение");
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RecordUpdateHandler - coordinator of changes in repair records table."
        + "[dbManager=");
    builder.append(dbManager);
    builder.append(", guiManager=");
    builder.append(guiManager);
    builder.append(", dateCalculationsHandler=");
    builder.append(dateCalculationsHandler);
    builder.append(", lastRepairHandler=");
    builder.append(lastRepairHandler);
    builder.append(", validator=");
    builder.append(validator);
    builder.append(", dialogWindow=");
    builder.append(dialogWindow);
    builder.append("]");
    return builder.toString();
  }
}
