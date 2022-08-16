package dbapi;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class IndexToColumnNameTranslatorTest {

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
  
  @ParameterizedTest
  @MethodSource("provideRepairRecordsValidIndices")
  @DisplayName("Translator returns valid strings on valid repair record column indices")
  void translatorReturnsValidStringsOnValidRepairRecordColumnIndices(final int index) {
   final String columnName = IndexToColumnNameTranslator.translateForRepairRecordsTable(index);
   assertTrue(columnName != null && !columnName.equals(""));
  }
  
  @ParameterizedTest
  @MethodSource("provideRepairPeriodsValidIndices")
  @DisplayName("Translator returns valid strings on valid repair period column indices")
  void translatorReturnsValidStringsOnValidRepairPeriodColumnIndices(final int index) {
   final String columnName = IndexToColumnNameTranslator.translateForRepairPeriodsTable(index);
   assertTrue(columnName != null && !columnName.equals(""));
  }
  
  @ParameterizedTest
  @MethodSource("provideRepairRecordsInvalidIndices")
  @DisplayName("Translator throws exception on invalid repair record column indices")
  void translatorThrowsExceptionOnInvalidRepairRecordColumnIndices(final int index) {
    assertThrows(IllegalArgumentException.class,
        () -> IndexToColumnNameTranslator.translateForRepairRecordsTable(index));
  }
  
  @ParameterizedTest
  @MethodSource("provideRepairPeriodsInvalidIndices")
  @DisplayName("Translator throws exception on invalid repair period column indices")
  void translatorThrowsExceptionOnInvalidRepairPeriodColumnIndices(final int index) {
    assertThrows(IllegalArgumentException.class,
        () -> IndexToColumnNameTranslator.translateForRepairPeriodsTable(index));
  }
  
  private static Stream<Integer> provideRepairRecordsValidIndices(){
    return Stream.iterate(0, n -> ++n).limit(19);
  }
  
  private static Stream<Integer> provideRepairPeriodsValidIndices(){
    return Stream.iterate(0, n -> ++n).limit(6);
  }
  
  private static Stream<Integer> provideRepairRecordsInvalidIndices(){
    return Stream.of(Integer.MIN_VALUE, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, -1, 19);
  }
  
  private static Stream<Integer> provideRepairPeriodsInvalidIndices(){
    return Stream.of(Integer.MIN_VALUE, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, -1, 6);
  }

}
