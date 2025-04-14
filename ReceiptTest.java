import config.FlatType;
import controller.interfaces.ApplicationController;
import manager.DIManager;
import manager.interfaces.DataManager;
import manager.interfaces.SessionManager;
import model.Application;
import model.BTOProject;
import model.User;
import util.DefaultDIContainer;

public class ReceiptTest {
    public static void main(String[] args) {
        DIManager.createInstance(new DefaultDIContainer());
        DIManager diManager = DIManager.getInstance();

        DataManager dataManager = diManager.resolve(DataManager.class);
        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        ApplicationController applicationController = diManager.resolve(ApplicationController.class);
        
        Application application = dataManager.getByPK(Application.class, "b3826f27-d5bf-48a2-af18-050c23855c14");
    }
}
