package form.field;

import config.FormField;
import exception.FieldParsingException;

public class BoolField extends Field<Boolean>{
    private static final String TRUE_STRING = "Y";
    private static final String FALSE_STRING = "N";

    public BoolField(String name, FormField formField){
        super(name, formField);
    }

    public BoolField(String name, Boolean originalValue, FormField formField){
        super(name, originalValue, formField);
    }

    @Override
    public String getConstraint() {
        return "%s/%s".formatted(TRUE_STRING, FALSE_STRING);
    }

    @Override
    public void validate(Boolean value) {
        // No validation for Boolean
    }

    @Override
    protected Boolean parseData(String input) {
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
