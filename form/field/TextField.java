package form.field;

import config.FormField;
import exception.FieldParsingException;

/**
 * Implementation of {@link Field} for {@code String}.
 * Support regex matching for validation.
 * 
 * @see Field
 */
public class TextField extends Field<String>{
    private final String regex;

    /**
     * Constructs a {@code TextField} with a name and associated {@link FormField}.
     *
     * @param name       the field's name
     * @param formField  the form field configuration
     */
    public TextField(String name, FormField formField){
        super(name, formField);
        this.regex = null;
    }

    /**
     * Constructs a {@code TextField} with an original value.
     *
     * @param name          the field's name
     * @param originalValue the original String value
     * @param formField     the form field configuration
     */
    public TextField(String name, String originalValue, FormField formField){
        super(name, originalValue, formField);
        this.regex = null;
    }

    /**
     * Constructs a {@code TextField} with a regular expression (regex) constraint for validation.
     *
     * @param name      the field's name
     * @param formField the form field configuration
     * @param regex     the regular expression for validating input
     */
    public TextField(String name, FormField formField, String regex){
        super(name, formField);
        this.regex = regex;
    }

    /**
     * Constructs a {@code TextField} with an original value and a regular expression constraint.
     *
     * @param name          the field's name
     * @param originalValue the original String value
     * @param formField     the form field configuration
     * @param regex         the regular expression for validating input
     */
    public TextField(String name, String originalValue, FormField formField, String regex){
        super(name, formField);
        this.regex = regex;
    }

    /**
     * Validates the input string using the regular expression, if provided.
     *
     * @param value the string value to validate
     * @throws FieldParsingException if the input does not match the regex
     */
    @Override
    public void validate(String value) throws FieldParsingException{
        if(regex == null) return;

        if(!value.matches(regex)){
            throw new FieldParsingException("Incorrect format.");
        }
    }

    /**
     * Parses the input string and returns it directly since no parsing is needed.
     *
     * @param input the raw string input
     * @return the parsed string (same as input)
     */
    @Override
    protected String parseData(String input) {
        return input;
    }
}

