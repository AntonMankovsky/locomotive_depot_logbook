package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import datavalidation.InputValidator;
import dbapi.DbManager;
import gui.GuiManager;

public class NewModelListener implements ActionListener {
  private final GuiManager guiManager;
  
  public NewModelListener(final GuiManager guiManager) {
    super();
    this.guiManager = guiManager;
  }

  @Override
  public void actionPerformed(final ActionEvent event) {
    String modelName = getUserInput();
    if (modelName == null) {
      return;
    }
    modelName = modelName.trim();
    if (!validateInput(modelName)) {
      return;
    }
    
    final List<Integer> periods = new ArrayList<>(6);
    for (int j = 0; j < 6; j++) {
      periods.add(1);
    }
    final boolean wasInserted =
        guiManager.getDbManager().insertNewModelRepairPeriods(modelName, periods);
    
    if (wasInserted) {
      guiManager
          .getNewRecordSubmenu().add(new JMenuItem(new NewRecordAction(modelName, guiManager)));
      
      final TableModel tm = guiManager.getModelsFrame().getRepairPeriodsTable().getModel();
      if (tm instanceof AbstractTableModel) {
        final AbstractTableModel periodsModel = (AbstractTableModel) tm;
        final int numberOfRows = guiManager.getDbManager().getAllRepairPeriodData().size();
        periodsModel.fireTableRowsInserted(numberOfRows - 1, numberOfRows - 1);
        guiManager.getModelsFrame().getRepairPeriodsTable()
            .setRowSelectionInterval(numberOfRows - 1, numberOfRows - 1);
      }
    } else {
      JOptionPane.showMessageDialog(
          guiManager.getModelsFrame(),
          "Не удалось создать новую запись",
          "Ошибка при добавлении записи",
          JOptionPane.ERROR_MESSAGE
          );
    }
    
  }
  
  private String getUserInput() {
    return (String) JOptionPane.showInputDialog(
        guiManager.getModelsFrame(),
        "Название модели",
        "Новая модель",
        JOptionPane.PLAIN_MESSAGE
        );
  }
  
  private boolean validateInput(final String modelName) {
    final InputValidator validator = new InputValidator(guiManager.getDbManager());
    try {
      validator.validateRepairPeriodsModelName(modelName);
      return true;
    } catch (final IllegalArgumentException err) {
      JOptionPane.showMessageDialog(
          guiManager.getModelsFrame(),
          "Некорректное название модели",
          "Операция отменена",
          JOptionPane.ERROR_MESSAGE
          );
      return false;
    }
  }

}
