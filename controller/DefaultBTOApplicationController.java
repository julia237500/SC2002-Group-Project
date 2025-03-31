package controller;

import config.FlatType;
import config.ResponseStatus;
import controller.interfaces.BTOApplicationController;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.ServiceResponse;
import model.User;
import service.interfaces.BTOApplicationService;
import view.interfaces.MessageView;

public class DefaultBTOApplicationController extends AbstractDefaultController implements BTOApplicationController{
    private BTOApplicationService btoApplicationService;
    private SessionManager sessionManager;

    public DefaultBTOApplicationController(BTOApplicationService btoApplicationService, MessageView messageView, SessionManager sessionManager){
        super(messageView);

        this.btoApplicationService = btoApplicationService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void apply(BTOProject btoProject, FlatType flatType) {
        User user = sessionManager.getUser();
        
        ServiceResponse<?> serviceResponse = btoApplicationService.apply(user, btoProject, flatType);
        // defaultShowServiceResponse(serviceResponse);

        if(serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS) System.out.println(". Success");
        else System.out.println(". Error");
    }
}
