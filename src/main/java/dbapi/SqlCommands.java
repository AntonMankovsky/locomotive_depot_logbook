package dbapi;

public class SqlCommands {
  
  /**
   * Select all rows and columns from periods table.
   */
  public static final String PT_ALL_DATA = "SELECT \n"
        + "    loco_model_name,\n"
        + "    three_maintenance,\n"
        + "    one_current_repair,\n"
        + "    two_current_repair,\n"
        + "    three_current_repair,\n"
        + "    medium_repair,\n"
        + "    overhaul\n"
        + "    FROM repair_periods;";
}
