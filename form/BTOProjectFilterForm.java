package form;

import config.FlatType;
import config.FormField;
import form.field.BoolField;
import form.field.TextField;

/**
 * Implementation of {@link Form} to get input needed to filter {@code BTOProject},
 * This form is for searching using {@code neighborhood} and filtering using {@code FlatType}.
 * 
 * @see Form
 */
public class BTOProjectFilterForm extends Form{
    @Override
    public String getTitle() {
        return "BTO Project Filter";
    }

    @Override
    public void initFields() {
        addField(new TextField("Neighborhood", FormField.NEIGHBORHOOD));

        for(FlatType flatType:FlatType.values()){
            addField(new BoolField("Has available %s".formatted(flatType.getStoredString()), flatType.getFilterFormField()));
        }
    }
}
