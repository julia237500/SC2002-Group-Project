package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import command.enquiry.DeleteEnquiryCommand;
import command.enquiry.EditEnquiryCommand;
import command.enquiry.ReplyEnquiryCommand;
import command.enquiry.ShowEnquiryCommand;
import command.general.MenuBackCommand;
import controller.interfaces.EnquiryController;
import manager.DIManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.Enquiry;
import model.User;
import view.interfaces.ConfirmationView;


/**
 * Factory class for creating command maps related to enquiry operations.
 * Generates context-sensitive commands based on:
 * <ul>
 *   <li>User role and permissions</li>
 *   <li>Enquiry state (whether it can be altered/replied to)</li>
 *   <li>Project handling status</li>
 * </ul>
 * 
 * <p><b>Command Numbering Scheme:</b>
 * <ul>
 *   <li><b>1-9:</b> Enquirer operations (edit/delete)</li>
 *   <li><b>10-19:</b> Officer/Manager operations (reply)</li> 
 *   <li><b>-1:</b> Always the back command</li>
 * </ul>
 */
public class EnquiryCommandFactory {
    private static final DIManager diManager = DIManager.getInstance();


    /**
     * Creates a command map for displaying a list of enquiries.
     * 
     * @param enquiries the list of enquiries to display
     * @return Map of numbered commands where:
     *         <ul>
     *           <li>Keys 1-n correspond to ShowEnquiryCommand for each enquiry</li>
     *           <li>Key -1 is the MenuBackCommand</li>
     *         </ul>
     */
    public static Map<Integer, Command> getShowEnquiriesCommands(List<Enquiry> enquiries) {
        EnquiryController enquiryController = diManager.resolve(EnquiryController.class);

        Map<Integer, Command> commands = new LinkedHashMap<>();
        MenuManager menuManager = diManager.resolve(MenuManager.class);
        
        int index = 1;
        for(Enquiry enquiry:enquiries){
            commands.put(index++, new ShowEnquiryCommand(enquiryController, enquiry));
        }

        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }

    /**
     * Creates a context-sensitive command map for enquiry operations.
     * Command availability depends on:
     * <ul>
     *   <li>Whether user is the original enquirer (for edit/delete)</li>
     *   <li>Whether user handles the project (for replies)</li>
     *   <li>Whether enquiry is in alterable state</li>
     * </ul>
     * 
     * @param enquiry the enquiry to operate on
     * @return Map containing available commands with:
     *         <ul>
     *           <li>1: Edit (enquirer only)</li>
     *           <li>2: Delete (enquirer only)</li>
     *           <li>3: Reply (project handlers only)</li>
     *           <li>-1: Back command</li>
     *         </ul>
     */
    public static Map<Integer, Command> getEnquiryOperationCommands(Enquiry enquiry) {
        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        User user = sessionManager.getUser();

        Map<Integer, Command> commands = new LinkedHashMap<>();

        EnquiryController enquiryController = diManager.resolve(EnquiryController.class);
        MenuManager menuManager = diManager.resolve(MenuManager.class);
        ConfirmationView confirmationView = diManager.resolve(ConfirmationView.class);

        if(enquiry.getEnquirer() == user && enquiry.canBeAltered()){
            commands.put(1, new EditEnquiryCommand(enquiryController, enquiry));
            commands.put(2, new DeleteEnquiryCommand(enquiryController, enquiry, menuManager, confirmationView));
        }
        
        if(enquiry.getBtoProject().isHandlingBy(user) && enquiry.canBeAltered()){
            commands.put(3, new ReplyEnquiryCommand(enquiryController, enquiry, menuManager));
        }

        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }
}