package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import command.Command;
import command.LambdaCommand;
import command.general.MenuBackCommand;
import controller.interfaces.OfficerRegistrationController;
import model.OfficerRegistration;
import model.User;
import policy.interfaces.OfficerRegistrationPolicy;

/**
 * A factory class responsible for generating commands related to officer registrations.
 * Provides methods to generate command maps for displaying and approving officer registrations.
 */
public class OfficerRegistrationCommandFactory extends AbstractCommandFactory {
    private static final int APPROVE_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, EDIT_CMD, 0);
    private static final int REJECT_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, EDIT_CMD, 1);

    /**
     * Generates a map of commands to show a list of officer registrations.
     * Each command corresponds to one officer registration.
     *
     * @param officerRegistrations A list of officer registrations to be shown.
     * @return A map of index-command pairs, including a back command with key -1.
     */
    public static Map<Integer, Command> getShowRegistrationsCommands(List<OfficerRegistration> officerRegistrations) {
        final OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);

        final Map<Integer, Command> commands = new LinkedHashMap<>();
        
        int index = 1;
        for(OfficerRegistration officerRegistration:officerRegistrations){
            commands.put(index++, getShowOfficerRegistrationCommand(officerRegistrationController, officerRegistration));
        }

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));

        return commands;
    }

    private static Command getShowOfficerRegistrationCommand(OfficerRegistrationController officerRegistrationController, OfficerRegistration officerRegistration) {
        final String description = "%s - %s (%s)".formatted(
            officerRegistration.getBTOProject().getName(),
            officerRegistration.getHDBOfficer().getName(),
            officerRegistration.getRegistrationStatus().getStoredString()
        );
        
        return new LambdaCommand(description, () -> {
            officerRegistrationController.showOfficerRegistration(officerRegistration);
        });
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
        final User user = sessionManager.getUser();
        final Map<Integer, Command> commands = new LinkedHashMap<>();

        addOfficerRegistrationUpdateRelatedCommands(user, officerRegistration, commands);

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));
        return commands;
    }

    private static void addOfficerRegistrationUpdateRelatedCommands(User user, OfficerRegistration officerRegistration, Map<Integer, Command> commands) {
        final OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);
        final OfficerRegistrationPolicy officerRegistrationPolicy = diManager.resolve(OfficerRegistrationPolicy.class);

        final Command approveOfficerRegistrationCommand = new LambdaCommand("Approve Officer Registration", () -> {
            officerRegistrationController.approveOfficerRegistration(officerRegistration, true);
        });

        final Command rejectOfficerRegistrationCommand = new LambdaCommand("Reject Officer Registration", () -> {
            officerRegistrationController.approveOfficerRegistration(officerRegistration, false);
        });

        if(officerRegistrationPolicy.canApproveOfficerRegistration(user, officerRegistration, true).isAllowed()){
            commands.put(APPROVE_OFFICER_REGISTRATION_CMD, approveOfficerRegistrationCommand);
        }

        if(officerRegistrationPolicy.canApproveOfficerRegistration(user, officerRegistration, false).isAllowed()){
            commands.put(REJECT_OFFICER_REGISTRATION_CMD, rejectOfficerRegistrationCommand);
        }
    }
}
