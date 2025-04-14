package command.enquiry;

import command.Command;
import controller.interfaces.EnquiryController;

public class ShowAllEnquiriesCommand implements Command{
    private EnquiryController enquiryController;

    public ShowAllEnquiriesCommand(EnquiryController enquiryController){
        this.enquiryController = enquiryController;
    }

    @Override
    public void execute() {
        enquiryController.showAllEnquiries();
    }

    @Override
    public String getDescription() {
        return "Show All Enquiries";
    }
}
