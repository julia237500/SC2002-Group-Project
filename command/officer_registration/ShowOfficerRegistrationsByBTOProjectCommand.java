package command.officer_registration;

import command.Command;
import controller.interfaces.OfficerRegistrationController;
import model.BTOProject;

public class ShowOfficerRegistrationsByBTOProjectCommand implements Command{
    private OfficerRegistrationController officerRegistrationController;
    private BTOProject btoProject;

    public ShowOfficerRegistrationsByBTOProjectCommand(OfficerRegistrationController officerRegistrationController, BTOProject btoProject){
        this.officerRegistrationController = officerRegistrationController;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        officerRegistrationController.showOfficerRegistrationsByBTOProject(btoProject);
    }

    @Override
    public String getDescription() {
        return "List of Officer Registrations";
    }
}