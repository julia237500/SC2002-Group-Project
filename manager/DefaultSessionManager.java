package manager;

import manager.interfaces.SessionManager;
import model.User;

/**
 * Default implementation of {@link SessionManager} that handles storing and 
 * resetting the current user session in memory.
 */
public class DefaultSessionManager implements SessionManager{
    private User user;

    /**
     * Sets the current logged-in user for the session.
     *
     * @param user the {@link User} to be set in the session
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Retrieves the currently logged-in user from the session.
     *
     * @return the current {@link User} in session
     */
    public User getUser() {
        return user;
    }

    /**
     * Clears the current session by resetting the user to {@code null}.
     * Typically used during logout.
     */
    public void resetUser() {
        this.user = null;
    }
}