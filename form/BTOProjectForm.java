package form;

import config.FlatType;
import config.FormField;
import form.field.DateField;
import form.field.NumField;
import form.field.TextField;
import model.BTOProject;

/**
 * Implementation of {@link Form} to get input needed to create or edit a {@code BTOProject}.
 * Contains fields such as name, neighborhood, opening date, and others.
 * 
 * @see Form
 */
public class BTOProjectForm extends Form{

    /**
     * The BTO project being edited, or {@code null} if creating a new project.
     */
    private final BTOProject editingBTOProject;

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
    
    /**
     * Initializes the fields for a new BTO project form.
     */
    private void initFieldsForNewForm(){
        addField(new TextField("Name", FormField.NAME));
        addField(new TextField("Neighbourhood", FormField.NEIGHBORHOOD));

        for(FlatType flatType:FlatType.values()){
            addField(new NumField("Number of %s".formatted(flatType.getStoredString()), flatType.getNumFormField(), 0, Integer.MAX_VALUE));
            addField(new NumField("Price of %s".formatted(flatType.getStoredString()), flatType.getPriceFormField(), 0, Integer.MAX_VALUE));
        }

        addField(new DateField("Opening Date", FormField.OPENING_DATE));
        addField(new DateField("Closing Date", FormField.CLOSING_DATE, false));
        addField(new NumField("HBD Officer Limit", FormField.HBD_OFFICER_LIMIT, BTOProject.MIN_HDB_OFFICER_LIMIT, BTOProject.MAX_HDB_OFFICER_LIMIT));
    }

    /**
     * Initializes the fields for editing an existing BTO project.
     * Pre-fills the form with the current values from the project.
     */
    private void initFieldsForEditForm(){
        addField(new TextField("Name (Readonly)", editingBTOProject.getName(), FormField.NAME));
        addField(new TextField("Neighbourhood", editingBTOProject.getNeighborhood(), FormField.NEIGHBORHOOD));

        for(FlatType flatType:FlatType.values()){
            addField(new NumField("Number of %s".formatted(flatType.getStoredString()), editingBTOProject.getFlatNum(flatType), flatType.getNumFormField(), 0, Integer.MAX_VALUE));
            addField(new NumField("Price of %s".formatted(flatType.getStoredString()), editingBTOProject.getFlatPrice(flatType), flatType.getPriceFormField(), 0, Integer.MAX_VALUE));
        }

        addField(new DateField("Opening Date", editingBTOProject.getOpeningDate(), FormField.OPENING_DATE));
        addField(new DateField("Closing Date", editingBTOProject.getClosingDate(), FormField.CLOSING_DATE, false));
        addField(new NumField("HBD Officer Limit", editingBTOProject.getHDBOfficerLimit(), FormField.HBD_OFFICER_LIMIT, 0, BTOProject.MAX_HDB_OFFICER_LIMIT));
    }
}
