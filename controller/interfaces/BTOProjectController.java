package controller.interfaces;

import model.BTOProject;


/**
 * Interface for managing operations related to BTO (Build-To-Order) projects.
 * <p>
 * Defines methods for adding, editing, displaying, toggling visibility, and deleting BTO projects.
 * </p>
 */
public interface BTOProjectController {
    
    /**
     * Initiates the creation process for a new BTO project.
     */
    void addBTOProject();

    /**
     * Initiates the editing process for the specified BTO project.
     *
     * @param btoProject the {@link BTOProject} to be edited.
     */
    void editBTOProject(BTOProject btoProject);
    void showAllBTOProjects();

    /**
     * Displays a list of all existing BTO projects.
     */
    void showBTOProjectsHandledByUser();

    /**
     * Displays a summary or overview of a specific BTO project.
     *
     * @param btoProject the {@link BTOProject} to display.
     */
    void showBTOProject(BTOProject btoProject);

    /**
     * Displays detailed information about a specific BTO project.
     *
     * @param btoProject the {@link BTOProject} whose details are to be shown.
     */
    void showBTOProjectDetail(BTOProject btoProject);

    /**
     * Toggles the visibility status of a specific BTO project.
     *
     * @param btoProject the {@link BTOProject} whose visibility is to be toggled.
     */
    void toggleBTOProjectVisibilty(BTOProject btoProject);

    /**
     * Deletes a specific BTO project from the system.
     *
     * @param btoProject the {@link BTOProject} to delete.
     */
    void deleteBTOProject(BTOProject btoProject);

    void setBTOProjectFilter();
    void resetBTOProjectFilter();
}
