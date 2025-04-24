package policy.interfaces;

import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import policy.PolicyResponse;

/**
 * Defines access control policies related to officer registrations in BTO projects.
 * <p>
 * Implementations should determine whether a specific {@link User} can perform
 * actions related to {@link OfficerRegistration} for {@link BTOProject}.
 */
public interface OfficerRegistrationPolicy {
    PolicyResponse canViewOfficerRegistrationsByBTOProject(User requestedUser, BTOProject btoProject);
    PolicyResponse canViewOfficerRegistrationsByOfficer(User requestedUser);
    PolicyResponse canViewOfficerRegistrationByUserAndBTOProject(User requestedUser, BTOProject btoProject);

    PolicyResponse canCreateOfficerRegistration(User requestedUser, BTOProject btoProject);
    PolicyResponse canApproveOfficerRegistration(User requestedUser, OfficerRegistration officerRegistration, boolean isApproving);
}
