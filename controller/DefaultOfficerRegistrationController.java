package controller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import command.Command;
import config.ResponseStatus;
import controller.interfaces.OfficerRegistrationController;
import factory.OfficerRegistrationCommandFactory;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import service.ServiceResponse;
import service.interfaces.OfficerRegistrationService;
import view.interfaces.ConfirmationView;
import view.interfaces.MessageView;
import view.interfaces.OfficerRegistrationView;


/**
 * Default implementation of the {@link OfficerRegistrationController} interface.
 * Handles user interaction and delegates officer registration logic to services and views.
 * For example, in the addOfficerRegistration() method of this class, it calls on the addOfficerRegistration() method of the officerRegistrationService class
 * This follows:
 * 1. Separation of Concerns (SoC):
 * - Controller: handles coordination.
 * - Service: contains business logic.
 * - View: handles presentation/output.
 * - Model: represents the data.
 * The controller’s main job is to coordinate — it's like the traffic cop or orchestra conductor 
 * that directs the flow between the user interface (view) and the business logic (service).
 */
public class DefaultOfficerRegistrationController extends AbstractDefaultController implements OfficerRegistrationController{
    private final OfficerRegistrationService officerRegistrationService;
    private final OfficerRegistrationView officerRegistrationView;
    private final SessionManager sessionManager;
    private final MenuManager menuManager;
    private final ConfirmationView confirmationView;

    /**
     * Constructs a DefaultOfficerRegistrationController with necessary dependencies.
     *
     * @param officerRegistrationService the service handling officer registration logic
     * @param officerRegistrationView    the view for displaying officer registration information
     * @param sessionManager             the session manager handling user login sessions
     * @param menuManager                the menu manager for displaying command options
     * @param messageView                the message view for displaying messages
     */
    public DefaultOfficerRegistrationController(OfficerRegistrationService officerRegistrationService, OfficerRegistrationView officerRegistrationView, SessionManager sessionManager, MenuManager menuManager, MessageView messageView, ConfirmationView confirmationView) {
        super(messageView);

        this.officerRegistrationService = officerRegistrationService;
        this.officerRegistrationView = officerRegistrationView;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
        this.confirmationView = confirmationView;
    }
    /**
     * Instead of doing this for our constructor:
     * public class DefaultOfficerRegistrationController {
     * private OfficerRegistrationService officerRegistrationService = new OfficerRegistrationService(); // tightly coupled
     * private OfficerRegistrationView officerRegistrationView = new OfficerRegistrationView(); },
     * We do:
     * public DefaultOfficerRegistrationController(OfficerRegistrationService officerRegistrationService, OfficerRegistrationView officerRegistrationView, SessionManager sessionManager, MenuManager menuManager, MessageView messageView){
        super(messageView);
     * This follows Inversion of Control (IoC) — the control of creating objects is moved outside of this class.
     * This is dependency injection (DI) because we’re passing in services and views via the constructor
     */

    /**
     * Adds a new officer registration for the currently logged-in officer to a specified BTO project.
     *
     * @param btoProject the BTO project to register to
     */
    @Override
    public void addOfficerRegistration(BTOProject btoProject) {
        if(!confirmationView.confirm("Are you sure you want to register for this BTO project? This is irreversible. Note that you can't apply for the same BTO project again.")){
            return;
        }

        final User user = sessionManager.getUser();
        final ServiceResponse<?> serviceResponse = officerRegistrationService.addOfficerRegistration(user, btoProject);
        defaultShowServiceResponse(serviceResponse);
    }

    /**
     * Approves or rejects an officer registration.
     *
     * @param officerRegistration the officer registration to update
     * @param isApproving         true to approve, false to reject
     */
    @Override
    public void approveOfficerRegistration(OfficerRegistration officerRegistration, boolean isApproving) {
        if(!confirmationView.confirm("Are you sure you want to %s this registration? This is irreversible.".formatted(isApproving ? "approve" : "reject"))){
            return;
        }

        final User user = sessionManager.getUser();
        final ServiceResponse<?> serviceResponse = officerRegistrationService.approveOfficerRegistration(user, officerRegistration, isApproving);
        defaultShowServiceResponse(serviceResponse);
    }

    /**
     * Displays all officer registrations made by the currently logged-in officer.
     */
    @Override
    public void showOfficerRegistrationsByOfficer() {
        final User user = sessionManager.getUser();
        menuManager.addCommands("Your Registrations as Officer", () ->
            generateShowOfficerRegistrationsCommand(() -> 
            officerRegistrationService.getOfficerRegistrationsByOfficer(user)
        ));
    }

    private Map<Integer, Command> generateShowOfficerRegistrationsCommand(Supplier<ServiceResponse<List<OfficerRegistration>>> serviceResponseSupplier) {
        ServiceResponse<List<OfficerRegistration>> serviceResponse = serviceResponseSupplier.get();

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return null;
        }

        List<OfficerRegistration> officerRegistrations = serviceResponse.getData();
        if(officerRegistrations.isEmpty()){
            messageView.info("Registration not found.");
            return null;
        }

        return OfficerRegistrationCommandFactory.getShowRegistrationsCommands(officerRegistrations);
    }

    /**
     * Displays all officer registrations associated with the specified BTO project.
     *
     * @param btoProject the BTO project whose registrations are to be shown
     */
    @Override
    public void showOfficerRegistrationsByBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();
        menuManager.addCommands("Registrations as Officer for BTO Project", () ->
            generateShowOfficerRegistrationsCommand(() -> 
            officerRegistrationService.getOfficerRegistrationsByBTOProject(user, btoProject)
        ));
    }

    @Override
    public void showOfficerRegistrationByOfficerAndBTOProject(BTOProject btoProject) {
        final User user = sessionManager.getUser();
        final ServiceResponse<OfficerRegistration> serviceResponse = officerRegistrationService.getOfficerRegistrationByOfficerAndBTOProject(user, btoProject);
        
        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
        }

        if(serviceResponse.getData() == null){
            messageView.info("Officer Registration not found.");
            return;
        }

        showOfficerRegistration(serviceResponse.getData());
    }

    /**
     * Shows the details of an officer registration and provides operations the officer can perform.
     *
     * @param officerRegistration the registration to be shown
     */
    @Override
    public void showOfficerRegistration(OfficerRegistration officerRegistration) {
        menuManager.addCommands("Operations", () ->
            generateShowOfficerRegistrationCommand(officerRegistration)
        );
    }

    private Map<Integer, Command> generateShowOfficerRegistrationCommand(OfficerRegistration officerRegistration) {
        showOfficerRegistrationDetail(officerRegistration);
        return OfficerRegistrationCommandFactory.getRegistrationOperationCommands(officerRegistration);
    }

    /**
     * Displays full details of an officer registration using the view.
     *
     * @param officerRegistration the officer registration to display
     */
    @Override
    public void showOfficerRegistrationDetail(OfficerRegistration officerRegistration) {
        officerRegistrationView.showOfficerRegistrationDetail(officerRegistration);
    }
}
