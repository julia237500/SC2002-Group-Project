package controller;

import config.FlatType;
import controller.interfaces.ApplicationController;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.User;
import service.ServiceResponse;
import service.interfaces.ApplicationService;
import view.interfaces.MessageView;

/**
 * Default implementation of the {@link ApplicationController} interface.
 * <p>
 * This controller is responsible for handling user-driven logic related to 
 * applications for BTO projects. It delegates core business logic to the 
 * {@link ApplicationService} and uses the {@link SessionManager} to retrieve 
 * the currently logged-in user.
 */
public class DefaultApplicationController extends AbstractDefaultController implements ApplicationController{
    private ApplicationService applicationService;
    private SessionManager sessionManager;

    /**
     * Constructs a new {@code DefaultApplicationController}.
     *
     * @param applicationService the service that handles application-related logic
     * @param messageView        the view used to display messages to the user
     * @param sessionManager     the session manager that provides user session data
     */
    public DefaultApplicationController(ApplicationService applicationService, MessageView messageView, SessionManager sessionManager){
        super(messageView);

        this.applicationService = applicationService;
        this.sessionManager = sessionManager;
    }

    /**
     * Adds an application for the given BTO project and flat type using the currently logged-in user.
     *
     * @param btoProject the BTO project the user is applying for
     * @param flatType   the type of flat the user is applying for
     */
    @Override
    public void addApplication(BTOProject btoProject, FlatType flatType) {
        User user = sessionManager.getUser();
        
        ServiceResponse<?> serviceResponse = applicationService.addApplication(user, btoProject, flatType);
        defaultShowServiceResponse(serviceResponse);
    }
}
