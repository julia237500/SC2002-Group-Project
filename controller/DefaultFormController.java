package controller;

import java.util.HashMap;
import java.util.Map;

import config.FormField;
import controller.interfaces.FormController;
import exception.FieldParsingException;
import form.FieldData;
import form.Form;
import form.field.Field;
import view.interfaces.FormView;
import view.interfaces.MessageView;

/**
 * Default implementation of {@link FormController}.
 * <p>
 * This controller is responsible for coordinating user-driven logic related to 
 * input and retrieval of data for {@link Form}. It display and prompts for input using {@link FormView}.
 * 
 * @see FormController
 * @see Form
 * @see FormView
 */
public class DefaultFormController implements FormController{
    private final FormView formView;
    private final MessageView messageView;

    private Form form;
    private final Map<FormField, FieldData<?>> formData = new HashMap<>();
    
    /**
     * Constructs a {@code DefaultFormController}.
     * 
     * @param formView the view responsible for form rendering and user input
     * @param messageView the view to display error message
     * 
     * @see FormView
     * @see MessageView
     */
    public DefaultFormController(FormView formView, MessageView messageView){
        this.formView = formView;
        this.messageView = messageView;
    }

    public void setForm(Form form){
        this.form = form;
        form.initFields();
    }

    public Map<FormField, FieldData<?>> getFormData() {
        inputFormData();
        return formData;
    }

    /**
     * Prompts user to input data for each field in the form and validates it.
     * Successfully validated input is stored in the {@code formData} map.
     */
    private void inputFormData() {
        formData.clear();
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
                    messageView.error(e.getMessage());
                }
            }
        }
    }
}
