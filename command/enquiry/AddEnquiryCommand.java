package command.enquiry;

import command.Command;
import controller.interfaces.EnquiryController;
import model.BTOProject;

public class AddEnquiryCommand implements Command{
    private EnquiryController enquiryController;
    private BTOProject btoProject;

    public AddEnquiryCommand(EnquiryController enquiryController, BTOProject btoProject){
        this.enquiryController = enquiryController;
        this.btoProject = btoProject;
    }

    @Override
    public void execute() {
        enquiryController.addEnquiry(btoProject);
    }

    @Override
    public String getDescription() {
        return "Enquire about BTO Project";
    }
}
