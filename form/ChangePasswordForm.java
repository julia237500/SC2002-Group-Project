package form;

import config.FormField;
import form.field.TextField;

public class ChangePasswordForm extends Form{
    @Override
    public String getTitle() {
        return "Change Password";
    }

    @Override
    public void initFields() {
        addFields(new TextField("Password", FormField.PASSWORD));
        addFields(new TextField("Confirm Password", FormField.CONFIRM_PASSWORD));
    }
}
