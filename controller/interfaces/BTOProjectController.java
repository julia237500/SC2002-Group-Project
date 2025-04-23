package controller.interfaces;

import model.BTOProject;
import model.User;


/**
 * A controller that processes {@link BTOProject} in accordance with the MVC architecture.
 * Entry point to actions such as create, edit, delete, and others.
 *
 * @implNote This controller should remain lightweight, with the sole responsibility of 
 * coordinating interactions between the service layer, view layer, and other components.
 * All business logic should be delegated to other components.
 * 
 * @see BTOProject
 */
public interface BTOProjectController {
    /**
     * Displays list of {@link BTOProject} that can viewed by logged-in {@link User},
     * filtered by the filter set by user.
     * 
     * @see BTOProject
     * @see User
     */
    void showAllBTOProjects();

    /**
     * Displays list of {@link BTOProject} that is handled by logged-in {@link User}.
     * 
     * @see BTOProject
     * @see User
     */
    void showBTOProjectsHandledByUser();

    /**
     * Displays the details of a {@link BTOProject} and its related action.
     *
     * @param btoProject the {@code BTOProject} to display
     * 
     * @see BTOProject
     */
    void showBTOProject(BTOProject btoProject);

    /**
     * Prompts and handles the creation process for a new {@link BTOProject}.
     * 
     * @see BTOProject
     */
    void addBTOProject();

    /**
     * Prompts and handles the editing process for a specific {@link BTOProject}.
     *
     * @param btoProject the {@code BTOProject} to be edited.
     */
    void editBTOProject(BTOProject btoProject);

    /**
     * Toggles the visibility status of a specific {@link BTOProject}.
     *
     * @param btoProject the {@code BTOProject} whose visibility is to be toggled.
     */
    void toggleBTOProjectVisibilty(BTOProject btoProject);

    /**
     * Deletes a specific BTO {@link BTOProject}.
     *
     * @param btoProject the {@link BTOProject} to delete.
     */
    void deleteBTOProject(BTOProject btoProject);

    /**
     * Prompts and sets the filter for {@code showAllBTOProjects()}
     */
    void setBTOProjectFilter();

    /**
     * Resets the filter for {@code showAllBTOProjects()}
     */
    void resetBTOProjectFilter();
}
