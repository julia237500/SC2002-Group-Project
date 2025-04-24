package controller.interfaces;

import java.util.Map;

import config.FormField;
import form.FieldData;
import form.Form;

/**
 * A controller that uses {@link Form} objects in accordance with the MVC architecture.
 * It prompts the user with {@link Form} to get inputs from user.
 *
 * @implNote This controller is allowed to handle the form logic directly.
 * Since {@link Form} is not part of the model layer, it typically does not require access to a service.
 *
 * @see Form
 */
public interface FormController {
    /**
     * Sets the {@link Form} to be used by this controller.
     * <p>
     * This method assigns a specific {@link Form} instance to the controller,
     * which will be used to prompt and capture user input. 
     * This function will initialise the form if needed.
     *
     * @param form the {@code Form} object that defines the structure and fields of the form.
     * 
     * @see Form
     */
    void setForm(Form form);

    /**
     * Retrieves user input data for all fields in the current {@link Form}.
     * <p>
     * The returned map links each {@link FieldData} object to its corresponding
     * {@link FormField}, which contains the validated and parsed user input.
     *
     * @return a map of form fields and their associated input data.
     * 
     * @see Form
     * @see FormField
     * @see FieldData
     */
    Map<FormField, FieldData<?>> getFormData();
}
