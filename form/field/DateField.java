package form.field;

import java.time.LocalDate;

import config.FormField;
import exception.FieldParsingException;

/**
 * A form field that handles date input using {@link LocalDate}.
 * Provides optional validation to disallow past dates and handles parsing from String input.
 */
public class DateField extends Field<LocalDate>{

    /**
     * Flag indicating whether past dates are allowed.
     * Defaults to {@code true}.
     */
    private boolean allowPast = true;

    /**
     * Constructs a DateField with a field name and configuration.
     *
     * @param name       The name of the field.
     * @param formField  The associated form field configuration.
     */
    public DateField(String name, FormField formField) {
        super(name, formField);
    }

    /**
     * Constructs a DateField with an initial value.
     *
     * @param name          The name of the field.
     * @param originalValue The original date value.
     * @param formField     The associated form field configuration.
     */
    public DateField(String name, LocalDate originalValue, FormField formField) {
        super(name, originalValue, formField);
    }

    /**
     * Constructs a DateField and specifies whether past dates are allowed.
     *
     * @param name       The name of the field.
     * @param formField  The associated form field configuration.
     * @param allowPast  Whether past dates are allowed.
     */
    public DateField(String name, FormField formField, boolean allowPast) {
        super(name, formField);
        this.allowPast = allowPast;
    }
    
    /**
     *
     * @param name          The name of the field.
     * @param originalValue The original date value.
     * @param formField     The associated form field configuration.
     * @param allowPast     Whether past dates are allowed.
     */
    public DateField(String name, LocalDate originalValue, FormField formField, boolean allowPast) {
        super(name, originalValue,formField);
        this.allowPast = allowPast;
    }

    /**
     * Returns a string that describes the required date format.
     *
     * @return A string indicating the expected date format.
     */
    @Override
    public String getConstraint() {
        return "Format: YYYY-MM-DD";
    }

    /**
     * Validates the parsed date.
     * Throws an exception if past dates are not allowed and the value is in the past.
     *
     * @param value The parsed {@link LocalDate} value to validate.
     * @throws FieldParsingException if the date is not valid.
     */
    @Override
    public void validate(LocalDate value) {
        if(!allowPast && value.isBefore(LocalDate.now())){
            throw new FieldParsingException("Date inputted cannot be past.");
        }
    }

    /**
     * Parses the user input string into a {@link LocalDate} object.
     *
     * @param input The user input string.
     * @return The parsed {@link LocalDate}.
     * @throws FieldParsingException if the input cannot be parsed.
     */
    @Override
    protected LocalDate parseData(String input) {
        try{
            return LocalDate.parse(input);
        } catch (Exception e) {
            throw new FieldParsingException("Please enter the correct format.");
        }
    }
}
