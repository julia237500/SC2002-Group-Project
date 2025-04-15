package controller.interfaces;

import config.FlatType;
import model.Application;
import model.BTOProject;

public interface ApplicationController {
    void addApplication(BTOProject btoProject, FlatType flatType);
    void approveApplication(Application application, boolean isApproving);
    void bookApplication(Application application);
    void withdrawApplication(Application application);
    void approveWithdrawApplication(Application application, boolean isApproving);
}
