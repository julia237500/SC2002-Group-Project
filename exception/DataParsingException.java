package exception;

/**
 * Thrown to indicate that a data parsing operation has failed due to misconfiguration.
 * <p>
 * This exception is unchecked and is typically used within {@code DataManager} and {@code DataParser}
 * classes to signal programmer error or system misconfiguration.
 */
public class DataParsingException extends RuntimeException{
    public DataParsingException(String message) {
        super(message);
    }
}
