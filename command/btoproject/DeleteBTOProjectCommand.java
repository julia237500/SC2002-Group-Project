package command.btoproject;

import command.Command;
import command.general.MenuBackCommand;
import controller.interfaces.BTOProjectController;
import manager.interfaces.MenuManager;
import model.BTOProject;
import view.interfaces.ConfirmationView;

public class DeleteBTOProjectCommand implements Command {
    private BTOProjectController btoProjectController;
    private ConfirmationView confirmationView;
    private MenuManager menuManager;
    private BTOProject btoProject;

    public DeleteBTOProjectCommand(BTOProjectController btoProjectController, ConfirmationView confirmationView, MenuManager menuManager, BTOProject btoProject){
        this.btoProjectController = btoProjectController;
        this.confirmationView = confirmationView;
        this.menuManager = menuManager;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        if(confirmationView.getConfirmation()){
            btoProjectController.deleteBTOProject(btoProject);

            MenuBackCommand menuBackCommand = new MenuBackCommand(menuManager);
            menuBackCommand.execute(); // Exit details menu
            menuBackCommand.execute(); // Exit list menu for refresh
            
            new ShowBTOProjectsCommand(btoProjectController).execute(); // Refresh list menu
        }
    }

    @Override
    public String getDescription() {
        return "Delete BTO Project";
    }
}
