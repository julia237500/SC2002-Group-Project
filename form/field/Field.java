package form.field;

import config.FormField;
import exception.FieldParsingException;
import form.FieldData;

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

    public abstract void validate(T value);
    protected abstract T parseData(String input);
    
}
