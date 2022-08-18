package gui.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JMenuItem;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import datavalidation.InputValidator;
import gui.GuiManager;
import gui.utility.DialogWindow;

public class NewModelListener implements ActionListener {
  private final GuiManager guiManager;
  private final DialogWindow dialogWindow;
  private final InputValidator validator;
  
  public NewModelListener(final GuiManager guiManager,
                          final DialogWindow dialogWindow, final InputValidator validator) {
    super();
    this.guiManager = guiManager;
    this.dialogWindow = dialogWindow;
    this.validator = validator;
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
    Stream.generate(() -> 1).limit(6).forEach(periods::add);

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
      dialogWindow.showErrorMessage(guiManager.getModelsFrame(), 
          "Ошибка при добавлении записи",
          "Не удалось создать новую запись");
    }
    
  }
  
  private String getUserInput() {
    return dialogWindow.showInputDialog(guiManager.getModelsFrame(),
           "Новая модель", "Название модели");
  }
  
  private boolean validateInput(final String modelName) {
    try {
      validator.validateRepairPeriodsModelName(modelName);
      return true;
    } catch (final IllegalArgumentException err) {
      dialogWindow.showErrorMessage(guiManager.getModelsFrame(), 
          "Операция отменена",
          "Недопустимое название модели");
      return false;
    }
  }
  
  @Override
  public String toString() {
    return "NewModelListener [guiManager=" + guiManager + "]";
  }

}
