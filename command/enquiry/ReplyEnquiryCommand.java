package command.enquiry;

import command.Command;
import controller.interfaces.EnquiryController;
import manager.interfaces.MenuManager;
import model.Enquiry;

public class ReplyEnquiryCommand implements Command{
    private EnquiryController enquiryController;
    private Enquiry enquiry;
    private MenuManager menuManager;

    public ReplyEnquiryCommand(EnquiryController enquiryController, Enquiry enquiry, MenuManager menuManager){
        this.enquiryController = enquiryController;
        this.enquiry = enquiry;
        this.menuManager = menuManager;
    }   

    @Override
    public void execute() {
        enquiryController.replyEnquiry(enquiry);

        menuManager.back();
        enquiryController.showEnquiry(enquiry);
    }

    @Override
    public String getDescription() {
        return "Reply enquiry";
    }
    
}
