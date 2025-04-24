package form;

import config.FormField;
import form.field.TextField;

/**
 * Implementation of {@link Form} to get input needed for for submitting a reply.
 * Consists only reply field.
 * 
 * @see Form
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
        addField(new TextField("Reply", FormField.REPLY));
    }
    
}
