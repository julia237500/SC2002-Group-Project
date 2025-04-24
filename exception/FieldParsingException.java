package exception;

import form.field.Field;

/**
 * Thrown when the user input for a form {@link Field} violates the specified constraints or format.
 * <p>
 * This exception is typically used to indicate that the input does not meet the required 
 * constraints such as maximum or minimum length, allowed format (e.g., email or phone number),
 * or other validation rules as defined for the form field.
 * This can be used to prompt the user to correct their input, similar to how input is validated 
 * in HTML forms.
 * 
 * @see Field
 */
public class FieldParsingException extends Exception{
    public FieldParsingException(String message) {
        super(message);
    }
}
