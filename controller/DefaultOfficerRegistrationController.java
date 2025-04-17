package controller;

import java.util.List;
import java.util.Map;

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
    private OfficerRegistrationService officerRegistrationService;
    private OfficerRegistrationView officerRegistrationView;
    private SessionManager sessionManager;
    private MenuManager menuManager;

    /**
     * Constructs a DefaultOfficerRegistrationController with necessary dependencies.
     *
     * @param officerRegistrationService the service handling officer registration logic
     * @param officerRegistrationView    the view for displaying officer registration information
     * @param sessionManager             the session manager handling user login sessions
     * @param menuManager                the menu manager for displaying command options
     * @param messageView                the message view for displaying messages
     */
    public DefaultOfficerRegistrationController(OfficerRegistrationService officerRegistrationService, OfficerRegistrationView officerRegistrationView, SessionManager sessionManager, MenuManager menuManager, MessageView messageView){
        super(messageView);

        this.officerRegistrationService = officerRegistrationService;
        this.officerRegistrationView = officerRegistrationView;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
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
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = officerRegistrationService.addOfficerRegistration(user, btoProject);
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
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = officerRegistrationService.approveOfficerRegistration(user, officerRegistration, isApproving);
        defaultShowServiceResponse(serviceResponse);
    }

    /**
     * Displays all officer registrations made by the currently logged-in officer.
     */
    @Override
    public void showOfficerRegistrationsByOfficer() {
        User user = sessionManager.getUser();
        ServiceResponse<List<OfficerRegistration>> serviceResponse = officerRegistrationService.getOfficerRegistrationsByOfficer(user);

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return;
        }

        List<OfficerRegistration> officerRegistrations = serviceResponse.getData();
        if(officerRegistrations.isEmpty()){
            messageView.info("Registration not found.");
            return;
        }

        Map<Integer, Command> commands = OfficerRegistrationCommandFactory.getShowRegistrationsCommands(officerRegistrations);
        menuManager.addCommands("Registrations as Officer", commands);
    }

    /**
     * Displays all officer registrations associated with the specified BTO project.
     *
     * @param btoProject the BTO project whose registrations are to be shown
     */
    @Override
    public void showOfficerRegistrationsByBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();
        ServiceResponse<List<OfficerRegistration>> serviceResponse = officerRegistrationService.getOfficerRegistrationsByBTOProject(user, btoProject);

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return;
        }

        List<OfficerRegistration> officerRegistrations = serviceResponse.getData();
        if(officerRegistrations.isEmpty()){
            messageView.info("Registration not found.");
            return;
        }

        Map<Integer, Command> commands = OfficerRegistrationCommandFactory.getShowRegistrationsCommands(officerRegistrations);
        menuManager.addCommands("Registrations as Officer", commands);
    }

    /**
     * Shows the details of an officer registration and provides operations the officer can perform.
     *
     * @param officerRegistration the registration to be shown
     */
    @Override
    public void showOfficerRegistration(OfficerRegistration officerRegistration) {
        showOfficerRegistrationDetail(officerRegistration);
        
        if(officerRegistration.hasUnreadUpdate()){
            ServiceResponse<?> serviceResponse = officerRegistrationService.markOfficerRegistrationAsRead(officerRegistration);
            if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
                messageView.error(serviceResponse.getMessage());
            }
        }

        Map<Integer, Command> commands = OfficerRegistrationCommandFactory.getRegistrationOperationCommands(officerRegistration);
        menuManager.addCommands("Operation", commands);
    }

    /**
     * Displays full details of an officer registration using the view.
     *
     * @param officerRegistration the officer registration to display
     */
    @Override
    public void showOfficerRegistrationDetail(OfficerRegistration officerRegistration) {
        User user = sessionManager.getUser();
        officerRegistrationView.showOfficerRegistrationDetail(user.getUserRole(), officerRegistration);
    }

    /**
     * Retrieves the officer registration of the currently logged-in officer for the specified BTO project.
     *
     * @param btoProject the BTO project in question
     * @return the matching {@link OfficerRegistration}, or null if not found or retrieval failed
     */
    @Override
    public OfficerRegistration getOfficerRegistrationByOfficerAndBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();
        ServiceResponse<OfficerRegistration> serviceResponse = officerRegistrationService.getOfficerRegistrationByOfficerAndBTOProject(user, btoProject);
        
        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
        }

        return serviceResponse.getData();
    }
}
