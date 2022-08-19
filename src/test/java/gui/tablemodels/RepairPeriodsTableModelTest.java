package gui.tablemodels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import datavalidation.InputValidator;
import dbapi.DbManager;
import gui.GuiManager;
import gui.ModelsFrame;
import gui.utility.DialogWindow;

public class RepairPeriodsTableModelTest {
  private static final String[] MODEL_NAMES = {"ТЭМ"};
  private static final String[] COLUMN_NAMES = {
                                                  "Модель",
                                                  "ТО-3",
                                                  "ТР-1",
                                                  "ТР-2",
                                                  "ТР-3",
                                                  "СР",
                                                  "КР",
                                                  };
  private static RepairPeriodsTableModel repairPeriodsTableModel;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static DialogWindow dialogWindowMock;
  private static InputValidator validatorMock;
  private static ModelsFrame modelsFrameMock;
  private static Map<String, List<Integer>> repairPeriodsTestData;
  
  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    repairPeriodsTestData = new HashMap<>(1);
    repairPeriodsTestData.put(MODEL_NAMES[0], Stream.of(
                                             30, 225, 450, 900, 2160, 4320)
                                            .collect(Collectors.toCollection(ArrayList::new)));
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
    guiManagerMock = mock(GuiManager.class);
    dbManagerMock = mock(DbManager.class);
    dialogWindowMock = mock(DialogWindow.class);
    validatorMock = mock(InputValidator.class);
    modelsFrameMock = mock(ModelsFrame.class);
    
    when(guiManagerMock.getModelsFrame()).thenReturn(modelsFrameMock);
    when(dbManagerMock.getAllRepairPeriodData()).thenReturn(repairPeriodsTestData);
    when(dbManagerMock.getAllModelNames()).thenReturn(MODEL_NAMES);
    
    repairPeriodsTableModel = new RepairPeriodsTableModel(
        dbManagerMock, guiManagerMock, dialogWindowMock, validatorMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check column names")
  void checkColumnNames(final int col) {
    assertEquals(repairPeriodsTableModel.getColumnName(col), COLUMN_NAMES[col]);
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check column classes")
  void checkColumnClasses(final int col) {
    if (col != 0) {
      assertEquals(Integer.class, repairPeriodsTableModel.getColumnClass(col));
    } else {
      assertEquals(String.class, repairPeriodsTableModel.getColumnClass(col));
    }
  }
  
  @Test
  @DisplayName("Check row count")
  void checkRowCount() {
    assertEquals(repairPeriodsTestData.size(), repairPeriodsTableModel.getRowCount());
  }
  
  @Test
  @DisplayName("Check column count")
  void checkColumCount() {
    assertEquals(COLUMN_NAMES.length, repairPeriodsTableModel.getColumnCount());
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check can edit anything but model name")
  void checkCanEditAnythingButModelName(final int col) {
    if (col == 0) {
      assertFalse(repairPeriodsTableModel.isCellEditable(0, col));
    } else {
      assertTrue(repairPeriodsTableModel.isCellEditable(0, col));
    }
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("getValueAt returns correct value")
  void getValueAtReturnsCorrectValue(final int col) {
    if (col == 0) {
      assertEquals(MODEL_NAMES[0], repairPeriodsTableModel.getValueAt(0, col));
    } else {
      assertEquals(repairPeriodsTestData.get(MODEL_NAMES[0]).get(col - 1),
                   repairPeriodsTableModel.getValueAt(0, col));
    }
  }
  
  @ParameterizedTest
  @MethodSource("providePeriodsColumnsIndices")
  @DisplayName("Check setValueAt no interactions on same value")
  void checkSetValueAtNoInteractionsOnSameValue(final int col) {
    repairPeriodsTableModel.setValueAt(
        repairPeriodsTestData.get(MODEL_NAMES[0]).get(col - 1), 0, col);
    
    verifyNoInteractions(validatorMock);
    verifyNoInteractions(dialogWindowMock);
    verify(dbManagerMock, never()).setRepairPeriodCell(anyString(), anyInt(), anyInt());
  }
  
  @ParameterizedTest
  @MethodSource("providePeriodsColumnsIndices")
  @DisplayName("Check setValueAt notifies user and not write to DB on invalid value")
  void checkSetValueAtNotifiesUserAndNotWriteToDbOnInvalidValue(final int col) {
    doThrow(new IllegalArgumentException()).when(validatorMock).validateRepairPeriod(-1);
    repairPeriodsTableModel.setValueAt(-1, 0, col);
    
    verify(validatorMock).validateRepairPeriod(-1);
    verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
    verify(dbManagerMock, never()).setRepairPeriodCell(anyString(), anyInt(), anyInt());
  }
  
  
  @ParameterizedTest
  @MethodSource("providePeriodsColumnsIndices")
  @DisplayName("Check setValueAt writes valid value to DB")
  void checkSetValueAtWritesValidValueToDb(final int col) {
    when(dbManagerMock.setRepairPeriodCell(anyString(), anyInt(), anyInt())).thenReturn(true);
    repairPeriodsTableModel.setValueAt(2, 0, col);
    
    verifyNoInteractions(dialogWindowMock);
    verify(dbManagerMock).setRepairPeriodCell(MODEL_NAMES[0], col - 1, 2);
  }
  
  @ParameterizedTest
  @MethodSource("providePeriodsColumnsIndices")
  @DisplayName("Check setValueAt notifies user if value not written to DB")
  void checkSetValueAtNotifiesUserIfValueNotWrittenToDb(final int col) {
    repairPeriodsTableModel.setValueAt(2, 0, col);
    
    verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = repairPeriodsTableModel.toString().toLowerCase();
    assertTrue(description.contains("guimanager="));
  }
  
  private static Stream<Integer> provideColumnsIndices() {
    return Stream.iterate(0, i -> ++i).limit(7);
  }
  
  private static Stream<Integer> providePeriodsColumnsIndices() {
    return Stream.iterate(1, i -> ++i).limit(6);
  }

}
