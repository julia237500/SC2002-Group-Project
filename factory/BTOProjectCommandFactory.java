package factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import command.btoproject.DeleteBTOProjectCommand;
import command.btoproject.EditBTOProjectCommand;
import command.btoproject.ShowBTOProjectCommand;
import command.btoproject.ToggleBTOProjectVisibilityCommand;
import command.general.MenuBackCommand;
import config.UserRole;
import controller.interfaces.BTOProjectController;
import manager.DIManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.User;
import view.interfaces.ConfirmationView;

public class BTOProjectCommandFactory {
     private static final DIManager diManager = DIManager.getInstance();

    public static Map<Integer, Command> getShowBTOProjectsCommands(List<BTOProject> btoProjects) {
        Map<Integer, Command> commands = new HashMap<>();

        BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        MenuManager menuManager = diManager.resolve(MenuManager.class);

        int index = 1;
        for(BTOProject btoProject:btoProjects){
            commands.put(index++, new ShowBTOProjectCommand(btoProjectController, btoProject));
        }

        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }

    public static Map<Integer, Command> getBTOProjectsOperationCommands(BTOProject btoProject) {
        Map<Integer, Command> commands = new HashMap<>();

        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        User user = sessionManager.getUser();

        BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        ConfirmationView confirmationView = diManager.resolve(ConfirmationView.class);
        MenuManager menuManager = diManager.resolve(MenuManager.class);

        if(user.getUserRole() == UserRole.HDB_MANAGER && btoProject.getHDBManager() ==  user){
            commands.put(1, new EditBTOProjectCommand(btoProjectController, btoProject));
            commands.put(2, new ToggleBTOProjectVisibilityCommand(btoProjectController, btoProject));
            commands.put(3, new DeleteBTOProjectCommand(btoProjectController, confirmationView, menuManager, btoProject));
        }
        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }
}
