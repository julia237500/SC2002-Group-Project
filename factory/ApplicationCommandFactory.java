package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import command.LambdaCommand;
import command.general.MenuBackCommand;
import controller.interfaces.ApplicationController;
import model.Application;
import model.User;
import policy.interfaces.ApplicationPolicy;

/**
 * A factory class for generating {@link Command} instances related to {@link Application}.
 * <p>
 * Commands are generated and stored in a {@code Map<Integer, Command>}.
 * This factory can generate commands for the following operations:
 * <ol>
 *   <li> Display details for a list of applications.
 *   <li> Execute operations on a specific application.
 * </ol>
 * <p>
 * Commands are only generated if they are permissible by the user, as defined in 
 * {@link ApplicationPolicy}.
 * 
 * @see Command
 * @see Application
 * @see ApplicationPolocy
 */
public class ApplicationCommandFactory extends AbstractCommandFactory{
    private static final int APPROVE_APPLICATION_CMD = getCommandID(APPLICATION_CMD, EDIT_CMD, 0);
    private static final int REJECT_APPLICATION_CMD = getCommandID(APPLICATION_CMD, EDIT_CMD, 1);
    private static final int BOOK_APPLICATION_CMD = getCommandID(APPLICATION_CMD, EDIT_CMD, 2);
    private static final int GENERATE_RECEIPT_CMD = getCommandID(APPLICATION_CMD, OTHER_OPERATION_CMD, 0);
    private static final int WITHDRAW_APPLICATION_CMD = getCommandID(APPLICATION_CMD, EDIT_CMD, 3);
    private static final int APPROVE_WITHDRAW_APPLICATION_CMD = getCommandID(APPLICATION_CMD, EDIT_CMD, 4);
    private static final int REJECT_WITHDRAW_APPLICATION_CMD = getCommandID(APPLICATION_CMD, EDIT_CMD, 5);

    /**
     * Generates a set of {@link Command} to display details for a list of {@link Application}.
     * <p>
     * Each application is mapped to a numbered command that triggers a view action.
     * A "Back" command is also included at the end of the list.
     *
     * @param applications the list of applications to be displayed
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     * @see Application
     */
    public static Map<Integer, Command> getShowApplicationsCommands(List<Application> applications) {
        final Map<Integer, Command> commands = new LinkedHashMap<>();

        final ApplicationController applicationController = diManager.resolve(ApplicationController.class);

        int index = 1;
        for(Application application:applications){
            commands.put(index++, getShowApplicationCommand(application, applicationController));
        }

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));

        return commands;
    }

    /**
     * Creates a {@link Command} to show details of a single {@link Application}.
     * <p>
     * The command's description includes the project name, applicant name, 
     * and current application status.
     *
     * @param application the application to be shown
     * @param applicationController the controller to execute the view action
     * @return a command that displays the application's details
     * 
     * @see Command
     * @see Application
     * @see ApplicationController
     */
    private static Command getShowApplicationCommand(Application application, ApplicationController applicationController) {
        final String description = "%s - %s (%s)".formatted(
            application.getBTOProject().getName(), 
            application.getApplicant().getName(), 
            application.getApplicationStatus().getStoredString()
        );

        return new LambdaCommand(description, () -> {
            applicationController.showApplication(application);
        });
    }

    /**
     * Generates a set of {@link Command} related to actions that can be performed on a single {@link Application}.
     * <p>
     * Includes approval, booking, receipt generation, and withdrawal-related commands 
     * depending on the current user’s permissions.
     * A "Back" command is included at the end.
     *
     * @param application the application to perform operations on
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     * @see Application
     */
    public static Map<Integer, Command> getApplicationOperationCommands(Application application) {  
        final Map<Integer, Command> commands = new LinkedHashMap<>();
        final User user = sessionManager.getUser();

        final ApplicationController applicationController = diManager.resolve(ApplicationController.class);
        final ApplicationPolicy applicationPolicy = diManager.resolve(ApplicationPolicy.class);

        getApplicationUpdateRelatedCommands(user, application, commands, applicationController, applicationPolicy);
        getApplicationWithdrawalRelatedCommands(user, application, commands, applicationController, applicationPolicy);
        
        commands.put(BACK_CMD, new MenuBackCommand(menuManager));

        return commands;
    }

    /**
     * Adds {@link Command} related to updating the {@link Application} such as approving, 
     * rejecting, booking, and generating a receipt.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link ApplicationPolicy}.
     *
     * @param user the current user
     * @param application the application being modified
     * @param commands the command map to add to
     * @param applicationController the controller handling application actions
     * @param applicationPolicy the policy defining allowed actions
     * 
     * @see Command
     * @see Application
     * @see ApplicationController
     * @see ApplicationPolicy
     */
    private static void getApplicationUpdateRelatedCommands(User user, Application application, Map<Integer, Command> commands, ApplicationController applicationController, ApplicationPolicy applicationPolicy) {
        final Command approveApplicationCommand = new LambdaCommand("Approve Application", () -> {
            applicationController.approveApplication(application, true);
        });

        final Command rejectApplicationCommand = new LambdaCommand("Reject Application", () -> {
            applicationController.approveApplication(application, false);
        });

        final Command bookApplicationCommand = new LambdaCommand("Book Application", () -> {
            applicationController.bookApplication(application);
        });

        final Command generateReceiptCommand = new LambdaCommand("Generate Receipt" , () -> {
            applicationController.generateReceipt(application);
        });

        if(applicationPolicy.canApproveApplication(user, application, true).isAllowed()){
            commands.put(APPROVE_APPLICATION_CMD, approveApplicationCommand);
        }

        if(applicationPolicy.canApproveApplication(user, application, false).isAllowed()){
            commands.put(REJECT_APPLICATION_CMD, rejectApplicationCommand);
        }

        if(applicationPolicy.canBookApplication(user, application).isAllowed()){
            commands.put(BOOK_APPLICATION_CMD, bookApplicationCommand);
        }

        if(applicationPolicy.canGenerateReceipt(user, application).isAllowed()){
            commands.put(GENERATE_RECEIPT_CMD, generateReceiptCommand);
        }
    }

    /**
     * Adds {@link Command} related to withdrawing an {@link Application} such as requesting,
     * approving, and rejecting.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link ApplicationPolicy}.
     *
     * @param user the current user
     * @param application the application being modified
     * @param commands the command map to add to
     * @param applicationController the controller handling application actions
     * @param applicationPolicy the policy defining allowed actions
     * 
     * @see Command
     * @see Application
     * @see ApplicationController
     * @see ApplicationPolicy
     */
    private static void getApplicationWithdrawalRelatedCommands(User user, Application application, Map<Integer, Command> commands, ApplicationController applicationController, ApplicationPolicy applicationPolicy) {
        final Command withdrawApplicationCommand = new LambdaCommand("Withdraw Application", () -> {
            applicationController.withdrawApplication(application);
        });

        final Command approveWithdrawApplicationCommand = new LambdaCommand("Approve Withdrawal", () -> {
            applicationController.approveWithdrawApplication(application, true);
        });

        final Command rejectWithdrawApplicationCommand = new LambdaCommand("Reject Withdrawal", () -> {
            applicationController.approveWithdrawApplication(application, false);
        });

        if(applicationPolicy.canWithdrawApplication(user, application).isAllowed()){
            commands.put(WITHDRAW_APPLICATION_CMD, withdrawApplicationCommand);
        }

        if(applicationPolicy.canApproveWithdrawApplication(user, application).isAllowed()){
            commands.put(APPROVE_WITHDRAW_APPLICATION_CMD, approveWithdrawApplicationCommand);
            commands.put(REJECT_WITHDRAW_APPLICATION_CMD, rejectWithdrawApplicationCommand);
        }
    }
}
