package gui.tablemodels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dbapi.DbManager;
import gui.GuiManager;
import gui.utility.RecordUpdateHandler;

public class RepairRecordsTableModelTest {
  private static final String[] COLUMN_NAMES = {
                                                  "Модель",
                                                  "Номер",
                                                  "ТО-3",
                                                  "ТР-1",
                                                  "ТР-2",
                                                  "ТР-3",
                                                  "СР",
                                                  "КР",
                                                  "Последний ремонт",
                                                  "Требуемый ремонт",
                                                  "Примечания"
                                                  };
  private static RepairRecordsTableModel recordsTableModel;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static RecordUpdateHandler updateHandlerMock;
  private static Map<Integer, List<String>> repairRecordsTestData;
  private static String[][] expectedTestValues;
  private static String[][] expectedTestValuesIfShowNextRepairsDatesIsFalse;


  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    initTestRecordsData();
    initExpectedTestValues();
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
    guiManagerMock = mock(GuiManager.class);
    dbManagerMock = mock(DbManager.class);
    updateHandlerMock = mock(RecordUpdateHandler.class);
    
    when(dbManagerMock.getIdByOrdinalNumber(anyInt())).thenReturn(1);
    when(dbManagerMock.getAllRepairRecords()).thenReturn(repairRecordsTestData);
    when(dbManagerMock.getRecordsCount()).thenReturn(repairRecordsTestData.size());
    
    recordsTableModel =
        new RepairRecordsTableModel(dbManagerMock, guiManagerMock, updateHandlerMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check column names")
  void checkColumnNames(final int col) {
    assertEquals(recordsTableModel.getColumnName(col), COLUMN_NAMES[col]);
  }
  
  @Test
  @DisplayName("Check row count")
  void checkRowCount() {
    assertEquals(repairRecordsTestData.size() * 2, recordsTableModel.getRowCount());
  }
  
  @Test
  @DisplayName("Check column count")
  void checkColumCount() {
    assertEquals(COLUMN_NAMES.length, recordsTableModel.getColumnCount());
  }
  
  @ParameterizedTest
  @MethodSource("provideRowAndColumnPairs")
  @DisplayName("Check isCellEditable returns correct values")
  void checkIsCellEditableReturnsCorrectValues(final int row, final int col) {
    if (row % 2 != 0 || col == 0 || col == 8 || col == 9) {
      assertFalse(recordsTableModel.isCellEditable(row, col));
    } else {
      assertTrue(recordsTableModel.isCellEditable(row, col));
    }
  }
  
  @ParameterizedTest
  @MethodSource("provideRowAndColumnPairs")
  @DisplayName("getValueAt returns correct value if ShowNextRepairs is true")
  void getValueAtReturnsCorrectValueIfShowNextRepairsIsTrue(final int row, final int col) {
    when(guiManagerMock.isShowNextRepairsDates()).thenReturn(true);
    assertEquals(expectedTestValues[row][col], recordsTableModel.getValueAt(row, col));
  }
  
  @ParameterizedTest
  @MethodSource("provideRowAndColumnPairs")
  @DisplayName("getValueAt returns correct value if ShowNextRepairs is false")
  void getValueAtReturnsCorrectValueIfShowNextRepairsIsFalse(final int row, final int col) {
    assertEquals(expectedTestValuesIfShowNextRepairsDatesIsFalse[row][col],
                 recordsTableModel.getValueAt(row, col));
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("setValueAt not calls updateHandler for odd row")
  void setValueAtNotCallsUpdateHandlerForOddRow(final int col) {
    recordsTableModel.setValueAt(null, 1, col);
    verifyNoInteractions(updateHandlerMock);
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("setValueAt calls updateHandler with new value")
  void setValueAtCallsUpdateHandlerWithNewValue(final int col) {
    recordsTableModel.setValueAt(" " + "01.01.1970" + " ", 0, col);
    updateHandlerMock.handleCellNewValue("01.01.1970", 0, col);
  }
  
  @ParameterizedTest
  @MethodSource("provideLastRepairsColumnsIndices")
  @DisplayName("setValueAt not calls updateHandler for same last repair date")
  void setValueAtNotCallsUpdateHandlerForSameValue(final int col) {
    recordsTableModel.setValueAt(" " + expectedTestValues[0][col] + " ", 0, col);
    verifyNoInteractions(updateHandlerMock);
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = recordsTableModel.toString().toLowerCase();
    assertTrue(description.contains("guimanager="));
  }
  
  private static Stream<Integer> provideColumnsIndices() {
    return Stream.iterate(0, i -> ++i).limit(11);
  }
  
  private static Stream<Integer> provideLastRepairsColumnsIndices() {
    return Stream.iterate(2, i -> ++i).limit(6);
  }
  
  private static Stream<Arguments> provideRowAndColumnPairs() {
    Stream<Arguments> row0 = Stream.iterate(0, i -> ++i).limit(11).map(i -> Arguments.of(0, i));
    return Stream.concat(row0,
           Stream.iterate(0, i -> ++i).limit(11).map(i -> Arguments.of(1, i)));
  }
  
  private static void initTestRecordsData() {
    final List<String> repairRecords = new ArrayList<>(19);
    // Randomly chosen record from real use case.
    // Changes to this values should lead to changes in expectedTestValues.
    Stream.of("ТГМ4А", "2180", "02.11.2020", "02.12.2020", "06.08.2020", "02.02.2021", "15.02.2020",
              "08.08.2021", "04.10.2017", "18.09.2020", "01.05.2015", "30.03.2021", "01.06.2009",
              "30.03.2021", "ТО-3", "02.11.2020", "ТР-3", "18.09.2020", "Поставлен в запас")
          .forEach(repairRecords::add);
    
    repairRecordsTestData = new HashMap<>(1);
    repairRecordsTestData.put(1, repairRecords);
  }
  
  private static void initExpectedTestValues() {
    // Values that correspond to repairRecordsTestData(1) values.
    // Change expected values if repairRecordsTestData(1) values was changed.
    expectedTestValues = new String[2][11];
    
    expectedTestValues[0][0] = "ТГМ4А";
    expectedTestValues[0][1] = "2180";
    expectedTestValues[0][2] = "02.11.2020";
    expectedTestValues[0][3] = "06.08.2020";
    expectedTestValues[0][4] = "15.02.2020";
    expectedTestValues[0][5] = "04.10.2017";
    expectedTestValues[0][6] = "01.05.2015";
    expectedTestValues[0][7] = "01.06.2009";
    expectedTestValues[0][8] = "ТО-3";
    expectedTestValues[0][9] = "ТР-3";
    expectedTestValues[0][10] = "Поставлен в запас";
    
    expectedTestValues[1][0] = "";
    expectedTestValues[1][1] = "";
    expectedTestValues[1][2] = "02.12.2020";
    expectedTestValues[1][3] = "02.02.2021";
    expectedTestValues[1][4] = "08.08.2021";
    expectedTestValues[1][5] = "18.09.2020";
    expectedTestValues[1][6] = "30.03.2021";
    expectedTestValues[1][7] = "30.03.2021";
    expectedTestValues[1][8] = "02.11.2020";
    expectedTestValues[1][9] = "18.09.2020";
    expectedTestValues[1][10] = "";
    
    expectedTestValuesIfShowNextRepairsDatesIsFalse = new String[2][11];
    
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][0] = expectedTestValues[0][0];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][1] = expectedTestValues[0][1];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][2] = expectedTestValues[0][2];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][3] = expectedTestValues[0][3];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][4] = expectedTestValues[0][4];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][5] = expectedTestValues[0][5];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][6] = expectedTestValues[0][6];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][7] = expectedTestValues[0][7];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][8] = expectedTestValues[0][8];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][9] = expectedTestValues[0][9];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[0][10] = expectedTestValues[0][10];
    
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][0] = expectedTestValues[1][0];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][1] = expectedTestValues[1][1];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][2] = "";
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][3] = "";
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][4] = "";
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][5] = "";
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][6] = "";
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][7] = "";
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][8] = expectedTestValues[1][8];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][9] = expectedTestValues[1][9];
    expectedTestValuesIfShowNextRepairsDatesIsFalse[1][10] = expectedTestValues[1][10];
  }

}
