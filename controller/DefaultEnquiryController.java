package controller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import command.Command;
import config.FormField;
import config.ResponseStatus;
import controller.interfaces.EnquiryController;
import controller.interfaces.FormController;
import factory.EnquiryCommandFactory;
import form.EnquiryForm;
import form.FieldData;
import form.ReplyForm;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.Enquiry;
import model.User;
import service.ServiceResponse;
import service.interfaces.EnquiryService;
import view.interfaces.ConfirmationView;
import view.interfaces.EnquiryView;
import view.interfaces.MessageView;


/**
 * Default implementation of {@link EnquiryController}.
 * <p>
 * This controller is responsible for coordinating user-driven logic related to {@link Enquiry}. 
 * It delegates core business logic to the {@link EnquiryService} 
 * and control UI using {@link EnquiryView}.
 * 
 * @see EnquiryController
 * @see Enquiry
 * @see EnquiryService
 * @see EnquiryView
 */
public class DefaultEnquiryController extends AbstractDefaultController implements EnquiryController{
    private final EnquiryService enquiryService;
    private final EnquiryView enquiryView;
    private final FormController formController;
    private final MenuManager menuManager;
    private final SessionManager sessionManager;
    private final ConfirmationView confirmationView;

    /**
     * Constructs a new {@code DefaultEnquiryController}.
     *
     * @param enquiryService   the service that manages enquiries
     * @param enquiryView      the view responsible for displaying enquiries
     * @param formController   the controller responsible for handling form input
     * @param menuManager      the manager responsible for showing command menus
     * @param sessionManager   the session manager that provides session-related information
     * @param messageView      the view for showing messages and errors
     * @param confirmationView the view that handles user confirmation prompts
     * 
     * @see EnquiryService
     * @see EnquiryView
     * @see FormController
     * @see MenuManager
     * @see SessionManager
     * @see MessageView
     * @see ConfirmationView
     */
    public DefaultEnquiryController(EnquiryService enquiryService, EnquiryView enquiryView, FormController formController, MenuManager menuManager, SessionManager sessionManager, MessageView messageView, ConfirmationView confirmationView) {
        super(messageView);

        this.enquiryService = enquiryService;
        this.enquiryView = enquiryView;
        this.formController = formController;
        this.menuManager = menuManager;
        this.sessionManager = sessionManager;
        this.confirmationView = confirmationView;
    }

    @Override
    public void showAllEnquiries() {
        final User user = sessionManager.getUser();

        menuManager.addCommands("List of All Enquiries", () -> 
            generateShowEnquiriesCommand(() -> enquiryService.getAllEnquiries(user))
        );
    }

    @Override
    public void showEnquiriesByUser() {
        final User user = sessionManager.getUser();

        menuManager.addCommands("Your Enquiries", () -> 
            generateShowEnquiriesCommand(() -> enquiryService.getEnquiriesByUser(user))
        );
    }

    @Override
    public void showEnquiriesByBTOProject(BTOProject btoProject) {
        final User user = sessionManager.getUser();

        menuManager.addCommands("Enquiries of the project", () -> 
            generateShowEnquiriesCommand(() -> enquiryService.getEnquiriesByBTOProject(user, btoProject))
        );
    }

    /**
     * Generates a mapping of {@link Command} to show lists of {@link Enquiry}, 
     * retrieved through the given supplier of {@link ServiceResponse}.
     * <p>
     * This method is intended to be passed as a {@code Supplier} to the {@link MenuManager}, allowing it to
     * dynamically refresh the list of enquries each time the menu is displayed. This supports auto-refresh
     * behavior without needing to manually update the menu contents elsewhere.
     * <p>
     * If the service call does not return a successful response or yields no enquries, a message will be shown
     * and {@code null} will be returned.
     *
     * @param serviceResponseSupplier a supplier that provides the latest {@code ServiceResponse} containing a list of enquiries
     * @return a map of enquiry indexes to their corresponding show-detail {@code Command}, or {@code null} if no data is available
     * 
     * @see MenuManager
     * @see Command
     * @see Enquiry
     * @see ServiceResponse
     */
    private Map<Integer, Command> generateShowEnquiriesCommand(Supplier<ServiceResponse<List<Enquiry>>> serviceResponseSupplier){
        final ServiceResponse<List<Enquiry>> serviceResponse = serviceResponseSupplier.get();

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return null;
        }

        final List<Enquiry> enquiries = serviceResponse.getData();
        if(enquiries.isEmpty()){
            messageView.info("Enquiries not found.");
            return null;
        }
        
        return EnquiryCommandFactory.getShowEnquiriesCommands(enquiries);
    }

    @Override
    public void showEnquiry(Enquiry enquiry) {
        menuManager.addCommands("Operation", () -> 
            generateShowEnquiryCommand(enquiry)
        );
    }

    /**
     * Generates a mapping of {@link Command} to show operations for a specific {@link Enquiry}, 
     * <p>
     * This method is intended to be passed as a {@code Supplier} to the {@link MenuManager}, allowing it to
     * dynamically refresh the operations each time the menu is displayed. This supports auto-refresh
     * behavior without needing to manually update the menu contents elsewhere.
     *
     * @param enquiry the enquiry to generate {@code Command} on
     * @return a map of operation indexes to their corresponding {@code Command}
     * 
     * @see MenuManager
     * @see Command
     * @see Enquiry
     */
    private Map<Integer, Command> generateShowEnquiryCommand(Enquiry enquiry){
        enquiryView.showEnquiryDetail(enquiry);
        return EnquiryCommandFactory.getEnquiryOperationCommands(enquiry);
    }

    @Override
    public void addEnquiry(BTOProject btoProject) {
        final User user = sessionManager.getUser();
        
        formController.setForm(new EnquiryForm());
        final Map<FormField, FieldData<?>> data = formController.getFormData();
        final String subject = (String) data.get(FormField.SUBJECT).getData();
        final String enquiryString = (String) data.get(FormField.ENQUIRY).getData();

        final ServiceResponse<?> serviceResponse = enquiryService.addEnquiry(user, btoProject, subject, enquiryString);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void editEnquiry(Enquiry enquiry) {
        final User user = sessionManager.getUser();

        formController.setForm(new EnquiryForm(enquiry));
        final Map<FormField, FieldData<?>> data = formController.getFormData();
        final String subject = (String) data.get(FormField.SUBJECT).getData();
        final String enquiryString = (String) data.get(FormField.ENQUIRY).getData();

        final ServiceResponse<?> serviceResponse = enquiryService.editEnquiry(user, enquiry, subject, enquiryString);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void deleteEnquiry(Enquiry enquiry) {
        if(!confirmationView.confirm("Are you sure you want to delete this enquiry? This is irreversible.")){
            return;
        }

        final User user = sessionManager.getUser();
        final ServiceResponse<?> serviceResponse = enquiryService.deleteEnquiry(user, enquiry);
        defaultShowServiceResponse(serviceResponse);

        if(serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS){
            menuManager.back();
        }
    }

    @Override
    public void replyEnquiry(Enquiry enquiry){
        final User user = sessionManager.getUser();

        formController.setForm(new ReplyForm());
        final Map<FormField, FieldData<?>> data = formController.getFormData();
        final String replyString = (String) data.get(FormField.REPLY).getData();

        final ServiceResponse<?> serviceResponse = enquiryService.replyEnquiry(user, enquiry, replyString);
        defaultShowServiceResponse(serviceResponse);
    }
}
