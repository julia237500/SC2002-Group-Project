package service.interfaces;

import java.util.List;

import config.FlatType;
import model.Application;
import model.BTOProject;
import model.User;
import service.ServiceResponse;

public interface ApplicationService {
    ServiceResponse<List<Application>> getAllApplications(User requestedUser);
    ServiceResponse<List<Application>> getApplicationsByUser(User requestedUser);
    ServiceResponse<List<Application>> getApplicationsByBTOProject(User requestedUser, BTOProject btoProject);
    ServiceResponse<Application> getApplicationByUserAndBTOProject(User requestedUser, BTOProject btoProject);
    ServiceResponse<?> addApplication(User requestedUser, BTOProject btoProject, FlatType flatType);
    ServiceResponse<?> approveApplication(User requestedUser, Application application, boolean isApproving);
    ServiceResponse<?> bookApplication(User requestedUser, Application application);
    ServiceResponse<?> withdrawApplication(User requestedUser, Application application);
    ServiceResponse<?> approveWithdrawApplication(User requestedUser, Application application, boolean isApproving);
}
