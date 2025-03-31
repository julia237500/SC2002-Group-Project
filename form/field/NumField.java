package form.field;

import config.FormField;
import exception.FieldParsingException;

public class NumField extends Field<Integer>{
    private Integer min;
    private Integer max;

    public NumField(String name, FormField formField){
        super(name, formField);
    }

    public NumField(String name, int originalValue, FormField formField){
        super(name, originalValue, formField);
    }

    public NumField(String name, FormField formField, int min, int max){
        super(name, formField);
        this.min = min;
        this.max = max;
    }

    public NumField(String name, int originalValue, FormField formField, int min, int max){
        super(name, originalValue, formField);
        this.min = min;
        this.max = max;
    }

    @Override
    public void validate(Integer value) {
        if(min == null || max == null) return;
        
        if(value < min || value > max){
            throw new FieldParsingException(String.format("Number must be between %d - %d (inclusive)", min, max));
        }
    }

    @Override
    protected Integer parseData(String input) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            throw new FieldParsingException("Please enter number only.");
        }
    }
}