package service;

import config.ResponseStatus;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.User;
import service.interfaces.AuthService;

/**
 * Default implementation of the {@link AuthService} interface that provides
 * authentication-related services including user login and password management.
 */
public class DefaultAuthService implements AuthService{
    private DataManager dataManager;

    /**
     * Constructs a DefaultAuthService with the specified data manager.
     * 
     * @param dataManager the data manager used for persistence operations 
     */
    public DefaultAuthService(DataManager dataManager){
        this.dataManager = dataManager;
    }

    /**
     * Authenticates a user with the provided NRIC and password.
     * 
     * @param NRIC the user's National Registration Identity Card number
     * @param password the user's password 
     * @return ServiceResponse containing:
     *         - SUCCESS status with User object if authentication succeeds
     *         - ERROR status with message if authentication fails
     * @see ServiceResponse
     */
    public ServiceResponse<User> login(String NRIC, String password){
        User user = null;

        try {
            user = dataManager.getByPK(User.class, NRIC);
        } catch (Exception e) {
            return new ServiceResponse<User>(ResponseStatus.ERROR, "Internal Error. " + e.getMessage());
        }
        
        if(user == null) return new ServiceResponse<User>(ResponseStatus.ERROR, "Invalid NRIC");
        if(!user.getPassword().equals(password)) return new ServiceResponse<User>(ResponseStatus.ERROR, "Incorrect Password");
        return new ServiceResponse<User>(ResponseStatus.SUCCESS, "Login Successful.", user);
    }

    /**
     * Changes a user's password after validating the new password matches confirmation.
     * Reverts to old password if persistence fails.
     * 
     * @param user the user whose password is being changed 
     * @param password the new password
     * @param confirmPassword the password confirmation 
     * @return ServiceResponse containing:
     *         - SUCCESS status if password change succeeds
     *         - FAILURE status if passwords don't match
     *         - ERROR status if persistence fails
     */
    public ServiceResponse<?> changePassword(User user, String password, String confirmPassword){
        if(!password.equals(confirmPassword)){
            return new ServiceResponse<>(ResponseStatus.FAILURE, "Password is not the same as Confirm Password");
        }

        try {
            user.setPassword(password);
            dataManager.save(user);
        } catch (DataSavingException e) {
            user.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal Error. " + e.getMessage());
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Password Changed Successful. Please login again.");
    }
}