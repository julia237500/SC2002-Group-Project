package command.application;

import command.Command;
import controller.interfaces.ApplicationController;

public class ShowAllApplicationsCommand implements Command{
    private final ApplicationController applicationController;

    public ShowAllApplicationsCommand(ApplicationController applicationController) {
        this.applicationController = applicationController;
    }

    @Override
    public void execute() {
        applicationController.showAllApplications();
    }

    @Override
    public String getDescription() {
        return "List of All Applications";
    }
    
}
