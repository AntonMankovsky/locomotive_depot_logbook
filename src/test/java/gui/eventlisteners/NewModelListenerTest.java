package gui.eventlisteners;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import datavalidation.InputValidator;
import dbapi.DbManager;
import gui.GuiManager;
import gui.ModelsFrame;
import gui.utility.DialogWindow;

public class NewModelListenerTest {
  private static NewModelListener newModelListener;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static DialogWindow dialogWindowMock;
  private static InputValidator validatorMock;
  private static JMenu newRecordSubmenuMock;
  private static ModelsFrame modelsFrameMock;
  private static JTable repairPeriodsTableMock;
  private static AbstractTableModel abstractTableModelMock;
  private static Map<String, List<Integer>> repairPeriodsDataMock;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @SuppressWarnings("unchecked")
  @BeforeEach
  protected void setUp() throws Exception {
    guiManagerMock = mock(GuiManager.class);
    dbManagerMock = mock(DbManager.class);
    dialogWindowMock = mock(DialogWindow.class);
    validatorMock = mock(InputValidator.class);
    modelsFrameMock = mock(ModelsFrame.class);
    repairPeriodsTableMock = mock(JTable.class);
    abstractTableModelMock = mock(AbstractTableModel.class);
    newRecordSubmenuMock = mock(JMenu.class);
    repairPeriodsDataMock = mock(HashMap.class);
    
    when(guiManagerMock.getDbManager()).thenReturn(dbManagerMock);
    when(guiManagerMock.getModelsFrame()).thenReturn(modelsFrameMock);
    when(guiManagerMock.getNewRecordSubmenu()).thenReturn(newRecordSubmenuMock);
    when(modelsFrameMock.getRepairPeriodsTable()).thenReturn(repairPeriodsTableMock);
    when(repairPeriodsTableMock.getModel()).thenReturn(abstractTableModelMock);
    when(dbManagerMock.getAllRepairPeriodData()).thenReturn(repairPeriodsDataMock);
    when(repairPeriodsDataMock.size()).thenReturn(1);
    
    newModelListener = new NewModelListener(guiManagerMock, dialogWindowMock, validatorMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @Test
  @DisplayName("Check no interactions if user input is null")
  void checkNoInteractionsIfUserInputIsNull() {
    when(dialogWindowMock.showInputDialog(any(), anyString(), anyString())).thenReturn(null);
    newModelListener.actionPerformed(null);
    
    verifyNoInteractions(dbManagerMock);
    verifyNoInteractions(validatorMock);
    verifyNoInteractions(repairPeriodsTableMock);
    verifyNoInteractions(abstractTableModelMock);
    verifyNoInteractions(newRecordSubmenuMock);
  }
  
  @Nested
  @DisplayName("Input not null")
  class InputNotNull {
    @BeforeEach
    void setUp() {
      when(dialogWindowMock.showInputDialog(any(), anyString(), anyString()))
          .thenReturn("test name");
    }
    
    @Test
    @DisplayName("Check notification and no interactions on invalid input")
    void checkNotificationAndNoInteractionsOnInvalidInput() {
      doThrow(new IllegalArgumentException())
              .when(validatorMock).validateRepairPeriodsModelName("test name");
      newModelListener.actionPerformed(null);
      
      verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
      verifyNoInteractions(dbManagerMock);
      verifyNoInteractions(repairPeriodsTableMock);
      verifyNoInteractions(abstractTableModelMock);
      verifyNoInteractions(newRecordSubmenuMock);
    }
    
    @Test
    @DisplayName("Check DB request")
    void checkDbRequest() {
      final List<Integer> periods = new ArrayList<>(6);
      Stream.generate(() -> 1).limit(6).forEach(periods::add);
      newModelListener.actionPerformed(null);
      
      verify(dbManagerMock).insertNewModelRepairPeriods("test name", periods);
    }
    
    @Test
    @DisplayName("Check not inserted notification and no interactions")
    void checkNotInsertedNotificationAndNoInteractions() {
      newModelListener.actionPerformed(null);
      
      verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
      verifyNoInteractions(repairPeriodsTableMock);
      verifyNoInteractions(abstractTableModelMock);
      verifyNoInteractions(newRecordSubmenuMock);
    }
    
    @Test
    @DisplayName("Check fire table row inserted and row selection")
    void checkFireTableRowInsertedAndRowSelection() {
      when(dbManagerMock.insertNewModelRepairPeriods(anyString(), anyList())).thenReturn(true);
      newModelListener.actionPerformed(null);
      
      verify(abstractTableModelMock).fireTableRowsInserted(0, 0);
      verify(repairPeriodsTableMock).setRowSelectionInterval(0, 0);
    }
    
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = newModelListener.toString().toLowerCase();
    assertTrue(description.contains("guimanager="));
  }
  
}
