package manager;

import controller.DefaultAuthController;
import controller.DefaultBTOProjectController;
import controller.DefaultCommandController;
import controller.DefaultFormController;
import controller.interfaces.AuthController;
import controller.interfaces.BTOProjectController;
import controller.interfaces.CommandController;
import controller.interfaces.FormController;
import exception.DependencyInjectorException;
import manager.interfaces.ApplicationManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import service.DefaultAuthService;
import service.DefaultBTOProjectService;
import service.interfaces.AuthService;
import service.interfaces.BTOProjectService;
import util.interfaces.DIContainer;
import view.interfaces.BTOProjectView;
import view.interfaces.CommandView;
import view.interfaces.ConfirmationView;
import view.interfaces.FormView;
import view.interfaces.MessageView;
import view.terminal.TerminalBTOProjectView;
import view.terminal.TerminalCommandView;
import view.terminal.TerminalConfirmationView;
import view.terminal.TerminalFormView;
import view.terminal.TerminalMessageView;

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

        container.register(AuthService.class, DefaultAuthService.class);
        container.register(AuthController.class, DefaultAuthController.class);

        container.register(FormController.class, DefaultFormController.class);
        container.register(FormView.class, TerminalFormView.class);

        container.register(CommandController.class, DefaultCommandController.class);
        container.register(CommandView.class, TerminalCommandView.class);

        container.register(BTOProjectController.class, DefaultBTOProjectController.class);
        container.register(BTOProjectService.class, DefaultBTOProjectService.class);
        container.register(BTOProjectView.class, TerminalBTOProjectView.class);

        container.register(MessageView.class, TerminalMessageView.class);
        container.register(ConfirmationView.class, TerminalConfirmationView.class);
    }
}
