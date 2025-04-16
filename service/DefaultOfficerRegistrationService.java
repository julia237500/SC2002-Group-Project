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
import service.interfaces.OfficerRegistrationService;

/**
 * Default implementation of {@link OfficerRegistrationService} that manages
 * registration of HDB officers to BTO projects, including application, approval,
 * and status tracking. Enforces business rules and access control.
 */
public class DefaultOfficerRegistrationService implements OfficerRegistrationService{
    private DataManager dataManager;

    /**
     * Constructs a DefaultOfficerRegistrationService with the specified data manager.
     * 
     * @param dataManager the data manager used for persistence operations 
     */
    public DefaultOfficerRegistrationService(DataManager dataManager){
        this.dataManager = dataManager;
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
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Officer can performed this action.");
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
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Manager can performed this action.");
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQuery(
            OfficerRegistration.class,
            registration -> registration.getBTOProject() == btoProject, 
            OfficerRegistration.SORT_BY_CREATED_AT_DESC
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, officerRegistrations);
    }
    
    // To do: 
    // 1. Applicant check logic
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
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Officer can perform this action.");
        }

        ServiceResponse<OfficerRegistration> serviceResponse = getOfficerRegistrationByOfficerAndBTOProject(requestedUser, btoProject);
        if(serviceResponse.getData() != null){
            return new ServiceResponse<>(ResponseStatus.ERROR, "You have registered for the same project before.");
        }

        if(btoProject.isExceedingHDBOfficerLimit()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "The project has reached maximum number of officers in-charge.");
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getHDBOfficer() == requestedUser,
            registration -> registration.getRegistrationStatus() != RegistrationStatus.UNSUCCESSFUL,
            registration -> registration.getBTOProject().isOverlappingWith(btoProject)
        )); 
        if(officerRegistrations.size() > 0){
            BTOProject otherBTOProject = officerRegistrations.get(0).getBTOProject();
            return new ServiceResponse<>(ResponseStatus.ERROR, """
                You have Pending/Successful registration under project with overlapping application period:
                Previous Registered Project: %s (%s - %s)
                Registering Project: %s (%s - %s)
                """.formatted(
                    otherBTOProject.getName(), otherBTOProject.getOpeningDate(), otherBTOProject.getClosingDate(), 
                    btoProject.getName(), btoProject.getOpeningDate(), btoProject.getClosingDate()
                ));
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
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Manager can perform this action.");
        }

        if(officerRegistration.getBTOProject().getHDBManager() != requestedUser){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only Responsible HDB Manager can perform this action.");
        }

        try {
            officerRegistration.updateRegistrationStatus(isApproving);
            dataManager.save(officerRegistration);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            officerRegistration.revertRegistrationStatus();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        if(isApproving) return new ServiceResponse<>(ResponseStatus.SUCCESS, "Approval of registration is successful.");
        else return new ServiceResponse<>(ResponseStatus.SUCCESS, "Rejection of registration is successful.");
    }
    
    /**
     * Marks a registration update as read by the officer.
     * 
     * @param officerRegistration the registration to update
     * @return ServiceResponse containing:
     *         - SUCCESS status if marked as read
     *         - ERROR status if no unread updates exist or persistence fails
     */
    @Override
    public ServiceResponse<?> markOfficerRegistrationAsRead(OfficerRegistration officerRegistration) {
        if(!officerRegistration.hasUnreadUpdate()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Registration don't have unread update.");
        }

        officerRegistration.markAsRead();
        try {
            dataManager.save(officerRegistration);
        } catch (Exception e) {
            officerRegistration.markAsUnread();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Mark as read success.");
    }
}
   

    
    
    

    

    

   