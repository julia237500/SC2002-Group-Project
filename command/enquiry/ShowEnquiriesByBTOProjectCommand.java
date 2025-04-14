package command.enquiry;

import command.Command;
import controller.interfaces.EnquiryController;
import model.BTOProject;

public class ShowEnquiriesByBTOProjectCommand implements Command{
    private EnquiryController enquiryController;
    private BTOProject btoProject;

    public ShowEnquiriesByBTOProjectCommand(EnquiryController enquiryController, BTOProject btoProject){
        this.enquiryController = enquiryController;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        enquiryController.showEnquiriesByBTOProject(btoProject);
    }

    @Override
    public String getDescription() {
        return "Show enquries regarding the BTO project";
    }
}
