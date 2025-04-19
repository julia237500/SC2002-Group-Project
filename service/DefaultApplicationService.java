package service;

import java.util.List;

import config.FlatType;
import config.ResponseStatus;
import exception.DataModelException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.Application;
import model.BTOProject;
import model.User;
import policy.PolicyResponse;
import policy.interfaces.ApplicationPolicy;
import service.interfaces.ApplicationService;

public class DefaultApplicationService implements ApplicationService{
    private final DataManager dataManager;
    private final ApplicationPolicy applicationPolicy;

    public DefaultApplicationService(DataManager dataManager, ApplicationPolicy applicationPolicy) {
        this.dataManager = dataManager;
        this.applicationPolicy = applicationPolicy;
    }

    @Override
    public ServiceResponse<List<Application>> getAllApplications(User requestedUser) {
        PolicyResponse policyResponse = applicationPolicy.canViewAllApplications(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Application> applications = dataManager.getAll(Application.class, Application.SORT_BY_CREATED_AT_DESC);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, applications);
    }

    @Override
    public ServiceResponse<List<Application>> getApplicationsByUser(User requestedUser) {
        PolicyResponse policyResponse = applicationPolicy.canViewApplicationsByUser(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Application> applications = dataManager.getByQuery(Application.class,
            application -> application.getApplicant() == requestedUser,
            Application.SORT_BY_CREATED_AT_DESC
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, applications);
    }

    @Override
    public ServiceResponse<List<Application>> getApplicationsByBTOProject(User requestedUser, BTOProject btoProject) {
        PolicyResponse policyResponse = applicationPolicy.canViewApplicationsByBTOProject(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Application> applications = dataManager.getByQuery(Application.class,
            application -> application.getBTOProject() == btoProject,
            Application.SORT_BY_CREATED_AT_DESC
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, applications);
    }

    @Override
    public ServiceResponse<Application> getApplicationByUserAndBTOProject(User requestedUser, BTOProject btoProject) {
        PolicyResponse policyResponse = applicationPolicy.canViewApplicationByUserAndBTOProject(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Application> applications = dataManager.getByQueries(Application.class, List.of(
            application -> application.getApplicant() == requestedUser,
            application -> application.getBTOProject() == btoProject
        ));

        Application application = null;
        if(!applications.isEmpty()){
            application = applications.get(0);
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, application);
    }

    @Override
    public ServiceResponse<?> addApplication(User requestedUser, BTOProject btoProject, FlatType flatType) {
        PolicyResponse policyResponse = applicationPolicy.canCreateApplication(requestedUser, btoProject, flatType);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
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
        PolicyResponse policyResponse = applicationPolicy.canApproveApplication(requestedUser, application, isApproving);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            application.approveApplication(isApproving);
            dataManager.save(application);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            application.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Application %s successful.".formatted(isApproving ? "approved" : "rejected"));
    }

    public ServiceResponse<?> bookApplication(User requestedUser, Application application) {
        PolicyResponse policyResponse = applicationPolicy.canBookApplication(requestedUser, application);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
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
        PolicyResponse policyResponse = applicationPolicy.canWithdrawApplication(requestedUser, application);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
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
        PolicyResponse policyResponse = applicationPolicy.canApproveWithdrawApplication(requestedUser, application);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
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
