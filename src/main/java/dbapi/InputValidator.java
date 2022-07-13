package dbapi;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Validates data before it goes into the database. 
 */
public class InputValidator {
  private static final Logger logger = LogManager.getLogger();
  private final DbManager dbManager;
  
  /**
   * Contains methods for validate data.
   */
  public InputValidator(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
  }

  /**
   * Validates repair record.
   * <p>
   * {@code loco_model_name} (index 0) should have corresponding entity in 
   * {@code repair_periods table.}
   * <br>
   * {@code loco_number} (index 1) may not be null, empty, or contain anything except digits.
   * <br>
   * Other elements, except {@code notes} (index 14, the last), could be null, empty, or match
   * format {@code dd.MM.yyyy} with correct date. 
   * <br>
   * Any date of next repair should always be
   * bigger than date of corresponding last repair.
   * <br>
   * If any string in next/last repair pair contains date, the other one should contain date too.
   * @param record to validate
   * @throws IllegalArgumentException if validation fails
   */
  public void validateRepairRecord(final List<String> record) throws IllegalArgumentException {
    validateRepairRecordModelName(record.get(0));
    validateLocoNumber(record.get(1));
  }
  
  /**
   * Validates model name for repair records table.
   * @param modelName to validate
   * @throws IllegalArgumentException if models table does not contain such model name.
   */
  public void validateRepairRecordModelName(final String modelName) throws 
                                                                        IllegalArgumentException {
    for (String validName : dbManager.getAllModelNames()) {
      if (modelName.equals(validName)) {
        logger.info(modelName + " passed model name validation.");
        return;
      }
    }
    final String logString = modelName + " failed model name validation:\n"
        + " DbManager`s model name list does not contain such model name.";
    logger.warn(logString);
    throw new IllegalArgumentException("No match found for such model name: " + modelName);
  }
  
  /**
   * Validates locomotive number for repair records table.
   * @param locoNumber to validate
   * @throws IllegalArgumentException when number is null or contains anything except digits
   */
  public void validateLocoNumber(final String locoNumber) throws IllegalArgumentException {
    Pattern pattern = Pattern.compile("[0-9]+");
    if (locoNumber == null || !pattern.matcher(locoNumber).matches()) {
      final String logString = locoNumber + " failed locomotive number validation:\n"
          + " it may not be null and should cointain only digits (one or more).";
      logger.warn(logString);
      throw new IllegalArgumentException("Invalid locomotive number: " + locoNumber);
    } else {
      logger.info(locoNumber + " passed locomotive number validation.");
    }
  }

  @Override
  public String toString() {
    return "InputValidator object - container for methods with input data validation.";
  }
  
}
