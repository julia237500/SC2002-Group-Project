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

/**
 * Default implementation of the {@link ApplicationService} interface.
 * Handles business logic for BTO project applications including validation,
 * eligibility checks, and application submissions or rejections.
 */
public class DefaultApplicationService implements ApplicationService{
    private DataManager dataManager;

    /**
     * Constructs a DefaultApplicationService with the specified data manager.
     * 
     * @param dataManager the data manager used for persistence operations
     */
    public DefaultApplicationService(DataManager dataManager){
        this.dataManager = dataManager;
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
}