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
 * This controller handles the operations related to enquiries, such as adding, editing, deleting, replying, and displaying enquiries.
 * It also provides the necessary functionality to show all enquiries, or filter them by user or BTO project.
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
     * @param enquiryService  the service that manages enquiries
     * @param enquiryView     the view responsible for displaying enquiries
     * @param formController  the controller responsible for handling form input
     * @param menuManager     the manager responsible for showing command menus
     * @param sessionManager  the session manager that provides session-related information
     * @param messageView     the view for showing messages and errors
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

    /**
     * Adds a new enquiry for a BTO project.
     *
     * @param btoProject the BTO project related to the enquiry
     */
    @Override
    public void addEnquiry(BTOProject btoProject) {
        User user = sessionManager.getUser();
        
        formController.setForm(new EnquiryForm());
        Map<FormField, FieldData<?>> data = formController.getFormData();
        String subject = (String) data.get(FormField.SUBJECT).getData();
        String enquiryString = (String) data.get(FormField.ENQUIRY).getData();

        ServiceResponse<?> serviceResponse = enquiryService.addEnquiry(user, btoProject, subject, enquiryString);
        defaultShowServiceResponse(serviceResponse);
    }

    /**
     * Edits an existing enquiry.
     *
     * @param enquiry the enquiry to edit
     */
    @Override
    public void editEnquiry(Enquiry enquiry) {
        User user = sessionManager.getUser();

        formController.setForm(new EnquiryForm(enquiry));
        Map<FormField, FieldData<?>> data = formController.getFormData();
        String subject = (String) data.get(FormField.SUBJECT).getData();
        String enquiryString = (String) data.get(FormField.ENQUIRY).getData();

        ServiceResponse<?> serviceResponse = enquiryService.editEnquiry(user, enquiry, subject, enquiryString);
        defaultShowServiceResponse(serviceResponse);
    }

    /**
     * Deletes an existing enquiry.
     *
     * @param enquiry the enquiry to delete
     */
    @Override
    public void deleteEnquiry(Enquiry enquiry) {
        if(!confirmationView.confirm("Are you sure you want to delete this enquiry? This is irreversible.")){
            return;
        }

        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = enquiryService.deleteEnquiry(user, enquiry);
        defaultShowServiceResponse(serviceResponse);

        if(serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS){
            menuManager.back();
        }
    }

    /**
     * Replies to an existing enquiry.
     *
     * @param enquiry the enquiry to reply to
     */
    @Override
    public void replyEnquiry(Enquiry enquiry){
        User user = sessionManager.getUser();

        formController.setForm(new ReplyForm());
        Map<FormField, FieldData<?>> data = formController.getFormData();
        String replyString = (String) data.get(FormField.REPLY).getData();

        ServiceResponse<?> serviceResponse = enquiryService.replyEnquiry(user, enquiry, replyString);
        defaultShowServiceResponse(serviceResponse);
    }


    /**
     * Displays all enquiries for the logged-in user.
     */
    @Override
    public void showAllEnquiries() {
        final User user = sessionManager.getUser();

        menuManager.addCommands("List of All Enquiries", () -> 
            generateShowEnquiriesCommand(() -> enquiryService.getAllEnquiries(user))
        );
    }

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


    /**
     * Displays all enquiries made by the logged-in user.
     */
    @Override
    public void showEnquiriesByUser() {
        User user = sessionManager.getUser();

        menuManager.addCommands("Your Enquiries", () -> 
            generateShowEnquiriesCommand(() -> enquiryService.getEnquiriesByUser(user))
        );
    }


    /**
     * Displays all enquiries related to a specific BTO project.
     *
     * @param btoProject the BTO project to filter the enquiries by
     */
    @Override
    public void showEnquiriesByBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();

        menuManager.addCommands("Enquiries of the project", () -> 
            generateShowEnquiriesCommand(() -> enquiryService.getEnquiriesByBTOProject(user, btoProject))
        );
    }


    /**
     * Displays the details of a specific enquiry and shows possible operations.
     *
     * @param enquiry the enquiry to display
     */
    @Override
    public void showEnquiry(Enquiry enquiry) {
        menuManager.addCommands("Operation", () -> 
            generateShowEnquiryCommand(enquiry)
        );
    }

    private Map<Integer, Command> generateShowEnquiryCommand(Enquiry enquiry){
        showEnquiryDetail(enquiry);
        return EnquiryCommandFactory.getEnquiryOperationCommands(enquiry);
    }


    /**
     * Displays the detailed view of a specific enquiry.
     *
     * @param enquiry the enquiry to display in detail
     */
    @Override
    public void showEnquiryDetail(Enquiry enquiry) {
        enquiryView.showEnquiryDetail(enquiry);
    }    
}
