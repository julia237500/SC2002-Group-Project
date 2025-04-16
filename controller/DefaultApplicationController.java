package controller;

import java.util.List;
import java.util.Map;

import command.Command;
import config.FlatType;
import config.ResponseStatus;
import controller.interfaces.ApplicationController;
import factory.ApplicationCommandFactory;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.Application;
import model.BTOProject;
import model.User;
import service.ServiceResponse;
import service.interfaces.ApplicationService;
import view.interfaces.ApplicationView;
import view.interfaces.ConfirmationView;
import view.interfaces.MessageView;

public class DefaultApplicationController extends AbstractDefaultController implements ApplicationController{
    private ApplicationService applicationService;
    private ApplicationView applicationView;
    private SessionManager sessionManager;
    private MenuManager menuManager;
    private ConfirmationView confirmationView;

    public DefaultApplicationController(ApplicationService applicationService, ApplicationView applicationView, MessageView messageView, SessionManager sessionManager, MenuManager menuManager, ConfirmationView confirmationView) {
        super(messageView);

        this.applicationService = applicationService;
        this.applicationView = applicationView;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
        this.confirmationView = confirmationView;
    }

    @Override
    public void addApplication(BTOProject btoProject, FlatType flatType) {
        User user = sessionManager.getUser();
        
        ServiceResponse<?> serviceResponse = applicationService.addApplication(user, btoProject, flatType);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void approveApplication(Application application, boolean isApproving) {
        if(!confirmationView.getConfirmation()){
            return;
        }

        User user = sessionManager.getUser();

        ServiceResponse<?> serviceResponse = applicationService.approveApplication(user, application, isApproving);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void bookApplication(Application application) {
        if(!confirmationView.getConfirmation()){
            return;
        }

        User user = sessionManager.getUser();

        ServiceResponse<?> serviceResponse = applicationService.bookApplication(user, application);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void withdrawApplication(Application application) {
        if(!confirmationView.getConfirmation()){
            return;
        }

        User user = sessionManager.getUser();

        ServiceResponse<?> serviceResponse = applicationService.withdrawApplication(user, application);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void approveWithdrawApplication(Application application, boolean isApproving) {
        if(!confirmationView.getConfirmation()){
            return;
        }
        
        User user = sessionManager.getUser();

        ServiceResponse<?> serviceResponse = applicationService.approveWithdrawApplication(user, application, isApproving);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void showAllApplications() {
        User user = sessionManager.getUser();
        ServiceResponse<List<Application>> serviceResponse = applicationService.getAllApplications(user);
        showApplications(serviceResponse, "All Applications");
    }

    @Override
    public void showApplicationsByUser() {
        User user = sessionManager.getUser();
        ServiceResponse<List<Application>> serviceResponse = applicationService.getApplicationsByUser(user);
        showApplications(serviceResponse, "Your Applications");
    }

    @Override
    public void showApplicationsByBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();
        ServiceResponse<List<Application>> serviceResponse = applicationService.getApplicationsByBTOProject(user, btoProject);
        showApplications(serviceResponse, "Applications of this BTO Project");
    }

    private void showApplications(ServiceResponse<List<Application>> serviceResponse, String title) {
        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            defaultShowServiceResponse(serviceResponse);
            return;
        }

        List<Application> applications = serviceResponse.getData();
        
        if(applications.isEmpty()){
            messageView.info("Applications not found.");
            return;
        }

        Map<Integer, Command> commands = ApplicationCommandFactory.getShowApplicationsCommands(applications);
        menuManager.addCommands("List of Applications", commands);
    }

    @Override
    public Application getApplicationByUserAndBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();
        ServiceResponse<Application> serviceResponse = applicationService.getApplicationByUserAndBTOProject(user, btoProject);
        
        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            defaultShowServiceResponse(serviceResponse);
            return null;
        }

        return serviceResponse.getData();
    }

    @Override
    public void showApplicationByUserAndBTOProject(BTOProject btoProject) {
        Application application = getApplicationByUserAndBTOProject(btoProject);
        if(application == null){
            messageView.info("Application not found.");
            return;
        }
        showApplication(application);
    }

    @Override
    public void showApplication(Application application) {
        showApplicationDetail(application);

        Map<Integer, Command> commands = ApplicationCommandFactory.getApplicationOperationCommands(application);
        menuManager.addCommands("Operations", commands);
    }


    public void showApplicationDetail(Application application) {
        applicationView.showApplicationDetail(application);
    }
}
