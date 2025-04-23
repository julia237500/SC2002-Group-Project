package policy.interfaces;

import config.FlatType;
import model.Application;
import model.BTOProject;
import model.User;
import policy.PolicyResponse;

/**
 * Defines the contract for application-related authorization policies within the system.
 * <p>
 * Implementations of this interface should contain business rules that determine whether
 * a given {@link User} has permission to perform specific actions related to {@link Application}s.
 * </p>
 * 
 * <p>Each method returns a {@link PolicyResponse}, which indicates whether the action is allowed,
 * and may optionally include messages or metadata useful for authorization handling.</p>
 */
public interface ApplicationPolicy {

    /**
     * Determines if the requested user can view all applications in the system.
     *
     * @param requestedUser the user attempting to access the application data
     * @return a {@link PolicyResponse} indicating whether the action is permitted
     */
    PolicyResponse canViewAllApplications(User requestedUser);

    /**
     * Determines if the user can view applications related to a specific BTO project.
     *
     * @param requestedUser the user attempting to view the applications
     * @param btoProject the BTO project in question
     * @return a {@link PolicyResponse} indicating if viewing is allowed
     */
    PolicyResponse canViewApplicationsByBTOProject(User requestedUser, BTOProject btoProject);

    /**
     * Determines if the user can view all applications they themselves submitted.
     *
     * @param requestedUser the user attempting to view their applications
     * @return a {@link PolicyResponse} indicating if access is granted
     */
    PolicyResponse canViewApplicationsByUser(User requestedUser);

    /**
     * Determines if the user can view their own application to a specific BTO project.
     *
     * @param requestedUser the user attempting to view the application
     * @param btoProject the project for which the application was made
     * @return a {@link PolicyResponse} indicating authorization status
     */
    PolicyResponse canViewApplicationByUserAndBTOProject(User requestedUser, BTOProject btoProject);

    /**
     * Checks whether the user can create a new application for the specified BTO project and flat type.
     *
     * @param requestedUser the user attempting to create the application
     * @param btoProject the target BTO project
     * @param flatType the desired flat type
     * @return a {@link PolicyResponse} representing permission status
     */
    PolicyResponse canCreateApplication(User requestedUser, BTOProject btoProject, FlatType flatType);

    /**
     * Determines if the user can approve or reject an application.
     *
     * @param requestedUser the user attempting the approval action
     * @param application the application to be approved or rejected
     * @param isApproving true if approving, false if rejecting
     * @return a {@link PolicyResponse} indicating if the action is permitted
     */
    PolicyResponse canApproveApplication(User requestedUser, Application application, boolean isApproving);

    /**
     * Checks if the user can book a flat through the given application.
     *
     * @param requestedUser the user attempting the booking
     * @param application the application involved in the booking
     * @return a {@link PolicyResponse} representing the authorization result
     */
    PolicyResponse canBookApplication(User requestedUser, Application application);

    /**
     * Checks whether a user has permission to generate a receipt for a specific application.
     *
     * @param requestedUser the user requesting receipt generation
     * @param application the application for which the receipt is to be generated
     * @return a {@link PolicyResponse} indicating if receipt generation is allowed
     */
    PolicyResponse canGenerateReceipt(User requestedUser, Application application);

    /**
     * Checks if the user can withdraw an application.
     *
     * @param requestedUser the user requesting to withdraw
     * @param application the application to be withdrawn
     * @return a {@link PolicyResponse} indicating permission status
     */
    PolicyResponse canWithdrawApplication(User requestedUser, Application application);

    /**
     * Determines if the user can approve the withdrawal of an application.
     *
     * @param requestedUser the user attempting to approve the withdrawal
     * @param application the application being withdrawn
     * @return a {@link PolicyResponse} indicating if approval is allowed
     */
    PolicyResponse canApproveWithdrawApplication(User requestedUser, Application application);

    /**
     * Checks whether a user can generate a report for applications related to a given BTO project.
     *
     * @param requestedUser the user attempting to generate the report
     * @param btoProject the BTO project in context
     * @return a {@link PolicyResponse} indicating authorization status
     */
    PolicyResponse canGenerateReport(User requestedUser, BTOProject btoProject);
}
