package manager;

import controller.*;
import controller.interfaces.*;
import exception.DependencyInjectorException;
import generator.report.ReportGenerator;
import generator.report.TerminalReportGenerator;
import generator.receipt.ReceiptGenerator;
import generator.receipt.TerminalReceiptGenerator;
import manager.interfaces.*;
import policy.*;
import policy.interfaces.*;
import service.*;
import service.interfaces.*;
import util.interfaces.DIContainer;
import view.interfaces.*;
import view.terminal.*;

/**
 * Manages the dependency injection lifecycle and configurations for the application.
 * Implements a singleton pattern to ensure only one instance exists.
 * The reason DIManager implements the singleton pattern — meaning only one instance can ever exist:
 * is to ensure centralized and consistent dependency management across the entire application. 
 * If multiple instances existed, different parts of the app might resolve different versions of the same object
 * leading to inconsistent behavior and bugs.
 * Avoid Duplicate Configuration:
 * - The config() method registers every class we'll need. This setup only needs to happen once.
 * - Running it multiple times might re-register or conflict services, waste memory, or introduce errors.
 */
public class DIManager{
    private static DIManager instance;   
    private DIContainer container;

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the dependency injection container.
     *
     * @param container The DI container used to register and resolve dependencies.
     * We prevent direct instantiation because:
     * - 1. Enforces Singleton Guarantee:
     * If the constructor were public, anyone could call new DIManager(...) 
     * and create multiple instances, defeating the whole purpose of the singleton.
     * That could lead to inconsistent dependency resolution or conflicting service setups.
     * - 2. Centralized Control:
     * By making the constructor private, we force everyone to go through createInstance() and getInstance(),
     * which gives us complete control over how and when the DI system is initialized.
     * - 3. Lazy Initialization:
     * We only create the instance when createInstance() is called, not at app startup.
     * This can improve performance, especially if dependency setup is expensive.
     * - 4. Safe Setup and Error Handling:
     * The createInstance() method checks if an instance already exists. If it does, it throws a custom exception.
     * This is much safer than allowing multiple parts of the codebase to blindly create new DIManager objects.
     */
    private DIManager(DIContainer container){
        this.container = container;
    }

    /**
     * Creates and configures the singleton instance of DIManager.
     * Can only be called once during the application lifecycle.
     *
     * @param container The DI container to be used by the DIManager.
     * @throws DependencyInjectorException if the instance has already been created.
     */
    public static void createInstance(DIContainer container){
        if(instance != null){
            throw new DependencyInjectorException("Instance already created.");
        }

        instance = new DIManager(container);
        instance.config();
    }

    /**
     * Retrieves the singleton instance of DIManager.
     *
     * @return The DIManager instance.
     * @throws DependencyInjectorException if the instance has not been created yet.
     */
    public static DIManager getInstance(){
        if(instance == null){
            throw new DependencyInjectorException("Instance should be created before getting.");
        }
        return instance;
    }

    /**
     * Resolves and returns an instance of the specified type from the container.
     *
     * @param type The class type to resolve.
     * @param <T>  The type of the class.
     * @return The resolved instance of the given class type.
     */
    public <T> T resolve(Class<T> type) {
        return container.resolve(type);
    }

    /**
     * Configures and registers all necessary application dependencies into the DI container.
     */
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
        container.register(BTOProjectPolicy.class, DefaultBTOProjectPolicy.class);
        container.register(BTOProjectView.class, TerminalBTOProjectView.class);

        container.register(OfficerRegistrationController.class, DefaultOfficerRegistrationController.class);
        container.register(OfficerRegistrationService.class, DefaultOfficerRegistrationService.class);
        container.register(OfficerRegistrationPolicy.class, DefaultOfficerRegistrationPolicy.class);
        container.register(OfficerRegistrationView.class, TerminalOfficerRegistrationView.class);

        container.register(EnquiryController.class, DefaultEnquiryController.class);
        container.register(EnquiryService.class, DefaultEnquiryService.class);
        container.register(EnquiryPolicy.class, DefaultEnquiryPolicy.class);
        container.register(EnquiryView.class, TerminalEnquiryView.class);

        container.register(ApplicationController.class, DefaultApplicationController.class);
        container.register(ApplicationService.class, DefaultApplicationService.class);
        container.register(ApplicationPolicy.class, DefaultApplicationPolicy.class);
        container.register(ApplicationView.class, TerminalApplicationView.class);

        container.register(MessageView.class, TerminalMessageView.class);
        container.register(ConfirmationView.class, TerminalConfirmationView.class);

        container.register(ReceiptGenerator.class, TerminalReceiptGenerator.class);

        container.register(ReportGenerator.class, TerminalReportGenerator.class);
    }
}
