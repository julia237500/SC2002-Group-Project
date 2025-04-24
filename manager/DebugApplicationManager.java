package manager;

import java.util.Scanner;

import manager.interfaces.ApplicationManager;
import manager.interfaces.DataManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.User;

/**
 * The implementation of {@link ApplicationManager} used for debug and testing.
 * <p>
 * This implementation skips the authentication process and allows testers
 * to manually select a user to simulate login.
 * </p>
 * <strong>Note:</strong> This class is intended for testing purposes only and
 * should never be used in production environments.
 */
public class DebugApplicationManager implements ApplicationManager{
    private final MenuManager menuManager;
    private final SessionManager sessionManager;

    /**
     * Constructs a DefaultApplicationManager with its required dependencies.
     *
     * @param menuManager     Handles menu display and dashboard looping.
     * @param sessionManager  Manages the user session.
     */
    public DebugApplicationManager(MenuManager menuManager, SessionManager sessionManager){
        this.menuManager = menuManager;
        this.sessionManager = sessionManager;
    }
    
    public void startApplication() {
        final DataManager dataManager = DIManager.getInstance().resolve(DataManager.class);
        @SuppressWarnings("resource")
        final Scanner sc = new Scanner(System.in);

        User user = null;
        User applicant1 = dataManager.getByPK(User.class, "S3456789E");
        User applicant2 = dataManager.getByPK(User.class, "S9876543C");
        User officer1 = dataManager.getByPK(User.class, "T1234567J");
        User officer2 = dataManager.getByPK(User.class, "S6543210I");
        User manager1 = dataManager.getByPK(User.class, "S5678901G");
        User manager2 = dataManager.getByPK(User.class, "T8765432F");

        while(true){ 
            System.out.println("1. Applicant 1");
            System.out.println("2. Applicant 2");
            System.out.println("3. Officer 1");
            System.out.println("4. Officer 2");
            System.out.println("5. Manager 1");
            System.out.println("6. Manager 2");
            System.out.print("Login Using (Debugging): ");
            
            switch (sc.nextInt()) {
                case 1:
                    user = applicant1;
                    break;
                case 2:
                    user = applicant2;
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

    public void logout(){
        menuManager.stopDashboardLoop();
        sessionManager.logout();
    }
}
