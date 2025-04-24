package form.field;

import config.FormField;
import exception.FieldParsingException;

/**
 * Implementation of {@link Field} for {@code Boolean}.
 * 
 * @see Field
 */
public class BoolField extends Field<Boolean>{
    private static final String TRUE_STRING = "Y";
    private static final String FALSE_STRING = "N";

    /**
     * Constructs a {@code BoolField} with a field name and configuration.
     *
     * @param name       The name of the field.
     * @param formField  The associated form field configuration.
     */
    public BoolField(String name, FormField formField){
        super(name, formField);
    }

    /**
     * Constructs a {@code BoolField} with an original value.
     *
     * @param name       The name of the field.
     * @param formField  The associated form field configuration.
     */
    public BoolField(String name, Boolean originalValue, FormField formField){
        super(name, originalValue, formField);
    }

    /**
     * Returns a string that describes the required format for {@code true} and {@code false}.
     *
     * @return A string indicating the expected format.
     */
    @Override
    public String getConstraint() {
        return "%s/%s".formatted(TRUE_STRING, FALSE_STRING);
    }

    /**
     * No validation for Boolean, since Boolean cannot have constraint.
     */
    @Override
    public void validate(Boolean value) {
        // No validation for Boolean
    }

    /**
     * Parses the user input string into a {@link Boolean} object.
     *
     * @param input The user input string.
     * @return The parsed {@link Boolean}.
     * @throws FieldParsingException if parsing fail.
     */
    @Override
    protected Boolean parseData(String input) throws FieldParsingException{
        if(input.toLowerCase().equals(TRUE_STRING.toLowerCase())){
            return true;
        }
        if(input.toLowerCase().equals(FALSE_STRING.toLowerCase())){
            return false;
        }

        throw new FieldParsingException("Please enter the %s or %s only.".formatted(
            TRUE_STRING, FALSE_STRING
        ));
    }
}
