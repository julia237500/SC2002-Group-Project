package manager;

import config.MaritalStatus;
import config.UserRole;
import controller.interfaces.AuthController;
import manager.interfaces.ApplicationManager;
import manager.interfaces.DataManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.User;
import view.terminal.AbstractTerminalView;

/**
 * The default implementation of {@link ApplicationManager} responsible for starting the application,
 * handling login (via debug simulation), session management, and controlling the dashboard loop.
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
     * @param authController  Handles authentication (not actively used in debug mode).
     */
    public DefaultApplicationManager(MenuManager menuManager, SessionManager sessionManager, AuthController authController){
        this.menuManager = menuManager;
        this.sessionManager = sessionManager;
        this.authController = authController;
    }
    
     /**
     * Starts the application loop. For debugging purposes, simulates login by allowing the user to select
     * from a predefined list of users (applicants, officers, managers).
     *
     * After login, sets the user in session and starts the dashboard loop.
     * This runs indefinitely in a `while(true)` loop to simulate continuous usage.
     */
    public void startApplication() {
        while(true){ 
            User user = null;
            DataManager dataManager = DIManager.getInstance().resolve(DataManager.class);
            // user = authController.handleLogin();
            /**
             * This is not real login — instead, it lets us quickly simulate different users for testing purposes 
             * without needing a username/password screen.
             * Debug: We use this to test the system easily.
             * Hardcoded users: These specific users are manually written into the code (not dynamically chosen or inputted by a real user).
             * Will be replaced with user = authController.handleLogin();
             * - With real authentication where users input their credentials. But for now, this “debug login” lets us test roles and flows fast.
             */
            
             // Debug login options using hardcoded users
            User applicant1 = dataManager.getByPK(User.class, "S3456789E");;
            User officer1 = dataManager.getByPK(User.class, "T1234567J");
            User officer2 = dataManager.getByPK(User.class, "S6543210I");
            User manager1 = dataManager.getByPK(User.class, "S5678901G");
            User manager2 = dataManager.getByPK(User.class, "T8765432F");
            
            System.out.println("1. Applicant 1");
            System.out.println("3. Officer 1");
            System.out.println("4. Officer 2");
            System.out.println("5. Manager 1");
            System.out.println("6. Manager 2");
            System.out.print("Login Using (Debugging): ");

            switch (AbstractTerminalView.getSc().nextInt()) {
                case 1:
                    user = applicant1;
                    break;
                case 3:
                    user = officer1;
                    break;
                case 4:
                    user = officer2;
                    break;
                case 5:
                    user = manager1;
                    break;
                case 6:
                    user = manager2;
                    break;
                default:
                    break;
            }
            
            sessionManager.setUser(user);
            menuManager.startDashboardLoop();
        }
    }

     /**
     * Logs out the current user by stopping the dashboard loop and clearing the session.
     */
    public void logout(){
        menuManager.stopDashboardLoop();
        sessionManager.resetUser();
    }
}
