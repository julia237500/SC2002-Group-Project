package policy;

import model.Application;
import model.BTOProject;
import model.User;

import java.util.List;

import config.UserRole;
import manager.interfaces.DataManager;
import policy.interfaces.BTOProjectPolicy;

public class DefaultBTOProjectPolicy implements BTOProjectPolicy {
    private final DataManager dataManager;

    public DefaultBTOProjectPolicy(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public PolicyResponse canViewAllBTOProjects(User requestedUser) {
        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canViewBTOProjectsHandledByUser(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER && requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only HDB Manager/Officer can view BTO projects they are handling.");
        }

        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canViewBTOProject(User requestedUser, BTOProject btoProject) {        
        if(requestedUser.getUserRole() == UserRole.APPLICANT) {
            if(hasAppliedForBTOProject(requestedUser, btoProject)){
                return PolicyResponse.allow();
            }

            if(!btoProject.isActive()){
                return PolicyResponse.deny("BTO Project is not active.");
            }
            
            if(!btoProject.canBeAppliedBy(requestedUser)){
                return PolicyResponse.deny("No eligible flat type.");
            }
        }

        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canCreateBTOProject(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return PolicyResponse.deny("Access Denied. Only HDB Manager can create BTO Project");
        }

        final PolicyResponse policyResponse = checkHasActiveBTOProjects(requestedUser);
        if(!policyResponse.isAllowed()) {
            return policyResponse;
        }

        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canEditBTOProject(User requestedUser, BTOProject btoProject) {
        if(btoProject.getHDBManager() != requestedUser) {
            return PolicyResponse.deny("Access Denied. Only Responsible HDB Manager can edit this BTO Project");
        }

        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canToggleBTOProjectVisibility(User requestedUser, BTOProject btoProject) {
        if(btoProject.getHDBManager() != requestedUser) {
            return PolicyResponse.deny("Access Denied. Only Responsible HDB Manager can edit this BTO Project");
        }

        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canDeleteBTOProject(User requestedUser, BTOProject btoProject) {
        if(btoProject.getHDBManager() != requestedUser) {
            return PolicyResponse.deny("Access Denied. Only Responsible HDB Manager can edit this BTO Project");
        }

        return PolicyResponse.allow();
    }

    private PolicyResponse checkHasActiveBTOProjects(User requestedUser) {
        final List<BTOProject> btoProjects = dataManager.getByQueries(BTOProject.class, List.of( 
            btoProject -> btoProject.getHDBManager() == requestedUser,
            btoProject -> btoProject.isActive()
        ));

        if(!btoProjects.isEmpty()){
            final BTOProject btoProject = btoProjects.get(0);
            return PolicyResponse.deny("You are involving in active project: %s. This action cannot be performed.".formatted(btoProject.getName()));
        }

        return PolicyResponse.allow();
    }

    private boolean hasAppliedForBTOProject(User requestedUser, BTOProject btoProject) {
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getBTOProject() == btoProject,
            application -> application.getApplicant() == requestedUser
        )) > 0;
    }
}
