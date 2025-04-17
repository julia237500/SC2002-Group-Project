package controller.interfaces;

import java.util.Map;

import config.FormField;
import form.FieldData;
import form.Form;

/**
 * Interface for handling form-related operations such as setting a form
 * and retrieving the user's input data from the form fields.
 */
public interface FormController {

    /**
     * Sets the form to be used by this controller.
     * <p>
     * This method initializes the controller with a specific {@link Form} instance,
     * which defines the fields and structure that the user will interact with.
     * </p>
     *
     * @param form the {@link Form} object containing the form structure and fields.
     */
    void setForm(Form form);

    /**
     * Retrieves the user input data for all fields in the current form.
     * <p>
     * The data is returned as a mapping from each {@link FormField} to its corresponding
     * {@link FieldData} object, which holds the validated and parsed value entered by the user.
     * </p>
     *
     * @return a map containing the form field definitions and their associated input data.
     */
    Map<FormField, FieldData<?>> getFormData();
}
