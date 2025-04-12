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

public class OfficerRegistrationCommandFactory {
    private static final DIManager diManager = DIManager.getInstance();

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

    public static Map<Integer, Command> getRegistrationOperationCommands(OfficerRegistration officerRegistration){
        OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);
        ConfirmationView confirmationView = diManager.resolve(ConfirmationView.class);

        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        User user = sessionManager.getUser();

        Map<Integer, Command> commands = new LinkedHashMap<>();
        MenuManager menuManager = diManager.resolve(MenuManager.class);

        if(user.getUserRole() == UserRole.HDB_MANAGER){
            if(officerRegistration.getRegistrationStatus() == RegistrationStatus.PENDING && officerRegistration.getBTOProject().getHDBManager() == user){
                commands.put(1, new ApproveOfficerRegistrationCommand(officerRegistrationController, officerRegistration, true, confirmationView));
                commands.put(2, new ApproveOfficerRegistrationCommand(officerRegistrationController, officerRegistration, false, confirmationView));
            }
        }

        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }
}
