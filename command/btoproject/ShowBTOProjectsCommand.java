package command.btoproject;

import command.Command;
import controller.interfaces.BTOProjectController;

public class ShowBTOProjectsCommand implements Command{
    private BTOProjectController btoProjectController;

    public ShowBTOProjectsCommand(BTOProjectController btoProjectController){
        this.btoProjectController = btoProjectController;
    }

    @Override
    public void execute() {
        btoProjectController.showBTOProjects();
    }

    @Override
    public String getDescription() {
        return "List of BTO Projects";
    }
    
}
