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
      + "    repair_records.last_repair_type,\n"
      + "    repair_records.last_repair_date,\n"
      + "    repair_records.required_repair_type,\n"
      + "    repair_records.required_repair_date,\n"
      + "    repair_records.notes\n"
      + "    FROM repair_records, repair_periods\n"
      + "    WHERE repair_periods.loco_model_name=repair_records.loco_model_name;";
  
  /**
   * Get max id from records table.
   */
  public static final String RT_MAX_ID = "SELECT *\n"
      + "    FROM repair_records\n"
      + "    ORDER BY id DESC\n"
      + "    LIMIT 1;";
  
  /**
   * Insert new row into repair_records table.
   */
  public static final String RT_INSERT_ROW = "INSERT INTO repair_records (\n"
      + "    loco_model_name,\n"
      + "    loco_number,\n"
      + "    last_three_maintenance,\n"
      + "    next_three_maintenance,\n"
      + "    last_three_current_repair,\n"
      + "    next_three_current_repair,\n"
      + "    last_two_current_repair,\n"
      + "    next_two_current_repair,\n"
      + "    last_one_current_repair,\n"
      + "    next_one_current_repair,\n"
      + "    last_medium_repair,\n"
      + "    next_medium_repair,\n"
      + "    last_overhaul,\n"
      + "    next_overhaul,\n"
      + "    last_repair_type,\n"
      + "    last_repair_date,\n"
      + "    required_repair_type,\n"
      + "    required_repair_date,\n"
      + "    notes\n"
      + "    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
  
  /**
   * Insert new row into repair_periods table.
   */
  public static final String PT_INSERT_ROW = "INSERT INTO repair_periods (\n"
      + "    loco_model_name,\n"
      + "    three_maintenance,\n"
      + "    one_current_repair,\n"
      + "    two_current_repair,\n"
      + "    three_current_repair,\n"
      + "    medium_repair,\n"
      + "    overhaul\n"
      + "    ) VALUES (?, ?, ?, ?, ?, ?, ?);";
  
  /**
   * Select all rows and columns (except id) from archive table.
   */
  public static final String AT_ALL_DATA = "SELECT\n"
      + "    loco_model_name,\n"
      + "    loco_number,\n"
      + "    last_three_maintenance,\n"
      + "    next_three_maintenance,\n"
      + "    last_three_current_repair,\n"
      + "    next_three_current_repair,\n"
      + "    last_two_current_repair,\n"
      + "    next_two_current_repair,\n"
      + "    last_one_current_repair,\n"
      + "    next_one_current_repair,\n"
      + "    last_medium_repair,\n"
      + "    next_medium_repair,\n"
      + "    last_overhaul,\n"
      + "    next_overhaul,\n"
      + "    notes\n"
      + "    FROM records_archive\n;";
  
  /**
   * Insert new row into repair_records table.
   */
  public static final String AT_INSERT_ROW = "INSERT INTO records_archive (\n"
      + "    loco_model_name,\n"
      + "    loco_number,\n"
      + "    last_three_maintenance,\n"
      + "    next_three_maintenance,\n"
      + "    last_three_current_repair,\n"
      + "    next_three_current_repair,\n"
      + "    last_two_current_repair,\n"
      + "    next_two_current_repair,\n"
      + "    last_one_current_repair,\n"
      + "    next_one_current_repair,\n"
      + "    last_medium_repair,\n"
      + "    next_medium_repair,\n"
      + "    last_overhaul,\n"
      + "    next_overhaul,\n"
      + "    notes\n"
      + "    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

}
