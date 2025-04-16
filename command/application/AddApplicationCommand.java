package command.application;

import command.Command;
import config.FlatType;
import controller.interfaces.ApplicationController;
import controller.interfaces.BTOProjectController;
import manager.interfaces.MenuManager;
import model.BTOProject;

public class AddApplicationCommand implements Command {
    private final ApplicationController applicationController;
    private final BTOProject btoProject;
    private final FlatType flatType;
    private final MenuManager menuManager;
    private final BTOProjectController btoProjectController;

    public AddApplicationCommand(ApplicationController applicationController, BTOProject btoProject, FlatType flatType, MenuManager menuManager, BTOProjectController btoProjectController) {
        this.applicationController = applicationController;
        this.btoProject = btoProject;
        this.flatType = flatType;
        this.menuManager = menuManager;
        this.btoProjectController = btoProjectController;
    }

    @Override
    public void execute() {
        applicationController.addApplication(btoProject, flatType);

        menuManager.back();
        btoProjectController.showBTOProject(btoProject);
    }

    @Override
    public String getDescription() {
        return "Apply for %s".formatted(
            flatType.getStoredString()
        );
    }
    
}
