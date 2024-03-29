package datecalculations;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import dbapi.DbManager;
import gui.GuiManager;
import gui.utility.DialogWindow;

/*
 * TODO: add tests for cases in which data should not be updated (when new next repair date is
 * before old next repair date for auto calculations).
 */
public class DateCalculationsHandlerTest {
  private static DateCalculationsHandler dateCalculationsHandler;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static RequiredRepairHandler requiredRepairHandlerMock;
  private static DialogWindow dialogWindowMock;
  private static Map<Integer, List<String>> repairRecordsMock;
  private static Map<String, List<Integer>> repairPeriodsMock;
  private static AbstractTableModel repairRecordsTableModelMock;
  private static JTable repairRecordsTableMock;
  private static List<Integer> periodsData;
  private static int rowIndex;
  private static Map<String, List<String>> correctStrings;
  private static List<String> expectedStrings1;
  private static List<String> expectedStrings2;
  private static List<Integer> expectedColumnIndices;
  private static List<String> recordData;
  // Purposely take a date so old that it does not possibly interfere with most tests that test
  // functionality which is independent of the current date. Used only to match method signature.
  private static LocalDate today = LocalDate.of(1960, 1, 01);
  
  /**
   * Initializes list of repair periods and lists with expected data for assertions.
   * <p>
   * Amount of days of any repair period for any model should be validated
   * at stage of user's input and always end up as positive integer.
   * Therefore, there is no need to different numbers and it would be convenient
   * to use one set of predefined typical values.
   * <p>
   * Expected indices is indices of each next_repair value in DbManager list of records data.
   * Those values could be hard coded cause they could have change only on big refactoring that
   * changes whole backend architecture and a lot of frontend methods, but accounting for that kind
   * of refactoring is out of scope on the current stage of a project.
   * @throws Exception
   */
  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    periodsData = new ArrayList<>(6);
    Stream.of(30, 225, 450, 900, 2160, 4320).forEach(periodsData::add);
    
    expectedColumnIndices = new ArrayList<>(6);
    Stream.of(3, 5, 7, 9, 11, 13).forEach(expectedColumnIndices::add);
    
    initExpectedStrings();
    correctStrings = new HashMap<>(2);
    correctStrings.put("01.01.1980", expectedStrings1);
    correctStrings.put("31.12.2022", expectedStrings2);
    
    recordData = new ArrayList<>(18);
    recordData.add("ТЭМ");
    Stream.generate(() -> "").limit(15).forEach(recordData::add);
    recordData.add(7, null);
    recordData.add(9, "01.01.1970");
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  /**
   * Prepares mocks for tests.
   * <p>
   * Row index of user`s input can be any even number and is never odd by application design.
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @BeforeEach
  protected void setUp() throws Exception {
    requiredRepairHandlerMock = mock(RequiredRepairHandler.class);
    dbManagerMock = mock(DbManager.class);
    dialogWindowMock = mock(DialogWindow.class);
    repairRecordsMock = mock(HashMap.class);
    repairPeriodsMock = mock(HashMap.class);
    
    guiManagerMock = mock(GuiManager.class);
    repairRecordsTableModelMock = mock(AbstractTableModel.class);
    repairRecordsTableMock = mock(JTable.class);
    
    when(repairRecordsMock.get(anyInt())).thenReturn(recordData);
    when(repairPeriodsMock.get("ТЭМ")).thenReturn(periodsData);
    when(dbManagerMock.getAllRepairRecords()).thenReturn(repairRecordsMock);
    when(dbManagerMock.getAllRepairPeriodData()).thenReturn(repairPeriodsMock);
    
    when(guiManagerMock.getRepairRecordsTable()).thenReturn(repairRecordsTableMock);
    when(repairRecordsTableMock.getModel()).thenReturn(repairRecordsTableModelMock);
    
    dateCalculationsHandler = new DateCalculationsHandler(
        guiManagerMock, dbManagerMock, requiredRepairHandlerMock, dialogWindowMock);
    rowIndex = (int) (Math.random() * 100);
    rowIndex = rowIndex % 2 == 0 ? rowIndex : rowIndex + 1;
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  /**
   * Initializes lists with expected, right strings for "ТЭМ" model repair periods.
   * <p>
   * Assumes that repair periods for tests is {30, 225, 450, 900, 2160, 4320}
   * and dates for tests is {"01.01.1980", "31.12.2022"}.
   */
  private static void initExpectedStrings() {
    expectedStrings1 = new ArrayList<>(6);
    expectedStrings2 = new ArrayList<>(6);
    Stream.of("31.01.1980", "13.08.1980", "26.03.1981", "19.06.1982", "30.11.1985", "30.10.1991")
        .forEach(expectedStrings1::add);
    Stream.of("30.01.2023", "13.08.2023", "25.03.2024", "18.06.2025", "29.11.2028", "29.10.2034")
        .forEach(expectedStrings2::add);
  }
  
  /**
   * Test`s parameters is two random strings of dates in strictly predefined format.
   * <p>
   * There is no test cases for invalid string format, null or empty string parameters, cause this
   * class must always be called with already validated arguments.
   * <br>
   * Responsibility of validation user`s input is encapsulated in other classes which should be
   * tested separately.
   * @return stream of strings representing possible dates of last repair
   */
  private static Stream<String> provideLastRepairStrings() {
    return Stream.of("01.01.1980", "31.12.2022");
  }
  
  /**
   * Provides integers representing JTable columns with repair dates
   * @return stream of integers from 2 to 7 inclusive
   */
  private static Stream<Integer> provideColumnsIndices() {
    return Stream.iterate(2, i -> ++i).limit(6);
  }
  
  private static Stream<Integer> provideInvalidIndices() {
    return Stream.of(Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
  }
  
  /**
   * Checks absence of calls to database when invalid index passed.
   * <p>
   * String argument of handleDateCalculations is random string which has nothing to do with this
   * test, except that it needed to call the method.
   * @param colIndex index that not connected with any repair column in GUI table
   */
  @ParameterizedTest
  @MethodSource("provideInvalidIndices")
  @DisplayName("Check does nothing on invalid column index")
  void checkDoesNothingOnInvalidColumnIndex(final int colIndex) {
    dateCalculationsHandler.handleDateCalculations("01.01.2000", rowIndex, colIndex, today);
    verify(dbManagerMock, never()).setRepairRecordCell(anyInt(), anyInt(), anyString());
  }
  
  @ParameterizedTest
  @MethodSource("provideLastRepairStrings")
  @DisplayName("Check DB request correctness for three_maintenance case")
  void checkDbRequestCorrectnessForThreeMaintenanceCase(final String lastRepairString) {
    verifyCorrectnessAlgorithm(lastRepairString, 2);
  }
  
  @ParameterizedTest
  @MethodSource("provideLastRepairStrings")
  @DisplayName("Check DB request correctness for one_current_repair case")
  void checkDbRequestCorrectnessForOneCurrentRepairCase(final String lastRepairString) {
    verifyCorrectnessAlgorithm(lastRepairString, 3);
  }
  
  @ParameterizedTest
  @MethodSource("provideLastRepairStrings")
  @DisplayName("Check DB request for two_current_repair case")
  void checkDbRequestCorrectnessForTwoCurrentRepairCase(final String lastRepairString) {
    verifyCorrectnessAlgorithm(lastRepairString, 4);
  }
  
  @ParameterizedTest
  @MethodSource("provideLastRepairStrings")
  @DisplayName("Check DB request for three_current_repair case")
  void checkDbRequestCorrectnessForThreeCurrentRepairCase(final String lastRepairString) {
    verifyCorrectnessAlgorithm(lastRepairString, 5);
  }
  
  @ParameterizedTest
  @MethodSource("provideLastRepairStrings")
  @DisplayName("Check DB request for medium_repair case")
  void checkDbRequestCorrectnessForMediumRepairCase(final String lastRepairString) {
    verifyCorrectnessAlgorithm(lastRepairString, 6);
  }
  
  @ParameterizedTest
  @MethodSource("provideLastRepairStrings")
  @DisplayName("Check DB request for overhaul case")
  void checkDbRequestCorrectnessForOverhaulCase(final String lastRepairString) {
    verifyCorrectnessAlgorithm(lastRepairString, 7);
  }
  
  private void verifyCorrectnessAlgorithm(final String lastRepairString, final int colIndex) {
    dateCalculationsHandler.handleDateCalculations(lastRepairString, rowIndex, colIndex, today);
    
    final ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);
    final ArgumentCaptor<Integer> intArg = ArgumentCaptor.forClass(Integer.class);
    verify(
        dbManagerMock, times(colIndex - 1))
        .setRepairRecordCell(anyInt(), intArg.capture(), strArg.capture());
    final List<String> actualStrings = strArg.getAllValues();
    final List<Integer> actualInts = intArg.getAllValues();
    final int n = actualStrings.size() - 1;
    for (int j = 0; j < actualStrings.size(); j++) {
      assertEquals(correctStrings.get(lastRepairString).get(n - j), actualStrings.get(j));
      assertEquals(expectedColumnIndices.get(n - j), actualInts.get(j));
    }
  }
  
  /**
   * Checks if GUI table was notified properly when cell changed.
   * <p>
   * If user changes cell with repair date in row with some {@code rowId}, cell(s) on next row
   * should be changed automatically on the next row (@code rowId + 1}.
   * <br>
   * If cell with bigger repair was changed by user, all cells with lower repairs should be changed
   * automatically in the next row.
   * <p>
   * Example: if cell with two_current_repair date was changed (column index in GUI table = 4),
   * table should be fired that cells with one_current_repair and three_maintenance dates (column
   * indices in GUI table = [3, 2]) has been changed too.
   * <p>
   * String argument of handleDateCalculations is random string which has nothing to do with this
   * test, except that it needed to call the method.
   * @param columnIndex
   */
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check fire table invocations")
  void checkFireTableInvocations(final int columnIndex) {
    dateCalculationsHandler.handleDateCalculations("01.01.2000", rowIndex, columnIndex, today);
    final ArgumentCaptor<Integer> rowCaptor = ArgumentCaptor.forClass(Integer.class);
    final ArgumentCaptor<Integer> colCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(repairRecordsTableModelMock, times(columnIndex + 1))
        .fireTableCellUpdated(rowCaptor.capture(), colCaptor.capture());
    
    final List<Integer> expectedColIndices = new ArrayList<>(columnIndex - 1);
    Stream.iterate(columnIndex, i -> --i).limit(columnIndex - 1).forEach(expectedColIndices::add);
    final List<Integer> actualColIndices = colCaptor.getAllValues();
    final List<Integer> actualRowIndices = rowCaptor.getAllValues();
    
    // Fire next repair dates updated (exclude required repair column from loop).
    rowIndex++;
    int tempInt = actualRowIndices.size() - 2;
    for (int j = 0; j < tempInt; j++) {
      assertEquals(rowIndex, actualRowIndices.get(j));
      assertEquals(expectedColIndices.get(j), actualColIndices.get(j));
    }
    // Fire required repair column updated.
    rowIndex--;
    for (; tempInt < actualRowIndices.size(); tempInt++) {
      assertEquals(rowIndex++, actualRowIndices.get(tempInt));
      assertEquals(9, actualColIndices.get(tempInt));
    }
  }
  
  /**
   * Checks that only one call to DB was performed by calculations handler if old dates is
   * bigger than auto calculated next repair dates.
   * @param columnIndex for repair type
   */
  @SuppressWarnings("unchecked")
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check No Calls For Older Dates")
  void CheckNoCallsForOlderDates (final int columnIndex) {
    /* thee_maintenence (column index=2) does not include any auto calculations for smaller repairs
    *  cause it`s the smallest of all repairs. Therefore, it should be excluded from the test.
    */
    if (columnIndex == 2) {
      return;
    }
    
    /* This test was added later than all other tests and it needs different data.
     * The easiest way to do that end up override global mocks with local ones.
     */
    final DbManager dbManager = mock(DbManager.class);
    final Map<Integer, List<String>> repairRecords = mock(HashMap.class);
    final Map<String, List<Integer>> repairPeriods = mock(HashMap.class);
    
    final List<String> recordData1 = new ArrayList<>(18);
    recordData1.add("ТЭМ");
    Stream.generate(() -> "31.12.2099").limit(17).forEach(recordData1::add);
    
    when(repairRecords.get(anyInt())).thenReturn(recordData1);
    when(repairPeriods.get("ТЭМ")).thenReturn(periodsData);
    when(dbManager.getAllRepairRecords()).thenReturn(repairRecords);
    when(dbManager.getAllRepairPeriodData()).thenReturn(repairPeriods);
    
    final DateCalculationsHandler handler = new DateCalculationsHandler(
                  guiManagerMock, dbManager, requiredRepairHandlerMock, dialogWindowMock);
    handler.handleDateCalculations("01.01.2000", rowIndex, columnIndex, today);
    
    verify(dbManager, times(1)).setRepairRecordCell(anyInt(), anyInt(), anyString());
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check user informed if lastRepairDate is after today")
  void checkUserInformedIfLastRepairDateIsAfterToday(final int columnIndex) {
    dateCalculationsHandler
        .handleDateCalculations("02.01.2000", rowIndex, columnIndex, LocalDate.of(2000, 1, 01));
    verify(dialogWindowMock).showInfoMessage(any(), anyString(), anyString());
  }
  
  @ParameterizedTest
  @MethodSource("provideColumnsIndices")
  @DisplayName("Check no dialog window if lastRepairDate is not after today")
  void checkNoDialogWindowIfLastRepairDateIsNotAfterToday(final int columnIndex) {
    dateCalculationsHandler
        .handleDateCalculations("01.01.2000", rowIndex, columnIndex, LocalDate.of(2000, 1, 02));
    verifyNoInteractions(dialogWindowMock);
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = dateCalculationsHandler.toString().toLowerCase();
    assertTrue(description.contains("dbmanager="));
  }
}
