package controller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import command.Command;
import config.ApplicationStatus;
import config.FlatType;
import config.ResponseStatus;
import controller.interfaces.ApplicationController;
import controller.interfaces.FormController;
import factory.ApplicationCommandFactory;
import generator.receipt.ReceiptGenerator;
import filter.ApplicationFilter;
import form.ApplicationFilterForm;
import generator.report.ReportGenerator;
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
    private final ApplicationService applicationService;
    private final ApplicationView applicationView;
    private final SessionManager sessionManager;
    private final MenuManager menuManager;
    private final ConfirmationView confirmationView;
    private final ReceiptGenerator receiptGenerator;
    private final ReportGenerator reportGenerator;
    private final FormController formController;

    public DefaultApplicationController(ApplicationService applicationService, ApplicationView applicationView, MessageView messageView, SessionManager sessionManager, MenuManager menuManager, ConfirmationView confirmationView, ReceiptGenerator receiptGenerator, ReportGenerator reportGenerator, FormController formController) {
        super(messageView);

        this.applicationService = applicationService;
        this.applicationView = applicationView;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
        this.confirmationView = confirmationView;
        this.receiptGenerator = receiptGenerator;
        this.reportGenerator = reportGenerator;
        this.formController = formController;
    }

    @Override
    public void addApplication(BTOProject btoProject, FlatType flatType) {
        final User user = sessionManager.getUser();
        
        final ServiceResponse<?> serviceResponse = applicationService.addApplication(user, btoProject, flatType);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void approveApplication(Application application, boolean isApproving) {
        if(!confirmationView.confirm("Are you sure you want to %s this application? This is irreversible.".formatted(isApproving ? "approve" : "reject"))){
            return;
        }

        final User user = sessionManager.getUser();

        final ServiceResponse<?> serviceResponse = applicationService.approveApplication(user, application, isApproving);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void bookApplication(Application application) {
        if(!confirmationView.confirm("Are you sure you want to book flat for this application? This is irreversible.")){
            return;
        }

        final User user = sessionManager.getUser();

        final ServiceResponse<?> serviceResponse = applicationService.bookApplication(user, application);
        defaultShowServiceResponse(serviceResponse);

        if(serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS){
            generateReceipt(application);
        }
    }

    @Override
    public void generateReceipt(Application application) {
        receiptGenerator.generateReceipt(application);
    }

    @Override
    public void withdrawApplication(Application application) {
        if(!confirmationView.confirm("Are you sure you want to withdraw from this application? This is irreversible.")){
            return;
        }

        final User user = sessionManager.getUser();

        final ServiceResponse<?> serviceResponse = applicationService.withdrawApplication(user, application);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void approveWithdrawApplication(Application application, boolean isApproving) {
        if(!confirmationView.confirm("Are you sure you want to %s the withdrawal this application? This is irreversible.".formatted(isApproving ? "approve" : "reject"))){
            return;
        }
        
        final User user = sessionManager.getUser();

        final ServiceResponse<?> serviceResponse = applicationService.approveWithdrawApplication(user, application, isApproving);
        defaultShowServiceResponse(serviceResponse);
    }

    private Map<Integer, Command> generateShowApplicationsCommand(Supplier<ServiceResponse<List<Application>>> serviceResponseSupplier) {
        final ServiceResponse<List<Application>> serviceResponse = serviceResponseSupplier.get();
        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            defaultShowServiceResponse(serviceResponse);
            return null;
        }

        final List<Application> applications = serviceResponse.getData();
        if(applications.isEmpty()){
            messageView.info("No Applications found.");
            return null;
        }

        return ApplicationCommandFactory.getShowApplicationsCommands(applications);
    }

    @Override
    public void showAllApplications() {
        final User user = sessionManager.getUser();
        menuManager.addCommands("List of All Applications", () -> 
            generateShowApplicationsCommand(() -> applicationService.getAllApplications(user))
        );
    }

    @Override
    public void showApplicationsByUser() {
        final User user = sessionManager.getUser();
        menuManager.addCommands("Your Applications", () -> 
            generateShowApplicationsCommand(() -> applicationService.getApplicationsByUser(user))
        );
    }

    @Override
    public void showApplicationsByBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();
        menuManager.addCommands("Applications of the Project ", () -> 
            generateShowApplicationsCommand(() -> applicationService.getApplicationsByBTOProject(user, btoProject))
        );
    }

    @Override
    public void showApplicationByUserAndBTOProject(BTOProject btoProject) {
        ServiceResponse<Application> serviceResponse = applicationService.getApplicationByUserAndBTOProject(sessionManager.getUser(), btoProject);
        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            defaultShowServiceResponse(serviceResponse);
            return;
        }

        final Application application = serviceResponse.getData();
        if(application == null){
            messageView.info("Application not found.");
            return;
        }
        
        showApplication(application);
    }

    @Override
    public void showApplication(Application application) {
        menuManager.addCommands("Operations", () -> 
            generateShowApplicationCommand(application)
        );
    }

    private Map<Integer, Command> generateShowApplicationCommand(Application application) {
        showApplicationDetail(application);
        return ApplicationCommandFactory.getApplicationOperationCommands(application);
    }

    @Override
    public void showApplicationDetail(Application application) {
        applicationView.showApplicationDetail(application);
    }

    @Override
    public void generateReport(BTOProject btoProject) {
        final User user = sessionManager.getUser();
        final ServiceResponse<List<Application>> serviceResponse = applicationService.getApplicationsByBTOProject(user, btoProject);

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            defaultShowServiceResponse(serviceResponse);
        }

        List<Application> applications = serviceResponse.getData();

        formController.setForm(new ApplicationFilterForm());
        final ApplicationFilter applicationFilter = ApplicationFilter.fromFormData(formController.getFormData());

        applications = applications.stream()
            .filter(application -> application.getApplicationStatus() == ApplicationStatus.BOOKED)
            .filter(applicationFilter.getFilter())
            .collect(Collectors.toList());

        if(applications.size() == 0){
            messageView.info("Application not found");
            return;
        }

        reportGenerator.generateReport(applications);
    }
}
