package manager.interfaces;

import model.User;

public interface SessionManager {
    void setUser(User user);
    User getUser();
    void resetUser();
}
