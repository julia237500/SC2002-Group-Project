package view.terminal;

import config.UserRole;
import model.OfficerRegistration;
import view.interfaces.OfficerRegistrationView;

public class TerminalOfficerRegistrationView extends AbstractTerminalView implements OfficerRegistrationView{
    @Override
    public void showOfficerRegistrationDetail(UserRole showingTo, OfficerRegistration officerRegistration) {
        showTitle("Officer Registration");

        if(showingTo == UserRole.HDB_OFFICER){
            System.out.println("""
                BTO Project: %s
                Status: %s
                """.formatted(officerRegistration.getBTOProject().getName(), officerRegistration.getRegistrationStatus()));
        }
        else if(showingTo == UserRole.HDB_MANAGER){
            System.out.println("""
                BTO Project: %s
                Officer Name: %s
                Status: %s
                """.formatted(officerRegistration.getBTOProject().getName(), officerRegistration.getHDBOfficer().getName(), officerRegistration.getRegistrationStatus()));
        }
    }
    
}
