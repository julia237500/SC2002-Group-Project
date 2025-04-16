package view.terminal;

import config.UserRole;
import model.BTOProject;
import model.OfficerRegistration;
import view.interfaces.OfficerRegistrationView;

public class TerminalOfficerRegistrationView extends AbstractTerminalView implements OfficerRegistrationView{
    @Override
    public void showOfficerRegistrationDetail(UserRole showingTo, OfficerRegistration officerRegistration) {
        showTitle("Officer Registration");
        BTOProject btoProject = officerRegistration.getBTOProject();

        if(showingTo == UserRole.HDB_OFFICER){
            System.out.println("""
                BTO Project: %s
                Number of Officers in Charge: %d/%d
                Status: %s
                """.formatted(
                    btoProject.getName(), 
                    btoProject.getHDBOfficers().size(),
                    btoProject.getHDBOfficerLimit(),
                    officerRegistration.getRegistrationStatus()));
        }
        else if(showingTo == UserRole.HDB_MANAGER){
            System.out.println("""
                BTO Project: %s
                Number of Officers in Charge: %d/%d
                Officer Name: %s
                Status: %s
                """.formatted(
                    btoProject.getName(), 
                    btoProject.getHDBOfficers().size(),
                    btoProject.getHDBOfficerLimit(),
                    officerRegistration.getHDBOfficer().getName(), 
                    officerRegistration.getRegistrationStatus()));
        }
    }
    
}
