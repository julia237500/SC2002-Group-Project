package form;

import config.FormField;
import form.field.TextField;

public class LoginForm extends Form{
    @Override
    public String getTitle() {
        return "Login";
    }

    @Override
    public void initFields() {
        addFields(new TextField("NRIC", FormField.NRIC));
        addFields(new TextField("Password", FormField.PASSWORD));
    }
}
