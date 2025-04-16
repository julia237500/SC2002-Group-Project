package command.application;

import command.Command;
import controller.interfaces.ApplicationController;
import model.BTOProject;

public class ShowApplicationsByBTOProjectCommand implements Command {
    private final ApplicationController applicationController;
    private final BTOProject btoProject;

    public ShowApplicationsByBTOProjectCommand(ApplicationController applicationController, BTOProject btoProject) {
        this.applicationController = applicationController;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        this.applicationController.showApplicationsByBTOProject(btoProject);
    }

    @Override
    public String getDescription() {
        return "List of Applications";
    }
    
}
