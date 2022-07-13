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
  
  /**
   * Select all rows and columns from records table.
   */
  public static final String RT_ALL_DATA = "SELECT\n"
      + "    repair_records.id,\n"
      + "    repair_periods.loco_model_name,\n"
      + "    repair_records.loco_number,\n"
      + "    repair_records.last_three_maintenance,\n"
      + "    repair_records.next_three_maintenance,\n"
      + "    repair_records.last_three_current_repair,\n"
      + "    repair_records.next_three_current_repair,\n"
      + "    repair_records.last_two_current_repair,\n"
      + "    repair_records.next_two_current_repair,\n"
      + "    repair_records.last_one_current_repair,\n"
      + "    repair_records.next_one_current_repair,\n"
      + "    repair_records.last_medium_repair,\n"
      + "    repair_records.next_medium_repair,\n"
      + "    repair_records.last_overhaul,\n"
      + "    repair_records.next_overhaul,\n"
      + "    repair_records.notes\n"
      + "    FROM repair_records, repair_periods\n"
      + "    WHERE repair_periods.loco_model_name=repair_records.loco_model_name;";
}
