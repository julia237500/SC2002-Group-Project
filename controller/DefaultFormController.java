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

public class DefaultFormController implements FormController{
    private Form form;
    private FormView formView;
    private Map<FormField, FieldData<?>> formData = new HashMap<>();
    
    public DefaultFormController(FormView formView){
        this.formView = formView;
    }

    public void setForm(Form form){
        this.form = form;
        form.initFields();
    }

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

    public Map<FormField, FieldData<?>> getFormData() {
        inputFormData();
        return formData;
    }
}
