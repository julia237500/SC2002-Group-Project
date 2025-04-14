package command.enquiry;

import command.Command;
import controller.interfaces.EnquiryController;
import model.Enquiry;

public class EditEnquiryCommand implements Command{
    private EnquiryController enquiryController;
    private Enquiry enquiry;

    public EditEnquiryCommand(EnquiryController enquiryController, Enquiry enquiry){
        this.enquiryController = enquiryController;
        this.enquiry = enquiry;
    }

    @Override
    public void execute() {
        enquiryController.editEnquiry(enquiry);
        enquiryController.showEnquiryDetail(enquiry);
    }
    @Override
    public String getDescription() {
        return "Edit enquiry";
    }
}
