package manager.interfaces;

import model.User;

/**
 * The {@code SessionManager} interface defines the contract for
 * managing user session state within the application.
 * 
 * It handles setting, retrieving, and resetting the currently logged-in user.
 */
public interface SessionManager {

    /**
     * Sets the current active user for the session.
     * This should be called after a successful login.
     *
     * @param user The {@code User} object representing the logged-in user.
     */
    void setUser(User user);

    /**
     * Retrieves the current user in the session.
     * 
     * @return The {@code User} object for the currently logged-in user
     */
    User getUser();

    /**
     * Resets the current session by clearing the user data.
     * Typically used during logout.
     */
    void resetUser();
}
