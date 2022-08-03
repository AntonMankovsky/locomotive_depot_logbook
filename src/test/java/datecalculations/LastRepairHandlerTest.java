package datecalculations;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import dbapi.DbManager;
import gui.GuiManager;

public class LastRepairHandlerTest {
  private static LastRepairHandler lastRepairHandler;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static Map<Integer, List<String>> repairRecordsMock;
  private static List<List<String>> datesForTests;
  private static String[] expectedDate;
  private static String[] expectedRepairType;
  private static AbstractTableModel repairRecordsTableModelMock;
  private static JTable repairRecordsTableMock;
  
  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    initTestsData();
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @SuppressWarnings("unchecked")
  @BeforeEach
  protected void setUp() throws Exception {
    dbManagerMock = mock(DbManager.class);
    repairRecordsMock = mock(HashMap.class);
    
    guiManagerMock = mock(GuiManager.class);
    repairRecordsTableModelMock = mock(AbstractTableModel.class);
    repairRecordsTableMock = mock(JTable.class);
    
    when(repairRecordsMock.get(0)).thenReturn(datesForTests.get(0));
    when(repairRecordsMock.get(1)).thenReturn(datesForTests.get(1));
    when(repairRecordsMock.get(2)).thenReturn(datesForTests.get(2));
    when(dbManagerMock.getAllRepairRecords()).thenReturn(repairRecordsMock);
    when(dbManagerMock.getIdByOrdinalNumber(0)).thenReturn(0);
    when(dbManagerMock.getIdByOrdinalNumber(1)).thenReturn(1);
    when(dbManagerMock.getIdByOrdinalNumber(2)).thenReturn(2);
    
    when(guiManagerMock.getRepairRecordsTable()).thenReturn(repairRecordsTableMock);
    when(repairRecordsTableMock.getModel()).thenReturn(repairRecordsTableModelMock);
    
    lastRepairHandler = new LastRepairHandler(guiManagerMock, dbManagerMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  /**
   * Imitates actual data.
   * <p>
   * Original functionality depends only on last repair values, so all other indices in list could
   * contain empty strings (identical data structure, but only needed values).
   */
  private static void initTestsData() {
    expectedDate = new String[3];
    expectedRepairType = new String[3];
    datesForTests = new ArrayList<>(3);
    
    List<String> tempList = new ArrayList<>(19);
    Stream.of(
          "", "", "01.01.2000", "", null, "", "01.02.2000", "", "02.02.2000", ""
          , "01.01.1999", "", "01.02.1999", "", "", "", "", "", "")
          .forEach(tempList::add);
    datesForTests.add(tempList);
    expectedDate[0] = "02.02.2000";
    expectedRepairType[0] = "ТР-3";
    
    tempList = new ArrayList<>(19);
    Stream.of(
          "", "", "01.01.1970", "", "31.12.2070", "", "11.11.2011", "", "20.11.2010", ""
          , "28.07.2007", "", null, "", "", "", "", "", "")
          .forEach(tempList::add);
    datesForTests.add(tempList);
    expectedDate[1] = "31.12.2070";
    expectedRepairType[1] = "ТР-1";
    
    tempList = new ArrayList<>(19);
    Stream.of(
          "", "", "", "", "25.06.2000", "", "10.12.1998", "", "19.09.2018", ""
          , "09.05.2019", "", "13.07.2022", "", "", "", "", "", "")
          .forEach(tempList::add);
    datesForTests.add(tempList);
    expectedDate[2] = "13.07.2022";
    expectedRepairType[2] = "КР";
  }
  
  @ParameterizedTest
  @ValueSource(ints = {0, 2, 4})
  @DisplayName("Handler updates and fires date and type")
  void handlerUpdatesAndFiresDateAndType(final int rowIndex) {
    lastRepairHandler.updateLastRepairColumn(rowIndex);
    
    verify(dbManagerMock).setRepairRecordCell(rowIndex / 2, 14, expectedRepairType[rowIndex / 2]);
    verify(dbManagerMock).setRepairRecordCell(rowIndex / 2, 15, expectedDate[rowIndex / 2]);
    
    verify(repairRecordsTableModelMock).fireTableCellUpdated(rowIndex, 8);
    verify(repairRecordsTableModelMock).fireTableCellUpdated(rowIndex + 1, 8);
  }
  
  @Test
  @DisplayName("Check no calls if no need update")
  void checkNoCallsIfNoNeedUpdate() {
    List<String> tempList = new ArrayList<>(19);
    for (int j = 0; j < 19; j++) {
      tempList.add("01.01.2000");
    }
    when(repairRecordsMock.get(0)).thenReturn(tempList);
    
    lastRepairHandler.updateLastRepairColumn(0);
    
    verify(dbManagerMock, never()).setRepairRecordCell(anyInt(), anyInt(), anyString());
    verify(repairRecordsTableModelMock, never()).fireTableCellUpdated(anyInt(), anyInt());
  }

}