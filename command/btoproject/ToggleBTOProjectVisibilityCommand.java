package command.btoproject;

import command.Command;
import controller.interfaces.BTOProjectController;
import model.BTOProject;

public class ToggleBTOProjectVisibilityCommand implements Command {
    private BTOProjectController btoProjectController;
    private BTOProject btoProject;

    public ToggleBTOProjectVisibilityCommand(BTOProjectController btoProjectController, BTOProject btoProject){
        this.btoProjectController = btoProjectController;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        btoProjectController.toggleBTOProjectVisibilty(btoProject);
        btoProjectController.showBTOProjectDetail(btoProject);
    }

    @Override
    public String getDescription() {
        return "Toggle BTO Project Visibility";
    }
}
