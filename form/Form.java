package form;

import java.util.ArrayList;
import java.util.List;

import form.field.Field;

public abstract class Form {
    private List<Field<?>> fields = new ArrayList<>();

    public abstract String getTitle();
    public abstract void initFields();
    
    protected void addFields(Field<?> field){
        fields.add(field);
    }

    public List<Field<?>> getFields() {
        return fields;
    }
}
