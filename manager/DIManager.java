package manager;

import controller.*;
import controller.interfaces.*;
import exception.DependencyInjectorException;
import manager.interfaces.*;
import service.*;
import service.interfaces.*;
import util.interfaces.DIContainer;
import view.interfaces.*;
import view.terminal.*;

public class DIManager{
    private static DIManager instance;   
    private DIContainer container;

    private DIManager(DIContainer container){
        this.container = container;
    }

    public static void createInstance(DIContainer container){
        if(instance != null){
            throw new DependencyInjectorException("Instance already created.");
        }

        instance = new DIManager(container);
        instance.config();
    }

    public static DIManager getInstance(){
        if(instance == null){
            throw new DependencyInjectorException("Instance should be created before getting.");
        }
        return instance;
    }

    public <T> T resolve(Class<T> type) {
        return container.resolve(type);
    }

    private void config() {
        container.register(ApplicationManager.class, DefaultApplicationManager.class);
        container.register(SessionManager.class, DefaultSessionManager.class);
        container.register(MenuManager.class, DefaultMenuManager.class);
        container.register(DataManager.class, CSVDataManager.class);

        container.register(AuthController.class, DefaultAuthController.class);
        container.register(AuthService.class, DefaultAuthService.class);

        container.register(FormController.class, DefaultFormController.class);
        container.register(FormView.class, TerminalFormView.class);

        container.register(CommandController.class, DefaultCommandController.class);
        container.register(CommandView.class, TerminalCommandView.class);

        container.register(BTOProjectController.class, DefaultBTOProjectController.class);
        container.register(BTOProjectService.class, DefaultBTOProjectService.class);
        container.register(BTOProjectView.class, TerminalBTOProjectView.class);

        container.register(OfficerRegistrationController.class, DefaultOfficerRegistrationController.class);
        container.register(OfficerRegistrationService.class, DefaultOfficerRegistrationService.class);
        container.register(OfficerRegistrationView.class, TerminalOfficerRegistrationView.class);

        container.register(EnquiryController.class, DefaultEnquiryController.class);
        container.register(EnquiryService.class, DefaultEnquiryService.class);
        container.register(EnquiryView.class, TerminalEnquiryView.class);

        container.register(ApplicationController.class, DefaultApplicationController.class);
        container.register(ApplicationService.class, DefaultApplicationService.class);
        container.register(ApplicationView.class, TerminalApplicationView.class);

        container.register(MessageView.class, TerminalMessageView.class);
        container.register(ConfirmationView.class, TerminalConfirmationView.class);
    }
}
