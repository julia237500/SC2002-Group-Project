package command.application;

import command.Command;
import controller.interfaces.ApplicationController;
import model.BTOProject;

public class ShowApplicationByUserAndBTOProject implements Command {
    private final ApplicationController applicationController;
    private final BTOProject btoProject;

    public ShowApplicationByUserAndBTOProject(ApplicationController applicationController, BTOProject btoProject) {
        this.applicationController = applicationController;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        this.applicationController.showApplicationByUserAndBTOProject(btoProject);
    }

    @Override
    public String getDescription() {
        return "Your Application";
    }
    
}
