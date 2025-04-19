package controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import command.Command;
import config.FlatType;
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

public class DefaultBTOProjectController extends AbstractDefaultController implements BTOProjectController{
    private static final String BTO_PROJECT_FILTER_SESSION_KEY = "bto_project_filter";

    private final BTOProjectService btoProjectService;
    private final BTOProjectView btoProjectView;
    private final FormController formController;
    private final SessionManager sessionManager;
    private final MenuManager menuManager;
    private final ConfirmationView confirmationView;

    public DefaultBTOProjectController(BTOProjectService btoProjectService, BTOProjectView btoProjectView, MessageView messageView, FormController formController, SessionManager sessionManager, MenuManager menuManager, ConfirmationView confirmationView) {
        super(messageView);

        this.btoProjectService = btoProjectService;
        this.btoProjectView = btoProjectView;
        this.formController = formController;
        this.sessionManager = sessionManager;
        this.menuManager = menuManager;
        this.confirmationView = confirmationView;
    }

    public void addBTOProject(){
        User user = sessionManager.getUser();

        formController.setForm(new BTOProjectForm());
        Map<FormField, FieldData<?>> data = formController.getFormData();
        BTOProjectDTO btoProjectDTO = createBTOProjectDTOFromFormData(data);

        ServiceResponse<?> addBTOProjectResponse = btoProjectService.addBTOProject(user, btoProjectDTO);
        defaultShowServiceResponse(addBTOProjectResponse);
    }

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

    public void editBTOProject(BTOProject btoProject){
        User user = sessionManager.getUser();

        formController.setForm(new BTOProjectForm(btoProject));
        Map<FormField, FieldData<?>> data = formController.getFormData();
        BTOProjectDTO btoProjectDTO = createBTOProjectDTOFromFormData(data);

        ServiceResponse<?> editBTOProjectResponse = btoProjectService.editBTOProject(user, btoProjectDTO, btoProject);
        defaultShowServiceResponse(editBTOProjectResponse);
    }

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

    public void showAllBTOProjects(){
        final User user = sessionManager.getUser();

        menuManager.addCommands("BTO Projects", () -> 
            generateShowBTOProjectsCommand(() -> btoProjectService.getAllBTOProjects(user)
        ));
    }

    public void showBTOProjectsHandledByUser(){
        final User user = sessionManager.getUser();

        menuManager.addCommands("Your BTO Projects", () -> 
            generateShowBTOProjectsCommand(() -> btoProjectService.getBTOProjectsHandledByUser(user))
        );
    }

    public void showBTOProject(BTOProject btoProject){
        menuManager.addCommands("Operations", () -> generateShowBTOProjectCommand(btoProject));
    }

    private Map<Integer, Command> generateShowBTOProjectCommand(BTOProject btoProject){
        showBTOProjectDetail(btoProject);
        return BTOProjectCommandFactory.getBTOProjectsOperationCommands(btoProject);
    }

    public void showBTOProjectDetail(BTOProject btoProject){
        final User user = sessionManager.getUser();

        if(user.getUserRole() == UserRole.APPLICANT){
            btoProjectView.showBTOProjectDetailRestricted(btoProject);
        }
        else{
            btoProjectView.showBTOProjectDetailFull(btoProject);
        }
    }

    public void toggleBTOProjectVisibilty(BTOProject btoProject){
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = btoProjectService.toggleBTOProjectVisibilty(user, btoProject);
        defaultShowServiceResponse(serviceResponse);
    }

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
}
