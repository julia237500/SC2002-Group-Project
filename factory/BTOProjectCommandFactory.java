package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import command.LambdaCommand;
import command.general.MenuBackCommand;
import config.FlatType;
import controller.interfaces.ApplicationController;
import controller.interfaces.BTOProjectController;
import controller.interfaces.EnquiryController;
import controller.interfaces.OfficerRegistrationController;
import model.Application;
import model.BTOProject;
import model.Enquiry;
import model.OfficerRegistration;
import model.User;
import policy.interfaces.ApplicationPolicy;
import policy.interfaces.BTOProjectPolicy;
import policy.interfaces.EnquiryPolicy;
import policy.interfaces.OfficerRegistrationPolicy;


/**
 * A factory class for generating {@link Command} instances related to {@link BTOProject}.
 * <p>
 * Commands are generated and stored in a {@code Map<Integer, Command>}.
 * This factory can generate commands for the following operations:
 * <ol>
 *   <li> Display details for a list of projects.
 *   <li> Execute operations on a specific project.
 * </ol>
 * <p>
 * Commands are only generated if they are permissible by the user, as defined in 
 * {@link BTOProjectPolicy}.
 * 
 * @see Command
 * @see BTOProject
 * @see BTOProjectPolicy
 */
public class BTOProjectCommandFactory extends AbstractCommandFactory{
    private static final int EDIT_BTO_PROJECT_CMD = getCommandID(BTO_PROJECT_CMD, EDIT_CMD, 0);
    private static final int TOGGLE_BTO_PROJECT_VISIBILITY_CMD = getCommandID(BTO_PROJECT_CMD, EDIT_CMD, 1);
    private static final int DELETE_BTO_PROJECT_CMD = getCommandID(BTO_PROJECT_CMD, DELETE_CMD, 0);

    private static final int SHOW_APPLICATIONS_CMD = getCommandID(APPLICATION_CMD, LIST_CMD, 0);
    private static final int SHOW_APPLICATION_CMD = getCommandID(APPLICATION_CMD, LIST_CMD, 1);
    private static final int GENERATE_REPORT_CMD = getCommandID(APPLICATION_CMD, OTHER_OPERATION_CMD, 0);

    private static final int SHOW_ENQUIRIES_CMD = getCommandID(ENQUIRY_CMD, LIST_CMD, 0);
    private static final int ADD_ENQUIRY_CMD = getCommandID(ENQUIRY_CMD, ADD_CMD, 0);

    private static final int SHOW_OFFICER_REGISTRATIONS_CMD = getCommandID(OFFICER_REGISTRATION_CMD, LIST_CMD, 0);
    private static final int SHOW_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, LIST_CMD, 1);
    private static final int ADD_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, ADD_CMD, 0);

    /**
     * Generates a set of {@link Command} to display details for a list of {@link BTOProject}.
     * <p>
     * Each project is mapped to a numbered command that triggers a view action.
     * A "Back" command is also included at the end of the list.
     *
     * @param btoProjects the list of projects to be displayed
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     * @see BTOProject
     */
    public static Map<Integer, Command> getShowBTOProjectsCommands(List<BTOProject> btoProjects) {
        final Map<Integer, Command> commands = new LinkedHashMap<>();
        final User user = sessionManager.getUser();

        final BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        final BTOProjectPolicy btoProjectPolicy = diManager.resolve(BTOProjectPolicy.class);

        final Command setBTOProjectFilterCommand = new LambdaCommand("Set Filter", 
            () -> btoProjectController.setBTOProjectFilter()
        );

        final Command resetBTOProjectFilterCommand = new LambdaCommand("Reset Filter", 
            () -> btoProjectController.resetBTOProjectFilter()
        );

        commands.put(SET_FILTER_CMD, setBTOProjectFilterCommand);
        commands.put(RESET_FILTER_CMD, resetBTOProjectFilterCommand);
        
        int index = 1;
        for(BTOProject btoProject:btoProjects){
            if(!btoProjectPolicy.canViewBTOProject(user, btoProject).isAllowed()) continue;
            
            commands.put(index++, getShowBTOProjectCommand(btoProjectController, btoProject, user));
        }

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));

        return commands;
    }

    /**
     * Creates a {@link Command} to show details of a single {@link BTOProject}.
     * <p>
     * The command's description includes the project name, neighbourhood, 
     * active status, and whether the project is handled by user.
     *
     * @param btoProjectController the controller to execute the view action
     * @param btoProject the project to be shown
     * 
     * @return a command that displays the project's details
     * 
     * @see Command
     * @see BTOProject
     * @see BTOProjectController
     */
    private static Command getShowBTOProjectCommand(BTOProjectController btoProjectController, BTOProject btoProject, User user) {
        String description = "%s (%s)".formatted(
            btoProject.getName(),
            btoProject.getNeighborhood()
        ); 

        if(btoProject.isActive()){
            description += " [Active]";
        }
        else{
            description += " [Inactive]";
        }

        if(btoProject.isHandlingBy(user)){
            description += " [Handled by you]";
        }

        return new LambdaCommand(description, () -> {
            btoProjectController.showBTOProject(btoProject);
        });
    }

    /**
     * Generates a set of {@link Command} related to actions that can be performed on a single {@link BTOProject}.
     * <p>
     * Includes updating, application, enquiry, registration and withdrawal-related commands 
     * depending on the current user’s permissions.
     * A "Back" command is included at the end.
     *
     * @param btoProject the project to perform operations on
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     * @see BTOProject
     */
    public static Map<Integer, Command> getBTOProjectsOperationCommands(BTOProject btoProject) {
        final Map<Integer, Command> commands = new LinkedHashMap<>();

        final User user = sessionManager.getUser();

        addBTOProjectUpdateRelatedCommands(user, btoProject, commands);
        addApplicationRelatedCommands(user, btoProject, commands);
        addEnquiryRelatedCommands(user, btoProject, commands);
        addOfficerRegistrationRelatedCommands(user, btoProject, commands);
        
        commands.put(BACK_CMD, new MenuBackCommand(menuManager));

        return commands;
    }

    /**
     * Adds {@link Command} related to updating the {@link BTOProject} such as editing, 
     * toggling visibility, and deleting.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link BTOProjectPolicy}.
     *
     * @param user the current user
     * @param btoProject the project being modified
     * @param commands the command map to add to
     * 
     * @see Command
     * @see BTOProject
     * @see BTOProjectPolicy
     */
    private static void addBTOProjectUpdateRelatedCommands(User user, BTOProject btoProject, Map<Integer, Command> commands) {
        final BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        final BTOProjectPolicy btoProjectPolicy = diManager.resolve(BTOProjectPolicy.class);

        final Command editBTOProjectCommand = new LambdaCommand("Edit BTO Project", () -> {
            btoProjectController.editBTOProject(btoProject);
        });

        final Command toggleBTOProjectVisibilityCommand = new LambdaCommand("Toggle BTO Project Visibility", () -> {
            btoProjectController.toggleBTOProjectVisibilty(btoProject);
        });

        final Command deleteBTOProjectCommand = new LambdaCommand("Delete BTO Project", () -> {
            btoProjectController.deleteBTOProject(btoProject);
        });

        if(btoProjectPolicy.canEditBTOProject(user, btoProject).isAllowed()){
            commands.put(EDIT_BTO_PROJECT_CMD, editBTOProjectCommand);
        }

        if(btoProjectPolicy.canToggleBTOProjectVisibility(user, btoProject).isAllowed()){
            commands.put(TOGGLE_BTO_PROJECT_VISIBILITY_CMD, toggleBTOProjectVisibilityCommand);
        }

        if(btoProjectPolicy.canDeleteBTOProject(user, btoProject).isAllowed()){
            commands.put(DELETE_BTO_PROJECT_CMD, deleteBTOProjectCommand);
        }
    }

    /**
     * Adds {@link Command} related to {@link Application} related to the {@link BTOProject}
     * such as displaying, creating new application, and generating a report.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link ApplicationPolicy}.
     *
     * @param user the current user
     * @param btoProject the project related to
     * @param commands the command map to add to
     * 
     * @see BTOProject
     * @see Command
     * @see Application
     * @see ApplicationPolicy
     */
    private static void addApplicationRelatedCommands(User user, BTOProject btoProject, Map<Integer, Command> commands) {
        final ApplicationController applicationController = diManager.resolve(ApplicationController.class);
        final ApplicationPolicy applicationPolicy = diManager.resolve(ApplicationPolicy.class);

        final Command showApplicationsByBTOProjectCommand = new LambdaCommand("Applications of the Project", () -> {
            applicationController.showApplicationsByBTOProject(btoProject);
        });

        final Command showApplicationByUserAndBTOProjectCommand = new LambdaCommand("Your Application", () -> {
            applicationController.showApplicationByUserAndBTOProject(btoProject);
        });

        final Command generateReportCommand = new LambdaCommand("Generate Report", () -> {
            applicationController.generateReport(btoProject);
        });

        if(applicationPolicy.canViewApplicationsByBTOProject(user, btoProject).isAllowed()){
            commands.put(SHOW_APPLICATIONS_CMD, showApplicationsByBTOProjectCommand);
        }

        if(applicationPolicy.canViewApplicationByUserAndBTOProject(user, btoProject).isAllowed()){
            commands.put(SHOW_APPLICATION_CMD, showApplicationByUserAndBTOProjectCommand);
        }

        if(applicationPolicy.canGenerateReport(user, btoProject).isAllowed()){
            commands.put(GENERATE_REPORT_CMD, generateReportCommand);
        }

        int subID = 0;
        for(FlatType flatType:FlatType.values()){
            int key = getCommandID(APPLICATION_CMD, ADD_CMD, subID);

            if(applicationPolicy.canCreateApplication(user, btoProject, flatType).isAllowed()){
                commands.put(key, getAddApplicationCommand(applicationController, btoProject, flatType));
            }

            subID++;
        }
    }

    /**
     * Helper function to generate {@link Command} to create {@link Application} for a specific {@link FlatType}.
     * 
     * @param applicationController controller used to create application
     * @param btoProject project to apply
     * @param flatType flat type to apply
     * @return command to create new application for the project and the flat type
     * 
     * @see Command
     * @see Application
     * @see FlatType
     */
    private static Command getAddApplicationCommand(ApplicationController applicationController, BTOProject btoProject, FlatType flatType) {
        final String description = "Apply for %s".formatted(flatType.getStoredString());
        return new LambdaCommand(description, () -> {
            applicationController.addApplication(btoProject, flatType);
        });
    }

    /**
     * Adds {@link Command} related to {@link Enquiry} related to the {@link BTOProject}
     * such as displaying and creating new enquiry.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link EnquiryPolicy}.
     *
     * @param user the current user
     * @param btoProject the project related to
     * @param commands the command map to add to
     * 
     * @see BTOProject
     * @see Command
     * @see Enquiry
     * @see EnquiryPolicy
     */
    private static void addEnquiryRelatedCommands(User user, BTOProject btoProject, Map<Integer, Command> commands) {
        final EnquiryController enquiryController = diManager.resolve(EnquiryController.class);
        final EnquiryPolicy enquiryPolicy = diManager.resolve(EnquiryPolicy.class);

        final Command showEnquiriesByBTOProjectCommand = new LambdaCommand("Enquiries of the Project", () -> {
            enquiryController.showEnquiriesByBTOProject(btoProject);
        });

        final Command addEnquiryCommand = new LambdaCommand("Add Enquiry", () -> {
            enquiryController.addEnquiry(btoProject);
        });

        if(enquiryPolicy.canViewEnquiriesByBTOProject(user, btoProject).isAllowed()){
            commands.put(SHOW_ENQUIRIES_CMD, showEnquiriesByBTOProjectCommand);
        }

        if(enquiryPolicy.canCreateEnquiry(user, btoProject).isAllowed()){
            commands.put(ADD_ENQUIRY_CMD, addEnquiryCommand);
        }
    }

    /**
     * Adds {@link Command} related to {@link OfficerRegistration} related to the {@link BTOProject}
     * such as displaying and creating new registration.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link OfficerRegistrationPolicy}.
     *
     * @param user the current user
     * @param btoProject the project related to
     * @param commands the command map to add to
     * 
     * @see BTOProject
     * @see Command
     * @see OfficerRegistration
     * @see OfficerRegistrationPolicy
     */
    private static void addOfficerRegistrationRelatedCommands(User user, BTOProject btoProject, Map<Integer, Command> commands) {
        final OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);
        final OfficerRegistrationPolicy officerRegistrationPolicy = diManager.resolve(OfficerRegistrationPolicy.class);

        final Command showOfficerRegistrationsByBTOProjectCommand = new LambdaCommand("List of Officer Registrations", () -> {
            officerRegistrationController.showOfficerRegistrationsByBTOProject(btoProject);
        });

        final Command addOfficerRegistrationCommand = new LambdaCommand("Add Officer Registration", () -> {
            officerRegistrationController.addOfficerRegistration(btoProject);
        });

        final Command showOfficerRegistrationCommand = new LambdaCommand("Your Officer Registration", () -> {
            officerRegistrationController.showOfficerRegistrationByOfficerAndBTOProject(btoProject);
        });

        if(officerRegistrationPolicy.canViewOfficerRegistrationsByBTOProject(user, btoProject).isAllowed()){
            commands.put(SHOW_OFFICER_REGISTRATIONS_CMD, showOfficerRegistrationsByBTOProjectCommand);
        }

        if(officerRegistrationPolicy.canCreateOfficerRegistration(user, btoProject).isAllowed()){
            commands.put(ADD_OFFICER_REGISTRATION_CMD, addOfficerRegistrationCommand);
        }

        if(officerRegistrationPolicy.canViewOfficerRegistrationByUserAndBTOProject(user, btoProject).isAllowed()){
            commands.put(SHOW_OFFICER_REGISTRATION_CMD, showOfficerRegistrationCommand);
        }
    }
}