package form;

import config.FormField;
import form.field.TextField;

/**
 * Implementation of {@link Form} to get input needed for user login.
 * Consists fields such as NRIC and password input.
 * 
 * @see Form
 */
public class LoginForm extends Form{
    @Override
    public String getTitle() {
        return "Login";
    }

    @Override
    public void initFields() {
        addField(new TextField("NRIC", FormField.NRIC));
        addField(new TextField("Password", FormField.PASSWORD));
    }
}
