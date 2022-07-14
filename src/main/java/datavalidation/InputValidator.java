package datavalidation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import dbapi.DbManager;

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
   * {@code loco_model_name} (index 0) should have corresponding model name in 
   * {@code repair_periods table} and may not be null. 
   * <br>
   * {@code loco_number} (index 1) may not be null, empty, or contain anything except digits.
   * <br>
   * Other elements, except {@code notes} (index 14, the last), could be null, empty, or match
   * format {@code dd.MM.yyyy} with correct date. 
   * <br>
   * Any date of next repair should always be bigger than date of corresponding last repair.
   * <br>
   * If any string in next/last repair pair contains date, the other one should contain date too.
   * @param record to validate
   * @throws IllegalArgumentException if validation fails
   */
  public void validateRepairRecord(final List<String> record) throws IllegalArgumentException {
    validateRepairRecordModelName(record.get(0));
    validateLocoNumber(record.get(1));
    for (int j = 2; j < record.size() - 2; j++) {
      validateRepairDate(record.get(j));
    }
    for (int j = 2; j < record.size() - 3; j+=2) {
      validateLastNextRepairPair(record.get(j), record.get(j + 1));
    }
  }
  
  /**
   * Validates model name for repair records table.
   * @param modelName to validate
   * @throws IllegalArgumentException if models table does not contain such model name.
   */
  public void validateRepairRecordModelName(final String modelName) throws 
                                                                        IllegalArgumentException {
    if (modelName == null) {
      logger.warn(modelName + " failed model name validation: it may not be NULL");
      throw new IllegalArgumentException("Invalid locomotive model name: it have to be not NULL");
    }
    
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
  
  /**
   * Validates repair date.
   * <br>
   * Could be null, empty, or match format {@code dd.MM.yyyy} with correct date.
   * Otherwise validation fails.
   * @param date to validate
   * @throws IllegalArgumentException if contains any characters while violates pattern dd.MM.yyyy
   */
  public void validateRepairDate(final String date) throws IllegalArgumentException {
    if (date == null || date.equals("")) {
      logger.info("Repair date passed validation cause nulls and empty strings are allowed.");
      return;
    }
    
    Pattern pattern = Pattern.compile("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}");
    if (!pattern.matcher(date).matches()) {
      logger.warn(date + " failed validation due to violation of required date format: dd.MM.yyyy");
      throw new IllegalArgumentException("Invalid date format: " + date + ". dd.MM.yyyy expected.");
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      try {
        LocalDate.parse(date, formatter);
      } catch (final DateTimeParseException e) {
        logger.warn(date + " failed validation due to invalid value: " + e.getMessage());
        throw new IllegalArgumentException("Invalid date value: " + e.getMessage());
      }
    }
    
    logger.info("Repair date " + date + " passed the validation.");
  }
  
  /**
   * Validates pair of strings: dates of last and next repairs.
   * <p>
   * Expects that given dates passed repair date validation and belongs to a single type of repair.
   * <b>
   * If any string in repair pair contains date, the other one should contain date too.
   * <br>
   * Date of next repair should always be bigger than date of corresponding last repair.
   * @param lastRepair to validate with {@code nextRepair}
   * @param nextRepair to validate with {@code lastRepair}
   * @throws IllegalArgumentException when described validation rules are violated
   */
  public void validateLastNextRepairPair(final String lastRepair, final String nextRepair) throws
                                                                        IllegalArgumentException {
    
    final boolean lastEmpty = (lastRepair == null) || (lastRepair.equals(""));
    final boolean nextEmpty = (nextRepair == null) || (nextRepair.equals(""));
    if (lastEmpty != nextEmpty) {
      final String logString = lastRepair + "-" + nextRepair + " repair dates pair failed \n"
          + "validation: if last repair string contains date, \n"
          + "next repair must contain date too, and vice versa.";
      logger.warn(logString);
      throw new IllegalArgumentException("Heterogeneous pair error: " + logString);
    }
    
    if (lastEmpty) {
      final String logString = "Last-Next repair date pair passed the validation due to both  \n"
          + "strings is either null or empty";
      logger.info(logString);
      return;
    }
    
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    final LocalDate dateLast = LocalDate.parse(lastRepair, formatter);
    final LocalDate dateNext = LocalDate.parse(nextRepair, formatter);
    if (dateLast.isBefore(dateNext)) {
      logger.info(lastRepair + "-" + nextRepair + " repair dates passed the validation due to \n"
          + " both strings match all requirements.");
    } else {
      final String logString = lastRepair + "-" + nextRepair + " repair dates pair failed \n"
          + "validation: last repair should be before next repair.";
      logger.warn(logString);
      throw new IllegalArgumentException("Invalid dates par: " + logString);
  }
  }
  
  /**
   * Validates row with model name and repair periods.
   * <p>
   * Model name must be non-empty unique string and may not be NULL.
   * <p>
   * Period of any repair must be positive integer.
   * @param modelName to validate
   * @param repairPeriods to validate
   * @throws IllegalArgumentException if validation fails
   */
  public void validateModelPeriods(final String modelName,final List<Integer> repairPeriods) throws
                                                                         IllegalArgumentException {
    validateRepairPeriodsModelName(modelName);
    for (int period : repairPeriods) {
      validateRepairPeriod(period);
    }
  }
  
  /**
   * Validates locomotive model name.
   * <p>
   * Model name must be non-empty unique string and may not be NULL.
   * @param modelName to validate
   * @throws IllegalArgumentException if validation fails
   */
  public void validateRepairPeriodsModelName(final String modelName) throws 
                                                                        IllegalArgumentException {
    if (modelName == null || modelName.equals("")) {
      logger.warn(modelName + " failed model name validation: it can not be empty or NULL");
      throw new IllegalArgumentException("Invalid locomotive model name: cannot be empty or NULL");
    }
    
    for (String name : dbManager.getAllModelNames()) {
      if (modelName.equals(name)) {
        final String logString = modelName + " failed model name validation: model with the same\n"
            + " name already exists.";
        logger.warn(logString);
        throw new IllegalArgumentException("Not unique model name: " + modelName);
      }
    }
    logger.info(modelName + " passed model name validation.");
  }
  
  /**
   * Validates repair period value.
   * <p>
   * Repair period must be a positive integer.
   * @param period value to validate
   * @throws IllegalArgumentException if period is not a positive number
   */
  public void validateRepairPeriod(final int period) throws IllegalArgumentException {
    if (period > 0) {
      logger.info("Repair period value " + period +  " passed the validation.");
    } else {
      logger.info("Repair period value " + period + " failed validation cause it`s not positive" );
      throw new IllegalArgumentException("Repair period " + period + " is not a positive number");
    }
  }
  
  @Override
  public String toString() {
    return "InputValidator object - container for methods with input data validation.";
  }
  
}
