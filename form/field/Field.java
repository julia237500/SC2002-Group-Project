package form.field;

import config.FormField;
import exception.FieldParsingException;
import form.FieldData;

/**
 * Abstract base class representing a form input field that processes and validates data
 * of a specific type {@code T}, which can be parsed from a single string input.
 * <p>
 * This class is inspired by the behavior of HTML input fields and is designed to handle 
 * form field data by parsing, validating, and converting a string representation into 
 * a specific data type. The type {@code T} can represent various data types, such as 
 * {@code String}, {@code Integer}, {@code LocalDate}, or any other type that can be 
 * meaningfully derived from a user-provided string input.
 * </p>
 *
 * <p>
 * Each concrete implementation of this class define how the string input
 * is parsed into type {@code T}, and how the parsed data is validated
 * against constraints such as format, range, or custom rules.
 * </p>
 *
 * @param <T> The type of data this field handles (e.g., {@code String}, {@code Integer}, 
 *            {@code LocalDate}), which can be parsed from a single string input.
 */
public abstract class Field<T> {
    private final String name;
    private final T originalValue;
    private final FormField formField;

    /**
     * Constructs a new {@code Field} with the given label and {@link FormField} identifier, without an initial value.
     *
     * @param name       The name of the field, typically displayed to the user (like the {@code <label>} in HTML).
     * @param formField  The identifier of the field, used internally to reference the input (like the {@code name} attribute in HTML).
     * 
     * @see FormField
     */
    public Field(String name, FormField formField){
        this.name = name;
        this.originalValue = null;
        this.formField = formField;
    }

    /**
     * Constructs a new {@code Field} with a predefined value for editing purposes.
     *
     * @param name          The name of the field, typically displayed to the user (like the {@code <label>} in HTML).
     * @param originalValue The original value of the field, used when editing existing data.
     * @param formField     The identifier of the field, used internally to reference the input (like the {@code name} attribute in HTML).
     * 
     * @see FormField
     */
    public Field(String name, T originalValue, FormField formField){
        this.name = name;
        this.originalValue = originalValue;
        this.formField = formField;
    }

    /**
     * Returns the name of the field.
     * <p>
     * This is typically shown to the user, similar to the {@code <label>} element in HTML forms.
     *
     * @return the name of the field
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the original value of the field.
     * <p>
     * This is mainly used in edit scenarios to prepopulate the form with existing data.
     *
     * @return the original value of the field, or {@code null} if not applicable
     */
    public T getOriginalValue() {
        return originalValue;
    }

    /**
     * Returns the {@link FormField} identifier.
     * <p>
     * This acts like the {@code name} attribute in an HTML input, used for internal data mapping and reference.
     *
     * @return the form field identifier
     * 
     * @see FormField
     */
    public FormField getFormField() {
        return formField;
    }

    /**
     * Processes the user input, applying parsing and validation.
     * Returns a wrapper {@link FieldData} with the parsed value.
     *
     * @param input The input string entered by the user.
     * 
     * @return A {@code FieldData<T>} object containing the processed value.
     * 
     * @throws FieldParsingException if input is empty or fails validation/parsing.
     * 
     * @see FieldData
     */
    public FieldData<T> processData(String input) throws FieldParsingException{
        if(input.equals("")){
            if(originalValue != null){
                return new FieldData<T>(originalValue);
            }
            throw new FieldParsingException("Please enter a value.");
        }

        T data = parseData(input);
        validate(data);
        return new FieldData<T>(data);
    }

    /**
     * Returns a string representation of the constraint or expected input format for this field.
     * <p>
     * This can be used to inform users of formatting rules or validation requirements,
     * such as "YYYY-MM-DD" for a date field.
     * <p>
     * Subclasses should override this method to provide specific constraints relevant to the field type.
     *
     * @return a string describing the constraint or format, or {@code null} if not specified
     */
    public String getConstraint(){
        return null;
    }

    /**
     * Validates the parsed value.
     *
     * @param value The value to validate
     * 
     * @throws FieldParsingException if validation fails.
     */
    protected abstract void validate(T value) throws FieldParsingException;

    /**
     * Parses the user input string into an object of type {@code T}.
     *
     * @param input The input string.
     * 
     * @return The parsed value of type {@code T}.
     * 
     * @throws FieldParsingException if parsing fails.
     */
    protected abstract T parseData(String input) throws FieldParsingException;
    
}
