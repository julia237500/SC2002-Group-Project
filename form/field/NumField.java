package form.field;

import config.FormField;
import exception.FieldParsingException;

/**
 * Represents a numeric field that handles Integer values in a form.
 * Supports optional minimum and maximum constraints.
 */
public class NumField extends Field<Integer>{
    private Integer min;
    private Integer max;

    /**
     * Constructs a NumField with a name and associated {@link FormField}.
     *
     * @param name       the field's name
     * @param formField  the form field configuration
     */
    public NumField(String name, FormField formField){
        super(name, formField);
    }

    /**
     * Constructs a NumField with a name, original value, and associated {@link FormField}.
     *
     * @param name          the field's name
     * @param originalValue the original integer value
     * @param formField     the form field configuration
     */
    public NumField(String name, int originalValue, FormField formField){
        super(name, originalValue, formField);
    }

    /**
     * Constructs a NumField with minimum and maximum range constraints.
     *
     * @param name       the field's name
     * @param formField  the form field configuration
     * @param min        the minimum allowed value (inclusive)
     * @param max        the maximum allowed value (inclusive)
     */
    public NumField(String name, FormField formField, int min, int max){
        super(name, formField);
        this.min = min;
        this.max = max;
    }

    /**
     * Constructs a NumField with an original value and range constraints.
     *
     * @param name          the field's name
     * @param originalValue the original integer value
     * @param formField     the form field configuration
     * @param min           the minimum allowed value (inclusive)
     * @param max           the maximum allowed value (inclusive)
     */
    public NumField(String name, int originalValue, FormField formField, int min, int max){
        super(name, originalValue, formField);
        this.min = min;
        this.max = max;
    }

    /**
     * Validates the input integer value.
     * If min and max are set, ensures that the value is within the specified range.
     *
     * @param value the integer value to validate
     * @throws FieldParsingException if the value is outside the allowed range
     */
    @Override
    public void validate(Integer value) {
        if(min == null || max == null) return;
        
        if(value < min || value > max){
            throw new FieldParsingException(String.format("Number must be between %d - %d (inclusive)", min, max));
        }
    }

    /**
     * Parses the input string to an Integer.
     *
     * @param input the input string
     * @return the parsed Integer
     * @throws FieldParsingException if the input is not a valid integer
     */
    @Override
    protected Integer parseData(String input) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            throw new FieldParsingException("Please enter number only.");
        }
    }
}