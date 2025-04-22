package manager;

import java.util.HashMap;
import java.util.Map;

import manager.interfaces.SessionManager;
import model.User;

/**
 * Default implementation of {@link SessionManager} that handles storing and 
 * resetting the current user session in memory.
 */
public class DefaultSessionManager implements SessionManager{
    private User user;
    private final Map<String, Object> sessionVariables = new HashMap<>();

    /**
     * Sets the current logged-in user for the session.
     *
     * @param user the {@link User} to be set in the session
     */
    @Override
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Retrieves the currently logged-in user from the session.
     *
     * @return the current {@link User} in session
     */
    @Override
    public User getUser() {
        return user;
    }

    @Override
    public <T> void setSessionVariable(String key, T variable) {
        sessionVariables.put(key, variable);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getSessionVariable(String key) {
        return (T) sessionVariables.get(key);
    }

    @Override
    /**
     * Clears the current session by resetting the user to {@code null}.
     * Typically used during logout.
     */
    public void logout() {
        user = null;
        sessionVariables.clear();
    }
}