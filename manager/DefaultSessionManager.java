package manager;

import manager.interfaces.SessionManager;
import model.User;

public class DefaultSessionManager implements SessionManager{
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void resetUser() {
        this.user = null;
    }
}
