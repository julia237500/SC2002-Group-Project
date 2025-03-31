package controller.interfaces;

import config.FlatType;
import model.BTOProject;

public interface BTOApplicationController {
    void apply(BTOProject btoProject, FlatType flatType);
}
