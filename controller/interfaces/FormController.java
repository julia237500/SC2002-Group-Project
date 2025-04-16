package controller.interfaces;

import java.util.Map;

import config.FormField;
import form.FieldData;
import form.Form;

public interface FormController {
    void setForm(Form form);
    Map<FormField, FieldData<?>> getFormData();
}
