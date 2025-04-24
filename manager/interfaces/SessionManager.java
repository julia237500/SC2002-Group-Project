package manager.interfaces;

import model.User;

/**
 * The {@code SessionManager} interface defines the contract for
 * managing user session state within the application.
 * <p>
 * It handles operations related to the currently logged-in user,
 * including setting, retrieving, and resetting the session user.
 * </p>
 * <p>
 * Additionally, this interface supports storing and retrieving arbitrary session-level data,
 * functioning similarly to PHP's {@code $_SESSION} superglobal. This allows temporary
 * state or user-specific values to be maintained across different parts of the application.
 * </p>
 */
public interface SessionManager {
    /**
     * Sets the current active user for the session.
     * This should be called after a successful login.
     *
     * @param user The {@code User} representing the logged-in user.
     * 
     * @see User
     */
    void setUser(User user);

    /**
     * Retrieves the current user in the session.
     * 
     * @return The {@code User} object for the currently logged-in user
     * 
     * @see USer
     */
    User getUser();

    /**
     * Stores a session-scoped variable identified by the specified key.
     * This allows temporary data (e.g., filters, user preferences) to persist 
     * across commands and menus during a user's active session.
     *
     * @param key      the unique identifier for the variable.
     * @param variable the value to store in the session.
     * @param <T>      the type of the variable.
     */
    <T> void setSessionVariable(String key, T variable);

    /**
     * Retrieves a session-scoped variable previously stored with the given key.
     * Returns {@code null} if no such variable exists or if it has been cleared.
     *
     * @param key  the unique identifier of the variable to retrieve.
     * @param <T>  the expected type of the variable.
     * @return     the session-stored variable, or {@code null} if not found.
     */
    <T> T getSessionVariable(String key);

    /**
     * Resets the current session by clearing the user data.
     */
    void logout();
}
