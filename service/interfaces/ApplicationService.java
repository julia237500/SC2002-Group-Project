package service.interfaces;

import config.FlatType;
import model.BTOProject;
import model.User;
import service.ServiceResponse;

/**
 * Service interface for managing BTO project applications.
 * Defines operations for submitting applications to BTO projects.
 */
public interface ApplicationService {
    /**
     * Submits a new application for a BTO project.
     * 
     * @param requestedUser the user submitting the application (must have appropriate role)
     * @param btoProject the BTO project being applied to (must be active)
     * @param flatType the type of flat being applied for
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if application is submitted
     *         - ERROR status with message if validation fails (including:
     *           - unauthorized user role
     *           - project not active
     *           - existing applications
     *           - other business rule violations)
     * @implNote Implementations should validate:
     *           - User has APPLICANT or HDB_OFFICER role
     *           - Project is currently active
     *           - No existing active applications by the user
     *           - No duplicate application for same project
     */
    ServiceResponse<?> addApplication(User requestedUser, BTOProject btoProject, FlatType flatType);
}