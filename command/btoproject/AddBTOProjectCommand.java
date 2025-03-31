package command.btoproject;

import command.Command;
import controller.interfaces.BTOProjectController;

public class AddBTOProjectCommand implements Command {
    private BTOProjectController btoProjectContoller;

    public AddBTOProjectCommand(BTOProjectController btoProjectController){
        this.btoProjectContoller = btoProjectController;
    }

    @Override
    public void execute() {
        btoProjectContoller.addBTOProject();
    }

    @Override
    public String getDescription() {
        return "Add New BTO Project";
    }
}