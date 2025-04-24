package form.field;

import java.time.LocalDate;

import config.FormField;
import exception.FieldParsingException;

/**
 * Implementation of {@link Field} for {@code LocalDate}.
 * By default, it allow past LocalDate, but able to disallow.
 * It parse String with default format specified by {@link LocalDate},
 * which is ISO-8601 calendar system, such as 2007-12-03.
 * 
 * @see Field
 */
public class DateField extends Field<LocalDate>{
    private boolean allowPast = true;
    /**
     * Constructs a {@code DateField} with a field name and configuration.
     *
     * @param name       The name of the field.
     * @param formField  The associated form field configuration.
     */
    public DateField(String name, FormField formField) {
        super(name, formField);
    }

    /**
     * Constructs a {@code DateField} with an initial value.
     *
     * @param name          The name of the field.
     * @param originalValue The original date value.
     * @param formField     The associated form field configuration.
     */
    public DateField(String name, LocalDate originalValue, FormField formField) {
        super(name, originalValue, formField);
    }

    /**
     * Constructs a {@code DateField} and specifies whether past dates are allowed.
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
     * Constructs a {@code DateField} with an original value and and specifies whether past dates are allowed.
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
    public void validate(LocalDate value) throws FieldParsingException{
        if(!allowPast && value.isBefore(LocalDate.now())){
            throw new FieldParsingException("Date inputted cannot be past.");
        }
    }

    /**
     * Parses the user input string into a {@link LocalDate} object.
     *
     * @param input The user input string.
     * @return The parsed {@link LocalDate}.
     * @throws FieldParsingException if parsing fail.
     */
    @Override
    protected LocalDate parseData(String input) throws FieldParsingException{
        try{
            return LocalDate.parse(input);
        } catch (Exception e) {
            throw new FieldParsingException("Please enter the correct format.");
        }
    }
}
