package manager.interfaces;

import model.User;

public interface SessionManager {
    void setUser(User user);
    User getUser();

    <T> void setSessionVariable(String key, T variable);
    <T> T getSessionVariable(String key);

    void logout();
}
