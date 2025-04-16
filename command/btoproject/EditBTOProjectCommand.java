package command.btoproject;

import command.Command;
import controller.interfaces.BTOProjectController;
import model.BTOProject;

public class EditBTOProjectCommand implements Command {
    private BTOProjectController btoProjectController;
    private BTOProject btoProject;
    
    public EditBTOProjectCommand(BTOProjectController btoProjectController, BTOProject btoProject){
        this.btoProjectController = btoProjectController;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        btoProjectController.editBTOProject(btoProject);
        btoProjectController.showBTOProjectDetail(btoProject);
    }

    @Override
    public String getDescription() {
        return "Edit BTO Project";
    }
}