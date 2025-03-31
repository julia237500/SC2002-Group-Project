package factory;

import java.util.HashMap;
import java.util.Map;

import command.Command;
import command.btoproject.AddBTOProjectCommand;
import command.btoproject.ShowBTOProjectsCommand;
import command.general.LogoutCommand;
import command.user.ChangePasswordCommand;
import config.UserRole;
import controller.interfaces.AuthController;
import controller.interfaces.BTOProjectController;
import manager.DIManager;
import manager.interfaces.ApplicationManager;
import manager.interfaces.SessionManager;
import model.User;

public class DashboardCommandFactory {
    private static final DIManager diManager = DIManager.getInstance();

    public static Map<Integer, Command> getCommands() {
        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        User user = sessionManager.getUser();

        ApplicationManager applicationManager = diManager.resolve(ApplicationManager.class);
        AuthController authController = diManager.resolve(AuthController.class);
        BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);

        Map<Integer, Command> commands = new HashMap<>();
        commands.put(1, new ChangePasswordCommand(authController, applicationManager));
        commands.put(9, new LogoutCommand(applicationManager));

        if(user.getUserRole() == UserRole.HDB_MANAGER){
            commands.put(20, new ShowBTOProjectsCommand(btoProjectController));
            commands.put(21, new AddBTOProjectCommand(btoProjectController));
        }

        return commands;
    }
}
