package controller.interfaces;

import config.FlatType;
import model.BTOProject;


/**
 * Interface for handling user interactions related to housing applications.
 * 
 * <p>This interface defines the core action of submitting an application
 * for a specific flat type under a BTO project.</p>
 */
public interface ApplicationController {

    /**
     * Submits an application for a given flat type under the specified BTO project.
     *
     * @param btoProject The BTO project to apply under.
     * @param flatType   The type of flat the user is applying for.
     */
    void addApplication(BTOProject btoProject, FlatType flatType);
}
