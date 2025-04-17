package form;

import config.FlatType;
import config.FormField;
import form.field.DateField;
import form.field.NumField;
import form.field.TextField;
import model.BTOProject;

/**
 * Represents a form for creating or editing a BTO Project.
 * This form is responsible for initializing the fields required for the form
 * based on whether a new project is being created or an existing one is being edited.
 */
public class BTOProjectForm extends Form{

    /**
     * The BTO project being edited, or {@code null} if creating a new project.
     */
    private BTOProject editingBTOProject;

    /**
     * Constructs a new form for creating a new BTO project.
     */
    public BTOProjectForm(){
        editingBTOProject = null;
    }

    /**
     * Constructs a new form for editing an existing BTO project.
     * 
     * @param editingBTOProject the project to be edited
     */
    public BTOProjectForm(BTOProject editingBTOProject){
        this.editingBTOProject = editingBTOProject;
    }

    /**
     * Returns the title of the form.
     * 
     * @return the form title
     */
    @Override
    public String getTitle() {
        return "BTO Project";
    }

    /**
     * Initializes the form fields. Determines whether to create fields for a new
     * form or pre-fill them for editing an existing project.
     */
    @Override
    public void initFields() {
        if(editingBTOProject == null){
            initFieldsForNewForm();
        }
        else{
            initFieldsForEditForm();
        }
    }
    
    /**
     * Initializes the fields for a new BTO project form.
     */
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

    /**
     * Initializes the fields for editing an existing BTO project.
     * Pre-fills the form with the current values from the project.
     */
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
