package command.officer_registration;

import command.Command;
import controller.interfaces.BTOProjectController;
import controller.interfaces.OfficerRegistrationController;
import manager.interfaces.MenuManager;
import model.BTOProject;
import view.interfaces.ConfirmationView;

public class AddOfficerRegistrationCommand implements Command{
    private OfficerRegistrationController officerRegistrationController;
    private BTOProjectController btoProjectController;
    private MenuManager menuManager;
    private BTOProject btoProject;
    private ConfirmationView confirmationView;

    public AddOfficerRegistrationCommand(OfficerRegistrationController officerRegistrationController, BTOProjectController btoProjectController, MenuManager menuManager, BTOProject btoProject, ConfirmationView confirmationView){
        this.officerRegistrationController = officerRegistrationController;
        this.btoProjectController = btoProjectController;
        this.menuManager = menuManager;
        this.btoProject = btoProject;
        this.confirmationView = confirmationView;
    }

    @Override
    public void execute() {
        if(confirmationView.getConfirmation()){
            officerRegistrationController.addOfficerRegistration(btoProject);
        
            // Refresh List
            menuManager.back();
            btoProjectController.showBTOProject(btoProject);
        }
    }

    @Override
    public String getDescription() {
        return "Register as Officer";
    }
}
