package gui.utility;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ModelNamesComparatorTest {
  private static ModelNamesComparator modelNamesComparator;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
    modelNamesComparator = new ModelNamesComparator();
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
  @DisplayName("Negative default name case")
  void negativeDefaultNameCase() {
    final int result = modelNamesComparator.compare("ТЭМ", "ТГМ4Бл");
    assertTrue(result < 0);
  }
  
  @Test
  @DisplayName("Positive default name case")
  void positiveDefaultNameCase() {
    final int result = modelNamesComparator.compare("ТГМ4Бл", "ТЭМ");
    assertTrue(result > 0);
  }
  
  @Test
  @DisplayName("Equal default name case")
  void equalDefaultNameCase() {
    final int result = modelNamesComparator.compare("ТЭМ", "ТЭМ");
    assertTrue(result == 0);
  }
  
  @Test
  @DisplayName("Negative custom name case")
  void negativeCustomNameCase() {
    final int result = modelNamesComparator.compare("ТЭМ", "Custom name");
    assertTrue(result < 0);
  }
  
  @Test
  @DisplayName("Positive custom name case")
  void positiveCustomNameCase() {
    final int result = modelNamesComparator.compare("Custom name", "ТЭМ");
    assertTrue(result > 0);
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = modelNamesComparator.toString().toLowerCase();
    assertTrue(description.contains("priority="));
  }

}
