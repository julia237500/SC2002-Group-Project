package command.application;

import command.Command;
import controller.interfaces.ApplicationController;

public class ShowApplicationsByUserCommand implements Command{
    ApplicationController applicationController;

    public ShowApplicationsByUserCommand(ApplicationController applicationController) {
        this.applicationController = applicationController;
    }

    @Override
    public void execute() {
        this.applicationController.showApplicationsByUser();
    }

    @Override
    public String getDescription() {
        return "Your Applications";
    }    
}
