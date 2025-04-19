package policy.interfaces;

import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import policy.PolicyResponse;

public interface OfficerRegistrationPolicy {
    PolicyResponse canViewOfficerRegistrationsByBTOProject(User requestedUser, BTOProject btoProject);
    PolicyResponse canViewOfficerRegistrationsByOfficer(User requestedUser);
    PolicyResponse canViewOfficerRegistrationByUserAndBTOProject(User requestedUser, BTOProject btoProject);

    PolicyResponse canCreateOfficerRegistration(User requestedUser, BTOProject btoProject);
    PolicyResponse canApproveOfficerRegistration(User requestedUser, OfficerRegistration officerRegistration, boolean isApproving);
}
