package form.field;

import java.time.LocalDate;

import config.FormField;
import exception.FieldParsingException;

public class DateField extends Field<LocalDate>{
    private boolean allowPast = true;

    public DateField(String name, FormField formField) {
        super(name, formField);
    }

    public DateField(String name, LocalDate originalValue, FormField formField) {
        super(name, originalValue, formField);
    }

    public DateField(String name, FormField formField, boolean allowPast) {
        super(name, formField);
        this.allowPast = allowPast;
    }
    
    public DateField(String name, LocalDate originalValue, FormField formField, boolean allowPast) {
        super(name, originalValue,formField);
        this.allowPast = allowPast;
    }

    @Override
    public String getConstraint() {
        return "Format: YYYY-MM-DD";
    }

    @Override
    public void validate(LocalDate value) {
        if(!allowPast && value.isBefore(LocalDate.now())){
            throw new FieldParsingException("Date inputted cannot be past.");
        }
    }

    @Override
    protected LocalDate parseData(String input) {
        try{
            return LocalDate.parse(input);
        } catch (Exception e) {
            throw new FieldParsingException("Please enter the correct format.");
        }
    }
}
