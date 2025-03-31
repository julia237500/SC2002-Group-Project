package controller.interfaces;

import model.User;

public interface AuthController {
    public User handleLogin();
    public boolean changePassword();
}