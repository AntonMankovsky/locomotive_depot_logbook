package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import datavalidation.InputValidator;
import gui.GuiManager;
import gui.utility.DialogWindow;

public class NewRecordAction extends AbstractAction {
  private final GuiManager guiManager;
  private final DialogWindow dialogWindow;
  private final InputValidator validator;
  
  public NewRecordAction(final String name, final GuiManager guiManager,
                         final DialogWindow dialogWindow, final InputValidator validator) {
    super();
    putValue(Action.NAME, name);
    this.guiManager = guiManager;
    this.dialogWindow = dialogWindow;
    this.validator = validator;
  }
  
  @Override
  public void actionPerformed(final ActionEvent e) {
    String locoNumber = getUserInput();
    if (locoNumber == null) {
      return;
    }
    locoNumber = locoNumber.trim();
    if (!validateInput(locoNumber)) {
      return;
    }
    
    final List<String> newRow = new ArrayList<>(19);
    newRow.add((String) getValue(Action.NAME));
    newRow.add(locoNumber);
    Stream.generate(() -> "").limit(17).forEach(newRow::add);
    
    final boolean wasInserted = guiManager.getDbManager().insertNewRepairRecord(newRow);
    
    if (wasInserted) {
      final TableModel tm = guiManager.getRepairRecordsTable().getModel();
      if (tm instanceof AbstractTableModel) {
        final AbstractTableModel recordsModel = (AbstractTableModel) tm;
        final int newNumberOfRowsInGuiTable = recordsModel.getRowCount();
        recordsModel.fireTableRowsInserted(
            newNumberOfRowsInGuiTable - 2, newNumberOfRowsInGuiTable - 1 );
        
        guiManager.getRepairRecordsTable()
            .setRowSelectionInterval(newNumberOfRowsInGuiTable - 2, newNumberOfRowsInGuiTable - 2);
      }
    } else {
      dialogWindow.showErrorMessage(guiManager.getMainFrame(), 
          "Ошибка при добавлении записи",
          "Не удалось создать новую запись");
    }
  }
  
  private String getUserInput() {
    return dialogWindow.showInputDialog(
           guiManager.getMainFrame(),
           (String) getValue(Action.NAME),
           "Номер тепловоза"
           );
  }
  
  private boolean validateInput(final String locoNumber) {
    try {
      validator.validateLocoNumber(locoNumber);
      return true;
    } catch (final IllegalArgumentException err) {
      dialogWindow.showErrorMessage(
          guiManager.getMainFrame(), "Операция отменена", "Номер должен состоять только из цифр");
      return false;
    }
  }
  
  @Override
  public String toString() {
    return "NewRecordAction [guiManager=" + guiManager + "; Name=" + getValue(Action.NAME) + "]";
  }

}
