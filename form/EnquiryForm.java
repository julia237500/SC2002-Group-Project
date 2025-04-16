package form;

import config.FormField;
import form.field.TextField;
import model.Enquiry;

public class EnquiryForm extends Form{
    private Enquiry editingEnquiry;

    public EnquiryForm() {
        editingEnquiry = null;
    }

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
    
    private void initFieldsForNewForm(){
        addFields(new TextField("Subject", FormField.SUBJECT));
        addFields(new TextField("Enquiry", FormField.ENQUIRY));
    }

    private void initFieldsForEditForm(){
        addFields(new TextField("Subject", editingEnquiry.getSubject(), FormField.SUBJECT));
        addFields(new TextField("Enquiry", editingEnquiry.getEnquiry(), FormField.ENQUIRY));
    }
}
