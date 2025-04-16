package command.btoproject;

import command.Command;
import controller.interfaces.BTOProjectController;
import model.BTOProject;

public class ShowBTOProjectCommand implements Command{
    private BTOProjectController btoProjectController;
    private BTOProject btoProject;

    public ShowBTOProjectCommand(BTOProjectController btoProjectController, BTOProject btoProject){
        this.btoProjectController = btoProjectController;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        btoProjectController.showBTOProject(btoProject);
    }

    @Override
    public String getDescription() {
        return String.format("%s (%s)", btoProject.getName(), btoProject.getNeighborhood());
    }
}
