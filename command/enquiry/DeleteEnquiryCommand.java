package command.enquiry;

import command.Command;
import controller.interfaces.EnquiryController;
import manager.interfaces.MenuManager;
import model.Enquiry;
import view.interfaces.ConfirmationView;

public class DeleteEnquiryCommand implements Command{
    private EnquiryController enquiryController;
    private Enquiry enquiry;
    private MenuManager menuManager;
    private ConfirmationView confirmationView;

    public DeleteEnquiryCommand(EnquiryController enquiryController, Enquiry enquiry, MenuManager menuManager, ConfirmationView confirmationView){
        this.enquiryController = enquiryController;
        this.enquiry = enquiry;
        this.menuManager = menuManager;
        this.confirmationView = confirmationView;
    }

    @Override
    public void execute() {
        if(confirmationView.getConfirmation()){
            enquiryController.deleteEnquiry(enquiry);

            menuManager.back();
            menuManager.back();
            enquiryController.showEnquiriesByUser();
        }
        else{
            enquiryController.showEnquiryDetail(enquiry);
        }
    }
    @Override
    public String getDescription() {
        return "Delete enquiry";
    }
}
