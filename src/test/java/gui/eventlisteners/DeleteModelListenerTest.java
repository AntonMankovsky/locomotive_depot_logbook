package gui.eventlisteners;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import gui.GuiManager;
import gui.ModelsFrame;
import gui.utility.DialogWindow;

public class DeleteModelListenerTest {
  private static DeleteModelListener deleteModelListener;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static DialogWindow dialogWindowMock;
  private static ModelsFrame modelsFrameMock;
  private static JTable repairPeriodsTableMock;
  private static AbstractTableModel abstractTableModelMock;
  private static Map<Integer, List<String>> recordsTestData;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    initRecordsTestData();
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
    guiManagerMock = mock(GuiManager.class);
    dbManagerMock = mock(DbManager.class);
    dialogWindowMock = mock(DialogWindow.class);
    modelsFrameMock = mock(ModelsFrame.class);
    repairPeriodsTableMock = mock(JTable.class);
    abstractTableModelMock = mock(AbstractTableModel.class);
    
    when(guiManagerMock.getDbManager()).thenReturn(dbManagerMock);
    when(guiManagerMock.getModelsFrame()).thenReturn(modelsFrameMock);
    when(modelsFrameMock.getRepairPeriodsTable()).thenReturn(repairPeriodsTableMock);
    when(repairPeriodsTableMock.getModel()).thenReturn(abstractTableModelMock);
    when(dbManagerMock.getAllRepairRecords()).thenReturn(recordsTestData);
    when(dbManagerMock.getRecordsCount()).thenReturn(recordsTestData.size());
    when(dbManagerMock.getIdByOrdinalNumber(anyInt())).thenAnswer(i -> (int) i.getArgument(0) + 1);
    
    deleteModelListener = new DeleteModelListener(guiManagerMock, dialogWindowMock);
  }
  
  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @Test
  @DisplayName("No interactions when no rows selected")
    void noInteractionsWhenNoRowsSelected() {
    when(repairPeriodsTableMock.getSelectedRow()).thenReturn(-1);
    deleteModelListener.actionPerformed(null);
    verifyNoInteractions(dbManagerMock);
    verifyNoInteractions(dialogWindowMock);
    verifyNoInteractions(abstractTableModelMock);   
    }
  
  @Nested
  @DisplayName("Row one is selected")
  class RowOneIsSelected {
    @BeforeEach
    void setUp() {
      when(repairPeriodsTableMock.getSelectedRow()).thenReturn(1);
    }
    
    @Test
    @DisplayName("When model in use notify user and do not delete model")
    void whenModelInUseNotifyUserAndDoNotDeleteModel() {
      when(repairPeriodsTableMock.getValueAt(anyInt(), anyInt()))
          .thenReturn(recordsTestData.get(1).get(0));
      deleteModelListener.actionPerformed(null);
        
      verify(dbManagerMock, never()).deleteRepairPeriods(anyString());
      verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
    }
    
    @Nested
    @DisplayName("Model not in use")
    class ModelNotInUse {
      @BeforeEach
      void setUp() {
        when(repairPeriodsTableMock.getValueAt(anyInt(), anyInt())).thenReturn("Free model");
      }
      
      @Test
      @DisplayName("Delete model")
      void notifyUserAndDoNotDeleteModel() {
        deleteModelListener.actionPerformed(null);
        verify(dbManagerMock).deleteRepairPeriods("Free model");
      }
      
      @Nested
      @DisplayName("Was deleted")
      class WasDeleted {
        @BeforeEach
        void setUp() {
          when(dbManagerMock.deleteRepairPeriods(anyString())).thenReturn(true);
        }
        
        @Test
        @DisplayName("Calls rebuild submenu")
        void callsRebuildSubmenu() {
          deleteModelListener.actionPerformed(null);
          verify(guiManagerMock).rebuildNewRecordSubmenu();
        }
        
        @Test
        @DisplayName("Fires row deleted")
        void firesRowDeleted() {
          deleteModelListener.actionPerformed(null);
          verify(abstractTableModelMock).fireTableRowsDeleted(1, 1);
        }
        
        @Test
        @DisplayName("Select another row if present")
        void notLastRow() {
          when(abstractTableModelMock.getRowCount()).thenReturn(1);
          deleteModelListener.actionPerformed(null);
          verify(repairPeriodsTableMock).setRowSelectionInterval(0, 0);
        }
      }
      
      @Test
      @DisplayName("Notify user if was not deleted")
      void notifyUserIfWasNotDeleted() {
        deleteModelListener.actionPerformed(null);
        verify(dialogWindowMock).showErrorMessage(any(), anyString(), anyString());
      }
    }
  }
    
  @Test
  @DisplayName("Select another row if row zero is selected, model not in use and was deleted")
  void selectAnotherRowIfRowZeroIsSelectedModelNotInUseAndWasDeleted() {
    when(repairPeriodsTableMock.getSelectedRow()).thenReturn(0);
    when(repairPeriodsTableMock.getValueAt(anyInt(), anyInt())).thenReturn("Free model");
    when(dbManagerMock.deleteRepairPeriods(anyString())).thenReturn(true);
    when(abstractTableModelMock.getRowCount()).thenReturn(1);
    deleteModelListener.actionPerformed(null);
      
    verify(repairPeriodsTableMock).setRowSelectionInterval(0, 0);
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = deleteModelListener.toString().toLowerCase();
    assertTrue(description.contains("guimanager="));
  }
  
  private static void initRecordsTestData() {
    recordsTestData = new HashMap<>(2);
    List<String> tempList = new ArrayList<>(1);
    tempList.add("ТЭМ");
    recordsTestData.put(1, tempList);
    
    tempList = new ArrayList<>(1);
    tempList.add("ТГМ4(Б)");
    recordsTestData.put(2, tempList);
  }

}
