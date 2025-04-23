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

public class DefaultApplicationPolicy implements ApplicationPolicy{
    private final DataManager dataManager;

    public DefaultApplicationPolicy(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public PolicyResponse canViewAllApplications(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return PolicyResponse.deny("Access denied. Only HDB Manager can view all applications.");
        }

        return PolicyResponse.allow();
    }

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

    @Override
    public PolicyResponse canViewApplicationsByUser(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.APPLICANT &&
            requestedUser.getUserRole() != UserRole.HDB_OFFICER){
                return PolicyResponse.deny("Access denied. Only applicants and HDB officers can view applications.");
        }

        return PolicyResponse.allow();
    }

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

    @Override
    public PolicyResponse canGenerateReport(User requestedUser, BTOProject btoProject) {
        if(requestedUser != btoProject.getHDBManager()){
            return PolicyResponse.deny("Access denied. Only HDB Manager handling the project can generate report");
        }

        return PolicyResponse.allow();
    }

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

    private boolean hasAppliedForBTOProject(User requestedUser, BTOProject btoProject) {
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getBTOProject() == btoProject,
            application -> application.getApplicant() == requestedUser
        )) > 0;
    }

    private boolean hasRegisteredForBTOProject(User requestedUser, BTOProject btoProject) {
        return dataManager.countByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getBTOProject() == btoProject,
            registration -> registration.getHDBOfficer() == requestedUser
        )) > 0;
    }

    private boolean hasNonUnsuccessfulApplications(User requestedUser) {
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getApplicant() == requestedUser,
            application -> application.getApplicationStatus() != ApplicationStatus.UNSUCCESSFUL
        )) > 0;
    }

    private boolean hasEnoughFlatNum(User requestedUser, Application approvingApplication) {
        return dataManager.countByQueries(Application.class, List.of(
            application -> application.getApplicationStatus() == ApplicationStatus.SUCCESSFUL,
            application -> application.getBTOProject() == application.getBTOProject(),
            application -> application.getFlatType() == approvingApplication.getFlatType()
        )) < approvingApplication.getFlatNum();
    }
}
