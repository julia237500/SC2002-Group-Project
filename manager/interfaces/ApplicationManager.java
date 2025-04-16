package manager.interfaces;


/**
 * The {@code ApplicationManager} interface defines the core methods
 * for controlling the lifecycle of the application.
 * 
 * Implementing classes should provide functionality to start the application
 * and handle user logout.
 */
public interface ApplicationManager {

    /**
     * Starts the application, presenting login and menus, and entering the main application loop.
     */
    public void startApplication();

    /**
     * Logs out the current user from the application.
     * This should reset user session data and perform any necessary cleanup.
     */
    public void logout();
}