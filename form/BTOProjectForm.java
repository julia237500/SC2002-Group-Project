package form;

import config.FlatType;
import config.FormField;
import form.field.DateField;
import form.field.NumField;
import form.field.TextField;
import model.BTOProject;

public class BTOProjectForm extends Form{
    private BTOProject editingBTOProject;

    public BTOProjectForm(){
        editingBTOProject = null;
    }

    public BTOProjectForm(BTOProject editingBTOProject){
        this.editingBTOProject = editingBTOProject;
    }

    @Override
    public String getTitle() {
        return "BTO Project";
    }

    @Override
    public void initFields() {
        if(editingBTOProject == null){
            initFieldsForNewForm();
        }
        else{
            initFieldsForEditForm();
        }
    }

    private void initFieldsForNewForm(){
        addFields(new TextField("Name", FormField.NAME));
        addFields(new TextField("Neighbourhood", FormField.NEIGHBORHOOD));

        for(FlatType flatType:FlatType.values()){
            addFields(new NumField("Number of %s".formatted(flatType.getStoredString()), flatType.getNumFormField(), 0, Integer.MAX_VALUE));
            addFields(new NumField("Price of %s".formatted(flatType.getStoredString()), flatType.getPriceFormField(), 0, Integer.MAX_VALUE));
        }

        addFields(new DateField("Opening Date", FormField.OPENING_DATE));
        addFields(new DateField("Closing Date", FormField.CLOSING_DATE, false));
        addFields(new NumField("HBD Officer Limit", FormField.HBD_OFFICER_LIMIT, BTOProject.MIN_HDB_OFFICER_LIMIT, BTOProject.MAX_HDB_OFFICER_LIMIT));
    }

    private void initFieldsForEditForm(){
        addFields(new TextField("Name", editingBTOProject.getName(), FormField.NAME));
        addFields(new TextField("Neighbourhood", editingBTOProject.getNeighborhood(), FormField.NEIGHBORHOOD));

        for(FlatType flatType:FlatType.values()){
            addFields(new NumField("Number of %s".formatted(flatType.getStoredString()), editingBTOProject.getFlatNum(flatType), flatType.getNumFormField(), 0, Integer.MAX_VALUE));
            addFields(new NumField("Price of %s".formatted(flatType.getStoredString()), editingBTOProject.getFlatPrice(flatType), flatType.getPriceFormField(), 0, Integer.MAX_VALUE));
        }

        addFields(new DateField("Opening Date", editingBTOProject.getOpeningDate(), FormField.OPENING_DATE));
        addFields(new DateField("Closing Date", editingBTOProject.getClosingDate(), FormField.CLOSING_DATE, false));
        addFields(new NumField("HBD Officer Limit", editingBTOProject.getHDBOfficerLimit(), FormField.HBD_OFFICER_LIMIT, 0, BTOProject.MAX_HDB_OFFICER_LIMIT));
    }
}
