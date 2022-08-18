package gui.tablemodels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
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

public class RecordsArchiveTableModelTest {
  private static final String[] COLUMN_NAMES = {
                                                  "Модель",
                                                  "Номер",
                                                  "ТО-3",
                                                  "ТР-1",
                                                  "ТР-2",
                                                  "ТР-3",
                                                  "СР",
                                                  "КР",
                                                  "Примечания"
                                                  };
  private static RecordsArchiveTableModel recordsArchiveTableModel;
  private static List<List<String>> recordsArchiveDataMock;
  private static List<String> testArchiveRecord;
  private static String[][] expectedTestValues;
  private static DbManager dbManagerMock;
  
  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    initTestArchiveRecord();
    initExpectedTestValues();
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @SuppressWarnings("unchecked")
  @BeforeEach
  protected void setUp() throws Exception {
    dbManagerMock = mock(DbManager.class);
    recordsArchiveDataMock = mock(List.class);
    
    when(dbManagerMock.getAllRecordsArchiveData()).thenReturn(recordsArchiveDataMock);
    when(recordsArchiveDataMock.get(0)).thenReturn(testArchiveRecord);
    when(recordsArchiveDataMock.size()).thenReturn(1);
    
    recordsArchiveTableModel = new RecordsArchiveTableModel(dbManagerMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @ParameterizedTest
  @MethodSource("provideRowAndColumnPairs")
  @DisplayName("Every cell is editable")
  void everyCellIsEditable(final int row, final int col) {
    assertTrue(recordsArchiveTableModel.isCellEditable(row, col));
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check column names")
  void checkColumnNames(final int col) {
    assertEquals(recordsArchiveTableModel.getColumnName(col), COLUMN_NAMES[col]);
  }
  
  @Test
  @DisplayName("Check row count")
  void checkRowCount() {
    assertEquals(2, recordsArchiveTableModel.getRowCount());
  }
  
  @Test
  @DisplayName("Check column count")
  void checkColumCount() {
    assertEquals(COLUMN_NAMES.length, recordsArchiveTableModel.getColumnCount());
  }
  
  @ParameterizedTest
  @MethodSource("provideRowAndColumnPairs")
  @DisplayName("getValueAt returns correct value")
  void getValueAtReturnsCorrectValue(final int row, final int col) {
    assertEquals(expectedTestValues[row][col], recordsArchiveTableModel.getValueAt(row, col));
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = recordsArchiveTableModel.toString().toLowerCase();
    assertTrue(description.contains("dbmanager="));
  }
  
  private static Stream<Arguments> provideRowAndColumnPairs() {
    Stream<Arguments> row0 = Stream.iterate(0, i -> ++i).limit(9).map(i -> Arguments.of(0, i));
    return Stream.concat(row0,
           Stream.iterate(0, i -> ++i).limit(9).map(i -> Arguments.of(1, i)));
  }
  
  private static Stream<Integer> provideColumnsIndices() {
    return Stream.iterate(0, i -> ++i).limit(9);
  }
  
  private static void initTestArchiveRecord() {
    testArchiveRecord = new ArrayList<>(1);
    // Just random values. Changes to this values should lead to changes in expectedTestValues.
    Stream.of("ТЭМ", "0909", "01.08.2022", "31.08.2022", "05.01.2018", "23.06.2020", 
              "09.09.2019", "02.12.2020",  "16.01.2021", "29.08.2021", "02.02.2017",
              "02.01.2023", "01.01.2015",  "30.10.2026", "some notes")
              .forEach(testArchiveRecord::add); 
  }
  
  private static void initExpectedTestValues(){
    // Values that correspond to testArchiveRecord values.
    // Change expected values if testArchiveRecord values was changed.
    expectedTestValues = new String[2][9];
    
    expectedTestValues[0][0] = "ТЭМ";
    expectedTestValues[0][1] = "0909";
    expectedTestValues[0][2] = "01.08.2022";
    expectedTestValues[0][3] = "05.01.2018";
    expectedTestValues[0][4] = "09.09.2019";
    expectedTestValues[0][5] = "16.01.2021";
    expectedTestValues[0][6] = "02.02.2017";
    expectedTestValues[0][7] = "01.01.2015";
    expectedTestValues[0][8] = "some notes";
    
    expectedTestValues[1][0] = "";
    expectedTestValues[1][1] = "";
    expectedTestValues[1][2] = "31.08.2022";
    expectedTestValues[1][3] = "23.06.2020";
    expectedTestValues[1][4] = "02.12.2020";
    expectedTestValues[1][5] = "29.08.2021";
    expectedTestValues[1][6] = "02.01.2023";
    expectedTestValues[1][7] = "30.10.2026";
    expectedTestValues[1][8] = "";
  }
  
}
