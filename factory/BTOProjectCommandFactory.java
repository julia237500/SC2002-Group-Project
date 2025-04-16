package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import command.btoproject.DeleteBTOProjectCommand;
import command.btoproject.EditBTOProjectCommand;
import command.btoproject.ShowBTOProjectCommand;
import command.btoproject.ToggleBTOProjectVisibilityCommand;
import command.enquiry.AddEnquiryCommand;
import command.enquiry.ShowEnquiriesByBTOProjectCommand;
import command.general.MenuBackCommand;
import command.officer_registration.AddOfficerRegistrationCommand;
import command.officer_registration.ShowOfficerRegistrationCommand;
import command.officer_registration.ShowOfficerRegistrationsByBTOProjectCommand;
import config.UserRole;
import controller.interfaces.BTOProjectController;
import controller.interfaces.EnquiryController;
import controller.interfaces.OfficerRegistrationController;
import manager.DIManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import view.interfaces.ConfirmationView;


/**
 * Factory class that creates command maps for BTO project operations.
 * Generates context-sensitive command menus based on:
 * <ul>
 *   <li>The current user's role</li>
 *   <li>Project ownership/assignment status</li>
 *   <li>Existing registrations/enquiries</li>
 * </ul>
 * 
 * <p>Commands are organized into logical groups with numbered options:
 * <ul>
 *   <li>1-9: Manager operations</li>
 *   <li>10-19: Officer registration operations</li>
 *   <li>20-29: Enquiry operations</li>
 *   <li>-1: Always the back command</li>
 * </ul>
 */

/**
 * A factory class is a design pattern used to create objects without specifying 
 * the exact class of the object that will be created. 
 */
public class BTOProjectCommandFactory {
     private static final DIManager diManager = DIManager.getInstance();

    /**
     * Creates a command map for displaying BTO projects.
     * 
     * @param btoProjects the list of projects to display
     * @return Map of command options where:
     *         - Keys are menu numbers (1-n for projects, -1 for back)
     *         - Values are executable ShowBTOProjectCommand instances
     */
    public static Map<Integer, Command> getShowBTOProjectsCommands(List<BTOProject> btoProjects) {
        Map<Integer, Command> commands = new LinkedHashMap<>();

        BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        MenuManager menuManager = diManager.resolve(MenuManager.class);

        int index = 1;
        for(BTOProject btoProject:btoProjects){
            commands.put(index++, new ShowBTOProjectCommand(btoProjectController, btoProject));
        }

        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }

    /**
     * Creates a context-sensitive command map for project operations.
     * Command availability depends on:
     * <ul>
     *   <li>User role (Manager/Officer/Applicant)</li>
     *   <li>Project ownership (for managers)</li>
     *   <li>Existing officer registration status</li>
     *   <li>Project handling status</li>
     * </ul>
     * 
     * @param btoProject the project to operate on
     * @return Map of command options containing:
     *         - Manager commands (1-9) if authorized
     *         - Officer registration commands (10-19)
     *         - Enquiry commands (20-29)
     *         - Always includes back command (-1)
     */

    /**
     * This intentional grouping makes the commands:
     * Organized - Related commands appear near each other numerically
     * Discoverable - Users can predict where to find certain command types
     * Extensible - There is room to add more commands in each category (e.g., could add command #12 for another officer action)
     */
    public static Map<Integer, Command> getBTOProjectsOperationCommands(BTOProject btoProject) {
        Map<Integer, Command> commands = new LinkedHashMap<>();

        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        User user = sessionManager.getUser();

        BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);
        EnquiryController enquiryController = diManager.resolve(EnquiryController.class);

        ConfirmationView confirmationView = diManager.resolve(ConfirmationView.class);
        MenuManager menuManager = diManager.resolve(MenuManager.class);

        if(user.getUserRole() == UserRole.HDB_MANAGER && btoProject.getHDBManager() == user){
            commands.put(1, new EditBTOProjectCommand(btoProjectController, btoProject));
            commands.put(2, new ToggleBTOProjectVisibilityCommand(btoProjectController, btoProject));
            commands.put(3, new DeleteBTOProjectCommand(btoProjectController, confirmationView, menuManager, btoProject));
            commands.put(4, new ShowOfficerRegistrationsByBTOProjectCommand(officerRegistrationController, btoProject));
        }

        if(user.getUserRole() == UserRole.HDB_OFFICER){
            OfficerRegistration officerRegistration = officerRegistrationController.getOfficerRegistrationByOfficerAndBTOProject(btoProject);
            if(officerRegistration == null){
                commands.put(10, new AddOfficerRegistrationCommand(officerRegistrationController, btoProjectController, menuManager, btoProject, confirmationView));
            }
            else{
                commands.put(11, new ShowOfficerRegistrationCommand(officerRegistrationController, officerRegistration, true));
            }
        }

        if(user.getUserRole() == UserRole.APPLICANT || (user.getUserRole() == UserRole.HDB_OFFICER && !btoProject.isHandlingBy(user))){
            commands.put(20, new AddEnquiryCommand(enquiryController, btoProject));
        } 

        if(user.getUserRole() == UserRole.HDB_MANAGER || btoProject.isHandlingBy(user)){
            commands.put(21, new ShowEnquiriesByBTOProjectCommand(enquiryController, btoProject));
        }

        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
        
    }
}
