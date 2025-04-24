package controller.interfaces;

import config.FlatType;
import model.Application;
import model.BTOProject;
import model.User;

/**
 * A controller that processes {@link Application} in accordance with the MVC architecture.
 * Entry point to actions such as create, approve, withdraw, and others.
 *
 * @implNote This controller should remain lightweight, with the sole responsibility of 
 * coordinating interactions between the service layer, view layer, and other components.
 * All business logic should be delegated to other components.
 * 
 * @see Application
 */
public interface ApplicationController {
    /**
     * Displays all {@link Application}
     * 
     * @see Application
     */
    void showAllApplications();

    /**
     * Displays list of {@link Application} where the applicant is the logged-in {@link User}
     * 
     * @see Application
     * @see User
     */
    void showApplicationsByUser();

    /**
     * Displays list of {@link Application} that have been submitted for a specific {@link BTOProject}.
     *
     * @param btoProject the {@code BTOProject} used to search for the related {@code Application}.
     * 
     * @see Application
     * @see BTOProject
     */
    void showApplicationsByBTOProject(BTOProject btoProject);

    /**
     * Displays an {@link Application} that have been submitted for a specific {@link BTOProject} by logged-in {@link User}.
     *
     * @param btoProject the {@code BTOProject} used to search for the related {@code Application}.
     * 
     * @see Application
     * @see BTOProject
     * @see User
     */
    void showApplicationByUserAndBTOProject(BTOProject btoProject);

    /**
     * Displays the detail of an {@link Application} and its related action.
     *
     * @param application the {@code Application} to show.
     * 
     * @see Application
     */
    void showApplication(Application application);

    /**
     * Creates an {@link Application} for a given {@link FlatType} for a specific {@link BTOProject}.
     *
     * @param btoProject The {@code BTOProject} to apply.
     * @param flatType   The {@code FlatType} the user is applying for.
     * 
     * @see Application
     * @see BTOProject
     * @see FlatType
     */
    void addApplication(BTOProject btoProject, FlatType flatType);

    /**
     * Approves or reject an {@link Application}
     * 
     * @param application The {@code Application} to approve or reject
     * @param isApproving {@code true} to approve; {@code false} to reject
     * 
     * @see Application
     */
    void approveApplication(Application application, boolean isApproving);

    /**
     * Books a flat for an {@link Application} if it is approved. 
     * This method also generates a receipt upon successful booking. 
     * 
     * @param application The {@code Application} to book
     * @param isApproving {@code true} to approve; {@code false} to reject
     * 
     * @see Application
     */
    void bookApplication(Application application);

    /**
     * Withdraw from an {@link Application}.
     * 
     * @param application The {@code Application} to withdraw from
     * 
     * @see Application
     */
    void withdrawApplication(Application application);

    /**
     * Approves or rejects the withdrawal of an {@link Application}.
     * 
     * @param application The {@code Application} to approve or reject the withdrawal
     * @param isApproving {@code true} to approve; {@code false} to reject
     * 
     * @see Application
     */
    void approveWithdrawApplication(Application application, boolean isApproving);

    /**
     * Generates receipt for a booked {@link Application}.
     * The receipt consists of details of the application.
     * 
     * @param application The {@code Application} to generate receipt for
     * 
     * @see Application
     */
    void generateReceipt(Application application);

    /**
     * Generates report for a {@link BTOProject}.
     * The report consists of details of all booked {@link Application} of the project.
     * 
     * @param btoProject The {@code BTOProject} to generate receipt for
     * 
     * @see Application
     * @see BTOProject
     */
    void generateReport(BTOProject btoProject);
}
