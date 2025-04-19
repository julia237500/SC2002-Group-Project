package policy.interfaces;

import model.BTOProject;
import model.User;
import policy.PolicyResponse;

public interface BTOProjectPolicy {
    PolicyResponse canViewAllBTOProjects(User requestedUser);
    PolicyResponse canViewBTOProjectsHandledByUser(User requestedUser);
    PolicyResponse canViewBTOProject(User requestedUser, BTOProject btoProject);

    PolicyResponse canCreateBTOProject(User requestedUser);
    PolicyResponse canEditBTOProject(User requestedUser, BTOProject btoProject);
    PolicyResponse canToggleBTOProjectVisibility(User requestedUser, BTOProject btoProject);
    PolicyResponse canDeleteBTOProject(User requestedUser, BTOProject btoProject);
}
