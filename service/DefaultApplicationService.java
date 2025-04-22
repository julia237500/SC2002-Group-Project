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

/**
 * Default implementation of the {@link ApplicationService} interface.
 * Handles business logic for BTO project applications including validation,
 * eligibility checks, and application submissions or rejections.
 */
public class DefaultApplicationService implements ApplicationService{
    private final DataManager dataManager;
    private final ApplicationPolicy applicationPolicy;

    /**
     * Constructs a DefaultApplicationService with the specified data manager.
     * 
     * @param dataManager the data manager used for persistence operations
     */
    public DefaultApplicationService(DataManager dataManager, ApplicationPolicy applicationPolicy) {
        this.dataManager = dataManager;
        this.applicationPolicy = applicationPolicy; 
    }


    /**
     * Submits a new BTO project application after performing validation checks.
     * Checks that user role is applicant or HDB officer, else access denied. 
     * 
     * @param requestedUser the user submitting the application
     * @param btoProject the BTO project being applied for 
     * @param flatType the type of flat being applied for
     * @return a {@link ServiceResponse} containing either:
     *         - SUCCESS status with confirmation message, or
     *         - ERROR status with reason for rejection
     */

    /**
 * Submits a new BTO project application after performing these validation checks:
 * <ol>
 *   <li>Verifies user has APPLICANT or HDB_OFFICER role</li>
 *   <li>Ensures HDB officers aren't applying to projects they handle</li>
 *   <li>Confirms project is currently active</li>
 *   <li>Checks for existing active applications by the user</li>
 *   <li>Verifies no duplicate application for same project</li>
 * </ol>
 * 
 * If all validations pass, creates and persists a new application.
 * 
 * @param requestedUser the applicant (must have appropriate role)
 * @param btoProject the project being applied to (must be active)
 * @param flatType the desired flat type
 * @return ServiceResponse with:
 *         - ERROR status and message if any check fails
 *         - SUCCESS status if application is submitted
 * @implNote Specific error messages include:
 *           - "Access denied. Only Applicant/HDB Officer can apply..."
 *           - "Application unsuccessful. You are handling this project..."
 *           - "Application unsuccessful. This project is not opened..."
 *           - "Application unsuccessful. You are applying for other projects"
 *           - "Application unsuccessful. You have applied for this project before"
 */
    @Override
    public ServiceResponse<List<Application>> getAllApplications(User requestedUser) {
        PolicyResponse policyResponse = applicationPolicy.canViewAllApplications(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Application> applications = dataManager.getAll(Application.class, Application.SORT_BY_CREATED_AT_DESC);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, applications);
    }


    /**
     * Submits a new BTO project application after performing validation checks.
     * Checks that user role is applicant or HDB officer, else access denied. 
     * 
     * @param requestedUser the user submitting the application
     * @param btoProject the BTO project being applied for 
     * @param flatType the type of flat being applied for
     * @return a {@link ServiceResponse} containing either:
     *         - SUCCESS status with confirmation message, or
     *         - ERROR status with reason for rejection
     */

    /**
 * Submits a new BTO project application after performing these validation checks:
 * <ol>
 *   <li>Verifies user has APPLICANT or HDB_OFFICER role</li>
 *   <li>Ensures HDB officers aren't applying to projects they handle</li>
 *   <li>Confirms project is currently active</li>
 *   <li>Checks for existing active applications by the user</li>
 *   <li>Verifies no duplicate application for same project</li>
 * </ol>
 * 
 * If all validations pass, creates and persists a new application.
 * 
 * @param requestedUser the applicant (must have appropriate role)
 * @param btoProject the project being applied to (must be active)
 * @param flatType the desired flat type
 * @return ServiceResponse with:
 *         - ERROR status and message if any check fails
 *         - SUCCESS status if application is submitted
 * @implNote Specific error messages include:
 *           - "Access denied. Only Applicant/HDB Officer can apply..."
 *           - "Application unsuccessful. You are handling this project..."
 *           - "Application unsuccessful. This project is not opened..."
 *           - "Application unsuccessful. You are applying for other projects"
 *           - "Application unsuccessful. You have applied for this project before"
 */
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