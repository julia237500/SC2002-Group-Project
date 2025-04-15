package service.interfaces;

import config.FlatType;
import model.Application;
import model.BTOProject;
import model.User;
import service.ServiceResponse;

public interface ApplicationService {
    ServiceResponse<?> addApplication(User requestedUser, BTOProject btoProject, FlatType flatType);
    ServiceResponse<?> approveApplication(User requestedUser, Application application, boolean isApproving);
    ServiceResponse<?> bookApplication(User requestedUser, Application application);
    ServiceResponse<?> withdrawApplication(User requestedUser, Application application);
    ServiceResponse<?> approveWithdrawApplication(User requestedUser, Application application, boolean isApproving);
}
