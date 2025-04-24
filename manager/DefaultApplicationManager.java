package manager;

import controller.interfaces.AuthController;
import manager.interfaces.ApplicationManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;

/**
 * The default implementation of {@link ApplicationManager} responsible
 * for controlling the core lifecycle of the application.
 * <p>
 * It handles login, session management, and manages the main dashboard loop,
 * delegating these responsibilities to appropriate managers.
 * </p>
 * 
 * @see ApplicationManager
 */
public class DefaultApplicationManager implements ApplicationManager{
    private MenuManager menuManager;
    private SessionManager sessionManager;
    private AuthController authController;

    /**
     * Constructs a DefaultApplicationManager with its required dependencies.
     *
     * @param menuManager     Handles menu display and dashboard looping.
     * @param sessionManager  Manages the user session.
     * @param authController  Handles authentication.
     * 
     * @see MenuManager
     * @see SessionManager
     * @see AuthController
     */
    public DefaultApplicationManager(MenuManager menuManager, SessionManager sessionManager, AuthController authController){
        this.menuManager = menuManager;
        this.sessionManager = sessionManager;
        this.authController = authController;
    }
    public void startApplication() {
        while(true){ 
            authController.handleLogin();
            menuManager.startDashboardLoop();
        }
    }

    public void logout(){
        menuManager.stopDashboardLoop();
        sessionManager.logout();
    }
}
