package controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import config.FlatType;
import config.FormField;
import config.ResponseStatus;
import controller.interfaces.BTOProjectController;
import controller.interfaces.FormController;
import dto.BTOProjectDTO;
import factory.BTOProjectCommandFactory;
import form.BTOProjectForm;
import form.FieldData;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.User;
import service.ServiceResponse;
import service.interfaces.BTOProjectService;
import view.interfaces.BTOProjectView;
import view.interfaces.MessageView;

/**
 * Default implementation of {@link BTOProjectController}.
 * <p>
 * This controller handles interactions related to BTO projects,
 * including creating, editing, showing, and managing their visibility and deletion.
 */
public class DefaultBTOProjectController extends AbstractDefaultController implements BTOProjectController{
    private BTOProjectService btoProjectService;
    private BTOProjectView btoProjectView;
    private FormController formController;
    private SessionManager sessionManager;
    private MenuManager menuManager;

    /**
     * Constructs a new {@code DefaultBTOProjectController}.
     *
     * @param btoProjectService the service that manages BTO project operations
     * @param btoProjectView    the view responsible for displaying BTO projects
     * @param messageView       the view for showing messages and errors
     * @param formController    the controller responsible for handling form input
     * @param sessionManager    the session manager that provides session-related information
     * @param menuManager       the manager responsible for showing command menus
     */
    public DefaultBTOProjectController(BTOProjectService btoProjectService, BTOProjectView btoProjectView, MessageView messageView, FormController formController, SessionManager sessionManager, MenuManager menuManager){
        super(messageView);

        this.btoProjectService = btoProjectService;
        this.btoProjectView = btoProjectView;
        this.formController = formController;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
    }

    /**
     * Prompts user to input a new BTO project and adds it via the service.
     */
    public void addBTOProject(){
        User user = sessionManager.getUser();

        formController.setForm(new BTOProjectForm());
        Map<FormField, FieldData<?>> data = formController.getFormData();
        BTOProjectDTO btoProjectDTO = createBTOProjectDTOFromFormData(data);

        ServiceResponse<?> addBTOProjectResponse = btoProjectService.addBTOProject(user, btoProjectDTO);
        defaultShowServiceResponse(addBTOProjectResponse);
    }

    /**
     * Converts form data into a {@link BTOProjectDTO} to be used in the service layer.
     *
     * @param data form data map collected from the user
     * @return a populated BTOProjectDTO object
     */
    private BTOProjectDTO createBTOProjectDTOFromFormData(Map<FormField, FieldData<?>> data){
        String name = (String) data.get(FormField.NAME).getData();
        String neighbourhood = (String) data.get(FormField.NEIGHBORHOOD).getData();

        Map<FlatType, Integer> flatNums = new HashMap<>();
        Map<FlatType, Integer> flatPrices = new HashMap<>();

        for(FlatType flatType:FlatType.values()){
            int flatNum = (Integer) data.get(flatType.getNumFormField()).getData();
            flatNums.put(flatType, flatNum);

            int flatPrice = (Integer) data.get(flatType.getPriceFormField()).getData();
            flatPrices.put(flatType, flatPrice);
        }

        LocalDate openingDate = (LocalDate) data.get(FormField.OPENING_DATE).getData();
        LocalDate closingDate = (LocalDate) data.get(FormField.CLOSING_DATE).getData();
        int HDBOfficerLimit = (Integer) data.get(FormField.HBD_OFFICER_LIMIT).getData();

        return new BTOProjectDTO(name, neighbourhood, flatNums, flatPrices, openingDate, closingDate, HDBOfficerLimit);
    }

    /**
     * Prompts user to edit an existing BTO project.
     *
     * @param btoProject the BTO project to be edited
     */
    public void editBTOProject(BTOProject btoProject){
        User user = sessionManager.getUser();

        formController.setForm(new BTOProjectForm(btoProject));
        Map<FormField, FieldData<?>> data = formController.getFormData();
        BTOProjectDTO btoProjectDTO = createBTOProjectDTOFromFormData(data);

        ServiceResponse<?> editBTOProjectResponse = btoProjectService.editBTOProject(user, btoProjectDTO, btoProject);
        defaultShowServiceResponse(editBTOProjectResponse);
    }


    /**
     * Displays a list of all current BTO projects as selectable commands.
     * If no projects are available, an info message is shown instead.
     */
    public void showBTOProjects(){
        ServiceResponse<List<BTOProject>> getBTOProjectServiceResponse = btoProjectService.getBTOProjects();

        if(getBTOProjectServiceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(getBTOProjectServiceResponse.getMessage());
        }

        List<BTOProject> btoProjects = getBTOProjectServiceResponse.getData();

        if(btoProjects.isEmpty()){
            messageView.info("No BTO Project is opened currently. Returning to dashboard.");
            return;
        }

        Map<Integer, Command> commands = BTOProjectCommandFactory.getShowBTOProjectsCommands(btoProjects);
        menuManager.addCommands("List of BTO Project", commands);
    }

    /**
     * Displays the detailed view of a BTO project and shows available operations.
     *
     * @param btoProject the selected BTO project
     */
    public void showBTOProject(BTOProject btoProject){
        showBTOProjectDetail(btoProject);

        Map<Integer, Command> commands = BTOProjectCommandFactory.getBTOProjectsOperationCommands(btoProject);
        menuManager.addCommands("Operations", commands);
    }

    /**
     * Displays detailed information about a BTO project.
     *
     * @param btoProject the project to display
     */
    public void showBTOProjectDetail(BTOProject btoProject){
        btoProjectView.showBTOProject(btoProject);
    }
    
    /**
     * Toggles the visibility status of a given BTO project.
     *
     * @param btoProject the project to toggle visibility for
     */
    public void toggleBTOProjectVisibilty(BTOProject btoProject){
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = btoProjectService.toggleBTOProjectVisibilty(user, btoProject);
        defaultShowServiceResponse(serviceResponse);
    }

    /**
     * Deletes a BTO project.
     *
     * @param btoProject the project to delete
     */
    public void deleteBTOProject(BTOProject btoProject){
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = btoProjectService.deleteBTOProject(user, btoProject);
        defaultShowServiceResponse(serviceResponse);
    }
}
