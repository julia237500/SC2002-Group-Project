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

    @Override
    public void setUser(User user) {
        this.user = user;
    }

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
    public void logout() {
        user = null;
        sessionVariables.clear();
    }
}