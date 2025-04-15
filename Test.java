import config.FlatType;
import controller.interfaces.ApplicationController;
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
        
        BTOProject btoProject = dataManager.getByPK(BTOProject.class, "Acacia Breeze");
        BTOProject btoProject2 = dataManager.getByPK(BTOProject.class, "Yellow Horizon");

        Application application = dataManager.getByPK(Application.class, "b3826f27-d5bf-48a2-af18-050c23855c14");

        User applicant = dataManager.getByPK(User.class, "S1234567A");
        User officer = dataManager.getByPK(User.class, "T1234567J");
        User manager = dataManager.getByPK(User.class, "S5678901G");
        
        sessionManager.setUser(applicant);
        // applicationController.addApplication(btoProject, FlatType.TWO_ROOM_FLAT);
        // applicationController.addApplication(btoProject, FlatType.THREE_ROOM_FLAT);
        // applicationController.withdrawApplication(application);
        // applicationController.approveWithdrawApplication(application, true);
        // applicationController.withdrawApplication(application);

        sessionManager.setUser(officer);
        // applicationController.addApplication(btoProject, FlatType.TWO_ROOM_FLAT);
        // applicationController.addApplication(btoProject, FlatType.THREE_ROOM_FLAT);
        // applicationController.withdrawApplication(application);
        // applicationController.approveWithdrawApplication(application, true);
        // applicationController.approveApplication(application, true);
        applicationController.bookApplication(application);

        sessionManager.setUser(manager);
        // applicationController.addApplication(btoProject, FlatType.TWO_ROOM_FLAT);
        // applicationController.addApplication(btoProject, FlatType.THREE_ROOM_FLAT);
        // applicationController.withdrawApplication(application);
        applicationController.approveWithdrawApplication(application, true);
        applicationController.bookApplication(application);
    }
}
