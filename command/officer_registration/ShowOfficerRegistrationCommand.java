package command.officer_registration;

import command.Command;
import controller.interfaces.OfficerRegistrationController;
import model.OfficerRegistration;

public class ShowOfficerRegistrationCommand implements Command{
    private OfficerRegistrationController officerRegistrationController;
    private OfficerRegistration officerRegistration;
    private boolean isShowingAction;
    
    public ShowOfficerRegistrationCommand(OfficerRegistrationController officerRegistrationController, OfficerRegistration officerRegistration){
        this.officerRegistrationController = officerRegistrationController;
        this.officerRegistration = officerRegistration;
        this.isShowingAction = false;
    }

    public ShowOfficerRegistrationCommand(OfficerRegistrationController officerRegistrationController, OfficerRegistration officerRegistration, boolean isShowingAction){
        this.officerRegistrationController = officerRegistrationController;
        this.officerRegistration = officerRegistration;
        this.isShowingAction = isShowingAction;
    }

    @Override
    public void execute() {
        officerRegistrationController.showOfficerRegistration(officerRegistration);
    }

    @Override
    public String getDescription() {
        String description = "";
        if(!isShowingAction){
            description = "%s - %s".formatted(officerRegistration.getBTOProject().getName(), officerRegistration.getHDBOfficer().getName());
        }
        else{
            description = "View Registration Status";
        }
        
        if(officerRegistration.hasUnreadUpdate()) description += " (Updated)";
        return description;
    }
    
}
