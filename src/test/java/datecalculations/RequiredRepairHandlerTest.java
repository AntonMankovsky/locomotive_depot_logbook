package datecalculations;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import dbapi.DbManager;

@ExtendWith(MockitoExtension.class)
public class RequiredRepairHandlerTest {
  private static LocalDate today;
  private static RequiredRepairHandler requiredRepairHandler;
  private static DbManager dbManagerMock;
  private static Map<Integer, List<String>> repairRecordsMock;
  private static Map<Integer, Boolean> overdueRepairsMapMock;
  private static List<List<String>> testRecordData;
  private static String[] expectedDate;
  private static String[] expectedRepairType;
  private static boolean[] expectedIsOverdueValue;
  private static int[] expectedCallsNumber;

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
    overdueRepairsMapMock = mock(HashMap.class);
    
    when(repairRecordsMock.get(anyInt())).thenReturn(null);
    when(repairRecordsMock.get(anyInt())).thenAnswer(i -> testRecordData.get(i.getArgument(0)));
    when(dbManagerMock.getAllRepairRecords()).thenReturn(repairRecordsMock);
    when(dbManagerMock.getOverdueRepairsMap()).thenReturn(overdueRepairsMapMock);
    
    requiredRepairHandler = new RequiredRepairHandler(dbManagerMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @ParameterizedTest
  @MethodSource("provideRowIndices")
  @DisplayName("Check correctness of update required repair values")
  void checkCorrectnessOfUpdateRequiredRepairValues(final int rowId) {
    requiredRepairHandler.updateRequiredRepairValues(rowId, today);
    verify(overdueRepairsMapMock).put(rowId, expectedIsOverdueValue[rowId]);
    
    if (expectedCallsNumber[rowId] == 0) {
      verify(dbManagerMock, never()).setRepairRecordCell(anyInt(), anyInt(), anyString());
    } else {
      verify(dbManagerMock).setRepairRecordCell(rowId, 16, expectedRepairType[rowId]);
      verify(dbManagerMock).setRepairRecordCell(rowId, 17, expectedDate[rowId]);
    }
  }
  
  private static Stream<Integer> provideRowIndices() {
    return Stream.iterate(0, i -> ++i).limit(25);
  }
  
  /**
   * Imitates actual data and corresponding 'right answers'.
   * <p>
   * Original functionality depends next repair values and required repair value, so all other
   * indices in list could contain empty strings or nulls (identical data structure, but only
   * needed values).
   */
  /*  Required indices from DB Manager:
   *  next_three_maintenance = 3
      next_one_current_repair = 5
      next_two_current_repair = 7
      next_three_current_repair = 9
      next_medium_repair = 11
      next_overhaul = 13
      required_repair_date = 17
   */
  private static void initTestsData() {
    expectedDate = new String[25];
    expectedRepairType = new String[25];
    expectedIsOverdueValue = new boolean[25];
    expectedCallsNumber = new int[25];
    testRecordData = new ArrayList<>(25);
    today = LocalDate.of(2022, 1, 1);
    
    overdueThreeMaintenanceCase();
    overdueOneCurrentRepairCase();
    overdueTwoCurrentRepairCase();
    overdueThreeCurrentRepairCase();
    overdueMediumRepairCase();
    overdueOverhaulCase();
    
    overdueThreeMaintenanceNoUpdateCase();
    overdueOneCurrentRepairNoUpdateCase();
    overdueTwoCurrentRepairNoUpdateCase();
    overdueThreeCurrentRepairNoUpdateCase();
    overdueMediumRepairNoUpdateCase();
    overdueOverhaulNoUpdateCase();
    
    nextThreeMaintenanceCase();
    nextOneCurrentRepairCase();
    nextTwoCurrentRepairCase();
    nextThreeCurrentRepairCase();
    nextMediumRepairCase();
    nextOverhaulCase();
    
    nextThreeMaintenanceNoUpdateCase();
    nextOneCurrentRepairNoUpdateCase();
    nextTwoCurrentRepairNoUpdateCase();
    nextThreeCurrentRepairNoUpdateCase();
    nextMediumRepairNoUpdateCase();
    nextOverhaulNoUpdateCase();
    
    noRepairsCase();
  }
  
  // =============================== OVERDUE REPAIRS CASES ===============================
  private static void overdueThreeMaintenanceCase() {
    testRecordData.add(Stream.of(
        null, null, null, "31.12.2021", null, "02.01.2022", null, "01.04.2022", null,
        "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "")
         .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[0] = "31.12.2021";
    expectedRepairType[0] = "ТО-3";
    expectedIsOverdueValue[0] = true;
    expectedCallsNumber[0] = 2;
  }
  
  private static void overdueOneCurrentRepairCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "31.12.2021", null, "01.04.2022", null,
            "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[1] = "31.12.2021";
    expectedRepairType[1] = "ТР-1";
    expectedIsOverdueValue[1] = true;
    expectedCallsNumber[1] = 2;
  }
  
  private static void overdueTwoCurrentRepairCase() {
    testRecordData.add(Stream.of(
            null, null, null, null, null, "29.12.2021", null, "31.12.2021", null,
            "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[2] = "31.12.2021";
    expectedRepairType[2] = "ТР-2";
    expectedIsOverdueValue[2] = true;
    expectedCallsNumber[2] = 2;
  }
  
  private static void overdueThreeCurrentRepairCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "29.12.2021", null, "28.12.2021", null,
            "31.12.2021", null, "01.06.2023", null, "01.12.2026", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[3] = "31.12.2021";
    expectedRepairType[3] = "ТР-3";
    expectedIsOverdueValue[3] = true;
    expectedCallsNumber[3] = 2;
  }
  
  private static void overdueMediumRepairCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "29.12.2021", null, "28.12.2021", null,
            "27.12.2021", null, "31.12.2021", null, "01.12.2026", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[4] = "31.12.2021";
    expectedRepairType[4] = "СР";
    expectedIsOverdueValue[4] = true;
    expectedCallsNumber[4] = 2;
  }
  
  private static void overdueOverhaulCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "29.12.2021", null, "28.12.2021", null,
            "27.12.2021", null, "26.12.2021", null, "31.12.2021", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[5] = "31.12.2021";
    expectedRepairType[5] = "КР";
    expectedIsOverdueValue[5] = true;
    expectedCallsNumber[5] = 2;
  }
  
  private static void overdueThreeMaintenanceNoUpdateCase() {
    testRecordData.add(Stream.of(
        null, null, null, "31.12.2021", null, "02.01.2022", null, "01.04.2022", null,
        "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "31.12.2021")
         .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[6] = "31.12.2021";
    expectedRepairType[6] = "ТО-3";
    expectedIsOverdueValue[6] = true;
    expectedCallsNumber[6] = 0;
  }
  
  private static void overdueOneCurrentRepairNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "31.12.2021", null, "01.04.2022", null,
            "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "31.12.2021")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[7] = "31.12.2021";
    expectedRepairType[7] = "ТР-1";
    expectedIsOverdueValue[7] = true;
    expectedCallsNumber[7] = 0;
  }
  
  private static void overdueTwoCurrentRepairNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "29.12.2021", null, "31.12.2021", null,
            "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "31.12.2021")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[8] = "31.12.2021";
    expectedRepairType[8] = "ТР-2";
    expectedIsOverdueValue[8] = true;
    expectedCallsNumber[8] = 0;
  }
  
  private static void overdueThreeCurrentRepairNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "29.12.2021", null, "28.12.2021", null,
            "31.12.2021", null, "01.06.2023", null, "01.12.2026", null, null, null, "31.12.2021")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[9] = "31.12.2021";
    expectedRepairType[9] = "ТР-3";
    expectedIsOverdueValue[9] = true;
    expectedCallsNumber[9] = 0;
  }
  
  private static void overdueMediumRepairNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "29.12.2021", null, "28.12.2021", null,
            "27.12.2021", null, "31.12.2021", null, "01.12.2026", null, null, null, "31.12.2021")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[10] = "31.12.2021";
    expectedRepairType[10] = "СР";
    expectedIsOverdueValue[10] = true;
    expectedCallsNumber[10] = 0;
  }
  
  private static void overdueOverhaulNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "30.12.2021", null, "29.12.2021", null, "28.12.2021", null,
            "27.12.2021", null, "26.12.2021", null, "31.12.2021", null, null, null, "31.12.2021")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[11] = "31.12.2021";
    expectedRepairType[11] = "КР";
    expectedIsOverdueValue[11] = true;
    expectedCallsNumber[11] = 0;
  }
  
  // =============================== NEXT REPAIRS CASES ===============================
  private static void nextThreeMaintenanceCase() {
    testRecordData.add(Stream.of(
        null, null, null, "08.01.2022", null, "02.02.2022", null, "01.04.2022", null,
        "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "")
         .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[12] = "08.01.2022";
    expectedRepairType[12] = "ТО-3";
    expectedIsOverdueValue[12] = false;
    expectedCallsNumber[12] = 2;
  }
  
  private static void nextOneCurrentRepairCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "09.01.2022", null, "01.04.2022", null,
            "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[13] = "09.01.2022";
    expectedRepairType[13] = "ТР-1";
    expectedIsOverdueValue[13] = false;
    expectedCallsNumber[13] = 2;
  }
  
  private static void nextTwoCurrentRepairCase() {
    testRecordData.add(Stream.of(
            null, null, null, null, null, "09.01.2022", null, "10.01.2022", null,
            "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[14] = "10.01.2022";
    expectedRepairType[14] = "ТР-2";
    expectedIsOverdueValue[14] = false;
    expectedCallsNumber[14] = 2;
  }
  
  private static void nextThreeCurrentRepairCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "09.01.2022", null, "10.01.2022", null,
            "11.01.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[15] = "11.01.2022";
    expectedRepairType[15] = "ТР-3";
    expectedIsOverdueValue[15] = false;
    expectedCallsNumber[15] = 2;
  }
  
  private static void nextMediumRepairCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "09.01.2022", null, "10.01.2022", null,
            "11.01.2022", null, "12.01.2022", null, "01.12.2026", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[16] = "12.01.2022";
    expectedRepairType[16] = "СР";
    expectedIsOverdueValue[16] = false;
    expectedCallsNumber[16] = 2;
  }
  
  private static void nextOverhaulCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "09.01.2022", null, "10.01.2022", null,
            "11.01.2022", null, "12.01.2022", null, "13.01.2022", null, null, null, "")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[17] = "13.01.2022";
    expectedRepairType[17] = "КР";
    expectedIsOverdueValue[17] = false;
    expectedCallsNumber[17] = 2;
  }
  
  private static void nextThreeMaintenanceNoUpdateCase() {
    testRecordData.add(Stream.of(
        null, null, null, "08.01.2022", null, "02.02.2022", null, "01.04.2022", null,
        "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "08.01.2022")
         .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[18] = "08.01.2022";
    expectedRepairType[18] = "ТО-3";
    expectedIsOverdueValue[18] = false;
    expectedCallsNumber[18] = 0;
  }
  
  private static void nextOneCurrentRepairNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "07.01.2022", null, "01.04.2022", null,
            "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "07.01.2022")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[19] = "07.01.2022";
    expectedRepairType[19] = "ТР-1";
    expectedIsOverdueValue[19] = false;
    expectedCallsNumber[19] = 0;
  }
  
  private static void nextTwoCurrentRepairNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "09.01.2022", null, "06.01.2022", null,
            "01.11.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "06.01.2022")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[20] = "06.01.2022";
    expectedRepairType[20] = "ТР-2";
    expectedIsOverdueValue[20] = false;
    expectedCallsNumber[20] = 0;
  }
  
  private static void nextThreeCurrentRepairNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "09.01.2022", null, "10.01.2022", null,
            "05.01.2022", null, "01.06.2023", null, "01.12.2026", null, null, null, "05.01.2022")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[21] = "05.01.2022";
    expectedRepairType[21] = "ТР-3";
    expectedIsOverdueValue[21] = false;
    expectedCallsNumber[21] = 0;
  }
  
  private static void nextMediumRepairNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "09.01.2022", null, "10.01.2022", null,
            "11.01.2022", null, "04.01.2022", null, "01.12.2026", null, null, null, "04.01.2022")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[22] = "04.01.2022";
    expectedRepairType[22] = "СР";
    expectedIsOverdueValue[22] = false;
    expectedCallsNumber[22] = 0;
  }
  
  private static void nextOverhaulNoUpdateCase() {
    testRecordData.add(Stream.of(
            null, null, null, "08.01.2022", null, "09.01.2022", null, "10.01.2022", null,
            "11.01.2022", null, "12.01.2022", null, "03.01.2022", null, null, null, "03.01.2022")
             .collect(Collectors.toCollection(ArrayList::new)));
    
    expectedDate[23] = "03.01.2022";
    expectedRepairType[23] = "КР";
    expectedIsOverdueValue[23] = false;
    expectedCallsNumber[23] = 0;
  }
  
  // =============================== NO REPAIRS CASE ===============================
  private static void noRepairsCase() {
    testRecordData.add(Stream.of(
        null, null, null, "", null, null, null, null, null,
        null, null, null, null, null, null, null, null, null)
         .collect(Collectors.toCollection(ArrayList::new)));

    expectedDate[24] = "";
    expectedRepairType[24] = "";
    expectedIsOverdueValue[24] = false;
    expectedCallsNumber[24] = 0;
  }

}
