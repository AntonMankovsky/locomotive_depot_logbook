package dbapi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SqliteConnectionTest {
  private static SqliteConnection sqliteConnection;

  @BeforeAll
  protected static void setUpBeforeClass() throws Exception {
  }

  @AfterAll
  protected static void tearDownAfterClass() throws Exception {
  }

  @BeforeEach
  protected void setUp() throws Exception {
    sqliteConnection = new SqliteConnection();
  }

  @AfterEach
  protected void tearDown() throws Exception {
  }
  
  @Test
  @DisplayName("Provided connection is not null")
  void providedConnectionIsNotNull() throws SQLException {
    assertNotNull(sqliteConnection.getConnection()); 
  }
  
  @Test
  @DisplayName("Check toString is overriden")
  void checkToStringIsOverriden() {
    final String description = sqliteConnection.toString().toLowerCase();
    assertTrue(description.contains("wrapper") && description.contains("connection"));
  }

}
