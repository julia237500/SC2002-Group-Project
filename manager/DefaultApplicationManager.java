package manager;

import config.MaritalStatus;
import config.UserRole;
import controller.interfaces.AuthController;
import manager.interfaces.ApplicationManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.User;
import view.terminal.AbstractTerminalView;

public class DefaultApplicationManager implements ApplicationManager{
    private MenuManager menuManager;
    private SessionManager sessionManager;
    private AuthController authController;

    public DefaultApplicationManager(MenuManager menuManager, SessionManager sessionManager, AuthController authController){
        this.menuManager = menuManager;
        this.sessionManager = sessionManager;
        this.authController = authController;
    }
    
    public void startApplication() {
        while(true){ 
            User user = null;
            user = authController.handleLogin();
            
            User applicant1 = new User("applicant1", "1", 1, MaritalStatus.SINGLE, "1", UserRole.APPLICANT);
            User manager1 = new User("manager1", "1", 1, MaritalStatus.SINGLE, "1", UserRole.HDB_MANAGER);
            User manager2 = new User("manager2", "1", 1, MaritalStatus.SINGLE, "1", UserRole.HDB_MANAGER);
            
            // System.out.println("1. Applicant 1");
            // System.out.println("5. Manager 1");
            // System.out.println("6. Manager 2");
            // System.out.print("Login Using (Debugging): ");

            // switch (AbstractTerminalView.getSc().nextInt()) {
            //     case 1:
            //         user = applicant1;
            //         break;
            //     case 5:
            //         user = manager1;
            //         break;
            //     case 6:
            //         user = manager2;
            //         break;
            //     default:
            //         break;
            // }
            
            System.out.println(user.getName());
            sessionManager.setUser(user);
            menuManager.startDashboardLoop();

            System.out.println("Logout");
        }
    }

    public void logout(){
        menuManager.stopDashboardLoop();
        sessionManager.resetUser();
    }
}
