package form;

import config.FormField;
import form.field.TextField;

/**
 * Represents the form used for submitting a reply.
 * <p>
 * This form is a simple implementation of the abstract {@link Form} class
 * and includes only a single text field for entering a reply.
 */
public class ReplyForm extends Form{

    /**
     * Returns the title of the reply form.
     *
     * @return A string representing the title of the form ("Reply").
     */
    @Override
    public String getTitle() {
        return "Reply";
    }

    /**
     * Initializes the fields required for the reply form.
     * <p>
     * This includes:
     * <ul>
     *   <li>A single text field for entering the reply content</li>
     * </ul>
     */
    @Override
    public void initFields() {
        addFields(new TextField("Reply", FormField.REPLY));
    }
    
}
