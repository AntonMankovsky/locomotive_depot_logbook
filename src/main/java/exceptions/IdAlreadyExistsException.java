package exceptions;

/**
 * An exception for id duplicate situation.
 * <p>
 * Should be used in situation when data structure which represents database table information 
 * is asked to put {@code id} key that already exists in the map. 
 * <br>
 * This kind of situation could potentially lead to inconsistent data.
 */
public class IdAlreadyExistsException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * An exception for id duplicate situation.
   * <p>
   * Should be used in situation when data structure which represents database table information 
   * is asked to put {@code id} key that already exists. 
   */
  public IdAlreadyExistsException() {
    super();
  }
  
  /**
   * An exception for id duplicate situation.
   * <p>
   * Should be used in situation when data structure which represents database table information 
   * is asked to put {@code id} key that already exists. 
   * @param errorMessage 
   */
  public IdAlreadyExistsException(final String errorMessage) {
    super(errorMessage);
  }

}
