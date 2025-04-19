package controller.interfaces;

import model.BTOProject;

public interface BTOProjectController {
    void addBTOProject();
    void editBTOProject(BTOProject btoProject);
    void showAllBTOProjects();
    void showBTOProjectsHandledByUser();
    void showBTOProject(BTOProject btoProject);
    void showBTOProjectDetail(BTOProject btoProject);
    void toggleBTOProjectVisibilty(BTOProject btoProject);
    void deleteBTOProject(BTOProject btoProject);
}
