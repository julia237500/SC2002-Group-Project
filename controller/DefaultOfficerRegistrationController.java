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
 * Default implementation of {@link OfficerRegistrationController}.
 * <p>
 * This controller is responsible for coordinating user-driven logic related to {@link OfficerRegistration}. 
 * It delegates core business logic to the {@link OfficerRegistrationService} 
 * and control UI using {@link OfficerRegistrationView}.
 * 
 * @see OfficerRegistrationController
 * @see OfficerRegistration
 * @see OfficerRegistrationService
 * @see OfficerRegistrationView
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
     * @param confirmationView           the view that handles user confirmation prompts
     * 
     * @see OfficerRegistrationService
     * @see OfficerRegistrationView
     * @see SessionManager
     * @see MenuManager
     * @see MessageView
     * @see ConfirmationView
     */
    public DefaultOfficerRegistrationController(OfficerRegistrationService officerRegistrationService, OfficerRegistrationView officerRegistrationView, SessionManager sessionManager, MenuManager menuManager, MessageView messageView, ConfirmationView confirmationView) {
        super(messageView);

        this.officerRegistrationService = officerRegistrationService;
        this.officerRegistrationView = officerRegistrationView;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
        this.confirmationView = confirmationView;
    }

    @Override
    public void showOfficerRegistrationsByOfficer() {
        final User user = sessionManager.getUser();
        menuManager.addCommands("Your Registrations as Officer", () ->
            generateShowOfficerRegistrationsCommand(() -> 
            officerRegistrationService.getOfficerRegistrationsByOfficer(user)
        ));
    }

    @Override
    public void showOfficerRegistrationsByBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();
        menuManager.addCommands("Registrations as Officer for BTO Project", () ->
            generateShowOfficerRegistrationsCommand(() -> 
            officerRegistrationService.getOfficerRegistrationsByBTOProject(user, btoProject)
        ));
    }

    /**
     * Generates a mapping of {@link Command} to show lists of {@link OfficerRegistration}, 
     * retrieved through the given supplier of {@link ServiceResponse}.
     * <p>
     * This method is intended to be passed as a {@code Supplier} to the {@link MenuManager}, allowing it to
     * dynamically refresh the list of registrations each time the menu is displayed. This supports auto-refresh
     * behavior without needing to manually update the menu contents elsewhere.
     * <p>
     * If the service call does not return a successful response or yields no registrations, a message will be shown
     * and {@code null} will be returned.
     *
     * @param serviceResponseSupplier a supplier that provides the latest {@code ServiceResponse} containing a list of registrations
     * @return a map of registration indexes to their corresponding show-detail {@code Command}, or {@code null} if no data is available
     * 
     * @see MenuManager
     * @see Command
     * @see OfficerRegistration
     * @see ServiceResponse
     */
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

    @Override
    public void showOfficerRegistration(OfficerRegistration officerRegistration) {
        menuManager.addCommands("Operations", () ->
            generateShowOfficerRegistrationCommand(officerRegistration)
        );
    }

    /**
     * Generates a mapping of {@link Command} to show operations for a specific {@link OfficerRegistration}, 
     * <p>
     * This method is intended to be passed as a {@code Supplier} to the {@link MenuManager}, allowing it to
     * dynamically refresh the operations each time the menu is displayed. This supports auto-refresh
     * behavior without needing to manually update the menu contents elsewhere.
     *
     * @param officerRegistration the registration to generate {@code Command} on
     * @return a map of operation indexes to their corresponding {@code Command}
     * 
     * @see MenuManager
     * @see Command
     * @see OfficerRegistration
     */
    private Map<Integer, Command> generateShowOfficerRegistrationCommand(OfficerRegistration officerRegistration) {
        officerRegistrationView.showOfficerRegistrationDetail(officerRegistration);
        return OfficerRegistrationCommandFactory.getRegistrationOperationCommands(officerRegistration);
    }

    @Override
    public void addOfficerRegistration(BTOProject btoProject) {
        if(!confirmationView.confirm("Are you sure you want to register for this BTO project? This is irreversible. Note that you can't apply for the same BTO project again.")){
            return;
        }

        final User user = sessionManager.getUser();
        final ServiceResponse<?> serviceResponse = officerRegistrationService.addOfficerRegistration(user, btoProject);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void approveOfficerRegistration(OfficerRegistration officerRegistration, boolean isApproving) {
        if(!confirmationView.confirm("Are you sure you want to %s this registration? This is irreversible.".formatted(isApproving ? "approve" : "reject"))){
            return;
        }

        final User user = sessionManager.getUser();
        final ServiceResponse<?> serviceResponse = officerRegistrationService.approveOfficerRegistration(user, officerRegistration, isApproving);
        defaultShowServiceResponse(serviceResponse);
    }
}
