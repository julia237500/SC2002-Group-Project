package form;

import config.FormField;
import form.field.TextField;
import model.Enquiry;

/**
 * Implementation of {@link Form} to get input needed to create and edit an enquiry.
 * Contains fields for entering the subject and the enquiry itself.
 */
public class EnquiryForm extends Form{
    /**
     * The enquiry that is being edited, or null if a new enquiry is being created.
     */
    private final Enquiry editingEnquiry;

    /**
     * Constructs a new form to create new enquiry.
     */
    public EnquiryForm() {
        editingEnquiry = null;
    }

    /**
     * Constructs a new form to edit an existing enquiry.
     *
     * @param editingEnquiry the enquiry to be edited
     */
    public EnquiryForm(Enquiry editingEnquiry){
        this.editingEnquiry = editingEnquiry;
    }

    @Override
    public String getTitle() {
        return "Enquiry";
    }

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
        addField(new TextField("Subject", FormField.SUBJECT));
        addField(new TextField("Enquiry", FormField.ENQUIRY));
    }

    /**
     * Initializes the fields for an edit enquiry form.
     * This will pre-fill the fields with the existing enquiry data.
     */
    private void initFieldsForEditForm(){
        addField(new TextField("Subject", editingEnquiry.getSubject(), FormField.SUBJECT));
        addField(new TextField("Enquiry", editingEnquiry.getEnquiry(), FormField.ENQUIRY));
    }
}
