package command.enquiry;

import command.Command;
import controller.interfaces.EnquiryController;

public class ShowEnquiriesByUserCommand implements Command{
    private EnquiryController enquiryController;

    public ShowEnquiriesByUserCommand(EnquiryController enquiryController){
        this.enquiryController = enquiryController;
    }

    @Override
    public void execute() {
       enquiryController.showEnquiriesByUser();
    }

    @Override
    public String getDescription() {
        return "Your enquiries";
    }

    
}
