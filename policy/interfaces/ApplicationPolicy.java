package policy.interfaces;

import config.FlatType;
import model.Application;
import model.BTOProject;
import model.User;
import policy.PolicyResponse;

public interface ApplicationPolicy {
    PolicyResponse canViewAllApplications(User requestedUser);
    PolicyResponse canViewApplicationsByBTOProject(User requestedUser, BTOProject btoProject);
    PolicyResponse canViewApplicationsByUser(User requestedUser);
    PolicyResponse canViewApplicationByUserAndBTOProject(User requestedUser, BTOProject btoProject);

    PolicyResponse canCreateApplication(User requestedUser, BTOProject btoProject, FlatType flatType);
    PolicyResponse canApproveApplication(User requestedUser, Application application, boolean isApproving);
    PolicyResponse canBookApplication(User requestedUser, Application application);
    PolicyResponse canGenerateReceipt(User requestedUser, Application application);

    PolicyResponse canWithdrawApplication(User requestedUser, Application application);
    PolicyResponse canApproveWithdrawApplication(User requestedUser, Application application);
}
