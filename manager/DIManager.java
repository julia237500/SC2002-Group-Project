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
 * Manages the lifecycle of application-wide dependency configuration and injection.
 * <p>
 * This class implements the Singleton pattern to ensure only one central manager exists,
 * providing consistent access to the {@link DIContainer} throughout the application.
 * </p>
 * 
 * <p>
 * This class uses an injected {@link DIContainer} to manage actual bindings and resolution.
 * Dependency configuration should be added in {@link #config()} if used.
 * </p>
 * 
 * <p>
 * Attempting to access the singleton instance before it's initialized will result in an error.
 * Ensure the instance is properly created at the start of application via the designated method before any usage.
 * </p>
 * 
 * <strong>Note</strong>:
 * {@code DIManager} is not abstracted behind an interface itself because it does not and 
 * cannot manage its own dependencies.
 * It acts as a composition root and coordinator.
 * Abstracting this manager would not align with its purpose and may introduce unnecessary complexity.
 * 
 * @see DIContainer
 */
public class DIManager{
    private static DIManager instance;   
    private DIContainer container;

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the dependency injection container.
     *
     * @param container The DI container used to register and resolve dependencies.
     * 
     * @see DIContainer
     */
    private DIManager(DIContainer container){
        this.container = container;
    }

    /**
     * Creates singleton instance of {@code DIManager} 
     * and configures the {@link DIContainer}.
     * Can only be called once during the application lifecycle.
     *
     * @param container The DI container to be used by the DIManager.
     * @throws DependencyInjectorException if the instance has already been created.
     * 
     * @see DIContainer
     */
    public static void createInstance(DIContainer container){
        if(instance != null){
            throw new DependencyInjectorException("Instance already created.");
        }

        instance = new DIManager(container);
        instance.config();
    }

    /**
     * Retrieves the singleton instance of {@code DIManager}.
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
     * <p>
     * <strong>Note</strong>: Hardcoding the configuration simplifies the process by directly specifying which dependencies
     * need to be registered. While using configuration files (e.g., XML, JSON) 
     * would provide more flexibility and truly adhere to OCP,
     * hardcoding minimizes the complexity of managing and 
     * reading multiple configuration files or dealing with potential configuration errors at runtime.
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
