import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.List;

import controller.DefaultOfficerRegistrationController;
import controller.interfaces.OfficerRegistrationController;
import manager.CSVDataManager;
import manager.DIManager;
import manager.DefaultSessionManager;
import manager.interfaces.DataManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import service.DefaultOfficerRegistrationService;
import service.ServiceResponse;
import service.interfaces.OfficerRegistrationService;
import util.DefaultDIContainer;
import view.terminal.TerminalMessageView;

public class Test {
    public static void main(String[] args) {
        DIManager.createInstance(new DefaultDIContainer());
        DIManager diManager = DIManager.getInstance();
        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        MenuManager menuManager = diManager.resolve(MenuManager.class);
        DataManager dataManager = diManager.resolve(DataManager.class);
        OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);

        User officer = dataManager.getByPK(User.class, "T1234567J");
        User manager = dataManager.getByPK(User.class, "S5678901G");

        BTOProject btoProject = dataManager.getByPK(BTOProject.class, "Acacia Breeze");
        BTOProject btoProject2 = dataManager.getByPK(BTOProject.class, "Catalan Garden");

        OfficerRegistration officerRegistration = dataManager.getByPK(OfficerRegistration.class, "e1e24484-0e12-413f-bf64-035509b9d8ac");
        
        sessionManager.setUser(manager);
        officerRegistrationController.showOfficerRegistrationsByOfficer();

        // officerRegistrationController.approveOfficerRegistration(officerRegistration, false);
        // officerRegistrationController.approveOfficerRegistration(officerRegistration, true);
        // officerRegistrationController.addOfficerRegistration(btoProject);

        sessionManager.setUser(officer);
        officerRegistrationController.showOfficerRegistrationsByOfficer();
        // officerRegistrationController.approveOfficerRegistration(officerRegistration, true);
        // officerRegistrationController.addOfficerRegistration(btoProject2);
        // officerRegistrationController.addOfficerRegistration(btoProject2);
        // officerRegistrationController.addOfficerRegistration(btoProject);
    }
}
