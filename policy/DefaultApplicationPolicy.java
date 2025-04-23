package policy;

import java.util.List;

import config.ApplicationStatus;
import config.FlatType;
import config.UserRole;
import manager.interfaces.DataManager;
import model.Application;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import policy.interfaces.ApplicationPolicy;


/**
 * DefaultApplicationPolicy provides implementation of application-related policies 
 * that govern the access control and actions a user can perform on BTO project applications.
 * It implements the {@link ApplicationPolicy} interface and offers various methods 
 * to check if users can view, create, approve, withdraw, or generate reports/receipts for applications.
 */
public class DefaultApplicationPolicy implements ApplicationPolicy{
    private final DataManager dataManager;

    /**
     * Constructs a DefaultApplicationPolicy instance with the specified DataManager.
     * 
     * @param dataManager the data manager to handle database interactions
     */
    public DefaultApplicationPolicy(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Checks if the requested user can view all applications.
     * Only users with {@link UserRole#HDB_MANAGER} have access to view all applications.
     * 
     * @param requestedUser the user requesting the access
     * @return PolicyResponse allowing or denying access based on user role
     */
    @Override
    public PolicyResponse canViewAllApplications(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return PolicyResponse.deny("Access denied. Only HDB Manager can view all applications.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user can view applications for a specific BTO project.
     * Access is granted for {@link UserRole#HDB_MANAGER} and {@link UserRole#HDB_OFFICER}, 
     * and HDB Officers must be handling the specific project.
     * 
     * @param requestedUser the user requesting the access
     * @param btoProject the BTO project to check for access
     * @return PolicyResponse allowing or denying access based on user role and project handling
     */
    @Override
    public PolicyResponse canViewApplicationsByBTOProject(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER &&
            requestedUser.getUserRole() != UserRole.HDB_OFFICER){
                return PolicyResponse.deny("Access denied. Only HDB Manager or HDB Officer can view applications of BTO Project.");
        }
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER && 
            !btoProject.isHandlingBy(requestedUser)){
                return PolicyResponse.deny("Access denied. Only HDB Officer handling the project can view applications of BTO Project.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user can view applications they have made or those assigned to them.
     * 
     * @param requestedUser the user requesting the access
     * @return PolicyResponse allowing or denying access based on user role
     */
    @Override
    public PolicyResponse canViewApplicationsByUser(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.APPLICANT &&
            requestedUser.getUserRole() != UserRole.HDB_OFFICER){
                return PolicyResponse.deny("Access denied. Only applicants and HDB officers can view applications.");
        }

        return PolicyResponse.allow();
    }
    
    /**
     * Checks if the requested user can view a specific application for a BTO project.
     * @param requestedUser the user requesting the access
     * @param btoProject the BTO project to check for access
     * @return PolicyResponse allowing or denying access based on user role and project handling
     */
    @Override
    public PolicyResponse canViewApplicationByUserAndBTOProject(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() != UserRole.APPLICANT &&
            requestedUser.getUserRole() != UserRole.HDB_OFFICER){
                return PolicyResponse.deny("Access denied. Only applicants and HDB officers can view application.");
        }
        if(requestedUser.getUserRole() == UserRole.HDB_OFFICER && 
            btoProject.isHandlingBy(requestedUser)){
                return PolicyResponse.deny("Access denied. Only HDB Officer not handling the project can view applications of BTO Project.");
        }
        
        if(!hasAppliedForBTOProject(requestedUser, btoProject)){
            return PolicyResponse.deny("Application not found.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user is allowed to create an application for a BTO project.
     * Validates the user's eligibility, project status, and application history before allowing the creation.
     * 
     * @param requestedUser the user requesting the application creation
     * @param btoProject the BTO project to apply for
     * @param flatType the type of flat the user wants to apply for
     * @return PolicyResponse allowing or denying the creation based on various conditions
     */
    @Override
    public PolicyResponse canCreateApplication(User requestedUser, BTOProject btoProject, FlatType flatType) {
        if(requestedUser.getUserRole() != UserRole.APPLICANT && requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only applicants and HDB officers can create applications.");
        }

        if(hasRegisteredForBTOProject(requestedUser, btoProject)){
            return PolicyResponse.deny("Access denied. HDB Officer cannot apply for the project they are handling.");
        }

        if(!btoProject.isActive()){
            return PolicyResponse.deny("Application unsuccessful. This project is not opened for application currently.");
        }

        if(hasNonUnsuccessfulApplications(requestedUser)){
            return PolicyResponse.deny("Application unsuccessful. You are applying for other project.");
        }

        if(hasAppliedForBTOProject(requestedUser, btoProject)){
            return PolicyResponse.deny("Application unsuccessful. You have already applied for this project before.");
        }

        if(!flatType.isEligible(requestedUser)){
            return PolicyResponse.deny("Application unsuccessful. You are not eligible for %s.".formatted(flatType.getStoredString()));
        }

        if(!btoProject.hasAvailableFlats(flatType)){
            return PolicyResponse.deny("Application unsuccessful. %s is not available for BTO Project.".formatted(flatType.getStoredString()));
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user can approve or reject an application.
     * Only the HDB Manager handling the project has this permission.
     * 
     * @param requestedUser the user requesting the approval action
     * @param application the application to be approved or rejected
     * @param isApproving flag to indicate if the action is an approval or rejection
     * @return PolicyResponse allowing or denying approval based on user role and application status
     */
    @Override
    public PolicyResponse canApproveApplication(User requestedUser, Application application, boolean isApproving) {
        if(application.getBTOProject().getHDBManager() != requestedUser){
            return PolicyResponse.deny("Access denied. Only HDB Manager handling the project can approve/reject application.");
        }

        if(!application.isApprovable()){
            return PolicyResponse.deny("Approve/reject Application unsuccessful. This application is not pending.");
        }

        if(isApproving && !hasEnoughFlatNum(requestedUser, application)){
            return PolicyResponse.deny("Approve application unsuccessful. %s is not available for all approved application.".formatted(application.getFlatType().getStoredString()));
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user can book an application.
     * Only an HDB Officer can book applications, and it must be approved and within the project limits.
     * 
     * @param requestedUser the user requesting the booking
     * @param application the application to be booked
     * @return PolicyResponse allowing or denying the booking based on user role and application status
     */
    @Override
    public PolicyResponse canBookApplication(User requestedUser, Application application) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only HDB Officer can book application.");
        }

        if(!application.getBTOProject().isHandlingBy(requestedUser)){
            return PolicyResponse.deny("Access denied. Only HDB Officer handling the project can book application.");
        }

        if(!application.isBookable()){
            return PolicyResponse.deny("Book Application unsuccessful. This application is not approved.");
        }

        if(!application.getBTOProject().hasAvailableFlats(application.getFlatType())){
            return PolicyResponse.deny("Book Application unsuccessful. %s is not available for BTO Project.".formatted(application.getFlatType().getStoredString()));
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user can withdraw an application.
     * Only the applicant can withdraw their application.
     * 
     * @param requestedUser the user requesting to withdraw
     * @param application the application to be withdrawn
     * @return PolicyResponse allowing or denying the withdrawal based on user role and application status
     */
    @Override
    public PolicyResponse canWithdrawApplication(User requestedUser, Application application) {
        if(requestedUser != application.getApplicant()){
            return PolicyResponse.deny("Access denied. Only applicant of this registration can withdraw.");
        }

        if(!application.isWithdrawable()){
            return PolicyResponse.deny("Approve/reject withdraw application unsuccessful. This application already under withdraw.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user can approve or reject an application withdrawal.
     * Only the HDB Manager handling the project has this permission.
     * 
     * @param requestedUser the user requesting the approval of withdrawal
     * @param application the application to be withdrawn
     * @return PolicyResponse allowing or denying the withdrawal approval based on user role and application status
     */
    @Override
    public PolicyResponse canApproveWithdrawApplication(User requestedUser, Application application) {
        if(application.getBTOProject().getHDBManager() != requestedUser){
            return PolicyResponse.deny("Access denied. Only HDB Manager handling the project can approve/reject withdrawal.");
        }

        if(!application.isWithdrawApprovable()){
            return PolicyResponse.deny("Approve/reject withdraw application unsuccessful.");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user can generate a report for a specific BTO project.
     * Only the HDB Manager handling the project has the permission.
     * 
     * @param requestedUser the user requesting the report generation
     * @param btoProject the BTO project for which the report is to be generated
     * @return PolicyResponse allowing or denying the report generation based on user role
     */
    @Override
    public PolicyResponse canGenerateReport(User requestedUser, BTOProject btoProject) {
        if(requestedUser != btoProject.getHDBManager()){
            return PolicyResponse.deny("Access denied. Only HDB Manager handling the project can generate report");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user can generate a receipt for an application.
     * Only an HDB Officer handling the project and the application must be booked.
     * 
     * @param requestedUser the user requesting the receipt generation
     * @param application the application for which the receipt is to be generated
     * @return PolicyResponse allowing or denying the receipt generation based on user role and application status
     */
    @Override
    public PolicyResponse canGenerateReceipt(User requestedUser, Application application) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return PolicyResponse.deny("Access denied. Only HDB Manager can generate receipt.");
        }

        if(!application.getBTOProject().isHandlingBy(requestedUser)){
            return PolicyResponse.deny("Access denied. Only HDB Manager handling the project can generate receipt.");
        }
        
        if(application.getApplicationStatus() != ApplicationStatus.BOOKED){
            return PolicyResponse.deny("Application is not booked");
        }

        return PolicyResponse.allow();
    }

    /**
     * Checks if the requested user has already applied for the specified BTO project.
     * 
     * @param requestedUser the user who wants to check their application status
     * @param btoProject the BTO project to check for an existing application
     * @return boolean true if the user has already applied for the project; false otherwise
     */
    private boolean hasAppliedForBTOProject(User requestedUser, BTOProject btoProject) {
        // Checks if an application already exists for the specified user and project
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getBTOProject() == btoProject,
            application -> application.getApplicant() == requestedUser
        )) > 0;
    }

    /**
     * Checks if the requested user is registered as an officer for the specified BTO project.
     * 
     * @param requestedUser the user to check for registration as an officer
     * @param btoProject the BTO project to check if the user is registered for
     * @return boolean true if the user is registered as an officer for the project; false otherwise
     */
    private boolean hasRegisteredForBTOProject(User requestedUser, BTOProject btoProject) {
        // Checks if the user is registered as an HDB officer for the given BTO project
        return dataManager.countByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getBTOProject() == btoProject,
            registration -> registration.getHDBOfficer() == requestedUser
        )) > 0;
    }

    /**
     * Checks if the requested user has non-unsuccessful applications.
     * 
     * @param requestedUser the user to check for non-unsuccessful applications
     * @return boolean true if the user has non-unsuccessful applications; false otherwise
     */
    private boolean hasNonUnsuccessfulApplications(User requestedUser) {
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getApplicant() == requestedUser,
            application -> application.getApplicationStatus() != ApplicationStatus.UNSUCCESSFUL
        )) > 0;
    }

    /**
     * Checks if there are enough flats available for a given application.
     * This method is used when approving an application to check if there are enough flats to accommodate all approved applicants.
     * 
     * @param requestedUser the user requesting to approve the application
     * @param approvingApplication the application being approved
     * @return boolean true if there are enough flats for all approved applications; false otherwise
     */
    private boolean hasEnoughFlatNum(User requestedUser, Application approvingApplication) {
        return dataManager.countByQueries(Application.class, List.of(
            // Checks if there are enough flats for the application based on the count of successful applications for the same flat type
            application -> application.getApplicationStatus() == ApplicationStatus.SUCCESSFUL,
            application -> application.getBTOProject() == application.getBTOProject(),
            application -> application.getFlatType() == approvingApplication.getFlatType()
        )) < approvingApplication.getFlatNum();
    }
}
