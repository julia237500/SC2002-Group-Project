package form.field;

import config.FormField;
import exception.FieldParsingException;

public class TextField extends Field<String>{
    private String regex;

    public TextField(String name, FormField formField){
        super(name, formField);
    }

    public TextField(String name, String originalValue, FormField formField){
        super(name, originalValue, formField);
    }

    public TextField(String name, FormField formField, String regex){
        super(name, formField);
        this.regex = regex;
    }

    public TextField(String name, String originalValue, FormField formField, String regex){
        super(name, formField);
        this.regex = regex;
    }

    @Override
    public void validate(String value) {
        if(regex == null) return;

        if(!value.matches(regex)){
            throw new FieldParsingException("Incorrect format.");
        }
    }

    @Override
    protected String parseData(String input) {
        return input;
    }
}
