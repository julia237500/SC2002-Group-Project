package form;

import config.FlatType;
import config.MaritalStatus;
import form.field.BoolField;

public class ApplicationFilterForm extends Form{
    @Override
    public String getTitle() {
        return "Application Filter";
    }

    @Override
    public void initFields() {
        for(MaritalStatus maritalStatus:MaritalStatus.values()){
            addFields(new BoolField("Is %s".formatted(maritalStatus.getStoredString()), maritalStatus.getFilterFormField()));
        }

        for(FlatType flatType:FlatType.values()){
            addFields(new BoolField("Applied for %s".formatted(flatType.getStoredString()), flatType.getFilterFormField()));
        }
    }
}
