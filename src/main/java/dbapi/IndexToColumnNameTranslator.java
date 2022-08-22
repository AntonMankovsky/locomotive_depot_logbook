package dbapi;

/**
 * Provides static methods to translate indices to corresponding column names.
 */
public abstract class IndexToColumnNameTranslator {
  private static final String LOCO_MODEL_NAME = "loco_model_name";
  private static final String LOCO_NUMBER = "loco_number";
  private static final String LAST_THREE_MAINTENANCE = "last_three_maintenance";
  private static final String NEXT_THREE_MAINTENANCE = "next_three_maintenance";
  private static final String LAST_ONE_CURRENT_REPAIR = "last_one_current_repair";
  private static final String NEXT_ONE_CURRENT_REPAIR = "next_one_current_repair";
  private static final String LAST_TWO_CURRENT_REPAIR = "last_two_current_repair";
  private static final String NEXT_TWO_CURRENT_REPAIR = "next_two_current_repair";
  private static final String LAST_THREE_CURRENT_REPAIR = "last_three_current_repair";
  private static final String NEXT_THREE_CURRENT_REPAIR = "next_three_current_repair";
  private static final String LAST_MEDIUM_REPAIR = "last_medium_repair";
  private static final String NEXT_MEDIUM_REPAIR = "next_medium_repair";
  private static final String LAST_OVERHAUL = "last_overhaul";
  private static final String NEXT_OVERHAUL = "next_overhaul";
  private static final String LAST_REPAIR_TYPE = "last_repair_type";
  private static final String LAST_REPAIR_DATE = "last_repair_date";
  private static final String REQUIRED_REPAIR_TYPE = "required_repair_type";
  private static final String REQUIRED_REPAIR_DATE = "required_repair_date";
  private static final String NOTES = "notes";
  
  private static final String THREE_MAINTENANCE = "three_maintenance";
  private static final String ONE_CURRENT_REPAIR = "one_current_repair";
  private static final String TWO_CURRENT_REPAIR = "two_current_repair";
  private static final String THREE_CURRENT_REPAIR = "three_current_repair";
  private static final String MEDIUM_REPAIR = "medium_repair";
  private static final String OVERHAUL = "overhaul";
  
  /**
   * Converts given index to corresponding column name in {@code repair_records} table.
   * <p>
   * Column {@code id} is not included. Example: for value 0 it will return
   * {@code "loco_model_name"}, and for index 18 it will return {@code "notes"}.
   * @param index of column from list that represents database table columns
   * @return corresponding column name
   * @throws IllegalArgumentException if index is not in range [0:18] inclusive
   */
  public static String translateForRepairRecordsTable(final int index)
                                                                  throws IllegalArgumentException {
    switch (index) {
    case 0:   return LOCO_MODEL_NAME;
    case 1:   return LOCO_NUMBER;
    case 2:   return LAST_THREE_MAINTENANCE;
    case 3:   return NEXT_THREE_MAINTENANCE;
    case 4:   return LAST_ONE_CURRENT_REPAIR;
    case 5:   return NEXT_ONE_CURRENT_REPAIR;
    case 6:   return LAST_TWO_CURRENT_REPAIR;
    case 7:   return NEXT_TWO_CURRENT_REPAIR;
    case 8:   return LAST_THREE_CURRENT_REPAIR;
    case 9:   return NEXT_THREE_CURRENT_REPAIR;
    case 10:  return LAST_MEDIUM_REPAIR;
    case 11:  return NEXT_MEDIUM_REPAIR;
    case 12:  return LAST_OVERHAUL;
    case 13:  return NEXT_OVERHAUL;
    case 14:  return LAST_REPAIR_TYPE;
    case 15:  return LAST_REPAIR_DATE;
    case 16:  return REQUIRED_REPAIR_TYPE;
    case 17:  return REQUIRED_REPAIR_DATE;
    case 18:  return NOTES;
    default:
      throw new IllegalArgumentException("Index " + index + " is out of bounds [0:18].");
    }
  }
  
  /**
   * Converts given index to corresponding column name in {@code repair_periods} table.
   * <p>
   * Column {@code loco_model_name} is not included. Example: for value 0 it will return
   * {@code "three_maintenance"},  and for index 5 it will return {@code "overhaul"}.
   * @param index of column from list that represents database table columns
   * @return corresponding column name
   * @throws IllegalArgumentException if index is not in range [0:5] inclusive
   */
  public static String translateForRepairPeriodsTable(final int index)
                                                                  throws IllegalArgumentException {
    switch (index) {
    case 0: return THREE_MAINTENANCE;
    case 1: return ONE_CURRENT_REPAIR;
    case 2: return TWO_CURRENT_REPAIR;
    case 3: return THREE_CURRENT_REPAIR;
    case 4: return MEDIUM_REPAIR;
    case 5: return OVERHAUL;
    default:
      throw new IllegalArgumentException("Index " + index + " is out of bounds [0:5].");
    }
  }
  
}
