package service.interfaces;

import config.FlatType;
import model.Application;
import model.BTOProject;
import model.User;
import service.ServiceResponse;

public interface ApplicationService {
    ServiceResponse<?> addApplication(User requestedUser, BTOProject btoProject, FlatType flatType);
    ServiceResponse<?> withdrawApplication(User requestedUser, Application application);
}
