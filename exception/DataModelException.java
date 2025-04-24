package exception;

import model.DataModel;

/**
 * Thrown to indicate that a {@link DataModel} operation has failed due to invalid or inconsistent data.
 * <p>
 * This exception is typically used during creation or modification of models when
 * validation checks fail (e.g., invalid input, business rule violations).
 * It is a checked exception to ensure that calling code explicitly handles such cases.
 */
public class DataModelException extends Exception{
    public DataModelException(String message) {
        super(message);
    }
}
