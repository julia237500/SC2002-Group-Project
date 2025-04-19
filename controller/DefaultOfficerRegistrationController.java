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

public class DefaultOfficerRegistrationController extends AbstractDefaultController implements OfficerRegistrationController{
    private final OfficerRegistrationService officerRegistrationService;
    private final OfficerRegistrationView officerRegistrationView;
    private final SessionManager sessionManager;
    private final MenuManager menuManager;
    private final ConfirmationView confirmationView;

    public DefaultOfficerRegistrationController(OfficerRegistrationService officerRegistrationService, OfficerRegistrationView officerRegistrationView, SessionManager sessionManager, MenuManager menuManager, MessageView messageView, ConfirmationView confirmationView) {
        super(messageView);

        this.officerRegistrationService = officerRegistrationService;
        this.officerRegistrationView = officerRegistrationView;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
        this.confirmationView = confirmationView;
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

    @Override
    public void showOfficerRegistrationDetail(OfficerRegistration officerRegistration) {
        User user = sessionManager.getUser();
        officerRegistrationView.showOfficerRegistrationDetail(user.getUserRole(), officerRegistration);
    }
}
