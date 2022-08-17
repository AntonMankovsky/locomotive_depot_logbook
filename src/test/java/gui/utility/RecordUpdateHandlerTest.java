package gui.utility;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import datavalidation.InputValidator;
import datecalculations.DateCalculationsHandler;
import datecalculations.LastRepairHandler;
import dbapi.DbManager;
import gui.GuiManager;

public class RecordUpdateHandlerTest {
  private static RecordUpdateHandler updateHandler;
  private static GuiManager guiManagerMock;
  private static DbManager dbManagerMock;
  private static DateCalculationsHandler dateCalculationsHandlerMock;
  private static LastRepairHandler lastRepairHandlerMock;
  private static InputValidator validatorMock;
  private static DialogWindow dialogWindowMock;
  private static int rowIndex;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
    guiManagerMock = mock(GuiManager.class);
    dbManagerMock = mock(DbManager.class);
    dateCalculationsHandlerMock = mock(DateCalculationsHandler.class);
    lastRepairHandlerMock = mock(LastRepairHandler.class);
    validatorMock = mock(InputValidator.class);
    dialogWindowMock = mock(DialogWindow.class);
    
    when(dbManagerMock.getIdByOrdinalNumber(anyInt())).thenAnswer(i -> i.getArgument(0));
    
    updateHandler = new RecordUpdateHandler(dbManagerMock,
                                            guiManagerMock,
                                            dateCalculationsHandlerMock,
                                            lastRepairHandlerMock,
                                            validatorMock,
                                            dialogWindowMock);
    
    rowIndex = (int) (Math.random() * 100);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @ParameterizedTest
  @MethodSource("provideRepairsColumnIndices")
  @DisplayName("handleCellNewValue calls lastRepairHandler")
  void handleCellNewValueCallsLastRepairHandler(final int colIndex) {
    setDbManagerReturnsTrue();
    updateHandler.handleCellNewValue("", rowIndex, colIndex);

    verify(lastRepairHandlerMock).updateLastRepairColumn(rowIndex);
  }
  
  @ParameterizedTest
  @MethodSource("provideRepairsColumnIndices")
  @DisplayName("handleCellNewValue calls dateCalculationsHandler on date value")
  void handleCellNewValueCallsDateCalculationsHandlerOnDateValue(final int colIndex) {
    setDbManagerReturnsTrue();
    updateHandler.handleCellNewValue("16.08.2022", rowIndex, colIndex);
    
    verify(dateCalculationsHandlerMock).handleDateCalculations("16.08.2022", rowIndex, colIndex);
  }

  @ParameterizedTest
  @MethodSource("provideRepairsColumnIndices")
  @DisplayName("handleCellNewValue not calls dateCalculationsHandler on non-date value")
  void handleCellNewValueNotCallsDateCalculationsHandlerOnNonDateValue(final int colIndex) {
    setDbManagerReturnsTrue();
    updateHandler.handleCellNewValue("", rowIndex, colIndex);
    updateHandler.handleCellNewValue(null, rowIndex, colIndex);
    
    verify(dateCalculationsHandlerMock, never())
          .handleDateCalculations(anyString(), anyInt(), anyInt());
  }
  
  @ParameterizedTest
  @MethodSource("provideRepairsColumnIndices")
  @DisplayName("handleCellNewValue not calls handlers if value not written to DB")
  void handleCellNewValueNotCallsHandlersIfValueNotWrittenToDb(final int colIndex) {
    updateHandler.handleCellNewValue("16.08.2022", rowIndex, colIndex);
    
    verify(lastRepairHandlerMock, never()).updateLastRepairColumn(anyInt());
    verify(dateCalculationsHandlerMock, never())
          .handleDateCalculations(any(), anyInt(), anyInt());
    
    verify(dialogWindowMock).showErrorMessage(any(), any(), any());
  }
  
  @ParameterizedTest
  @MethodSource("provideRepairsColumnIndices")
  @DisplayName("handleCellNewValue calls setRepairRecordCell on valid value")
  void handleCellNewValueCallsSetRepairRecordCellOnValidValue(final int colIndex) {
    updateHandler.handleCellNewValue("17.08.2022", rowIndex, colIndex);
    
    verify(dbManagerMock).setRepairRecordCell(rowIndex / 2, colIndex * 2 - 2, "17.08.2022");
  }
  
  @ParameterizedTest
  @MethodSource("provideRepairsColumnIndices")
  @DisplayName("handleCellNewValue not calls dbManager on invalid date value")
  void handleCellNewValueNotCallsDbManagerOnInvalidDateValue(final int colIndex) {
    setValidatorThrowsException();
    updateHandler.handleCellNewValue("32.01.2000", rowIndex, colIndex);
    
    verify(dbManagerMock, never()).setRepairRecordCell(anyInt(), anyInt(), any());
    verify(dialogWindowMock).showErrorMessage(any(), any(), any());
  }
  
  @Test
  @DisplayName("handleCellNewValue calls setRepairRecordCell on valid loco number")
  void handleCellNewValueCallsSetRepairRecordCellOnValidLocoNumber() {
    setDbManagerReturnsTrue();
    updateHandler.handleCellNewValue("123", rowIndex, 1);
    
    verify(dbManagerMock).setRepairRecordCell(rowIndex / 2, 1, "123");
  }
  
  @Test
  @DisplayName("handleCellNewValue notifies user if loco number not written to DB")
  void handleCellNewValueNotifiesUserIfLocoNumberNotWrittenToDb() {
    updateHandler.handleCellNewValue("123", rowIndex, 1);
    
    verify(dialogWindowMock).showErrorMessage(any(), any(), any());
  }
  
  @Test
  @DisplayName("handleCellNewValue notifies user on invalid loco number")
  void handleCellNewValueNotifiesUserOnInvalidLocoNumber() {
    setValidatorThrowsException();
    updateHandler.handleCellNewValue("123", rowIndex, 1);
    
    verify(dialogWindowMock).showErrorMessage(any(), any(), any());
  }
  
  @Test
  @DisplayName("handleCellNewValue calls setRepairRecordCell on notes value")
  void handleCellNewValueCallsSetRepairRecordCellOnNotesValue() {
    setDbManagerReturnsTrue();
    updateHandler.handleCellNewValue("Some notes.", rowIndex, 10);
    
    verify(dbManagerMock).setRepairRecordCell(rowIndex / 2, 18, "Some notes.");
  }
  
  @Test
  @DisplayName("handleCellNewValue notifies user if notes not written to DB")
  void handleCellNewValueNotifiesUserIfNotesNotWrittenToDb() {
    updateHandler.handleCellNewValue("Some notes.", rowIndex, 10);
    System.out.println(updateHandler);
    verify(dialogWindowMock).showErrorMessage(any(), any(), any());
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = updateHandler.toString().toLowerCase();
    assertTrue(description.contains("dbmanager="));
  }
  
  private static Stream<Integer> provideRepairsColumnIndices(){
    return Stream.iterate(2, i -> ++i).limit(6);
  }
  
  /**
   * Sets up dbManager mock to return true on {@code setRepairRecordCell} method call.
   */
  private static void setDbManagerReturnsTrue() {
    when(dbManagerMock.setRepairRecordCell(anyInt(), anyInt(), any())).thenReturn(true);
  }
  
  /**
   * Sets up validator mock to throw IllegalArgumentException on validation methods calls.
   */
  private static void setValidatorThrowsException() {
    doThrow(new IllegalArgumentException()).when(validatorMock).validateRepairDate(any());
    doThrow(new IllegalArgumentException()).when(validatorMock).validateLocoNumber(any());
  }

}