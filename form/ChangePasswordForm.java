package form;

import config.FormField;
import form.field.TextField;

/**
 * Implementation of {@link Form} to get input needed to change a user's password.
 * Contains fields for entering a new password and confirming it.
 * 
 * @see Form
 */
public class ChangePasswordForm extends Form{
    @Override
    public String getTitle() {
        return "Change Password";
    }

    @Override
    public void initFields() {
        addField(new TextField("Password", FormField.PASSWORD));
        addField(new TextField("Confirm Password", FormField.CONFIRM_PASSWORD));
    }
}
