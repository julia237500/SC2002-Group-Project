package form;

import config.FormField;
import form.field.TextField;

/**
 * Represents the form used for user login.
 * <p>
 * This form includes fields for NRIC and password input,
 * and it extends the abstract {@link Form} class.
 */
public class LoginForm extends Form{

    /**
     * Returns the title of the login form.
     *
     * @return A string representing the title of the form ("Login").
     */
    @Override
    public String getTitle() {
        return "Login";
    }

    /**
     * Initializes the fields required for the login form.
     * <p>
     * This includes:
     * <ul>
     *   <li>NRIC field for user identification</li>
     *   <li>Password field for authentication</li>
     * </ul>
     */
    @Override
    public void initFields() {
        addFields(new TextField("NRIC", FormField.NRIC));
        addFields(new TextField("Password", FormField.PASSWORD));
    }
}
