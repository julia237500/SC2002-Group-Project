package form.field;

import config.FormField;
import exception.FieldParsingException;
import form.FieldData;

/**
 * Abstract base class representing a form input field that processes
 * and validates data of a specific type {@code T}.
 *
 * @param <T> The type of data this field handles (e.g., String, Integer, LocalDate).
 */
public abstract class Field<T> {
    private String name;
    private T originalValue;
    private FormField formField;

    public Field(String name, FormField formField){
        this.name = name;
        this.formField = formField;
    }

    public Field(String name, T originalValue, FormField formField){
        this.name = name;
        this.originalValue = originalValue;
        this.formField = formField;
    }

    public String getName() {
        return name;
    }

    public T getOriginalValue() {
        return originalValue;
    }

    public FormField getFormField() {
        return formField;
    }

    /**
     * Processes the user input, applying parsing and validation.
     * Returns a {@link FieldData} object wrapping the parsed value.
     *
     * @param input The input string entered by the user.
     * @return A {@code FieldData<T>} object containing the processed value.
     * @throws FieldParsingException if input is empty or fails validation/parsing.
     */
    public FieldData<T> processData(String input){
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

    public String getConstraint(){
        return null;
    }

    /**
     * Validates the parsed value.
     *
     * @param value The value to validate
     */
    public abstract void validate(T value);

    /**
     * Parses the user input string into an object of type {@code T}.
     *
     * @param input The input string.
     * @return The parsed value of type {@code T}.
     * @throws FieldParsingException if parsing fails.
     */
    protected abstract T parseData(String input);
    
}
