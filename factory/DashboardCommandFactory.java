package factory;

import java.util.LinkedHashMap;
import java.util.Map;

import command.Command;
import command.btoproject.AddBTOProjectCommand;
import command.btoproject.ShowBTOProjectsCommand;
import command.enquiry.ShowAllEnquiriesCommand;
import command.enquiry.ShowEnquiriesByUserCommand;
import command.general.LogoutCommand;
import command.officer_registration.ShowOfficerRegistrationsByOfficerCommand;
import command.user.ChangePasswordCommand;
import config.UserRole;
import controller.interfaces.AuthController;
import controller.interfaces.BTOProjectController;
import controller.interfaces.EnquiryController;
import controller.interfaces.OfficerRegistrationController;
import manager.DIManager;
import manager.interfaces.ApplicationManager;
import manager.interfaces.SessionManager;
import model.User;

/**
 * Factory class that generates command maps for the main dashboard interface.
 * Creates role-specific command sets based on the current user's permissions.
 * 
 * <p><b>Command Numbering Scheme:</b>
 * <ul>
 *   <li><b>1-19:</b> Common user commands (change password, etc.)</li>
 *   <li><b>20-29:</b> BTO Project commands</li>
 *   <li><b>30-39:</b> Officer registration commands</li>
 *   <li><b>40-49:</b> Enquiry commands</li>
 *   <li><b>9:</b> Always logout command (reserved)</li>
 * </ul>
 */
public class DashboardCommandFactory {
    private static final DIManager diManager = DIManager.getInstance();

    public static Map<Integer, Command> getCommands() {
        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        User user = sessionManager.getUser();

        ApplicationManager applicationManager = diManager.resolve(ApplicationManager.class);
        AuthController authController = diManager.resolve(AuthController.class);
        BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);
        EnquiryController enquiryController = diManager.resolve(EnquiryController.class);

        /**
         * Generates a dashboard command map tailored to the current user's role.
         * @return Map of command options where:
         * Keys represent menu option numbers (following the numbering scheme)
         * - Values are executable Command instances
         */
        Map<Integer, Command> commands = new LinkedHashMap<>();
        commands.put(1, new ChangePasswordCommand(authController, applicationManager));

        commands.put(20, new ShowBTOProjectsCommand(btoProjectController));

        if(user.getUserRole() == UserRole.HDB_MANAGER){
            commands.put(21, new AddBTOProjectCommand(btoProjectController));
        }

        if(user.getUserRole() == UserRole.HDB_OFFICER){
            commands.put(30, new ShowOfficerRegistrationsByOfficerCommand(officerRegistrationController));
        }

        if(user.getUserRole() == UserRole.HDB_MANAGER){
            commands.put(40, new ShowAllEnquiriesCommand(enquiryController));
        }
        else{
            commands.put(41, new ShowEnquiriesByUserCommand(enquiryController));
        }

        commands.put(9, new LogoutCommand(applicationManager));

        return commands;
    }
}