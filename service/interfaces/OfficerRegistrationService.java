package service.interfaces;

import java.util.List;

import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import service.ServiceResponse;

/**
 * Service interface for managing HDB officer registrations to BTO projects.
 * Handles registration applications, approvals, and status tracking.
 */
public interface OfficerRegistrationService {

    /**
     * Retrieves a specific officer's registration for a BTO project.
     * 
     * @param HDBOfficer the officer to query (cannot be null)
     * @param btoProject the project to query (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status with OfficerRegistration (may be null if no registration exists)
     */
    ServiceResponse<OfficerRegistration> getOfficerRegistrationByOfficerAndBTOProject(User HDBOfficer, BTOProject btoProject);

    /**
     * Retrieves all registrations for a specific officer (HDB Officer only).
     * 
     * @param requestedUser the officer whose registrations to retrieve (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status with List<OfficerRegistration> if authorized
     *         - ERROR status with message if access denied
     */
    ServiceResponse<List<OfficerRegistration>> getOfficerRegistrationsByOfficer(User requestedUser);

    /**
     * Retrieves all registrations for a specific BTO project (HDB Manager only).
     * 
     * @param requestedUser the manager making the request (cannot be null)
     * @param btoProject the project to query (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status with List<OfficerRegistration> if authorized
     *         - ERROR status with message if access denied
     */
    ServiceResponse<List<OfficerRegistration>> getOfficerRegistrationsByBTOProject(User requestedUser, BTOProject btoProject);

    /**
     * Submits a new officer registration for a BTO project.
     * 
     * @param requestedUser the officer applying (must be HDB_OFFICER)
     * @param btoProject the project to register for (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if registered
     *         - ERROR status with message if:
     *           - User not authorized
     *           - Project at officer capacity
     *           - Existing overlapping registration
     *           - Other validation fails
     */
    ServiceResponse<?> addOfficerRegistration(User requestedUser, BTOProject btoProject);

    /**
     * Approves or rejects an officer registration (HDB Manager only).
     * 
     * @param requestedUser the manager processing the request (must be project's HDB_MANAGER)
     * @param officerRegistration the registration to process (cannot be null)
     * @param isApproving true to approve, false to reject
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message
     *         - ERROR status with message if:
     *           - User not authorized
     *           - Processing fails
     */
    ServiceResponse<?> approveOfficerRegistration(User requestedUser, OfficerRegistration officerRegistration, boolean isApproving);

    /**
     * Marks a registration status update as read by the officer.
     * 
     * @param officerRegistration the registration to update (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status if marked read
     *         - ERROR status if:
     *           - No unread updates exist
     *           - Update fails
     */
    ServiceResponse<?> markOfficerRegistrationAsRead(OfficerRegistration officerRegistration);
}