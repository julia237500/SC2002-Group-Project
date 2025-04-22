package controller.interfaces;

/**
 * Interface for handling user authentication operations.
 * <p>
 * This interface defines methods for logging in a user and changing a user's password.
 * </p>
 */
public interface AuthController {

    /**
     * Prompts and handles the login process for a user.
     *
     * @return the authenticated {@link User} if login is successful
     */
    public void handleLogin();

    /**
     * Initiates and processes a password change for the currently authenticated user.
     *
     * @return {@code true} if the password was successfully changed; {@code false} otherwise.
     */
    public boolean changePassword();
}