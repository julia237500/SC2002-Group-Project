package controller;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import command.Command;
import config.FormField;
import config.ResponseStatus;
import config.UserRole;
import controller.interfaces.BTOProjectController;
import controller.interfaces.FormController;
import dto.BTOProjectDTO;
import factory.BTOProjectCommandFactory;
import filter.BTOProjectFilter;
import form.BTOProjectFilterForm;
import form.BTOProjectForm;
import form.FieldData;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.User;
import service.ServiceResponse;
import service.interfaces.BTOProjectService;
import view.interfaces.BTOProjectView;
import view.interfaces.ConfirmationView;
import view.interfaces.MessageView;

/**
 * Default implementation of {@link BTOProjectController}.
 * <p>
 * This controller is responsible for coordinating user-driven logic related to {@link BTOProject}. 
 * It delegates core business logic to the {@link BTOProjectService} 
 * and control UI using {@link BTOProjectView}.
 * 
 * @see BTOProjectController
 * @see BTOProject
 * @see BTOProjectService
 * @see BTOProjectView
 */
public class DefaultBTOProjectController extends AbstractDefaultController implements BTOProjectController{
    private static final String BTO_PROJECT_FILTER_SESSION_KEY = "bto_project_filter";

    private final BTOProjectService btoProjectService;
    private final BTOProjectView btoProjectView;
    private final FormController formController;
    private final SessionManager sessionManager;
    private final MenuManager menuManager;
    private final ConfirmationView confirmationView;

    /**
     * Constructs a new {@code DefaultBTOProjectController}.
     *
     * @param btoProjectService the service that manages BTO project operations
     * @param btoProjectView    the view responsible for displaying BTO projects
     * @param messageView       the view for showing messages and errors
     * @param formController    the controller responsible for handling form input
     * @param sessionManager    the session manager that provides session-related information
     * @param menuManager       the manager responsible for showing command menus
     * @param confirmationView  the view that handles user confirmation prompts
     * 
     * @see BTOProjectService
     * @see BTOProjectView
     * @see MessageView
     * @see FormController
     * @see SessionManager
     * @see MenuManager
     * @see ConfirmationView
     */
    public DefaultBTOProjectController(BTOProjectService btoProjectService, BTOProjectView btoProjectView, MessageView messageView, FormController formController, SessionManager sessionManager, MenuManager menuManager, ConfirmationView confirmationView) {
        super(messageView);

        this.btoProjectService = btoProjectService;
        this.btoProjectView = btoProjectView;
        this.formController = formController;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
        this.confirmationView = confirmationView;
    }

    @Override
    public void showAllBTOProjects(){
        final User user = sessionManager.getUser();

        menuManager.addCommands("BTO Projects", () -> 
            generateShowBTOProjectsCommand(() -> btoProjectService.getAllBTOProjects(user)
        ));
    }

    @Override
    public void showBTOProjectsHandledByUser(){
        final User user = sessionManager.getUser();

        menuManager.addCommands("Your BTO Projects", () -> 
            generateShowBTOProjectsCommand(() -> btoProjectService.getBTOProjectsHandledByUser(user))
        );
    }

    /**
     * Generates a mapping of {@link Command} to show lists of {@link BTOProject}, 
     * retrieved through the given supplier of {@link ServiceResponse}.
     * <p>
     * This method is intended to be passed as a {@code Supplier} to the {@link MenuManager}, allowing it to
     * dynamically refresh the list of BTO projects each time the menu is displayed. This supports auto-refresh
     * behavior without needing to manually update the menu contents elsewhere.
     * <p>
     * If the service call does not return a successful response or yields no projects, a message will be shown
     * and {@code null} will be returned.
     *
     * @param serviceResponseSupplier a supplier that provides the latest {@code ServiceResponse} containing a list of BTO projects
     * @return a map of BTO project indexes to their corresponding show-detail {@code Command}, or {@code null} if no data is available
     * 
     * @see MenuManager
     * @see Command
     * @see BTOProject
     * @see ServiceResponse
     */
    private Map<Integer, Command> generateShowBTOProjectsCommand(Supplier<ServiceResponse<List<BTOProject>>> serviceResponseSupplier){
        final ServiceResponse<List<BTOProject>> serviceResponse = serviceResponseSupplier.get();
        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return null;
        }

        List<BTOProject> btoProjects = serviceResponse.getData();

        if(btoProjects.isEmpty()){
            messageView.info("BTO Projects not found");
            return null;
        }

        BTOProjectFilter btoProjectFilter = sessionManager.getSessionVariable(BTO_PROJECT_FILTER_SESSION_KEY);
        if(btoProjectFilter != null){
            btoProjects = btoProjects.stream()
                .filter(btoProjectFilter.getFilter())
                .collect(Collectors.toList());
        }

        return BTOProjectCommandFactory.getShowBTOProjectsCommands(btoProjects);
    }

    @Override
    public void showBTOProject(BTOProject btoProject){
        menuManager.addCommands("Operations", () -> generateShowBTOProjectCommand(btoProject));
    }

    /**
     * Generates a mapping of {@link Command} to show operations for a specific {@link BTOProject}, 
     * <p>
     * This method is intended to be passed as a {@code Supplier} to the {@link MenuManager}, allowing it to
     * dynamically refresh the operations each time the menu is displayed. This supports auto-refresh
     * behavior without needing to manually update the menu contents elsewhere.
     *
     * @param btoProject the BTO project to generate {@code Command} on
     * @return a map of operation indexes to their corresponding {@code Command}
     * 
     * @see MenuManager
     * @see Command
     * @see BTOProject
     */
    private Map<Integer, Command> generateShowBTOProjectCommand(BTOProject btoProject){
        showBTOProjectDetail(btoProject);
        return BTOProjectCommandFactory.getBTOProjectsOperationCommands(btoProject);
    }

    /**
     * Displays detail information about a {@link BTOProject} according to {@link UserRole}.
     *
     * @param btoProject the project to display
     * 
     * @see BTOProject
     * @see UserRole
     */
    private void showBTOProjectDetail(BTOProject btoProject){
        final User user = sessionManager.getUser();

        if(user.getUserRole() == UserRole.APPLICANT){
            btoProjectView.showBTOProjectDetailRestricted(btoProject);
        }
        else{
            btoProjectView.showBTOProjectDetailFull(btoProject);
        }
    }

    @Override
    public void addBTOProject(){
        final User user = sessionManager.getUser();

        formController.setForm(new BTOProjectForm());
        final Map<FormField, FieldData<?>> data = formController.getFormData();
        final BTOProjectDTO btoProjectDTO = BTOProjectDTO.fromFormData(data);

        final ServiceResponse<?> addBTOProjectResponse = btoProjectService.addBTOProject(user, btoProjectDTO);
        defaultShowServiceResponse(addBTOProjectResponse);
    }
    
    @Override
    public void editBTOProject(BTOProject btoProject){
        final User user = sessionManager.getUser();

        formController.setForm(new BTOProjectForm(btoProject));
        final Map<FormField, FieldData<?>> data = formController.getFormData();
        final BTOProjectDTO btoProjectDTO = BTOProjectDTO.fromFormData(data);

        final ServiceResponse<?> editBTOProjectResponse = btoProjectService.editBTOProject(user, btoProjectDTO, btoProject);
        defaultShowServiceResponse(editBTOProjectResponse);
    }

    @Override
    public void toggleBTOProjectVisibilty(BTOProject btoProject){
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = btoProjectService.toggleBTOProjectVisibilty(user, btoProject);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void deleteBTOProject(BTOProject btoProject){
        if(!confirmationView.confirm("Are you sure you want to delete this BTO Project? This is irreversible.")){
            return;
        }

        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = btoProjectService.deleteBTOProject(user, btoProject);
        defaultShowServiceResponse(serviceResponse);

        if(serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS){
            menuManager.back();
        }
    }

    @Override
    public void setBTOProjectFilter(){
        formController.setForm(new BTOProjectFilterForm());
        Map<FormField, FieldData<?>> formData = formController.getFormData();

        BTOProjectFilter btoProjectFilter = BTOProjectFilter.fromFormData(formData);
        sessionManager.setSessionVariable(BTO_PROJECT_FILTER_SESSION_KEY, btoProjectFilter);
    }

    @Override
    public void resetBTOProjectFilter() {
        sessionManager.setSessionVariable(BTO_PROJECT_FILTER_SESSION_KEY, null);
    }
}
