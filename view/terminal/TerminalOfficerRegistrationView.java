package view.terminal;

import model.BTOProject;
import model.OfficerRegistration;
import view.interfaces.OfficerRegistrationView;

public class TerminalOfficerRegistrationView extends AbstractTerminalView implements OfficerRegistrationView{
    @Override
    public void showOfficerRegistrationDetail(OfficerRegistration officerRegistration) {
        showTitle("Officer Registration");
        BTOProject btoProject = officerRegistration.getBTOProject();

        System.out.println("""
            BTO Project: %s
            Number of Officers in Charge: %d/%d
            Officer Name: %s
            Status: %s
            """.formatted(
                btoProject.getName(), 
                btoProject.getHDBOfficers().size(),
                btoProject.getHDBOfficerLimit(),
                officerRegistration.getHDBOfficer().getName(), 
                officerRegistration.getRegistrationStatus()));
    }
}
