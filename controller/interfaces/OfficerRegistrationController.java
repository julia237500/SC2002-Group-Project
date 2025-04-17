package controller.interfaces;

import model.BTOProject;
import model.OfficerRegistration;

/**
 * Interface for managing HDB officer registrations for BTO projects.
 * Provides methods for creating, approving, viewing, and retrieving officer registration records.
 */
public interface OfficerRegistrationController {

    /**
     * Submits a new registration request for the currently logged-in officer
     * to be assigned to the specified BTO project.
     *
     * @param btoProject the {@link BTOProject} the officer wants to register for.
     */
    void addOfficerRegistration(BTOProject btoProject);

    /**
     * Approves or rejects an officer registration request.
     *
     * @param officerRegistration the {@link OfficerRegistration} to be updated.
     * @param isApproving         true to approve the registration, false to reject it.
     */
    void approveOfficerRegistration(OfficerRegistration officerRegistration, boolean isApproving);

    /**
     * Displays all registration records submitted by the currently logged-in officer.
     */
    void showOfficerRegistrationsByOfficer();

    /**
     * Displays all officer registration requests associated with a specific BTO project.
     *
     * @param btoProject the {@link BTOProject} whose registrations should be shown.
     */
    void showOfficerRegistrationsByBTOProject(BTOProject btoProject);

    /**
     * Displays a brief summary of a specific officer registration.
     *
     * @param officerRegistration the {@link OfficerRegistration} to display.
     */
    void showOfficerRegistration(OfficerRegistration officerRegistration);

    /**
     * Displays full details of a specific officer registration,
     *
     * @param officerRegistration the {@link OfficerRegistration} whose details are to be shown.
     */
    void showOfficerRegistrationDetail(OfficerRegistration officerRegistration);

    /**
     * Retrieves the officer registration record for the currently logged-in officer
     * and a specific BTO project, if it exists.
     *
     * @param btoProject the {@link BTOProject} in question.
     * @return the {@link OfficerRegistration} matching the officer and project
     */
    OfficerRegistration getOfficerRegistrationByOfficerAndBTOProject(BTOProject btoProject);
}
