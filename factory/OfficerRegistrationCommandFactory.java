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
 * A factory class for generating {@link Command} instances related to {@link OfficerRegistration}.
 * <p>
 * Commands are generated and stored in a {@code Map<Integer, Command>}.
 * This factory can generate commands for the following operations:
 * <ol>
 *   <li> Display details for a list of registrations.
 *   <li> Execute operations on a specific registration.
 * </ol>
 * <p>
 * Commands are only generated if they are permissible by the user, as defined in 
 * {@link OfficerRegistrationPolicy}.
 * 
 * @see Command
 * @see OfficerRegistration
 * @see OfficerRegistrationPolicy
 */
public class OfficerRegistrationCommandFactory extends AbstractCommandFactory {
    private static final int APPROVE_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, EDIT_CMD, 0);
    private static final int REJECT_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, EDIT_CMD, 1);

    /**
     * Generates a set of {@link Command} to display details for a list of {@link OfficerRegistration}.
     * <p>
     * Each registration is mapped to a numbered command that triggers a view action.
     * A "Back" command is also included at the end of the list.
     *
     * @param officerRegistrations the list of registrations to be displayed
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     * @see OfficerRegistration
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

    /**
     * Creates a {@link Command} to show details of a single {@link OfficerRegistration}.
     * <p>
     * The command's description includes the project name, officer name and 
     * registration status.
     *
     * @param officerRegistrationController the controller to execute the view action
     * @param officerRegistration the registration to be shown
     * 
     * @return a command that displays the registration's details
     * 
     * @see Command
     * @see OfficerRegistration
     * @see OfficerRegistrationController
     */
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
     * Generates a set of {@link Command} related to actions that can be performed on a single {@link OfficerRegistration}.
     * <p>
     * Includes editing, deleting, and replying-related commands 
     * depending on the current user’s permissions.
     * A "Back" command is included at the end.
     *
     * @param officerRegistration the registration to perform operations on
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     * @see OfficerRegistration
     */
    public static Map<Integer, Command> getRegistrationOperationCommands(OfficerRegistration officerRegistration){
        final User user = sessionManager.getUser();
        final Map<Integer, Command> commands = new LinkedHashMap<>();

        addOfficerRegistrationUpdateRelatedCommands(user, officerRegistration, commands);

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));
        return commands;
    }

    /**
     * Adds {@link Command} related to updating the {@link OfficerRegistration} 
     * such as approving and rejecting.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link OfficerRegistrationPolicy}.
     *
     * @param user the current user
     * @param officerRegistration the registration being modified
     * @param commands the command map to add to
     * 
     * @see Command
     * @see OfficerRegistration
     * @see OfficerRegistrationPolicy
     */
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
