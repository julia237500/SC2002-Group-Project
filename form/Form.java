package form;

import java.util.ArrayList;
import java.util.List;

import form.field.Field;

/**
 * The base class representing a form in the application.
 * This class provides a template for creating forms with specific fields,
 * managing form fields, and initializing them for different form types.
 */
public abstract class Form {
    // List to store form fields 
    private List<Field<?>> fields = new ArrayList<>();

    /**
     * Returns the title of the form.
     * Subclasses must implement this method to provide a specific title.
     *
     * @return the title of the form.
     */
    public abstract String getTitle();

    /**
     * Initializes the fields for the form.
     * Subclasses must implement this method to initialize fields specific to the form type.
     */
    public abstract void initFields();
    
    /**
     * Adds a field to the form.
     * This method allows the addition of form fields
     *
     * @param field the field to add to the form.
     */
    protected void addFields(Field<?> field){
        fields.add(field);
    }
    
    /**
     * Retrieves the list of fields associated with the form.
     * This method can be used to access the fields for validation, submission, or display purposes.
     *
     * @return a list of fields in the form.
     */
    public List<Field<?>> getFields() {
        return fields;
    }
}

/**
 * This provides extensibility:
 * - By defining Form as an abstract base class, we enable the ability to easily extend the form class
 * - to add specific form types (e.g., BTOProjectForm, ChangePasswordForm, etc.). Each form can implement its own version of getTitle() and initFields() without affecting the base class.
 * - Design Consideration: This supports open-closed principle, meaning that new forms can be added without changing existing code.
 * This also provides Polymorphism and Flexibility:
 * - With the Form class being abstract and defining abstract methods (getTitle() and initFields()), 
 * - this is especially useful when working with multiple types of forms that all share a common behavior.
 */
