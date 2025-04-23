package form;

import config.FlatType;
import config.MaritalStatus;
import form.field.BoolField;

/**
 * Implementation of {@link Form} to get input needed to filter {@code Application}.
 * This form is for filtering using {@code MaritalStatus} and {@code FlatType}.
 * 
 * @see Form
 */
public class ApplicationFilterForm extends Form{
    @Override
    public String getTitle() {
        return "Application Filter";
    }

    @Override
    public void initFields() {
        for(MaritalStatus maritalStatus:MaritalStatus.values()){
            addField(new BoolField("Is %s".formatted(maritalStatus.getStoredString()), maritalStatus.getFilterFormField()));
        }

        for(FlatType flatType:FlatType.values()){
            addField(new BoolField("Applied for %s".formatted(flatType.getStoredString()), flatType.getFilterFormField()));
        }
    }
}
