package service;

import java.util.List;

import config.ApplicationStatus;
import config.FlatType;
import config.ResponseStatus;
import config.UserRole;
import exception.DataModelException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.Application;
import model.BTOProject;
import model.User;
import service.interfaces.ApplicationService;

public class DefaultApplicationService implements ApplicationService{
    private DataManager dataManager;

    public DefaultApplicationService(DataManager dataManager){
        this.dataManager = dataManager;
    }

    @Override
    public ServiceResponse<?> addApplication(User requestedUser, BTOProject btoProject, FlatType flatType) {
        if(requestedUser.getUserRole() != UserRole.APPLICANT && requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only Applicant/HDB Officer can apply for BTO Project.");
        }

        if(btoProject.isHandlingBy(requestedUser)){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Application unsuccessful. You are handling this project as HDB Officer.");
        }

        if(!btoProject.isActive()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Application unsuccessful. This project is not opened for application currently.");
        }

        List<Application> applications = dataManager.getByQueries(Application.class, List.of(
            application -> application.getApplicant() == requestedUser,
            application -> application.getApplicationStatus() != ApplicationStatus.UNSUCCESSFUL
        ));

        if(applications.size() > 0){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Application unsuccessful. You are applying for other projects.");
        }

        applications = dataManager.getByQueries(Application.class, List.of(
            application -> application.getApplicant() == requestedUser,
            application -> application.getBtoProject() == btoProject
        ));

        if(applications.size() > 0){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Application unsuccessful. You have applied for this project before.");
        }

        try {
            Application application = new Application(requestedUser, btoProject, flatType);
            dataManager.save(application);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Application submitted successfully. Kindly wait for approval.");
    }

    @Override
    public ServiceResponse<?> approveApplication(User requestedUser, Application application, boolean isApproving) {
        if(application.getBtoProject().getHDBManager() != requestedUser){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Manager handling the project can approve/reject application.");
        }

        try {
            application.approveApplication(isApproving);
            dataManager.save(application);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            application.revertApplicationStatus();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Application %s successful.".formatted(isApproving ? "approved" : "rejected"));
    }

    public ServiceResponse<?> bookApplication(User requestedUser, Application application) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Officer can book application.");
        }

        if(!application.getBtoProject().isHandlingBy(requestedUser)){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Officer handling the project can book application.");
        }

        try {
            application.bookApplication();
            dataManager.save(application);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            application.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Booking successful.");
    }

    @Override
    public ServiceResponse<?> withdrawApplication(User requestedUser, Application application) {
        if(requestedUser != application.getApplicant()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only applicant of this registration can withdraw.");
        }

        try {
            application.requestWithdrawal();
            dataManager.save(application);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            application.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Withdrawal requested successful. Kindly wait for approval.");
    }

    @Override
    public ServiceResponse<?> approveWithdrawApplication(User requestedUser, Application application, boolean isApproving) {
        if(!application.getBtoProject().isHandlingBy(requestedUser)){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Manager/Officer handling the project can approve/reject withdrawal.");
        }

        try {
            application.approveWithdrawal(isApproving);
            dataManager.save(application);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            application.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Withdrawal %s successful.".formatted(isApproving ? "approved" : "rejected"));
    }
}
