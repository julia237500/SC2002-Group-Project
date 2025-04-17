package form.field;

import config.FormField;
import exception.FieldParsingException;

/**
 * Represents a text field that handles String input in a form.
 * Supports optional validation using regular expressions (regex).
 */
public class TextField extends Field<String>{
    private String regex;

    /**
     * Constructs a TextField with a name and associated {@link FormField}.
     *
     * @param name       the field's name
     * @param formField  the form field configuration
     */
    public TextField(String name, FormField formField){
        super(name, formField);
    }

    /**
     * Constructs a TextField with an original value.
     *
     * @param name          the field's name
     * @param originalValue the original String value
     * @param formField     the form field configuration
     */
    public TextField(String name, String originalValue, FormField formField){
        super(name, originalValue, formField);
    }

    /**
     * Constructs a TextField with a regular expression (regex) constraint for validation.
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
     * Constructs a TextField with an original value and a regular expression constraint.
     *
     * @param name          the field's name
     * @param originalValue the original String value
     * @param formField     the form field configuration
     * @param regex         the regular expression for validating input
     */

    /**
     * This constructor is the entry point for regex configuration — 
     * it allows whoever is instantiating the TextField to specify a regex pattern at the time of creation.
     * We're not hardcoding the regex inside the TextField class itself. 
     * Instead, the regex is passed externally — so the TextField becomes a reusable and customizable component.
     * This is an example of Dependency Injection: the regex rule is passed into the class, not created inside it.
     * Also follows Open/Closed Principle: validation logic can be extended without modifying the base TextField class.
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
    public void validate(String value) {
        if(regex == null) return;

        if(!value.matches(regex)){
            throw new FieldParsingException("Incorrect format.");
        }
    }

    /**
     * Parses the input string and returns it directly since no transformation is needed.
     *
     * @param input the raw string input
     * @return the parsed string (same as input)
     */
    @Override
    protected String parseData(String input) {
        return input;
    }
}

