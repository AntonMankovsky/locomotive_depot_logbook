package dbapi;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

/**
 * Wrapper class for JDBC connection which allows to mock it for unit tests.
 */
@Component
public class SqliteConnection {
  private static final Path PATH = Paths.get(".", "src", "main", "resources", "db", "app.db");
  private static final String CONNECTION_URL = "jdbc:sqlite:" + PATH;
  private Connection connection;
  
  /**
   * Object that provides access to connection with SQlite database.
   */
  public SqliteConnection() {
    super();
  }

  /**
   * Returns connection with SQlite database.
   * @return a connection to the database.
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {
    connection = DriverManager.getConnection(CONNECTION_URL);
    return connection;
  }

  @Override
  public String toString() {
    return "Wrapper class for JDBC connection which allows to mock it for unit tests.\n";
  }
}
