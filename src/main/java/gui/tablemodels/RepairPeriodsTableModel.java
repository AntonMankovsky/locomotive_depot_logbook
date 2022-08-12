package gui.tablemodels;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import datavalidation.InputValidator;
import dbapi.DbManager;
import gui.GuiManager;

public class RepairPeriodsTableModel extends AbstractTableModel {
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

  /**
   * Provides methods for core operations with repair records table.
   * <p>
   * Actively interacts with database API and data validation classes. 
   * @param dbManager that provides API for working with database
   */
  public RepairPeriodsTableModel(final DbManager dbManager, final GuiManager guiManager) {
    super();
    this.dbManager = dbManager;
    this.guiManager = guiManager;
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

    final InputValidator validator = new InputValidator(dbManager);
    try {
      validator.validateRepairPeriod(valueInt);
    } catch (final IllegalArgumentException err) {
      JOptionPane.showMessageDialog(
          guiManager.getModelsFrame(),
          "Период ремонта должен быть положительным числом",
          "Недопустимый ввод",
          JOptionPane.ERROR_MESSAGE
          );
      return;
    }

    if (!dbManager.setRepairPeriodCell((String) getValueAt(rowIndex, 0), colIndex - 1, valueInt)) {
      JOptionPane.showMessageDialog(
          guiManager.getModelsFrame(),
          "Не удалось обновить значение",
          "Ошибка при изменении ячейки",
          JOptionPane.ERROR_MESSAGE
          );
    }
  }
  
  @Override
  public String toString() {
    return "Model for repair periods table, performs core operations on data in the table.";
  }

}
