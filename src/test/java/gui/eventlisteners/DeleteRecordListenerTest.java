package gui.eventlisteners;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import dbapi.DbManager;
import gui.ArchiveFrame;
import gui.GuiManager;
import gui.utility.DialogWindow;

public class DeleteRecordListenerTest {
  private static DeleteRecordListener deleteRecordListener;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static DialogWindow dialogWindowMock;
  private static JTable repairRecordsTableMock;
  private static JTable repairRecordsArchiveTableMock;
  private static AbstractTableModel recordsTableModelMock;
  private static AbstractTableModel archiveTableModelMock;
  private static ArchiveFrame archiveFrameMock;

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
    archiveFrameMock = mock(ArchiveFrame.class);
    repairRecordsTableMock = mock(JTable.class);
    repairRecordsArchiveTableMock = mock(JTable.class);
    recordsTableModelMock = mock(AbstractTableModel.class);
    archiveTableModelMock = mock(AbstractTableModel.class);
    
    when(guiManagerMock.getDbManager()).thenReturn(dbManagerMock);
    when(guiManagerMock.getRepairRecordsTable()).thenReturn(repairRecordsTableMock);
    when(repairRecordsTableMock.getModel()).thenReturn(recordsTableModelMock);
    when(guiManagerMock.getArchiveFrame()).thenReturn(archiveFrameMock);
    when(archiveFrameMock.getRecordsArchiveTable()).thenReturn(repairRecordsArchiveTableMock);
    when(repairRecordsArchiveTableMock.getModel()).thenReturn(archiveTableModelMock);
    when(dbManagerMock.getIdByOrdinalNumber(anyInt())).thenAnswer(i -> (int) i.getArgument(0) + 1);
    when(archiveTableModelMock.getRowCount()).thenReturn(2);
    
    deleteRecordListener = new DeleteRecordListener(guiManagerMock, dialogWindowMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @Test
  @DisplayName("No interactions when no rows selected")
    void noInteractionsWhenNoRowsSelected() {
    when(repairRecordsTableMock.getSelectedRow()).thenReturn(-1);
    deleteRecordListener.actionPerformed(null);
    verifyNoInteractions(dbManagerMock);
    verifyNoInteractions(dialogWindowMock);
    verifyNoInteractions(recordsTableModelMock);
    verifyNoInteractions(archiveTableModelMock);  
    }
  
  @Nested
  @DisplayName("Row three is selected")
  class RowThreeIsSelected {
    @BeforeEach
    void setUp() {
      when(repairRecordsTableMock.getSelectedRow()).thenReturn(3);
    }
  
    @Test
    @DisplayName("Check request to delete")
    void checkRequestToDelete() {
      deleteRecordListener.actionPerformed(null);
      verify(dbManagerMock).deleteRepairRecord(2);
    }
    
    @Nested
    @DisplayName("Was deleted")
    class WasDeleted {
      @BeforeEach
      void setUp() {
        when(dbManagerMock.deleteRepairRecord(2)).thenReturn(true);
      }
      
      @Test
      @DisplayName("Fire archive table if initialized")
      void fireArchiveTableIfInitialized() {
        when(archiveFrameMock.isInitialized()).thenReturn(true);
        deleteRecordListener.actionPerformed(null);
        
        verify(archiveTableModelMock).fireTableRowsInserted(0, 1);
      }
      
      @Test
      @DisplayName("Do not fire archive table if not initialized")
      void doNotFireArchiveTableIfNotInitialized() {
        deleteRecordListener.actionPerformed(null);
        verify(archiveTableModelMock, never()).fireTableRowsInserted(anyInt(), anyInt());
      }
      
      @Test
      @DisplayName("Fires row deleted")
      void firesRowDeleted() {
        deleteRecordListener.actionPerformed(null);
        verify(recordsTableModelMock).fireTableRowsDeleted(2, 3);
      }
      
      @Test
      @DisplayName("Select another row if present")
      void selectAnotherRowIfPresent() {
        when(recordsTableModelMock.getRowCount()).thenReturn(2);
        deleteRecordListener.actionPerformed(null);
        verify(repairRecordsTableMock).setRowSelectionInterval(0, 0);
      }
      
      @Test
      @DisplayName("Not try to select another row if not present")
      void notTryToSelectAnotherRowIfNotPresent() {
        when(recordsTableModelMock.getRowCount()).thenReturn(0);
        deleteRecordListener.actionPerformed(null);
        verify(repairRecordsTableMock, never()).setRowSelectionInterval(anyInt(), anyInt());
      }
    }
    
    @Test
    @DisplayName("Was not deleted notify user")
    void wasNotDeletedNotifyUser() {
      deleteRecordListener.actionPerformed(null);
      verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
    }
    
  }
  
  @Test
  @DisplayName("Row 0 was selected, was deleted, check select another row if present")
  void checkSelectAnotherRowIfPresent() {
    when(repairRecordsTableMock.getSelectedRow()).thenReturn(0);
    when(dbManagerMock.deleteRepairRecord(1)).thenReturn(true);
    when(recordsTableModelMock.getRowCount()).thenReturn(2);
    
    deleteRecordListener.actionPerformed(null);
    verify(repairRecordsTableMock).setRowSelectionInterval(0, 0);
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = deleteRecordListener.toString().toLowerCase();
    assertTrue(description.contains("guimanager="));
  }
}
