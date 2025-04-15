package controller.interfaces;

import config.FlatType;
import model.Application;
import model.BTOProject;

public interface ApplicationController {
    void addApplication(BTOProject btoProject, FlatType flatType);
    void withdrawApplication(Application application);
}
