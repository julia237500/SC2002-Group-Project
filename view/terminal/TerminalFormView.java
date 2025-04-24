package view.terminal;

import form.field.Field;
import view.interfaces.FormView;


/**
 * Terminal-based implementation of the {@link FormView} interface.
 * <p>
 * This view is responsible for displaying a form to the user, including prompting for inputs and
 * displaying any constraints or original values associated with the form fields.
 * </p>
 */
public class TerminalFormView extends AbstractTerminalView implements FormView{

    /**
     * Displays the title of the form in the terminal.
     * 
     * @param title the title of the form to be displayed
     */
    public void show(String title){
        showTitle(title);
    }

    /**
     * Prompts the user for input for a given form field.
     * <p>
     * The user's input is returned as a trimmed string.
     * </p>
     * 
     * @param field the {@link Field} object representing the form field
     * @return the user input as a trimmed string
     */
    public String getInput(Field<?> field){
        showFieldPrompt(field);
        return sc.nextLine().trim();
    }

    /**
     * Displays the prompt for a given field, including its name, any constraints, and the original value.
     * <p>
     * The user is informed that leaving the field blank will retain the original value (if applicable).
     * </p>
     * 
     * @param field the {@link Field} object representing the form field to prompt for input
     */
    private void showFieldPrompt(Field<?> field){
        System.out.print(field.getName());
        if(field.getConstraint() != null) System.out.print(String.format(" (%s)", field.getConstraint()));
        System.out.print(": ");
        if(field.getOriginalValue() != null) System.out.print(String.format("%s -> (Blank to keep original value) ", field.getOriginalValue()));
    }
}
