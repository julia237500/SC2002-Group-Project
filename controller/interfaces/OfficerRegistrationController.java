package controller.interfaces;

import model.BTOProject;
import model.OfficerRegistration;

public interface OfficerRegistrationController {
    void addOfficerRegistration(BTOProject btoProject);
    void approveOfficerRegistration(OfficerRegistration officerRegistration, boolean isApproving);
    void showOfficerRegistrationsByOfficer();
    void showOfficerRegistrationsByBTOProject(BTOProject btoProject);
    void showOfficerRegistration(OfficerRegistration officerRegistration);
    void showOfficerRegistrationDetail(OfficerRegistration officerRegistration);
    OfficerRegistration getOfficerRegistrationByOfficerAndBTOProject(BTOProject btoProject);
}
