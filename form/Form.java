package form;

import java.util.ArrayList;
import java.util.List;

import form.field.Field;

/**
 * An abstract base class representing a form in the application.
 * <p>
 * This class is designed to mimic the behavior of an HTML form,
 * allowing dynamic configuration of input fields ({@link Field}) 
 * tailored to different types of data and use cases.
 * <p>
 * Subclasses should define and initialize the required form fields 
 * based on their specific needs. This class provides the structure 
 * for managing and accessing fields.
 * 
 * @see Field
 */
public abstract class Form {
    private final List<Field<?>> fields = new ArrayList<>();

    /**
     * Returns the title of the form.
     * @implSpec Subclasses must implement this method to provide a specific title.
     *
     * @return the title of the form.
     */
    public abstract String getTitle();

    /**
     * Initializes the fields for the form. Must be called before accessing the fields.
     * @implSpec Subclasses must implement this method to initialize fields specific to the form type.
     */
    public abstract void initFields();
    
    /**
     * Adds a {@link Field} to the form.
     * This method allows the addition of form fields
     *
     * @param field the field to add to the form.
     * 
     * @see Field
     */
    protected void addField(Field<?> field){
        fields.add(field);
    }
    
    /**
     * Retrieves the list of {@link Field} associated with the form.
     * This method can be used to access the fields for validation, submission, or display purposes.
     *
     * @return a list of fields in the form.
     * 
     * @see Field
     */
    public List<Field<?>> getFields() {
        return fields;
    }
}