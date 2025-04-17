package view.interfaces;

import form.field.Field;

/**
 * Represents the interface for displaying forms and retrieving user input.
 *
 * <p>This interface is part of the view layer in an MVC (Model-View-Controller) architecture. 
 * It abstracts how a form is presented to the user and how input is collected for each field, 
 * allowing for flexible UI implementations (e.g., terminal, GUI, web).</p>
 */
public interface FormView {

    /**
     * Displays the form title or heading to the user.
     *
     * @param title The title of the form to be displayed.
     */
    void show(String title);

    /**
     * Prompts the user to enter input for the specified form field.
     *
     * @param field The {@link Field} object representing the form field to be filled.
     * @return The user input as a string.
     */
    String getInput(Field<?> field);
}
