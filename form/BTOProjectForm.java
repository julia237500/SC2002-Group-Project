package form;

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
        addFields(new NumField("Number of 2-Room Flat", FormField.TWO_ROOM_FLAT_NUM, 0, Integer.MAX_VALUE));
        addFields(new NumField("Price of 2-Room Flat", FormField.TWO_ROOM_FLAT_PRICE, 0, Integer.MAX_VALUE));
        addFields(new NumField("Number of 3-Room Flat", FormField.THREE_ROOM_FLAT_NUM, 0, Integer.MAX_VALUE));
        addFields(new NumField("Price of 3-Room Flat", FormField.THREE_ROOM_FLAT_PRICE, 0, Integer.MAX_VALUE));
        addFields(new DateField("Opening Date", FormField.OPENING_DATE));
        addFields(new DateField("Closing Date", FormField.CLOSING_DATE, false));
        addFields(new NumField("HBD Officer Limit", FormField.HBD_OFFICER_LIMIT, 0, BTOProject.MAX_HDB_OFFICER_LIMIT));
    }

    private void initFieldsForEditForm(){
        addFields(new TextField("Name", editingBTOProject.getName(), FormField.NAME));
        addFields(new TextField("Neighbourhood", editingBTOProject.getNeighborhood(), FormField.NEIGHBORHOOD));
        addFields(new NumField("Number of 2-Room Flat", editingBTOProject.getTwoRoomFlatNum(), FormField.TWO_ROOM_FLAT_NUM, 0, Integer.MAX_VALUE));
        addFields(new NumField("Price of 2-Room Flat", editingBTOProject.getTwoRoomFlatPrice(), FormField.TWO_ROOM_FLAT_PRICE, 0, Integer.MAX_VALUE));
        addFields(new NumField("Number of 3-Room Flat", editingBTOProject.getThreeRoomFlatNum(), FormField.THREE_ROOM_FLAT_NUM, 0, Integer.MAX_VALUE));
        addFields(new NumField("Price of 3-Room Flat", editingBTOProject.getThreeRoomFlatPrice(), FormField.THREE_ROOM_FLAT_PRICE, 0, Integer.MAX_VALUE));
        addFields(new DateField("Opening Date", editingBTOProject.getOpeningDate(), FormField.OPENING_DATE));
        addFields(new DateField("Closing Date", editingBTOProject.getClosingDate(), FormField.CLOSING_DATE, false));
        addFields(new NumField("HBD Officer Limit", editingBTOProject.getHDBOfficerLimit(), FormField.HBD_OFFICER_LIMIT, 0, BTOProject.MAX_HDB_OFFICER_LIMIT));
    }
}
