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
   * Contains methods for data validation.
   */
  public InputValidator(final DbManager dbManager) {
    super();
    this.dbManager = dbManager;
  }

  /**
   * Validates locomotive number for repair records table.
   * @param locoNumber to validate
   * @throws IllegalArgumentException when number is null or contains anything except digits
   */
  public void validateLocoNumber(final String locoNumber) throws IllegalArgumentException {
    Pattern pattern = Pattern.compile("[0-9]+");
    if (locoNumber == null || !pattern.matcher(locoNumber.trim()).matches()) {
      final String logString = "\"" + locoNumber + "\"" + " failed locomotive number validation:"
          + " it may not be null and should cointain only digits (one or more).";
      logger.warn(logString);
      throw new IllegalArgumentException("Invalid locomotive number: " + locoNumber);
    } else {
      logger.info("\"" + locoNumber + "\"" + " passed locomotive number validation.");
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
  public void validateRepairDate(String date) throws IllegalArgumentException {
    if (date == null || date.equals("")) {
      //logger.info("Repair date passed validation cause nulls and empty strings are allowed.");
      return;
    }
    
    date = date.trim();
    Pattern pattern = Pattern.compile("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}");
    if (!pattern.matcher(date).matches()) {
      logger.warn(date + " failed validation due to violation of required date format: dd.MM.yyyy");
      throw new IllegalArgumentException("Invalid date format: " + date + ". dd.MM.yyyy expected.");
    } else {
      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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
   * Validates locomotive model name.
   * <p>
   * Model name must be non-empty unique string and may not be NULL. 
   * It cannot contain special characters " ' or \.
   * @param modelName to validate
   * @throws IllegalArgumentException if validation fails
   */
  public void validateRepairPeriodsModelName(String modelName) throws 
                                                                        IllegalArgumentException {
    modelName = modelName == null ? "" : modelName.trim();
    if (modelName.equals("")) {
      logger.warn(modelName + " failed model name validation: it can not be empty or NULL");
      throw new IllegalArgumentException("Invalid locomotive model name: cannot be empty or NULL");
    }
    
    if (modelName.contains("\"") || modelName.contains("\'") || modelName.contains("\\")) {
      logger.warn(modelName + " failed model name validation: it contains special character");
      throw new IllegalArgumentException("Invalid locomotive model name: special character.");
    }
    
    for (String name : dbManager.getAllModelNames()) {
      if (modelName.equals(name)) {
        final String logString = modelName + " failed model name validation: model with the same "
            + "name already exists.";
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
      logger.warn("Repair period value " + period + " failed validation cause it`s not positive" );
      throw new IllegalArgumentException("Repair period " + period + " is not a positive number");
    }
  }
  
  @Override
  public String toString() {
    return "InputValidator object - container for methods with input data validation.";
  }
  
}
