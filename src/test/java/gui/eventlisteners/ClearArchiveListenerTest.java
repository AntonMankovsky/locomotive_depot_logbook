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

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dbapi.DbManager;
import gui.ArchiveFrame;
import gui.GuiManager;
import gui.utility.DialogWindow;

public class ClearArchiveListenerTest {
  private static ClearArchiveListener clearArchiveListener;
  private static ArchiveFrame archiveFrameMock;
  private static GuiManager guiManagerMock;
  private static DialogWindow dialogWindowMock;
  private static DbManager dbManagerMock;
  private static JTable recordsArchiveTableMock;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
    guiManagerMock = mock(GuiManager.class);
    dialogWindowMock = mock(DialogWindow.class);
    dbManagerMock = mock(DbManager.class);
    archiveFrameMock = mock(ArchiveFrame.class);
    recordsArchiveTableMock = mock(JTable.class);
    
    when(guiManagerMock.getArchiveFrame()).thenReturn(archiveFrameMock);
    when(archiveFrameMock.getRecordsArchiveTable()).thenReturn(recordsArchiveTableMock);
    when(guiManagerMock.getDbManager()).thenReturn(dbManagerMock);
    
    clearArchiveListener = new ClearArchiveListener(guiManagerMock, dialogWindowMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @Test
  @DisplayName("actionPerformed does nothing if row count is zero")
  void actionPerformedDoesNothingIfRowCountIsZero() {
    setRowCount(0);
    clearArchiveListener.actionPerformed(null);
    
    verifyNoInteractions(dbManagerMock);
    verifyNoInteractions(dialogWindowMock);
  }
  
  @Test
  @DisplayName("actionPerformed not write in DB or fire table if user cancelled")
  void actionPerformedNotWriteInDbOrFireTableIfUserCancelled() {
    setRowCount((int) (Math.random() * 100 + 1));
    setUserCancelled();
    clearArchiveListener.actionPerformed(null);
    
    verifyNoInteractions(dbManagerMock);
    verify(recordsArchiveTableMock, never()).getModel();
    verify(dialogWindowMock, never()).showErrorMessage(any(), anyString(),  anyString());
  }
  
  @Test
  @DisplayName("actionPerformed write in DB if user agree")
  void actionPerformedWriteInDbIfUserAgree() {
    setRowCount((int) (Math.random() * 100 + 1));
    setUserAgreed();
    clearArchiveListener.actionPerformed(null);
    
    verify(dbManagerMock).clearArchive();
  }
  
  @Test
  @DisplayName("actionPerformed notifies user on db request failure")
  void actionPerformedNotifiesUserOnDbRequestFailure() {
    setRowCount((int) (Math.random() * 100 + 1));
    setUserAgreed();
    clearArchiveListener.actionPerformed(null);
    
    verify(dialogWindowMock).showErrorMessage(any(), anyString(),  anyString());
  }
  
  @Test
  @DisplayName("actionPerformed fires table data changed on success")
  void actionPerformedFiresTableDataChangedOnSuccess() {
    setRowCount((int) (Math.random() * 100 + 1));
    setUserAgreed();
    
    when(dbManagerMock.clearArchive()).thenReturn(true);
    final AbstractTableModel tmMock = mock(AbstractTableModel.class);
    when(recordsArchiveTableMock.getModel()).thenReturn(tmMock);
    
    clearArchiveListener.actionPerformed(null);
    
    verify(tmMock).fireTableDataChanged();
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = clearArchiveListener.toString().toLowerCase();
    assertTrue(description.contains("guimanager="));
  }
  
  private void setRowCount(final int rowsNumber) {
    when(recordsArchiveTableMock.getRowCount()).thenReturn(rowsNumber);
  }
  
  private void setUserCancelled() {
    when(dialogWindowMock.showConfirmDialog(any(), anyString(),  anyString(), anyInt()))
        .thenReturn(Integer.MIN_VALUE);
  }

  private void setUserAgreed() {
    when(dialogWindowMock.showConfirmDialog(any(), anyString(),  anyString(), anyInt()))
        .thenReturn(0);
  }
}
