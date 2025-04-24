package controller.interfaces;

/**
 * A controller that handle user authentication in accordance with the MVC architecture.
 * Entry point to actions such as login and change password.
 *
 * @implNote This controller should remain lightweight, with the sole responsibility of 
 * coordinating interactions between the service layer, view layer, and other components.
 * All business logic should be delegated to other components.
 */
public interface AuthController {
    /**
     * Prompts and handles the login process for a user, including setting the user to session.
     */
    public void handleLogin();

    /**
     * Prompts and handles the password change for the logged-in user.
     *
     * @return {@code true} if the password was successfully changed; {@code false} otherwise.
     */
    public boolean changePassword();
}