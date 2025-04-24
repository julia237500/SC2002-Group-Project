package service.interfaces;

import model.User;
import service.ServiceResponse;

/**
 * Service interface for authentication and user credential management.
 * Defines operations for user login and password changes.
 */
public interface AuthService {

    /**
     * Authenticates a user with the provided credentials.
     * 
     * @param NRIC the user's National Registration Identity Card number 
     *             (cannot be null or empty)
     * @param password the user's password (cannot be null or empty)
     * @return ServiceResponse containing:
     *         - SUCCESS status with User object if authentication succeeds
     *         - ERROR status with message if authentication fails (including:
     *           - invalid NRIC
     *           - incorrect password
     *           - system errors)
     */
    public ServiceResponse<User> login(String NRIC, String password);


    /**
     * Changes a user's password after validation.
     * 
     * @param user the user whose password is being changed (cannot be null)
     * @param password the new password (cannot be null or empty)
     * @param confirmPassword the password confirmation (cannot be null or empty)
     * @return ServiceResponse containing:
     *         - SUCCESS status if password change succeeds
     *         - ERROR status with message if validation fails (including:
     *           - password/confirmation mismatch
     *           - persistence errors)
     * @implNote Implementations should:
     *           - Validate password matches confirmation
     *           - Rollback changes on persistence failure
     */
    public ServiceResponse<?> changePassword(User user, String password, String confirmPassword);
}