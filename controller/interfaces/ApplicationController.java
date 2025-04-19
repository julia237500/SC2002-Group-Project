package controller.interfaces;

import config.FlatType;
import model.Application;
import model.BTOProject;

public interface ApplicationController {
    void showAllApplications();
    void showApplicationsByUser();
    void showApplicationsByBTOProject(BTOProject btoProject);
    void showApplicationByUserAndBTOProject(BTOProject btoProject);
    void showApplication(Application application);
    void showApplicationDetail(Application application);
    void addApplication(BTOProject btoProject, FlatType flatType);
    void approveApplication(Application application, boolean isApproving);
    void bookApplication(Application application);
    void withdrawApplication(Application application);
    void approveWithdrawApplication(Application application, boolean isApproving);

    void generateReceipt(Application application);
}
