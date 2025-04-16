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

public class EnquiryCommandFactory extends AbstractCommandFactory {
    private static final int EDIT_ENQUIRY_CMD = getCommandID(ENQUIRY_CMD, EDIT_CMD, 0);
    private static final int DELETE_ENQUIRY_CMD = getCommandID(ENQUIRY_CMD, DELETE_CMD, 0);
    private static final int REPLY_ENQUIRY_CMD = getCommandID(ENQUIRY_CMD, EDIT_CMD, 1);

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

    private static Command getShowEnquiryCommand(EnquiryController enquiryController, Enquiry enquiry) {
        String description = enquiry.getSubject();
        if(enquiry.getEnquiryStatus() == EnquiryStatus.REPLIED){
            description += " (Replied)";
        }

        return new LambdaCommand(description, () -> {
            enquiryController.showEnquiry(enquiry);
        });
    }

    public static Map<Integer, Command> getEnquiryOperationCommands(Enquiry enquiry) {
        final User user = sessionManager.getUser();
        final Map<Integer, Command> commands = new LinkedHashMap<>();

        addEnquiryUpdateRelatedCommands(user, enquiry, commands);

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));
        
        return commands;
    }

    private static void addEnquiryUpdateRelatedCommands(User user, Enquiry enquiry, Map<Integer, Command> commands) {
        final EnquiryController enquiryController = diManager.resolve(EnquiryController.class);

        final Command editEnquiryCommand = new LambdaCommand("Edit Enquiry", () -> {
            enquiryController.editEnquiry(enquiry);
        });

        final Command deleteEnquiryCommand = new LambdaCommand("Delete Enquiry", () -> {
            enquiryController.deleteEnquiry(enquiry);
        });

        final Command replyEnquiryCommand = new LambdaCommand("Reply Enquiry", () -> {
            enquiryController.replyEnquiry(enquiry);
        });

        if(enquiry.getEnquirer() == user && enquiry.canBeAltered()){
            commands.put(EDIT_ENQUIRY_CMD, editEnquiryCommand);
            commands.put(DELETE_ENQUIRY_CMD, deleteEnquiryCommand);
        }
        
        if(enquiry.getBTOProject().isHandlingBy(user) && enquiry.canBeAltered()){
            commands.put(REPLY_ENQUIRY_CMD, replyEnquiryCommand);
        }
    }
}
