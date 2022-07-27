package gui.utility;

import javax.swing.JOptionPane;

import datavalidation.InputValidator;
import datecalculations.DateCalculationsHandler;
import dbapi.DbManager;
import gui.GuiManager;

/**
 * Provides appropriate reaction to changes in repair records table model. 
 */
public class RecordUpdateHandler {
  private final DbManager dbManager;
  private final GuiManager guiManager;
  private final DateCalculationsHandler dateCalculationsHandler;
  
  public RecordUpdateHandler(final DbManager dbManager, final GuiManager guiManager) {
    super();
    this.dbManager = dbManager;
    this.guiManager = guiManager;
    dateCalculationsHandler = new DateCalculationsHandler(guiManager, dbManager);
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
    final InputValidator validator = new InputValidator(dbManager);
    try {
      validator.validateRepairDate(value);
    } catch (final IllegalArgumentException err) {
      JOptionPane.showMessageDialog(
          guiManager.getMainFrame(),
          "Запись должна соответствовать формату дд.ММ.гггг и указывать на существующую дату",
          "Некорректный ввод",
          JOptionPane.ERROR_MESSAGE
          );
      return;
    }
    
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    if (!dbManager.setRepairRecordCell(rowId, colIndex * 2 - 2, value)) {
      notifyUserOnOperationFailure();
    } else if (value != null && !value.equals("")){
      dateCalculationsHandler.handleDateCalculations(value, rowIndex, colIndex);
      }
    }
    
  private void editNumberCase(final String value, final int rowIndex) {
    final InputValidator validator = new InputValidator(dbManager);
    try {
      validator.validateLocoNumber(value);
    } catch (final IllegalArgumentException err) {
      JOptionPane.showMessageDialog(
          guiManager.getMainFrame(),
          "Номер должен состоять из цифр",
          "Операция отменена",
          JOptionPane.ERROR_MESSAGE
          );
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
    JOptionPane.showMessageDialog(
        guiManager.getMainFrame(),
        "Не удалось обновить значение",
        "Ошибка при изменении ячейки",
        JOptionPane.ERROR_MESSAGE
        );
  }
}
