package dbapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DbManagerSqliteImpTest {
  private static DbManagerSqliteImp dbManager;
  private static SqliteConnection sqliteConnectionMock;
  private static Connection connectionMock;
  
  private static DbManagerSqliteImp dbManagerCorruptedConnection;
  private static SqliteConnection sqliteCorruptedConnectionMock;
  private static Connection corruptedConnectionMock;
  
  private static PreparedStatement prepStatementMockVacuum;
  private static PreparedStatement prepStatementMockFetchPeriodsData;
  private static PreparedStatement prepStatementMockFetchRecordsData;
  private static PreparedStatement prepStatementMockFetchArchiveData;
  private static PreparedStatement prepStatementMockInsertRecordRow;
  private static PreparedStatement prepStatementMockInsertModelRow;
  private static PreparedStatement preparedStatementInsertArchiveRow;
  private static PreparedStatement preparedStatementMockGetMaxId;
  
  private static ResultSet resultSetMockPeriodsData;
  private static ResultSet resultSetMockRecordsData;
  private static ResultSet resultSetMockArchiveData;
  private static ResultSet resultSetMockGetMaxId;
  
  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @Test
  @DisplayName("Check vacuum call")
  void checkVacuumCall() throws SQLException {
    setUpDbManager();
    verify(prepStatementMockVacuum).executeUpdate();
  }
  
  // ============================== Tests for repair records table ==============================
  
  @Test
  @DisplayName("insertNewRepairRecord sends correct request to DB")
  void insertNewRepairRecordSendsCorrectRequestToDb() throws SQLException {
    setUpDbManager();
    final List<String> testRow = new ArrayList<>(19);
    testRow.add("ТЭМ");
    testRow.add("0150");
    testRow.add("15.08.2022");
    testRow.add("14.09.2022");
    Stream.generate(() -> "").limit(15).forEach(testRow::add);
    
    final boolean actualIsSuccess = dbManager.insertNewRepairRecord(testRow);
    final boolean expectedIsSuccess = true;
    assertEquals(expectedIsSuccess, actualIsSuccess);
    
    for (int j = 0; j < 19; j++) {
      verify(prepStatementMockInsertRecordRow).setString(j + 1, testRow.get(j));
    }
    verify(prepStatementMockInsertRecordRow).executeUpdate();
  }
  
  @Test
  @DisplayName("insertNewRepairRecord not crush on bad connection")
  void insertNewRepairRecordNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    final boolean actualIsSuccess =
        dbManagerCorruptedConnection.insertNewRepairRecord(null);
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("getAllRepairRecords returns correct data after row insertion")
  void getAllRepairRecordsReturnsCorrectDataAfterRowInsertion() throws SQLException {
    setUpDbManager();
    final List<String> testRow = new ArrayList<>(19);
    testRow.add("ТЭМ");
    testRow.add("0150");
    testRow.add("15.08.2022");
    testRow.add("14.09.2022");
    Stream.generate(() -> "").limit(15).forEach(testRow::add);
    dbManager.insertNewRepairRecord(testRow);
    
    final List<String> actualDataAfterInsertion = dbManager.getAllRepairRecords().get(2);
      assertEquals(testRow, actualDataAfterInsertion);
  }
  
  @Test
  @DisplayName("setRepairRecordCell sends correct request to DB")
  void setRepairRecordCellSendsCorrectRequestToDb() throws SQLException {
    setUpDbManager();
    final PreparedStatement prepStatementMockUpdateCell = mock(PreparedStatement.class);
    final String sqlStatement = 
        "UPDATE repair_records SET " 
        + IndexToColumnNameTranslator.translateForRepairRecordsTable(2)
        + " = '15.08.2022' WHERE id = 1";
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockUpdateCell);
    
    final boolean actualIsSuccess = dbManager.setRepairRecordCell(1, 2, "15.08.2022");
    final boolean expectedIsSuccess = true;
    assertEquals(expectedIsSuccess, actualIsSuccess);
    
    verify(prepStatementMockUpdateCell).executeUpdate();
  }
  
  @Test
  @DisplayName("getAllRepairRecords returns correct data after cell change")
  void getAllRepairRecordsReturnsCorrectDataAfterCellChange () throws SQLException {
    setUpDbManager();
    final PreparedStatement prepStatementMockUpdateCell = mock(PreparedStatement.class);
    final String expectedCellValue = "16.08.2022";
    final String sqlStatement = 
        "UPDATE repair_records SET " 
        + IndexToColumnNameTranslator.translateForRepairRecordsTable(2)
        + " = '16.08.2022' WHERE id = 1";
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockUpdateCell);
    dbManager.setRepairRecordCell(1, 2, expectedCellValue);
    
    final String actualCellValue = dbManager.getAllRepairRecords().get(1).get(2);
    assertEquals(expectedCellValue, actualCellValue);
  }
  
  @Test
  @DisplayName("setRepairRecordCell not crush on bad connection")
  void setRepairRecordCellNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    final boolean actualIsSuccess =
        dbManagerCorruptedConnection.setRepairRecordCell(1, 2, null);
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("setRepairRecordCell not crush on bad colIndex")
  void setRepairRecordCellNotCrushOnBadColIndex() throws SQLException {
    setUpDbManager();
    final boolean actualIsSuccess = dbManager.setRepairRecordCell(1, -1, "15.08.2022");
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("deleteRepairRecord sends correct request to DB")
  void deleteRepairRecordSendsCorrectRequestToDb() throws SQLException {
    setUpDbManager();
    final String sqlStatement = "DELETE FROM repair_records WHERE id = 1";
    final PreparedStatement prepStatementMockDeleteRecord = mock(PreparedStatement.class);
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockDeleteRecord);
    
    final boolean actualIsSuccess = dbManager.deleteRepairRecord(1);
    final boolean expectedIsSuccess = true;
    assertEquals(expectedIsSuccess, actualIsSuccess);
    
    verify(prepStatementMockDeleteRecord).executeUpdate();
  }
  
  @Test
  @DisplayName("deleteRepairRecord writes deleted row in archive")
  void deleteRepairRecordWritesDeletedRowInArchive() throws SQLException {
    setUpDbManager();
    final String sqlStatement = "DELETE FROM repair_records WHERE id = 1";
    final PreparedStatement prepStatementMockDeleteRecord = mock(PreparedStatement.class);
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockDeleteRecord);
    
    final List<String> insertToArchiveExpectedData = dbManager.getAllRepairRecords().get(1);
    dbManager.deleteRepairRecord(1);
    
    for (int j = 0; j < insertToArchiveExpectedData.size(); j++) {
      if (j == 14) {
        verify(preparedStatementInsertArchiveRow).setString(15, insertToArchiveExpectedData.get(j));
        break;
      }
      verify(
          preparedStatementInsertArchiveRow).setString(j + 1, insertToArchiveExpectedData.get(j));
    }
    verify(preparedStatementInsertArchiveRow).executeUpdate();
  }
  
  @Test
  @DisplayName("deleteRepairRecord not crush on bad connection")
  void deleteRepairRecordNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    final boolean actualIsSuccess = dbManagerCorruptedConnection.deleteRepairRecord(1);
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("getRecordsCount returns correct value")
  void getRecordsCountReturnsCorrectValue() throws SQLException {
    setUpDbManager();
    // After initialization
    assertEquals(1, dbManager.getRecordsCount());
    
    // After insertion
    final List<String> testRow = new ArrayList<>(19);
    testRow.add("ТЭМ");
    testRow.add("0150");
    Stream.generate(() -> "").limit(17).forEach(testRow::add);
    dbManager.insertNewRepairRecord(testRow);
    
    assertEquals(2, dbManager.getRecordsCount());
    
    // After deletion
    final String sqlStatement = "DELETE FROM repair_records WHERE id = 2";
    final PreparedStatement prepStatementMockDeleteRecord = mock(PreparedStatement.class);
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockDeleteRecord);
    dbManager.deleteRepairRecord(2);
    
    assertEquals(1, dbManager.getRecordsCount());
  }
  
  // ============================== Tests for repair periods table ==============================
  
  @Test
  @DisplayName("insertNewModelRepairPeriods sends correct request to DB")
  void insertNewModelRepairPeriodsSendsCorrectRequestToDb() throws SQLException {
    setUpDbManager();
    final List<Integer> testRow = new ArrayList<>(6);
    Stream.iterate(30, n -> n += 30).limit(6).forEach(testRow::add);
    
    final boolean actualIsSuccess = dbManager.insertNewModelRepairPeriods("ТГМ", testRow);
    final boolean expectedIsSuccess = true;
    assertEquals(expectedIsSuccess, actualIsSuccess);
    
    verify(prepStatementMockInsertModelRow).setString(1, "ТГМ");
    for (int j = 0; j < 6; j++) {
      verify(prepStatementMockInsertModelRow).setInt(j + 2, testRow.get(j));
    }
    verify(prepStatementMockInsertModelRow).executeUpdate();
  }
  
  @Test
  @DisplayName("insertNewModelRepairPeriods not crush on bad connection")
  void insertNewModelRepairPeriodsNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    final boolean actualIsSuccess =
        dbManagerCorruptedConnection.insertNewModelRepairPeriods(null, null);
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
 
  @Test
  @DisplayName("getAllRepairPeriodData returns correct data after row insertion")
  void getAllRepairPeriodDataReturnsCorrectDataAfterRowInsertion() throws SQLException {
    setUpDbManager();
    final List<Integer> testRow = new ArrayList<>(6);
    Stream.iterate(30, n -> n += 60).limit(6).forEach(testRow::add);
    dbManager.insertNewModelRepairPeriods("ТГМ", testRow);
    
    assertTrue(dbManager.getAllRepairPeriodData().containsKey("ТГМ"));
    final List<Integer> actualDataAfterInsertion = dbManager.getAllRepairPeriodData().get("ТГМ");
    assertEquals(testRow, actualDataAfterInsertion);
  }
  
  @Test
  @DisplayName("setRepairPeriodCell sends correct request to DB")
  void setRepairPeriodCellSendsCorrectRequestToDb() throws SQLException {
    setUpDbManager();
    final PreparedStatement prepStatementMockUpdateCell = mock(PreparedStatement.class);
    final String sqlStatement = 
        "UPDATE repair_periods SET "
        + IndexToColumnNameTranslator.translateForRepairPeriodsTable(1)
        + " = 30 WHERE loco_model_name = 'ТЭМ'";
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockUpdateCell);
    
    final boolean actualIsSuccess = dbManager.setRepairPeriodCell("ТЭМ", 1, 30);
    final boolean expectedIsSuccess = true;
    assertEquals(expectedIsSuccess, actualIsSuccess);
    
    verify(prepStatementMockUpdateCell).executeUpdate();
  }
  
  @Test
  @DisplayName("getAllRepairPeriodData returns correct data after cell change")
  void getAllRepairPeriodDataReturnsCorrectDataAfterCellChange () throws SQLException {
    setUpDbManager();
    final PreparedStatement prepStatementMockUpdateCell = mock(PreparedStatement.class);
    final int expectedCellValue = 1500;
    final String sqlStatement = 
        "UPDATE repair_periods SET "
        + IndexToColumnNameTranslator.translateForRepairPeriodsTable(5)
        + " = 1500 WHERE loco_model_name = 'ТЭМ'";
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockUpdateCell);
    dbManager.setRepairPeriodCell("ТЭМ", 5, expectedCellValue);
    
    final int actualCellValue = dbManager.getAllRepairPeriodData().get("ТЭМ").get(5);
    assertEquals(expectedCellValue, actualCellValue);
  }
  
  @Test
  @DisplayName("setRepairPeriodCell not crush on bad connection")
  void setRepairPeriodCellNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    final boolean actualIsSuccess =
        dbManagerCorruptedConnection.setRepairPeriodCell(null, 0, 0);
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("setRepairPeriodCell not crush on bad colIndex")
  void setRepairPeriodCellNotCrushOnBadColIndex() throws SQLException {
    setUpDbManager();
    final boolean actualIsSuccess = dbManager.setRepairPeriodCell("ТЭМ", -1, 0);
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("deleteRepairPeriods sends correct request to DB")
  void deleteRepairPeriodsSendsCorrectRequestToDb() throws SQLException {
    setUpDbManager();
    final String sqlStatement = "DELETE FROM repair_periods WHERE loco_model_name = " + "\"ТЭМ\"";
    final PreparedStatement prepStatementMockDeleteRecord = mock(PreparedStatement.class);
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockDeleteRecord);
    
    final boolean actualIsSuccess = dbManager.deleteRepairPeriods("ТЭМ");
    final boolean expectedIsSuccess = true;
    assertEquals(expectedIsSuccess, actualIsSuccess);
    
    verify(prepStatementMockDeleteRecord).executeUpdate();
  }
  
  @Test
  @DisplayName("deleteRepairPeriods not crush on bad connection")
  void deleteRepairPeriodsNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    final boolean actualIsSuccess = dbManagerCorruptedConnection.deleteRepairPeriods(null);
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("getAllModelNames returns correct names")
  void getAllModelNamesReturnsCorrectNames() throws SQLException {
    setUpDbManager();
    final String[] actualNames = dbManager.getAllModelNames();
    assertTrue(actualNames.length == 1);
    assertTrue(actualNames[0].equals("ТЭМ"));
  }
  
  @Test
  @DisplayName("getAllModelNames returns correct names after row insertion and deletion")
  void getAllModelNamesReturnsCorrectNamesAfterRowInsertionAndDeletion() throws SQLException {
    setUpDbManager();
    final List<Integer> testRow = new ArrayList<>(6);
    Stream.generate(() -> 1).limit(6).forEach(testRow::add);
    dbManager.insertNewModelRepairPeriods("ТГМ", testRow);
    
    String[] actualNames = dbManager.getAllModelNames();
    assertTrue(actualNames.length == 2);
    assertTrue(actualNames[0].equals("ТЭМ"));
    assertTrue(actualNames[1].equals("ТГМ"));
    
    final String sqlStatement = "DELETE FROM repair_periods WHERE loco_model_name = " + "\"ТГМ\"";
    final PreparedStatement prepStatementMockDeleteRecord = mock(PreparedStatement.class);
    when(connectionMock.prepareStatement(sqlStatement)).thenReturn(prepStatementMockDeleteRecord);
    dbManager.deleteRepairPeriods("ТГМ");
    
    actualNames = dbManager.getAllModelNames();
    assertTrue(actualNames.length == 1);
    assertTrue(actualNames[0].equals("ТЭМ"));
  }
  
  // ============================== Tests for archive records table ==============================
  
  @Test
  @DisplayName("getAllRecordsArchiveData returns correct data")
  void getAllRecordsArchiveDataReturnsCorrectData() throws SQLException {
    setUpDbManager();
    final List<String> expectedData = new ArrayList<>(15);
    expectedData.add("ТГМ4(Б)");
    expectedData.add("0011");
    Stream.generate(() -> "").limit(13).forEach(expectedData::add);
    
    assertTrue(dbManager.getAllRecordsArchiveData().size() == 1);
    final List<String> actualData = dbManager.getAllRecordsArchiveData().get(0);
    assertEquals(expectedData, actualData);
  }
  
  @Test
  @DisplayName("getAllRecordsArchiveData not crush on bad connection")
  void getAllRecordsArchiveDataNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    dbManagerCorruptedConnection.getAllRecordsArchiveData();
  }
  
  @Test
  @DisplayName("insertNewArchiveRecord not crush on bad connection")
  void insertNewArchiveRecordNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    dbManagerCorruptedConnection.insertNewArchiveRecord(null);
  }
  
  @Test
  @DisplayName("getAllRecordsArchiveData returns correct data after insertion when initialized")
  void getAllRecordsArchiveDataReturnsCorrectDataAfterInsertionWhenInitialized()
      throws SQLException {
    setUpDbManager();
    dbManager.getAllRecordsArchiveData();
    
    final List<String> expectedData = new ArrayList<>(19);
    expectedData.add("ТГМ4У");
    expectedData.add("5412");
    expectedData.add("15.08.2022");
    expectedData.add("14.09.2022");
    Stream.generate(() -> "").limit(15).forEach(expectedData::add);
    
    dbManager.insertNewArchiveRecord(expectedData);
    assertEquals(expectedData, dbManager.getAllRecordsArchiveData().get(1));
  }

  @Test
  @DisplayName("clearArchive sends correct request to DB")
  void clearArchiveSendsCorrectRequestToDb() throws SQLException {
    setUpDbManager();
    final PreparedStatement prepStatementMockClearTable = mock(PreparedStatement.class);
    when(connectionMock
        .prepareStatement("DELETE FROM records_archive;")).thenReturn(prepStatementMockClearTable);
    final boolean actualIsSuccess = dbManager.clearArchive();
    final boolean expectedIsSuccess = true;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("clearArchive not crush on bad connection")
  void clearArchiveNotCrushOnBadConnection() throws SQLException {
    setUpDbManagerWithCorruptedConnection();
    final boolean actualIsSuccess = dbManagerCorruptedConnection.clearArchive();
    final boolean expectedIsSuccess = false;
    assertEquals(expectedIsSuccess, actualIsSuccess);
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() throws SQLException {
    setUpDbManager();
    final String description = dbManager.toString().toLowerCase();
    assertTrue(description.contains("repairrecordstabledata") 
            && description.contains("repairperiodstabledata"));
  }
  
  // ===================================== PREPARE TESTS DATA =====================================
  
  /**
   * Instantiates dbManager with Connection that throws SQLException on any prepareStatement call.
   * @throws SQLException
   */
  private void setUpDbManagerWithCorruptedConnection() throws SQLException {
    corruptedConnectionMock = mock(Connection.class);
    sqliteCorruptedConnectionMock = mock(SqliteConnection.class);
    
    when(sqliteCorruptedConnectionMock.getConnection()).thenReturn(corruptedConnectionMock);
    when(corruptedConnectionMock.prepareStatement(anyString())).thenThrow(SQLException.class);
    
    dbManagerCorruptedConnection = new DbManagerSqliteImp(sqliteCorruptedConnectionMock);
  }
  
  /**
   * Instantiates dbManager for tests.
   * <p>
   * Sets up all mocks that required for dbManager instantiation and some other mocks for
   * interactions with data base connection.
   * <p><ul>
   * New dbManager instance will have:
   * <li>one repair record with
   * <br>
   * {@code id=1}, {@code loco_model_name="ТЭМ"}, {@code loco_number="123"},
   * and other values as empty strings;
   * <br>
   * <li>one repair periods record with
   * <br>
   * {@code loco_model_name="ТЭМ"} and other values as 1;
   * <br>
   * <li>one archive record with
   * <br>
   * {@code loco_model_name="ТГМ4(Б)"}, {@code loco_number="0011"},
   * and other values as empty strings.
   * </ul>
   * @throws SQLException
   */
  private void setUpDbManager() throws SQLException {
    setUpResultSetMocks();
    setUpPreparedStatementMocks();
    setUpConnectionMock();
    
    sqliteConnectionMock = mock(SqliteConnection.class);
    when(sqliteConnectionMock.getConnection()).thenReturn(connectionMock);
    
    dbManager = new DbManagerSqliteImp(sqliteConnectionMock);
  }
  
  private static void setUpPreparedStatementMocks() throws SQLException {
    prepStatementMockVacuum = mock(PreparedStatement.class);
    prepStatementMockFetchPeriodsData = mock(PreparedStatement.class);
    prepStatementMockFetchRecordsData = mock(PreparedStatement.class);
    prepStatementMockFetchArchiveData = mock(PreparedStatement.class);
    prepStatementMockInsertModelRow = mock(PreparedStatement.class);
    prepStatementMockInsertRecordRow = mock(PreparedStatement.class);
    preparedStatementInsertArchiveRow = mock(PreparedStatement.class);
    preparedStatementMockGetMaxId = mock(PreparedStatement.class);
    
    when(prepStatementMockFetchPeriodsData.executeQuery()).thenReturn(resultSetMockPeriodsData);
    when(prepStatementMockFetchRecordsData.executeQuery()).thenReturn(resultSetMockRecordsData);
    when(prepStatementMockFetchArchiveData.executeQuery()).thenReturn(resultSetMockArchiveData);
    when(preparedStatementMockGetMaxId.executeQuery()).thenReturn(resultSetMockGetMaxId);
  }
  
  private static void setUpConnectionMock() throws SQLException {
    connectionMock = mock(Connection.class);
    when(connectionMock.prepareStatement("VACUUM;")).thenReturn(prepStatementMockVacuum);
    when(connectionMock
        .prepareStatement(SqlCommands.PT_ALL_DATA)).thenReturn(prepStatementMockFetchPeriodsData);
    when(connectionMock
        .prepareStatement(SqlCommands.RT_ALL_DATA)).thenReturn(prepStatementMockFetchRecordsData);
    when(connectionMock
        .prepareStatement(SqlCommands.AT_ALL_DATA)).thenReturn(prepStatementMockFetchArchiveData);
    when(connectionMock
        .prepareStatement(SqlCommands.PT_INSERT_ROW)).thenReturn(prepStatementMockInsertModelRow);
    when(connectionMock
        .prepareStatement(SqlCommands.RT_INSERT_ROW)).thenReturn(prepStatementMockInsertRecordRow);
    when(connectionMock
        .prepareStatement(SqlCommands.AT_INSERT_ROW)).thenReturn(preparedStatementInsertArchiveRow);
    when(connectionMock
        .prepareStatement(SqlCommands.RT_MAX_ID)).thenReturn(preparedStatementMockGetMaxId);
  }
  
  private static void setUpResultSetMocks() throws SQLException  {
    setUpResultSetPeriodsDataMock();
    setUpResultSetRecordsDataMock();
    setUpResultSetArchiveDataMock();
    setUpResultSetMockGetMaxId();
  }
  
  private static void setUpResultSetPeriodsDataMock() throws SQLException {
    resultSetMockPeriodsData = mock(ResultSet.class);
    when(resultSetMockPeriodsData.next()).thenReturn(true, false);
    when(resultSetMockPeriodsData.getString(anyString())).thenReturn("ТЭМ");
    when(resultSetMockPeriodsData.getInt(anyString())).thenReturn(1);
  }
  
  private static void setUpResultSetRecordsDataMock() throws SQLException {
    resultSetMockRecordsData = mock(ResultSet.class);
    when(resultSetMockRecordsData.next()).thenReturn(true, false);
    when(resultSetMockRecordsData.getInt(anyString())).thenReturn(1);
    when(resultSetMockRecordsData.getString(anyString())).thenAnswer(s -> {
      final String columnLabel = s.getArgument(0);
      if (columnLabel.equals("loco_model_name")) {
        return "ТЭМ";
      } else if (columnLabel.equals("loco_number")) {
        return "123";
      } else {
        return null;
      } 
    });
  }
  
  private static void setUpResultSetArchiveDataMock() throws SQLException {
    resultSetMockArchiveData = mock(ResultSet.class);
    when(resultSetMockArchiveData.next()).thenReturn(true, false);
    when(resultSetMockArchiveData.getString(anyString())).thenAnswer(s -> {
      final String columnLabel = s.getArgument(0);
      if (columnLabel.equals("loco_model_name")) {
        return "ТГМ4(Б)";
      } else if (columnLabel.equals("loco_number")) {
        return "0011";
      } else {
        return "";
      } 
    });
  }
  
  private static void setUpResultSetMockGetMaxId() throws SQLException {
    resultSetMockGetMaxId = mock(ResultSet.class);
    when(resultSetMockGetMaxId.getInt("id")).thenReturn(2);
  }
  
}
