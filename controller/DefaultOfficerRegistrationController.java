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

public class DefaultOfficerRegistrationController extends AbstractDefaultController implements OfficerRegistrationController{
    private OfficerRegistrationService officerRegistrationService;
    private OfficerRegistrationView officerRegistrationView;
    private SessionManager sessionManager;
    private MenuManager menuManager;

    public DefaultOfficerRegistrationController(OfficerRegistrationService officerRegistrationService, OfficerRegistrationView officerRegistrationView, SessionManager sessionManager, MenuManager menuManager, MessageView messageView){
        super(messageView);

        this.officerRegistrationService = officerRegistrationService;
        this.officerRegistrationView = officerRegistrationView;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
    }

    @Override
    public void addOfficerRegistration(BTOProject btoProject) {
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = officerRegistrationService.addOfficerRegistration(user, btoProject);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void approveOfficerRegistration(OfficerRegistration officerRegistration, boolean isApproving) {
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = officerRegistrationService.approveOfficerRegistration(user, officerRegistration, isApproving);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void showOfficerRegistrationsByOfficer() {
        User user = sessionManager.getUser();
        ServiceResponse<List<OfficerRegistration>> serviceResponse = officerRegistrationService.getOfficerRegistrationsByOfficer(user);

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return;
        }

        List<OfficerRegistration> officerRegistrations = serviceResponse.getData();
        Map<Integer, Command> commands = OfficerRegistrationCommandFactory.getShowRegistrationsCommands(officerRegistrations);
        menuManager.addCommands("Registrations as Officer", commands);
    }

    @Override
    public void showOfficerRegistrationsByBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();
        ServiceResponse<List<OfficerRegistration>> serviceResponse = officerRegistrationService.getOfficerRegistrationsByBTOProject(user, btoProject);

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return;
        }

        List<OfficerRegistration> officerRegistrations = serviceResponse.getData();
        Map<Integer, Command> commands = OfficerRegistrationCommandFactory.getShowRegistrationsCommands(officerRegistrations);
        menuManager.addCommands("Registrations as Officer", commands);
    }

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

    @Override
    public void showOfficerRegistrationDetail(OfficerRegistration officerRegistration) {
        User user = sessionManager.getUser();
        officerRegistrationView.showOfficerRegistrationDetail(user.getUserRole(), officerRegistration);
    }

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
