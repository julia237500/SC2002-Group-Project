package policy.interfaces;

import model.BTOProject;
import model.User;
import policy.PolicyResponse;

/**
 * This interface defines the access control policies related to BTO projects.
 * Implementing classes should define rules for creating, viewing, editing,
 * toggling visibility, and deleting BTO projects based on the requesting user's role and permissions.
 */
public interface BTOProjectPolicy {
    PolicyResponse canViewAllBTOProjects(User requestedUser);
    PolicyResponse canViewBTOProjectsHandledByUser(User requestedUser);
    PolicyResponse canViewBTOProject(User requestedUser, BTOProject btoProject);

    PolicyResponse canCreateBTOProject(User requestedUser);
    PolicyResponse canEditBTOProject(User requestedUser, BTOProject btoProject);
    PolicyResponse canToggleBTOProjectVisibility(User requestedUser, BTOProject btoProject);
    PolicyResponse canDeleteBTOProject(User requestedUser, BTOProject btoProject);
}
