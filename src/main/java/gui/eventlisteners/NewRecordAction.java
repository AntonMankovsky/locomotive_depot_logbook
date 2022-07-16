package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import datavalidation.InputValidator;
import gui.GuiManager;
import gui.RepairRecordsTableModel;

public class NewRecordAction extends AbstractAction {
  private final GuiManager guiManager;
  
  public NewRecordAction(final String name, final GuiManager guiManager) {
    super();
    putValue(Action.NAME, name);
    this.guiManager = guiManager;
    
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
    
    final List<String> newRow = new ArrayList<>(15);
    newRow.add((String) getValue(Action.NAME));
    newRow.add(locoNumber);
    final boolean wasInserted = guiManager.getDbManager().insertNewRepairRecord(newRow);
    
    if (wasInserted) {
      if (guiManager.getRepairRecordsTable().getModel() instanceof AbstractTableModel) {
        final AbstractTableModel recordsModel =
        (AbstractTableModel) guiManager.getRepairRecordsTable().getModel();
        final int newNumberOfRowsInGuiTable = guiManager.getDbManager().getRecordsCount() * 2;
        System.out.println(
            "First inserted row: " + (newNumberOfRowsInGuiTable - 2));
        System.out.println(
            "Last inserted row: " + (newNumberOfRowsInGuiTable - 1));
        recordsModel.fireTableRowsInserted(
            newNumberOfRowsInGuiTable - 2, newNumberOfRowsInGuiTable - 1 );
      }
    } else {
      JOptionPane.showMessageDialog(
          guiManager.getMainFrame(),
          "Не удалось создать новую запись",
          "Ошибка при добавлении записи",
          JOptionPane.ERROR_MESSAGE
          );
    }
  }
  
  private String getUserInput() {
    return (String) JOptionPane.showInputDialog(
        guiManager.getMainFrame(),
        "Номер тепловоза",
        (String) getValue(Action.NAME),
        JOptionPane.PLAIN_MESSAGE
        );
  }
  
  private boolean validateInput(final String locoNumber) {
    final InputValidator validator = new InputValidator(guiManager.getDbManager());
    try {
      validator.validateLocoNumber(locoNumber);
      return true;
    } catch (final IllegalArgumentException err) {
      JOptionPane.showMessageDialog(
          guiManager.getMainFrame(),
          "Номер должен состоять из цифр",
          "Операция отменена",
          JOptionPane.ERROR_MESSAGE
          );
      return false;
    }
  }
  

}
