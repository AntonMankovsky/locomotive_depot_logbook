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
import java.util.List;
import java.util.stream.Stream;

import javax.swing.Action;
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
import gui.utility.DialogWindow;

public class NewRecordActionTest {
  private static NewRecordAction newRecordAction;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static DialogWindow dialogWindowMock;
  private static InputValidator validatorMock;
  private static JTable repairRecordsTableMock;
  private static AbstractTableModel abstractTableModelMock;
  
  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
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
    repairRecordsTableMock = mock(JTable.class);
    abstractTableModelMock = mock(AbstractTableModel.class);
    
    when(guiManagerMock.getDbManager()).thenReturn(dbManagerMock);
    when(guiManagerMock.getRepairRecordsTable()).thenReturn(repairRecordsTableMock);
    when(repairRecordsTableMock.getModel()).thenReturn(abstractTableModelMock);
    when(abstractTableModelMock.getRowCount()).thenReturn(2);
    
    newRecordAction = new NewRecordAction("test", guiManagerMock, dialogWindowMock, validatorMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @Test
  @DisplayName("Check no interactions if user input is null")
  void checkNoInteractionsIfUserInputIsNull() {
    when(dialogWindowMock.showInputDialog(any(), anyString(), anyString())).thenReturn(null);
    newRecordAction.actionPerformed(null);
    
    verifyNoInteractions(dbManagerMock);
    verifyNoInteractions(validatorMock);
    verifyNoInteractions(repairRecordsTableMock);
    verifyNoInteractions(abstractTableModelMock);
  }
  
  @Nested
  @DisplayName("Input not null")
  class InputNotNull {
    @BeforeEach
    void setUp() {
      when(dialogWindowMock.showInputDialog(any(), anyString(), anyString())).thenReturn("123");
    }
    
    @Test
    @DisplayName("Check notification and no interactions on invalid input")
    void checkNotificationAndNoInteractionsOnInvalidInput() {
      doThrow(new IllegalArgumentException())
              .when(validatorMock).validateLocoNumber("123");
      newRecordAction.actionPerformed(null);
      
      verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
      verifyNoInteractions(dbManagerMock);
      verifyNoInteractions(repairRecordsTableMock);
      verifyNoInteractions(abstractTableModelMock);
    }
    
    @Test
    @DisplayName("Check DB request")
    void checkDbRequest() {
      final List<String> expectedRow = new ArrayList<>(19);
      expectedRow.add("test");
      expectedRow.add("123");
      Stream.generate(() -> "").limit(17).forEach(expectedRow::add);
      newRecordAction.actionPerformed(null);
      
      verify(dbManagerMock).insertNewRepairRecord(expectedRow);
    }
    
    @Test
    @DisplayName("Check not inserted notification and no interactions")
    void checkNotInsertedNotificationAndNoInteractions() {
      newRecordAction.actionPerformed(null);
      
      verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
      verifyNoInteractions(repairRecordsTableMock);
      verifyNoInteractions(abstractTableModelMock);
    }
    
    @Test
    @DisplayName("Check fire table row inserted and row selection")
    void checkFireTableRowInsertedAndRowSelection() {
      when(dbManagerMock.insertNewRepairRecord(anyList())).thenReturn(true);
      newRecordAction.actionPerformed(null);
      
      verify(abstractTableModelMock).fireTableRowsInserted(0, 1);
      verify(repairRecordsTableMock).setRowSelectionInterval(0, 0);
    }
    
    @Test
    @DisplayName("Check toString is overriden")
    void checkToStringIsOverriden() {
      final String description = newRecordAction.toString().toLowerCase();
      assertTrue(description.contains("guimanager="));
    }
  }

}
