package form;

import config.FlatType;
import config.FormField;
import form.field.BoolField;
import form.field.TextField;

public class BTOProjectFilterForm extends Form{
    @Override
    public String getTitle() {
        return "BTO Project Filter";
    }

    @Override
    public void initFields() {
        addFields(new TextField("Neighborhood", FormField.NEIGHBORHOOD));

        for(FlatType flatType:FlatType.values()){
            addFields(new BoolField("Has available %s".formatted(flatType.getStoredString()), flatType.getFilterFormField()));
        }
    }
    
}
