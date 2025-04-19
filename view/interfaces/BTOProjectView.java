package view.interfaces;

import config.FlatType;
import model.BTOProject;

public interface BTOProjectView {
    void showBTOProjectDetailRestricted(BTOProject btoProject, FlatType[] flatTypesToShow);
    void showBTOProjectDetailFull(BTOProject btoProject);
}
