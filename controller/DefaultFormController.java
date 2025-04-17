package controller;

import java.util.HashMap;
import java.util.Map;

import config.FormField;
import controller.interfaces.FormController;
import exception.FieldParsingException;
import form.FieldData;
import form.Form;
import form.field.Field;
import manager.DIManager;
import view.interfaces.FormView;
import view.interfaces.MessageView;


/**
 * Controller responsible for handling form interactions.
 * <p>
 * This class is responsible for:
 * <ul>
 *   <li>Displaying the form to the user via the {@link FormView}</li>
 *   <li>Processing and validating user input for each form field</li>
 *   <li>Storing the input as structured {@link FieldData}</li>
 * </ul>
 * It delegates display responsibilities to {@link FormView}, 
 * and error messages to {@link MessageView}.
 */
public class DefaultFormController implements FormController{
    private Form form;
    private FormView formView;
    private Map<FormField, FieldData<?>> formData = new HashMap<>();
    

    /**
     * Constructs a DefaultFormController with the provided FormView.
     * 
     * @param formView the view responsible for form rendering and user input
     */
    public DefaultFormController(FormView formView){
        this.formView = formView;
    }

    /**
     * Sets the form to be handled by this controller and initializes its fields.
     *
     * @param form the form to be set
     */
    public void setForm(Form form){
        this.form = form;
        form.initFields();
    }

    /**
     * Prompts user to input data for each field in the form and validates it.
     * Successfully validated input is stored in the {@code formData} map.
     */
    private void inputFormData() {
        formView.show(form.getTitle());

        for(Field<?> field:form.getFields()){
            while(true){
                String input = formView.getInput(field);

                try{
                    FieldData<?> fieldData = field.processData(input);
                    formData.put(field.getFormField(), fieldData);
                    break;
                }
                catch(FieldParsingException e){
                    MessageView messageView = DIManager.getInstance().resolve(MessageView.class);
                    messageView.error(e.getMessage());
                }
            }
        }
    }

    /**
     * Starts the process of gathering input for all form fields and 
     * returns the resulting field data.
     *
     * @return a map of form fields to their corresponding field data
     */
    public Map<FormField, FieldData<?>> getFormData() {
        inputFormData();
        return formData;
    }
}
