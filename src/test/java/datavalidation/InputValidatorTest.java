package datavalidation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbapi.DbManager;

public class InputValidatorTest {
  private static InputValidator inputValidator;
  private static DbManager dbManagerMock;
  private static String[] testModelNames = {"ТЭМ", "ТГМ4(Б)", "ТЭМ2У"};

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
    dbManagerMock = mock(DbManager.class);
    inputValidator = new InputValidator(dbManagerMock);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @ParameterizedTest
  @MethodSource("provideBadLocoNumbers")
  @DisplayName("Throws exception on bad loco number")
  void throwsExceptionOnBadLocoNumber(final String badLocoNumber) {
    assertThrows(IllegalArgumentException.class,
                 () -> inputValidator.validateLocoNumber(badLocoNumber));
  }
  
  @ParameterizedTest
  @MethodSource("provideGoodLocoNumbers")
  @DisplayName("Throws no exception on good loco number")
  void throwsNoExceptionOnGoodLocoNumber(final String goodLocoNumber) {
    inputValidator.validateLocoNumber(goodLocoNumber);
  }
  
  @ParameterizedTest
  @MethodSource("provideBadRepairDates")
  @DisplayName("Throws exception on bad repair date")
  void throwsExceptionOnBadRepairDate(final String badRepairDate) {
    assertThrows(IllegalArgumentException.class,
                 () -> inputValidator.validateRepairDate(badRepairDate));
  }
  
  @ParameterizedTest
  @MethodSource("provideGoodRepairDates")
  @DisplayName("Throws no exception on good repair date")
  void throwsNoExceptionOnGoodRepairDate(final String goodRepairDate) {
    inputValidator.validateRepairDate(goodRepairDate);
  }
  
  @ParameterizedTest
  @MethodSource("provideBadRepairPeriods")
  @DisplayName("Throws exception on bad repair period")
  void throwsExceptionOnBadRepairPeriod(final int badRepairPeriod) {
    assertThrows(IllegalArgumentException.class,
                 () -> inputValidator.validateRepairPeriod(badRepairPeriod));
  }
  
  @ParameterizedTest
  @MethodSource("provideGoodRepairPeriods")
  @DisplayName("Throws no exception on bad repair period")
  void throwsNoExceptionOnBadRepairPeriod(final int goodRepairPeriod) {
    inputValidator.validateRepairPeriod(goodRepairPeriod);
  }
  
  @ParameterizedTest
  @MethodSource("provideBadModelNames")
  @DisplayName("Throws exception on bad model name")
  void throwsExceptionOnBadModelName(final String badModelName) {
    when(dbManagerMock.getAllModelNames()).thenReturn(testModelNames);
    assertThrows(IllegalArgumentException.class,
                 () -> inputValidator.validateRepairPeriodsModelName(badModelName));
  }
  
  @ParameterizedTest
  @MethodSource("provideGoodModelNames")
  @DisplayName("Throws no exception on good model name")
  void throwsNoExceptionOnGoodModelName(final String goodModelName) {
    when(dbManagerMock.getAllModelNames()).thenReturn(testModelNames);
    inputValidator.validateRepairPeriodsModelName(goodModelName);
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = inputValidator.toString().toLowerCase();
    assertTrue(description.contains("data") && description.contains("validation"));
  }
  
  private static Stream<String> provideBadLocoNumbers() {
    return Stream.of(null, "", "abc", "1a", "-s0", "abc0", "1/2", "12 56", "123-123", "/", "1'2");
  }
  
  private static Stream<String> provideGoodLocoNumbers() {
    return Stream.of(" 000", "0123 ", " 65100 ", "4856");
  }
  
  private static Stream<String> provideBadRepairDates() {
    return Stream.of("14/08/2022", "14.08/2022", "14/08/2022", "2022.08.08", "0.00.2000",
                     "01. 01.2022", "32.01.2022", "01.13.2022", "/-'", "10102010", "0-.05.2030");
  }
  
  private static Stream<String> provideGoodRepairDates() {
    return Stream.of(null, "", " 14.08.2022", "01.01.1960 ", " 31.12.2050 ", "01.01.2022");
  }
  
  private static Stream<Integer> provideBadRepairPeriods() {
    return Stream.of(Integer.MIN_VALUE, Integer.MIN_VALUE / 2, 0);
  }
  
  private static Stream<Integer> provideGoodRepairPeriods() {
    return Stream.of(1, Integer.MAX_VALUE / 2, Integer.MAX_VALUE);
  }
  
  private static Stream<String> provideBadModelNames() {
    return Stream.concat(Stream.of(null, "", "ABC\\", "A1'Z", "\"LOC99\"", "\\", " ", "'", "\""),
           Arrays.asList(testModelNames).stream().map(s -> ((String) s)));
  }
  
  private static Stream<String> provideGoodModelNames(){
    return Stream.of(" ABC", "123 ", " ABC123 ", "name(test123)");
  }
  
}
