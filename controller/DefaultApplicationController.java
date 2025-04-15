package controller;

import config.FlatType;
import controller.interfaces.ApplicationController;
import manager.interfaces.SessionManager;
import model.Application;
import model.BTOProject;
import model.User;
import service.ServiceResponse;
import service.interfaces.ApplicationService;
import view.interfaces.MessageView;

public class DefaultApplicationController extends AbstractDefaultController implements ApplicationController{
    private ApplicationService applicationService;
    private SessionManager sessionManager;

    public DefaultApplicationController(ApplicationService applicationService, MessageView messageView, SessionManager sessionManager){
        super(messageView);

        this.applicationService = applicationService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void addApplication(BTOProject btoProject, FlatType flatType) {
        User user = sessionManager.getUser();
        
        ServiceResponse<?> serviceResponse = applicationService.addApplication(user, btoProject, flatType);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void withdrawApplication(Application application) {
        User user = sessionManager.getUser();

        ServiceResponse<?> serviceResponse = applicationService.withdrawApplication(user, application);
        defaultShowServiceResponse(serviceResponse);
    }
}
