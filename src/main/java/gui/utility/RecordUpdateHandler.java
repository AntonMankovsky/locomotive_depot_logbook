package gui.utility;

import java.time.LocalDate;
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
  
  /**
   * Object that handles user input in repair records table,
   * depending on the column whose cell has been changed. 
   * @param dbManager to obtain and write data into the database
   * @param guiManager to access main frame that should be used to show dialog windows
   * @param dateCalculationsHandler to handle automatic date calculations
   * @param lastRepairHandler to update "last repair" column
   * @param validator to validate cell value
   * @param dialogWindow to communicate with user
   */
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
  
  /**
   * Performs core operations with new cell value.
   * <p>
   * Depending on the column, validates the value with an appropriate method.
   * <br>
   * On validation success, writes a new value to the database or calls the necessary handlers;
   * on validation failure, informs the user about it through dialog window.
   * @param value to process
   * @param rowIndex where value was inserted
   * @param colIndex where value was inserted
   */
  public void handleCellNewValue(final String value, final int rowIndex, final int colIndex) {
    if (colIndex > 1 && colIndex < 8) {
      editRepairDateCase(value, rowIndex, colIndex);
    } else if (colIndex == 1) {
      editNumberCase(value, rowIndex);
    } else if (colIndex == 10) {
      editNotesCase(value, rowIndex);
    } 
    
  }
  
  private void editRepairDateCase(String value, final int rowIndex, final int colIndex) {
    value = convertDateFormat(value);
    try {
      validator.validateRepairDate(value);
    } catch (final IllegalArgumentException err) {
      dialogWindow.showErrorMessage(guiManager.getMainFrame(), "Некорректный ввод",
          "Запись должна быть в формате день.месяц.год и указывать на существующую дату");
      return;
    }
    
    final int rowId = dbManager.getIdByOrdinalNumber(rowIndex / 2);
    if (!dbManager.setRepairRecordCell(rowId, colIndex * 2 - 2, value)) {
      notifyUserOnOperationFailure();
    } else {
      lastRepairHandler.updateLastRepairColumn(rowIndex);
      if (value != null && !value.equals("")){
      dateCalculationsHandler.handleDateCalculations(value, rowIndex, colIndex, LocalDate.now());
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
  
  /**
   * Converts day and month values from one to two digits, and year from two to four digits format.
   * <p>
   * Does not perform any other string modifications.
   * <p>
   * <blockquote>
   * <table class="plain">
   * <caption style="display:none">Examples</caption>
   * <thead>
   * <tr>
   * <th scope="col">Takes</th>
   * <th scope="col">Returns</th>
   * </tr>
   * </thead>
   * <tbody>
   * <tr>
   * <th scope="row" style="text-weight:normal">1.2.18</th>
   * <td>{@code 01.02.2018}</td>
   * </tr>
   * <tr>
   * <th scope="row" style="text-weight:normal">09.5.2020</th>
   * <td>{@code 09.05.2020}</td>
   * </tr>
   * <tr>
   * <th scope="row" style="text-weight:normal">foo.10.22</th>
   * <td>{@code foo.10.2022}</td>
   * </tr>
   * </tbody>
   * </table>
   * </blockquote>
   * Does nothing and returns exactly the same value if given string cannot be
   * split on three strings with dot separator.
   * @param value string to convert from short to long date format if possible
   * @return the same value as was given, except for short-to-long format conversions
   * (returns {@code null} for {@code null} parameter)
   */
  private String convertDateFormat(String value) {
    if (value == null) {
      return null;
    }
    
    final String[] stringParts = value.split("\\.");
    if (stringParts.length == 3) {
      
      String day = stringParts[0];
      if (day.matches("[1-9]{1}")) {
        day = "0" + day;
      }
      
      String month = stringParts[1];
      if (month.matches("[1-9]{1}")) {
        month = "0" + month;
      }
      
      String year = stringParts[2];
      if (year.matches("[0-9]{2}")) {
        year = "20" + year;
      }
      
      value = day + "." + month + "." + year;
    }
    
    return value;
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
