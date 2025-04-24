package policy;

import java.util.List;

import config.RegistrationStatus;
import config.UserRole;
import manager.interfaces.DataManager;
import model.Application;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import policy.interfaces.OfficerRegistrationPolicy;

/**
 * Default implementation of {@link OfficerRegistrationPolicy} that enforces
 * business rules and permissions for officer registration actions in BTO projects.
 */
public class DefaultOfficerRegistrationPolicy implements OfficerRegistrationPolicy{
    private final DataManager dataManager;

    /**
     * Constructs a DefaultOfficerRegistrationPolicy with the provided data manager.
     *
     * @param dataManager the data access interface for querying application and registration data
     */
    public DefaultOfficerRegistrationPolicy(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Determines if a user can view all officer registrations associated with a specific BTO project.
     *
     * @param requestedUser the user requesting access
     * @param btoProject the BTO project whose registrations are being viewed
     * @return a {@link PolicyResponse} indicating whether the access is permitted
     */
    @Override
    public PolicyResponse canViewOfficerRegistrationsByBTOProject(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return PolicyResponse.deny("Access denied. Only HDB Manager can view officer registrations.");
        }
        
        return PolicyResponse.allow();
    }

    /**
     * Determines if a user can view all officer registrations made by themselves.
     *
     * @param requestedUser the user requesting access
     * @return a {@link PolicyResponse} indicating access permission
     */
    @Override
    public PolicyResponse canViewOfficerRegistrationsByOfficer(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only HDB Officer can view his/her officer registrations.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if a user can view a specific officer registration based on the user and the project.
     *
     * @param requestedUser the user making the request
     * @param btoProject the BTO project the registration belongs to
     * @return a {@link PolicyResponse} indicating access permission
     */
    @Override
    public PolicyResponse canViewOfficerRegistrationByUserAndBTOProject(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only HDB Officer can his/her officer registration.");
        }

        if(!hasRegisteredForBTOProject(requestedUser, btoProject)){
            return PolicyResponse.deny("Officer registration not found");
        }

        return PolicyResponse.allow();
    }

    /**
     * Determines whether a user can submit a new officer registration for a given BTO project.
     *
     * @param requestedUser the user attempting to create the registration
     * @param btoProject the target BTO project for the registration
     * @return a {@link PolicyResponse} indicating whether creation is permitted
     */
    @Override
    public PolicyResponse canCreateOfficerRegistration(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only HDB Officer can perform this action.");
        }

        if(hasAppliedForBTOProject(requestedUser, btoProject)){
            return PolicyResponse.deny("You have applied for the same project before.");
        }

        if(hasRegisteredForBTOProject(requestedUser, btoProject)){
            return PolicyResponse.deny("You have registered for the same project before.");
        }

        if(btoProject.isExceedingHDBOfficerLimit()){
            return PolicyResponse.deny("The project has reach maximum number of officer in-charge.");
        }
        
        PolicyResponse policyResponse = hasOverlappingOfficerRegistration(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return policyResponse;
        }
        
        return PolicyResponse.allow();
    }

    /**
     * Determines whether a user can approve or reject a pending officer registration.
     *
     * @param requestedUser the user attempting to approve/reject
     * @param officerRegistration the officer registration being acted upon
     * @param isApproving true if approving, false if rejecting
     * @return a {@link PolicyResponse} indicating the result
     */
    @Override
    public PolicyResponse canApproveOfficerRegistration(User requestedUser, OfficerRegistration officerRegistration, boolean isApproving) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return PolicyResponse.deny("Access denied. Only HDB Manager handling this project can approve officer registration.");
        }

        if(!officerRegistration.getBTOProject().isHandlingBy(requestedUser)){
            return PolicyResponse.deny("Access denied. Only HDB Manager handling this project can approve officer registration.");
        }

        if(!officerRegistration.isApprovable()){
            return PolicyResponse.deny("Approve/reject officer registration unsuccessful. Officer registration is already approved.");
        }

        if(isApproving && officerRegistration.getBTOProject().isExceedingHDBOfficerLimit()){
            return PolicyResponse.deny("Approve officer registration unsuccessful. BTO Project exceeding HDB Officer limit.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks whether a user has already registered for a specific BTO project.
     *
     * @param requestedUser the officer in question
     * @param btoProject the BTO project
     * @return true if already registered, false otherwise
     */
    private boolean hasRegisteredForBTOProject(User requestedUser, BTOProject btoProject) {
        return dataManager.countByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getBTOProject() == btoProject,
            registration -> registration.getHDBOfficer() == requestedUser
        )) > 0;
    }

    /**
     * Checks whether a user has already applied for a specific BTO project.
     *
     * @param requestedUser the user in question
     * @param btoProject the BTO project
     * @return true if applied, false otherwise
     */
    private boolean hasAppliedForBTOProject(User requestedUser, BTOProject btoProject) {
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getBTOProject() == btoProject,
            application -> application.getApplicant() == requestedUser
        )) > 0;
    }

    /**
     * Checks whether the user has existing officer registrations that overlap
     * with the target BTO project's application period.
     *
     * @param requestedUser the officer attempting to register
     * @param btoProject the project the officer is trying to register for
     * @return a {@link PolicyResponse} denying or allowing based on overlap
     */
    private PolicyResponse hasOverlappingOfficerRegistration(User requestedUser, BTOProject btoProject) {
        List<OfficerRegistration> officerRegistrations = dataManager.getByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getHDBOfficer() == requestedUser,
            registration -> registration.getRegistrationStatus() != RegistrationStatus.UNSUCCESSFUL,
            registration -> registration.getBTOProject().isOverlappingWith(btoProject)
        )); 

        if(officerRegistrations.size() > 0){
            BTOProject otherBTOProject = officerRegistrations.get(0).getBTOProject();
            return PolicyResponse.deny(
                """
                You have Pending/Successful registration under project with overlapping application period:
                Previous Registered Project: %s (%s - %s)
                Registering Project: %s (%s - %s)
                """.formatted(
                    otherBTOProject.getName(), otherBTOProject.getOpeningDate(), otherBTOProject.getClosingDate(), 
                    btoProject.getName(), btoProject.getOpeningDate(), btoProject.getClosingDate()
                ));
        }

        return PolicyResponse.allow();
    }
}
