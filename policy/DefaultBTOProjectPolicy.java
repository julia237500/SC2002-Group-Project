package policy;

import model.Application;
import model.BTOProject;
import model.User;

import java.util.List;

import config.UserRole;
import manager.interfaces.DataManager;
import policy.interfaces.BTOProjectPolicy;

/**
 * Default implementation of the {@link BTOProjectPolicy} interface.
 * This class defines the authorization logic for actions related to BTO projects,
 * including viewing, creating, editing, toggling visibility, and deleting.
 */
public class DefaultBTOProjectPolicy implements BTOProjectPolicy {
    private final DataManager dataManager;

    /**
     * Constructs a new DefaultBTOProjectPolicy with the given DataManager.
     *
     * @param dataManager the data manager used to query existing data for policy decisions
     */
    public DefaultBTOProjectPolicy(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Allows any user to view all BTO projects.
     */
    @Override
    public PolicyResponse canViewAllBTOProjects(User requestedUser) {
        return PolicyResponse.allow();
    }

    /**
     * Allows only HDB Managers and Officers to view the BTO projects they are handling.
     */
    @Override
    public PolicyResponse canViewBTOProjectsHandledByUser(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER && requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only HDB Manager/Officer can view BTO projects they are handling.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Determines if a user can view a specific BTO project.
     * Applicants can only view it if they have applied or are eligible and the project is active.
     */
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

            return PolicyResponse.deny("BTO Project not eligible.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Allows only HDB Managers to create a new BTO project,
     * provided they are not already managing an active project.
     */
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

    /**
     * Allows editing only if the user is the HDB Manager responsible for the BTO project.
     */
    @Override
    public PolicyResponse canEditBTOProject(User requestedUser, BTOProject btoProject) {
        if(btoProject.getHDBManager() != requestedUser) {
            return PolicyResponse.deny("Access Denied. Only Responsible HDB Manager can edit this BTO Project");
        }

        return PolicyResponse.allow();
    }

    /**
     * Allows toggling visibility only if the user is the responsible HDB Manager.
     */
    @Override
    public PolicyResponse canToggleBTOProjectVisibility(User requestedUser, BTOProject btoProject) {
        if(btoProject.getHDBManager() != requestedUser) {
            return PolicyResponse.deny("Access Denied. Only Responsible HDB Manager can edit this BTO Project");
        }

        return PolicyResponse.allow();
    }

    /**
     * Allows deletion only if the user is the responsible HDB Manager.
     */
    @Override
    public PolicyResponse canDeleteBTOProject(User requestedUser, BTOProject btoProject) {
        if(btoProject.getHDBManager() != requestedUser) {
            return PolicyResponse.deny("Access Denied. Only Responsible HDB Manager can edit this BTO Project");
        }

        return PolicyResponse.allow();
    }

     /**
     * Helper method to check if a manager already has an active BTO project.
     *
     * @param requestedUser the HDB Manager being checked
     * @return a denial response if an active project is found; otherwise allow
     */
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

    /**
     * Checks whether the applicant has already applied for the specified BTO project.
     *
     * @param requestedUser the user in question
     * @param btoProject the project being queried
     * @return true if the user has applied for the project; false otherwise
     */
    private boolean hasAppliedForBTOProject(User requestedUser, BTOProject btoProject) {
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getBTOProject() == btoProject,
            application -> application.getApplicant() == requestedUser
        )) > 0;
    }
}
