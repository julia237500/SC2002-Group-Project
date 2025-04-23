package controller.interfaces;

import model.BTOProject;
import model.OfficerRegistration;

/**
 * A controller that processes {@link OfficerRegistration} in accordance with the MVC architecture.
 * Entry point to actions such as create, approve, and others.
 *
 * @implNote This controller should remain lightweight, with the sole responsibility of 
 * coordinating interactions between the service layer, view layer, and other components.
 * All business logic should be delegated to other components.
 * 
 * @see OfficerRegistration
 */
public interface OfficerRegistrationController {
     /**
     * Displays list of {@link OfficerRegistration} created by the logged-in user.
     * 
     * @see OfficerRegistration
     */
    void showOfficerRegistrationsByOfficer();

    /**
     * Displays list of {@link OfficerRegistration} associated with a specific {@link BTOProject}.
     *
     * @param btoProject the {@code BTOProject} to filter {@code OfficerRegistration}.
     * 
     * @see OfficerRegistration
     * @see BTOProject
     */
    void showOfficerRegistrationsByBTOProject(BTOProject btoProject);

    /**
     * Displays list of {@link OfficerRegistration} associated with a specific {@link BTOProject} and createdby the logged-in user.
     *
     * @param btoProject the {@code BTOProject} to filter {@code OfficerRegistration}.
     * 
     * @see OfficerRegistration
     * @see BTOProject
     */
    void showOfficerRegistrationByOfficerAndBTOProject(BTOProject btoProject);

    /**
     * Displays the details of a {@link OfficerRegistration} and its related action.
     *
     * @param officerRegistration the {@code OfficerRegistration} to display.
     * 
     * @see OfficerRegistration
     */
    void showOfficerRegistration(OfficerRegistration officerRegistration);

    /**
     * Creates an {@link OfficerRegistration} for a specific {@link BTOProject}.
     *
     * @param btoProject the {@code BTOProject} the officer wants to register for.
     */
    void addOfficerRegistration(BTOProject btoProject);

    /**
     * Approves or rejects an {@link OfficerRegistration}.
     *
     * @param officerRegistration the {@code OfficerRegistration} to approve or reject.
     * @param isApproving         {@code true} to approve, {@code false} to reject.
     * 
     * @see OfficerRegistration
     */
    void approveOfficerRegistration(OfficerRegistration officerRegistration, boolean isApproving);    
}
