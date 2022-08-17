package gui.eventlisteners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import anton.mankovsky.locomotivedepotlogbook.LocomotiveDepotLogbookApplication;
import gui.GuiManager;
import gui.utility.DialogWindow;

public class ChooseThemeActionTest {
  private static GuiManager guiManagerMock;
  private static DialogWindow dialogWindowMock;
  private static Path uiConfigPath;
  private static String initialString;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    uiConfigPath = LocomotiveDepotLogbookApplication.getUiConfigPath();
    try (final BufferedReader reader =
        Files.newBufferedReader(uiConfigPath, Charset.forName("UTF-8"))) {
      initialString = reader.readLine().trim();
    }
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
    try (final BufferedWriter writer = Files.newBufferedWriter(
        uiConfigPath, Charset.forName("UTF-8"))) {
      writer.write(initialString);
    }
  }

  @BeforeEach
  protected void setUp() throws Exception {
    guiManagerMock = mock(GuiManager.class);
    dialogWindowMock = mock(DialogWindow.class);
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
    
  @Test
  @DisplayName("actionPerformed Light writes correct line in UiConfig file")
  void actionPerformedLightWritesCorrectLineInUiConfigFile() throws IOException {
    final ChooseThemeAction chooseLightThemeAction =
        new ChooseThemeAction("Светлая", guiManagerMock, dialogWindowMock);
    chooseLightThemeAction.actionPerformed(null);
    
    String actualString = null;
    try (final BufferedReader reader =
        Files.newBufferedReader(uiConfigPath, Charset.forName("UTF-8"))) {
      actualString = reader.readLine().trim();
    }
    final String expectedString = "uitheme=light";
    
    assertEquals(expectedString, actualString);
    verify(dialogWindowMock).showInfoMessage(any(), anyString(), anyString());
  }
  
  @Test
  @DisplayName("actionPerformed Dark writes correct line in UiConfig file")
  void actionPerformedDarkWritesCorrectLineInUiConfigFile() throws IOException {
    final ChooseThemeAction chooseDarkThemeAction =
        new ChooseThemeAction("Тёмная", guiManagerMock, dialogWindowMock);
    chooseDarkThemeAction.actionPerformed(null);
    
    String actualString = null;
    try (final BufferedReader reader =
        Files.newBufferedReader(uiConfigPath, Charset.forName("UTF-8"))) {
      actualString = reader.readLine().trim();
    }
    final String expectedString = "uitheme=dark";
    
    assertEquals(expectedString, actualString);
    verify(dialogWindowMock).showInfoMessage(any(), anyString(), anyString());
  }
  
  @Test
  @DisplayName("Cannot create ChooseThemeAction with wrong theme name")
  void cannotCreateChooseThemeActionWithWrongThemeName() throws IOException {
    ChooseThemeAction chooseThemeAction = null;
    try {
      chooseThemeAction =
        new ChooseThemeAction("Unsupported theme", guiManagerMock, dialogWindowMock);
    } catch (final IllegalArgumentException exception) {
        assertNull(chooseThemeAction);
    }
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final ChooseThemeAction chooseLightThemeAction =
        new ChooseThemeAction("Светлая", guiManagerMock, dialogWindowMock);
    final String description = chooseLightThemeAction.toString().toLowerCase();
    assertTrue(description.contains("светлая"));
  }
  
}
