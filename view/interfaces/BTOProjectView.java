package view.interfaces;

import model.BTOProject;

/**
 * This interface fits into the Model-View-Controller (MVC) pattern, 
 * It helps decouple our UI logic from the data and business logic, 
 * making our code more modular.
 * Separation of Concerns, Single Responsibility Principle: 
 * - View classes are only responsible for display and UI logic 
 */

/**
 * Interface for displaying BTO project-related data in the view layer.
 * <p>
 * This interface defines the contract for how BTO project information
 * should be presented to the user, whether as a list or as a detailed
 * view of a single project.
 */
public interface BTOProjectView {

    /**
     * Displays a list of BTO projects to the user.
     *
     * @param btoProjects A list of {@link BTOProject} objects to be shown.
     */
    void showBTOProjectDetailRestricted(BTOProject btoProject);

    /**
     * Displays the details of a single BTO project.
     *
     * @param btoProject The {@link BTOProject} object to be shown.
     */
    void showBTOProjectDetailFull(BTOProject btoProject);
}
