package view.interfaces;

import java.util.List;

import model.BTOProject;

public interface BTOProjectView {
    void showBTOProjects(List<BTOProject> btoProjects);
    void showBTOProject(BTOProject btoProject);
}
