package form;

import config.FormField;
import form.field.TextField;
import model.Enquiry;

/**
 * Represents a form for submitting and editing an enquiry.
 * Contains fields for entering the subject and the enquiry itself.
 */
public class EnquiryForm extends Form{

    /**
     * The enquiry that is being edited, or null if a new enquiry is being created.
     */
    private Enquiry editingEnquiry;

    /**
     * Constructs a new, empty enquiry form.
     */
    public EnquiryForm() {
        editingEnquiry = null;
    }

    /**
     * Constructs a form for editing an existing enquiry.
     *
     * @param editingEnquiry the enquiry to be edited
     */
    public EnquiryForm(Enquiry editingEnquiry){
        this.editingEnquiry = editingEnquiry;
    }

    /**
     * Returns the title of the form.
     * 
     * @return the form title, which is "Enquiry"
     */
    @Override
    public String getTitle() {
        return "Enquiry";
    }

    /**
     * Initializes the fields for the enquiry form based on whether
     * it's a new enquiry or an edit of an existing enquiry.
     */
    @Override
    public void initFields() {
        if(editingEnquiry == null){
            initFieldsForNewForm();
        }
        else{
            initFieldsForEditForm();
        }
    }
    
    /**
     * Initializes the fields for a new enquiry form.
     * This will include fields for entering the subject and the enquiry text.
     */
    private void initFieldsForNewForm(){
        addFields(new TextField("Subject", FormField.SUBJECT));
        addFields(new TextField("Enquiry", FormField.ENQUIRY));
    }

    /**
     * Initializes the fields for an edit enquiry form.
     * This will pre-fill the fields with the existing enquiry data.
     */
    private void initFieldsForEditForm(){
        addFields(new TextField("Subject", editingEnquiry.getSubject(), FormField.SUBJECT));
        addFields(new TextField("Enquiry", editingEnquiry.getEnquiry(), FormField.ENQUIRY));
    }
}
