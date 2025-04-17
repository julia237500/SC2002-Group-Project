package form;

import config.FormField;
import form.field.TextField;

/**
 * Represents a form for changing a user's password.
 * Contains fields for entering a new password and confirming it.
 */
public class ChangePasswordForm extends Form{

    /**
     * Returns the title of the form.
     * 
     * @return the form title, which is "Change Password"
     */
    @Override
    public String getTitle() {
        return "Change Password";
    }

    /**
     * Initializes the fields required for the change password form.
     * Includes:
     * <ul>
     *     <li>A password field for the new password</li>
     *     <li>A confirm password field to validate the user's intention to change password</li>
     * </ul>
     */
    @Override
    public void initFields() {
        addFields(new TextField("Password", FormField.PASSWORD));
        addFields(new TextField("Confirm Password", FormField.CONFIRM_PASSWORD));
    }
}
