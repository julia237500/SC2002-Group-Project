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

public class OfficerRegistrationCommandFactory extends AbstractCommandFactory {
    private static final int APPROVE_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, EDIT_CMD, 0);
    private static final int REJECT_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, EDIT_CMD, 1);

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
