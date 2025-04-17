package view.terminal;

import config.UserRole;
import model.BTOProject;
import model.OfficerRegistration;
import view.interfaces.OfficerRegistrationView;


/**
 * Terminal-based implementation of the {@link OfficerRegistrationView} interface.
 * <p>
 * This view is responsible for displaying the details of an officer registration for a BTO project.
 * Depending on the user role, it shows different levels of information about the officer registration.
 * </p>
 */
public class TerminalOfficerRegistrationView extends AbstractTerminalView implements OfficerRegistrationView{

    /**
     * Displays the details of the officer registration for a BTO project in the terminal.
     * The information displayed varies depending on the user role.
     * 
     * @param showingTo the {@link UserRole} representing the role of the user requesting the information
     * @param officerRegistration the {@link OfficerRegistration} containing the registration details to be displayed
     */
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
