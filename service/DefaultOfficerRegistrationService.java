package service;

import java.util.List;

import config.RegistrationStatus;
import config.ResponseStatus;
import config.UserRole;
import exception.DataModelException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import policy.PolicyResponse;
import policy.interfaces.OfficerRegistrationPolicy;
import service.interfaces.OfficerRegistrationService;

/**
 * Default implementation of {@link OfficerRegistrationService} that manages
 * registration of HDB officers to BTO projects, including application, approval,
 * and status tracking. Enforces business rules and access control.
 */
public class DefaultOfficerRegistrationService implements OfficerRegistrationService{
    private final DataManager dataManager;
    private final OfficerRegistrationPolicy officerRegistrationPolicy;

    /**
     * Constructs a DefaultOfficerRegistrationService with the specified data manager.
     * 
     * @param dataManager the data manager used for persistence operations 
     */
    public DefaultOfficerRegistrationService(DataManager dataManager, OfficerRegistrationPolicy officerRegistrationPolicy) {
        this.dataManager = dataManager;
        this.officerRegistrationPolicy = officerRegistrationPolicy;
    }

    /**
     * Retrieves an officer's registration for a specific BTO project.
     * 
     * @param HDBOfficer the officer to check registration for
     * @param btoProject the project to check registration against
     * @return ServiceResponse containing:
     *         - SUCCESS status with OfficerRegistration
     */
    @Override
    public ServiceResponse<OfficerRegistration> getOfficerRegistrationByOfficerAndBTOProject(User HDBOfficer, BTOProject btoProject) {
        final PolicyResponse policyResponse = officerRegistrationPolicy.canViewOfficerRegistrationByUserAndBTOProject(HDBOfficer, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getBTOProject() == btoProject,
            registration -> registration.getHDBOfficer() == HDBOfficer
        )); 

        OfficerRegistration officerRegistration = null;
        if(officerRegistrations.size() > 0) officerRegistration = officerRegistrations.get(0);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, officerRegistration);
    }

    /**
     * Retrieves all registrations for a specific officer (HDB Officer only).
     * Results are sorted by creation date in reversed order.
     * 
     * @param requestedUser the officer whose registrations to retrieve
     * @return ServiceResponse containing:
     *         - SUCCESS status with List<OfficerRegistration> if authorized
     *         - ERROR status with message if access denied
     */
    @Override
    public ServiceResponse<List<OfficerRegistration>> getOfficerRegistrationsByOfficer(User requestedUser) {
        final PolicyResponse policyResponse = officerRegistrationPolicy.canViewOfficerRegistrationsByOfficer(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQuery(
            OfficerRegistration.class,
            registration -> registration.getHDBOfficer() == requestedUser, 
            OfficerRegistration.SORT_BY_CREATED_AT_DESC
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, officerRegistrations);
    }

    /**
     * Retrieves all registrations for a specific BTO project (HDB Manager only).
     * Results are sorted by creation date in reversed order.
     * 
     * @param requestedUser the manager making the request
     * @param btoProject the project to filter registrations by
     * @return ServiceResponse containing:
     *         - SUCCESS status with List<OfficerRegistration> if authorized
     *         - ERROR status with message if access denied
     */
    @Override
    public ServiceResponse<List<OfficerRegistration>> getOfficerRegistrationsByBTOProject(User requestedUser, BTOProject btoProject) {
        final PolicyResponse policyResponse = officerRegistrationPolicy.canViewOfficerRegistrationsByBTOProject(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQuery(
            OfficerRegistration.class,
            registration -> registration.getBTOProject() == btoProject, 
            OfficerRegistration.SORT_BY_CREATED_AT_DESC
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, officerRegistrations);
    }
    
    /**
     * Registers an officer for a BTO project after validating:
     * <ol>
     *   <li>User has HDB_OFFICER role</li>
     *   <li>No existing registration for same project</li>
     *   <li>Project hasn't reached its maximum officer limit</li>
     *   <li>No overlapping projects with Pending/Successful status</li>
     * </ol>
     * 
     * @param requestedUser the officer requesting registration
     * @param btoProject the project to register for
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if registered
     *         - ERROR status with detailed message if validation fails
     */
    @Override
    public ServiceResponse<?> addOfficerRegistration(User requestedUser, BTOProject btoProject) {
        final PolicyResponse policyResponse = officerRegistrationPolicy.canCreateOfficerRegistration(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            OfficerRegistration officerRegistration = new OfficerRegistration(btoProject, requestedUser);
            dataManager.save(officerRegistration);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Registration successful. Kindly wait for approval from HDB Manager.");
    }

    /**
     * Approves or rejects an officer registration (HDB Manager only).
     * 
     * @param requestedUser the manager processing the request
     * @param officerRegistration the registration to approve/reject
     * @param isApproving true to approve, false to reject
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message
     *         - ERROR status with message if validation fails
     * @throws DataModelException if registration status is invalid
     * @throws DataSavingException if persistence fails
     */
    @Override
    public ServiceResponse<?> approveOfficerRegistration(User requestedUser, OfficerRegistration officerRegistration, boolean isApproving) {
        final PolicyResponse policyResponse = officerRegistrationPolicy.canApproveOfficerRegistration(requestedUser, officerRegistration, isApproving);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            officerRegistration.updateRegistrationStatus(isApproving);
            dataManager.save(officerRegistration);
            officerRegistration.getBTOProject().addHDBOfficer(officerRegistration.getHDBOfficer());
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            officerRegistration.restore();;
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "%s registration successful.".formatted(isApproving ? "Approve" : "Reject"));
    }
}
   

    
    
    

    

    

   