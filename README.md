# SC2002-Group-Project

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