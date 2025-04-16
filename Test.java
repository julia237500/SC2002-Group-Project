import config.FlatType;
import controller.interfaces.ApplicationController;
import controller.interfaces.BTOProjectController;
import manager.DIManager;
import manager.interfaces.DataManager;
import manager.interfaces.SessionManager;
import model.Application;
import model.BTOProject;
import model.User;
import util.DefaultDIContainer;

public class Test {
    public static void main(String[] args) {
        DIManager.createInstance(new DefaultDIContainer());
        DIManager diManager = DIManager.getInstance();

        DataManager dataManager = diManager.resolve(DataManager.class);
        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        ApplicationController applicationController = diManager.resolve(ApplicationController.class);
        BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        
    }
}
