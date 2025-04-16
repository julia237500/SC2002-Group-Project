package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import command.Command;
import command.general.MenuBackCommand;
import command.officer_registration.ApproveOfficerRegistrationCommand;
import command.officer_registration.ShowOfficerRegistrationCommand;
import config.RegistrationStatus;
import config.UserRole;
import controller.interfaces.OfficerRegistrationController;
import manager.DIManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.OfficerRegistration;
import model.User;
import view.interfaces.ConfirmationView;

/**
 * A factory class responsible for generating commands related to officer registrations.
 * Provides methods to generate command maps for displaying and approving officer registrations.
 */
public class OfficerRegistrationCommandFactory {
    private static final DIManager diManager = DIManager.getInstance();

    /**
     * Generates a map of commands to show a list of officer registrations.
     * Each command corresponds to one officer registration.
     *
     * @param officerRegistrations A list of officer registrations to be shown.
     * @return A map of index-command pairs, including a back command with key -1.
     */
    public static Map<Integer, Command> getShowRegistrationsCommands(List<OfficerRegistration> officerRegistrations) {
        OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);

        Map<Integer, Command> commands = new LinkedHashMap<>();
        MenuManager menuManager = diManager.resolve(MenuManager.class);
        
        int index = 1;
        for(OfficerRegistration officerRegistration:officerRegistrations){
            commands.put(index++, new ShowOfficerRegistrationCommand(officerRegistrationController, officerRegistration));
        }

        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }

    /**
     * Generates a map of commands that allow a HDB Manager to approve or reject
     * a specific officer registration, based on the current session's user.
     *
     * Only available if:
     * - The user is an HDB Manager.
     * - The officer registration is pending.
     * - The current user is the assigned HDB manager of the registration's BTO project.
     *
     * Always includes a back command with key -1.
     *
     * @param officerRegistration The officer registration to perform actions on.
     * @return A map of index-command pairs for approval/rejection actions and back navigation.
     */
    public static Map<Integer, Command> getRegistrationOperationCommands(OfficerRegistration officerRegistration){
        OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);
        ConfirmationView confirmationView = diManager.resolve(ConfirmationView.class);

        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        User user = sessionManager.getUser();

        Map<Integer, Command> commands = new LinkedHashMap<>();
        MenuManager menuManager = diManager.resolve(MenuManager.class);

        if(user.getUserRole() == UserRole.HDB_MANAGER){
            if(officerRegistration.getRegistrationStatus() == RegistrationStatus.PENDING && officerRegistration.getBTOProject().getHDBManager() == user){
                commands.put(1, new ApproveOfficerRegistrationCommand(officerRegistrationController, officerRegistration, true, confirmationView, menuManager));
                commands.put(2, new ApproveOfficerRegistrationCommand(officerRegistrationController, officerRegistration, false, confirmationView, menuManager));
            }
        }

        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }
}
