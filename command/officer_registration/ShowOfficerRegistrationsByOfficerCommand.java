package command.officer_registration;

import command.Command;
import controller.interfaces.OfficerRegistrationController;

public class ShowOfficerRegistrationsByOfficerCommand implements Command{
    private OfficerRegistrationController officerRegistrationController;

    public ShowOfficerRegistrationsByOfficerCommand(OfficerRegistrationController officerRegistrationController){
        this.officerRegistrationController = officerRegistrationController;
    }

    @Override
    public void execute() {
        officerRegistrationController.showOfficerRegistrationsByOfficer();
    }

    @Override
    public String getDescription() {
        return "List of Officer Regist