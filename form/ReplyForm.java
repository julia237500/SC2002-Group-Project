package form;

import config.FormField;
import form.field.TextField;

public class ReplyForm extends Form{
    @Override
    public String getTitle() {
        return "Reply";
    }

    @Override
    public void initFields() {
        addFields(new TextField("Reply", FormField.REPLY));
    }
    
}
