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

public class EnquiryCommandFactory {
    private static final DIManager diManager = DIManager.getInstance();

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
