package gui.tablemodels;

import javax.swing.table.AbstractTableModel;
import datavalidation.InputValidator;
import dbapi.DbManager;
import gui.GuiManager;
import gui.utility.DialogWindow;

/**
 * Custom table model for repair periods table.
 */
public class RepairPeriodsTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;
  private static final String[] COLUMN_NAMES = {
                                                  "Модель",
                                                  "ТО-3",
                                                  "ТР-1",
                                                  "ТР-2",
                                                  "ТР-3",
                                                  "СР",
                                                  "КР",
                                                  };
  private final DbManager dbManager;
  private final GuiManager guiManager;
  private final DialogWindow dialogWindow;
  private final InputValidator validator;

  /**
   * Provides methods for core operations with repair records table.
   * @param dbManager that provides API for working with database
   * @param guiManager to provide access to ModelsFrame
   * @param dialogWindow to show error notifications to the user
   * @param validator to validate repair period from user`s input
   */
  public RepairPeriodsTableModel(final DbManager dbManager, final GuiManager guiManager,
                                 final DialogWindow dialogWindow, final InputValidator validator) {
    super();
    this.dbManager = dbManager;
    this.guiManager = guiManager;
    this.dialogWindow = dialogWindow;
    this.validator = validator;
  }
  
  @Override
  public String getColumnName(final int columnIndex) {
    return COLUMN_NAMES[columnIndex];
  }
  
  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    if (columnIndex != 0) {
      return Integer.class;
    }
    return String.class;
  }

  @Override
  public int getRowCount() {
    return dbManager.getAllRepairPeriodData().size();
  }

  @Override
  public int getColumnCount() {
    return 7;
  }
  
  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {
    if (columnIndex == 0) {
      return false;
    }
    return true;
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    final String[] models = dbManager.getAllModelNames();
    if (columnIndex == 0) {
      return models[rowIndex];
    } else {
      return dbManager.getAllRepairPeriodData().get(models[rowIndex]).get(columnIndex - 1);
    }
  }
  
  @Override
  public void setValueAt(final Object value, final int rowIndex, final int colIndex) {
    final int valueInt = (int) value;
    if (valueInt == (int) getValueAt(rowIndex, colIndex)) {
      return;
    }

    try {
      validator.validateRepairPeriod(valueInt);
    } catch (final IllegalArgumentException err) {
      dialogWindow.showErrorMessage(guiManager.getModelsFrame(), 
                                   "Недопустимый ввод",
                                   "Период ремонта должен быть умеренным положительным числом");
      return;
    }

    if (!dbManager.setRepairPeriodCell((String) getValueAt(rowIndex, 0), colIndex - 1, valueInt)) {
      dialogWindow.showErrorMessage(guiManager.getModelsFrame(), 
                                    "Ошибка при изменении ячейки",
                                    "Не удалось обновить значение");
    }
  }

  @Override
  public String toString() {
    return "RepairPeriodsTableModel [dbManager=" + dbManager + ", guiManager=" + guiManager + "]"
         + "Model for repair periods table, performs core operations on data in the table.";
  }

}
