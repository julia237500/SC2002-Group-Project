package command.enquiry;

import command.Command;
import config.EnquiryStatus;
import controller.interfaces.EnquiryController;
import model.Enquiry;

public class ShowEnquiryCommand implements Command{
    private EnquiryController enquiryController;
    private Enquiry enquiry;

    public ShowEnquiryCommand(EnquiryController enquiryController, Enquiry enquiry){
        this.enquiryController = enquiryController;
        this.enquiry = enquiry;
    }

    @Override
    public void execute() {
        enquiryController.showEnquiry(enquiry);
    }

    @Override
    public String getDescription() {
        String description = enquiry.getSubject();
        if(enquiry.getEnquiryStatus() == EnquiryStatus.REPLIED) description += " (Replied)";
        return description;
    }
}
