package command.officer_registration;

import command.Command;
import controller.interfaces.OfficerRegistrationController;
import manager.interfaces.MenuManager;
import model.OfficerRegistration;
import view.interfaces.ConfirmationView;

public class ApproveOfficerRegistrationCommand implements Command{
    private OfficerRegistrationController officerRegistrationController;
    private OfficerRegistration officerRegistration;
    private boolean isApproving;
    private ConfirmationView confirmationView;
    private MenuManager menuManager;

    public ApproveOfficerRegistrationCommand(OfficerRegistrationController officerRegistrationController, OfficerRegistration officerRegistration, boolean isApproving, ConfirmationView confirmationView, MenuManager menuManager){
        this.officerRegistrationController = officerRegistrationController;
        this.officerRegistration = officerRegistration;
        this.isApproving = isApproving;
        this.confirmationView = confirmationView;
        this.menuManager = menuManager;
    }

    @Override
    public void execute() {
        if(confirmationView.getConfirmation()){
            officerRegistrationController.approveOfficerRegistration(officerRegistration, isApproving);
            menuManager.back();
            officerRegistrationController.showOfficerRegistration(officerRegistration);
        }
    }

    @Override
    public String getDescription() {
        return isApproving ?
            "Approve Officer Registration" :
            "Reject Officer Registration";
    }
    
}
