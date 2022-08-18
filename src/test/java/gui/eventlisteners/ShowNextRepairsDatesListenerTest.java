package gui.eventlisteners;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import gui.GuiManager;

public class ShowNextRepairsDatesListenerTest {
  private static ShowNextRepairsDatesListener showNextRepairsDatesListener;
  private static GuiManager guiManagerMock;
  private static JTable repairRecordsTableMock;
  private static AbstractTableModel abstractTableModelMock;
  private static TableCellEditor tableCellEditorMock;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
    guiManagerMock = mock(GuiManager.class);
    repairRecordsTableMock = mock(JTable.class);
    abstractTableModelMock = mock(AbstractTableModel.class);
    tableCellEditorMock = mock(TableCellEditor.class);
    
    when(guiManagerMock.getRepairRecordsTable()).thenReturn(repairRecordsTableMock);
    when(repairRecordsTableMock.getModel()).thenReturn(abstractTableModelMock);
    when(repairRecordsTableMock.getCellEditor()).thenReturn(tableCellEditorMock);
    
    showNextRepairsDatesListener = new ShowNextRepairsDatesListener(guiManagerMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @Test
  @DisplayName("itemStateChanged inverts isShowNextRepairsDates value to true")
  void itemStateChangedInvertsIsShowNextRepairsDatesValueToTrue() {
    when(guiManagerMock.isShowNextRepairsDates()).thenReturn(false);
    showNextRepairsDatesListener.itemStateChanged(null);
    
    verify(guiManagerMock).setShowNextRepairsDates(true);
  }
  
  @Test
  @DisplayName("itemStateChanged inverts isShowNextRepairsDates value to false")
  void itemStateChangedInvertsIsShowNextRepairsDatesValueToFalse() {
    when(guiManagerMock.isShowNextRepairsDates()).thenReturn(true);
    showNextRepairsDatesListener.itemStateChanged(null);
    
    verify(guiManagerMock).setShowNextRepairsDates(false);
  }
  
  @ParameterizedTest
  @ValueSource(ints = {0, 50, 100})
  @DisplayName("Check fire cell updated")
  void checkFireCellUpdated(final int numberOfRows) {
    when(abstractTableModelMock.getRowCount()).thenReturn(numberOfRows);
    showNextRepairsDatesListener.itemStateChanged(null);
    for (int j = 1; j < numberOfRows; j+=2) {
      for (int k = 2; k < 8; k++) {
      verify(abstractTableModelMock).fireTableCellUpdated(j, k);
      }
    }
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = showNextRepairsDatesListener.toString().toLowerCase();
    assertTrue(description.contains("guimanager="));
  }
  
}
