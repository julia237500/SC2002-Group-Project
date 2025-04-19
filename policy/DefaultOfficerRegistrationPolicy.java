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

public class DefaultOfficerRegistrationPolicy implements OfficerRegistrationPolicy{
    private final DataManager dataManager;

    public DefaultOfficerRegistrationPolicy(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    @Override
    public PolicyResponse canViewOfficerRegistrationsByBTOProject(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return PolicyResponse.deny("Access denied. Only HDB Manager can view officer registrations.");
        }
        
        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canViewOfficerRegistrationsByOfficer(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only HDB Officer can view his/her officer registrations.");
        }

        return PolicyResponse.allow();
    }

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

    private boolean hasRegisteredForBTOProject(User requestedUser, BTOProject btoProject) {
        return dataManager.countByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getBTOProject() == btoProject,
            registration -> registration.getHDBOfficer() == requestedUser
        )) > 0;
    }

    private boolean hasAppliedForBTOProject(User requestedUser, BTOProject btoProject) {
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getBTOProject() == btoProject,
            application -> application.getApplicant() == requestedUser
        )) > 0;
    }

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
