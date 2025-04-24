package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import command.LambdaCommand;
import command.general.MenuBackCommand;
import config.EnquiryStatus;
import controller.interfaces.EnquiryController;
import model.Enquiry;
import model.User;
import policy.interfaces.EnquiryPolicy;

/**
 * A factory class for generating {@link Command} instances related to {@link Enquiry}.
 * <p>
 * Commands are generated and stored in a {@code Map<Integer, Command>}.
 * This factory can generate commands for the following operations:
 * <ol>
 *   <li> Display details for a list of enquiries.
 *   <li> Execute operations on a specific enquiry.
 * </ol>
 * <p>
 * Commands are only generated if they are permissible by the user, as defined in 
 * {@link EnquiryPolicy}.
 * 
 * @see Command
 * @see Enquiry
 * @see EnquiryPolicy
 */
public class EnquiryCommandFactory extends AbstractCommandFactory {
    private static final int EDIT_ENQUIRY_CMD = getCommandID(ENQUIRY_CMD, EDIT_CMD, 0);
    private static final int DELETE_ENQUIRY_CMD = getCommandID(ENQUIRY_CMD, DELETE_CMD, 0);
    private static final int REPLY_ENQUIRY_CMD = getCommandID(ENQUIRY_CMD, EDIT_CMD, 1);

    /**
     * Generates a set of {@link Command} to display details for a list of {@link Enquiry}.
     * <p>
     * Each enquiry is mapped to a numbered command that triggers a view action.
     * A "Back" command is also included at the end of the list.
     *
     * @param enquiries the list of enquiry to be displayed
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     * @see Enquiry
     */
    public static Map<Integer, Command> getShowEnquiriesCommands(List<Enquiry> enquiries) {
        final EnquiryController enquiryController = diManager.resolve(EnquiryController.class);

        final Map<Integer, Command> commands = new LinkedHashMap<>();
        
        int index = 1;
        for(Enquiry enquiry:enquiries){
            commands.put(index++, getShowEnquiryCommand(enquiryController, enquiry));
        }

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));

        return commands;
    }

    /**
     * Creates a {@link Command} to show details of a single {@link Enquiry}.
     * <p>
     * The command's description includes the enquiry name and 
     * replied status.
     *
     * @param enquiryController the controller to execute the view action
     * @param enquiry the enquiry to be shown
     * 
     * @return a command that displays the enquiry's details
     * 
     * @see Command
     * @see Enquiry
     * @see EnquiryController
     */
    private static Command getShowEnquiryCommand(EnquiryController enquiryController, Enquiry enquiry) {
        String description = enquiry.getSubject();
        if(enquiry.getEnquiryStatus() == EnquiryStatus.REPLIED){
            description += " (Replied)";
        }

        return new LambdaCommand(description, () -> {
            enquiryController.showEnquiry(enquiry);
        });
    }

    /**
     * Generates a set of {@link Command} related to actions that can be performed on a single {@link Enquiry}.
     * <p>
     * Includes editing, deleting, and replying-related commands 
     * depending on the current user’s permissions.
     * A "Back" command is included at the end.
     *
     * @param enquiry the enquiry to perform operations on
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     * @see Enquiry
     */
    public static Map<Integer, Command> getEnquiryOperationCommands(Enquiry enquiry) {
        User user = sessionManager.getUser();

        Map<Integer, Command> commands = new LinkedHashMap<>();

        addEnquiryUpdateRelatedCommands(user, enquiry, commands);

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));
        
        return commands;
    }
    
    /**
     * Adds {@link Command} related to updating the {@link Enquiry} such as editing, 
     * replying, and deleting.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link EnquiryPolicy}.
     *
     * @param user the current user
     * @param enquiry the enquiry being modified
     * @param commands the command map to add to
     * 
     * @see Command
     * @see Enquiry
     * @see EnquiryPolicy
     */
    private static void addEnquiryUpdateRelatedCommands(User user, Enquiry enquiry, Map<Integer, Command> commands) {
        final EnquiryController enquiryController = diManager.resolve(EnquiryController.class);
        final EnquiryPolicy enquiryPolicy = diManager.resolve(EnquiryPolicy.class);

        final Command editEnquiryCommand = new LambdaCommand("Edit Enquiry", () -> {
            enquiryController.editEnquiry(enquiry);
        });

        final Command deleteEnquiryCommand = new LambdaCommand("Delete Enquiry", () -> {
            enquiryController.deleteEnquiry(enquiry);
        });

        final Command replyEnquiryCommand = new LambdaCommand("Reply Enquiry", () -> {
            enquiryController.replyEnquiry(enquiry);
        });

        if(enquiryPolicy.canEditEnquiry(user, enquiry).isAllowed()){
            commands.put(EDIT_ENQUIRY_CMD, editEnquiryCommand);
        }

        if(enquiryPolicy.canDeleteEnquiry(user, enquiry).isAllowed()){
            commands.put(DELETE_ENQUIRY_CMD, deleteEnquiryCommand);
        }
        
        if(enquiryPolicy.canReplyEnquiry(user, enquiry).isAllowed()){
            commands.put(REPLY_ENQUIRY_CMD, replyEnquiryCommand);
        }
    }
}
