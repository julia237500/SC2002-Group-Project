package factory;

import java.util.LinkedHashMap;
import java.util.Map;

import command.Command;
import command.LambdaCommand;
import command.general.LogoutCommand;
import controller.interfaces.ApplicationController;
import controller.interfaces.AuthController;
import controller.interfaces.BTOProjectController;
import controller.interfaces.EnquiryController;
import controller.interfaces.OfficerRegistrationController;
import manager.interfaces.ApplicationManager;
import model.User;
import policy.interfaces.ApplicationPolicy;
import policy.interfaces.BTOProjectPolicy;
import policy.interfaces.EnquiryPolicy;
import policy.interfaces.OfficerRegistrationPolicy;

/**
 * A factory class for generating {@link Command} instances for the dashboard (main menu).
 * <p>
 * Commands are generated and stored in a {@code Map<Integer, Command>}.
 * This factory can generate commands for the operations related to:
 * <ol>
 *   <li> {@code User}
 *   <li> {@code BTOProject}
 *   <li> {@code Application}
 *   <li> {@code Enquiry}
 *   <li> {@code OfficerRegistration} 
 *   <li> System, such as logout
 * </ol>
 * <p>
 * Commands are only generated if they are permissible by the user
 */
public class DashboardCommandFactory extends AbstractCommandFactory {
    private static final int CHANGE_PASSWORD_CMD = getCommandID(USER_CMD, EDIT_CMD, 0);

    private static final int SHOW_ALL_BTO_PROJECTS_CMD = getCommandID(BTO_PROJECT_CMD, LIST_CMD, 0);
    private static final int SHOW_BTO_PROJECTS_HANDLED_BY_USER_CMD = getCommandID(BTO_PROJECT_CMD, LIST_CMD, 1);
    private static final int ADD_BTO_PROJECT_CMD = getCommandID(BTO_PROJECT_CMD, ADD_CMD, 0);

    private static final int SHOW_ALL_APPLICATIONS_CMD = getCommandID(APPLICATION_CMD, LIST_CMD, 0);
    private static final int SHOW_APPLICATIONS_BY_USER_CMD = getCommandID(APPLICATION_CMD, LIST_CMD, 1);

    private static final int SHOW_ALL_ENQUIRIES_CMD = getCommandID(ENQUIRY_CMD, LIST_CMD, 0);
    private static final int SHOW_ENQUIRIES_BY_USER_CMD = getCommandID(ENQUIRY_CMD, LIST_CMD, 1);

    private static final int SHOW_OFFICER_REGISTRATIONS_BY_OFFICER_CMD = getCommandID(OFFICER_REGISTRATION_CMD, LIST_CMD, 0);

    /**
     * Generates a set of {@link Command} for dashboard.
     * <p>
     * Each command is mapped to a number that triggers a view action.
     * A "Logout" command is also included at the end of the list.
     * </p>
     * 
     * Generates commands for the operations related to:
     * <ol>
     *   <li> {@code User}
     *   <li> {@code BTOProject}
     *   <li> {@code Application}
     *   <li> {@code Enquiry}
     *   <li> {@code OfficerRegistration} 
     *   <li> System, such as logout
     * </ol>
     *
     * @return a map of command IDs to corresponding commands
     * 
     * @see Command
     */
    public static Map<Integer, Command> getCommands() {
        final Map<Integer, Command> commands = new LinkedHashMap<>();
        
        final User user = sessionManager.getUser();
        
        addUserRelatedCommands(user, commands);
        addBTOProjectsRelatedCommands(user, commands);
        addApplicationRelatedCommands(user, commands);
        addEnquiryRelatedCommands(user, commands);
        addOfficerRegistrationRelatedCommands(user, commands);

        final ApplicationManager applicationManager = diManager.resolve(ApplicationManager.class);
        commands.put(LOGOUT_CMD, new LogoutCommand(applicationManager));

        return commands;
    }

    /**
     * Adds {@link Command} related to {@code User} 
     * such as change password.
     * <p>
     *
     * @param user the current user
     * @param commands the command map to add to
     * 
     * @see Command
     */
    private static void addUserRelatedCommands(User user, Map<Integer, Command> commands) {
        final AuthController authController = diManager.resolve(AuthController.class);
        final ApplicationManager applicationManager = diManager.resolve(ApplicationManager.class);

        final Command changePasswordCommand = new LambdaCommand("Change Password", () -> {
            if(authController.changePassword()){
                applicationManager.logout();
            }
        });

        commands.put(CHANGE_PASSWORD_CMD, changePasswordCommand);
    }

    /**
     * Adds {@link Command} related {@code BTOProject} 
     * such as displaying a list of project and creating new project.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link BTOProjectPolicy}.
     *
     * @param user the current user
     * @param commands the command map to add to
     * 
     * @see Command
     * @see BTOProjectPolicy
     */
    private static void addBTOProjectsRelatedCommands(User user, Map<Integer, Command> commands) {
        final BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);
        final BTOProjectPolicy btoProjectPolicy = diManager.resolve(BTOProjectPolicy.class);

        final Command showAllBTOProjectsCommand = new LambdaCommand("List of All BTO Projects", () -> {
            btoProjectController.showAllBTOProjects();
        });

        final Command showBTOProjectsByHDBManagerCommand = new LambdaCommand("Your BTO Projects", () -> {
            btoProjectController.showBTOProjectsHandledByUser();
        });

        final Command addBTOProjectCommand = new LambdaCommand("Add New BTO Project", () -> {
            btoProjectController.addBTOProject();
        });
                
        if(btoProjectPolicy.canViewAllBTOProjects(user).isAllowed()){
            commands.put(SHOW_ALL_BTO_PROJECTS_CMD, showAllBTOProjectsCommand);
        }

        if(btoProjectPolicy.canViewBTOProjectsHandledByUser(user).isAllowed()){
            commands.put(SHOW_BTO_PROJECTS_HANDLED_BY_USER_CMD, showBTOProjectsByHDBManagerCommand);
        }

        if(btoProjectPolicy.canCreateBTOProject(user).isAllowed()){
            commands.put(ADD_BTO_PROJECT_CMD, addBTOProjectCommand);
        }
    }

    /**
     * Adds {@link Command} related {@code Application} 
     * such as displaying a list of applications.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link ApplicationPolicy}.
     *
     * @param user the current user
     * @param commands the command map to add to
     * 
     * @see Command
     * @see ApplicationPolicy
     */
    private static void addApplicationRelatedCommands(User user, Map<Integer, Command> commands) {
        final ApplicationController applicationController = diManager.resolve(ApplicationController.class);
        final ApplicationPolicy applicationPolicy = diManager.resolve(ApplicationPolicy.class);

        final Command showAllApplicationsCommand = new LambdaCommand("List of All Applications", () -> {
            applicationController.showAllApplications();
        });

        final Command showApplicationsByUserCommand = new LambdaCommand("Your Applications", () -> {
            applicationController.showApplicationsByUser();
        });

        if(applicationPolicy.canViewAllApplications(user).isAllowed()){
            commands.put(SHOW_ALL_APPLICATIONS_CMD, showAllApplicationsCommand);
        }

        if(applicationPolicy.canViewApplicationsByUser(user).isAllowed()){
            commands.put(SHOW_APPLICATIONS_BY_USER_CMD, showApplicationsByUserCommand);
        }
    }

    /**
     * Adds {@link Command} related {@code Enquiry} 
     * such as displaying a list of enquiries.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link EnquiryPolicy}.
     *
     * @param user the current user
     * @param commands the command map to add to
     * 
     * @see Command
     * @see EnquiryPolicy
     */
    private static void addEnquiryRelatedCommands(User user, Map<Integer, Command> commands) {
        final EnquiryController enquiryController = diManager.resolve(EnquiryController.class);
        final EnquiryPolicy enquiryPolicy = diManager.resolve(EnquiryPolicy.class);

        final Command showAllEnquiriesCommand = new LambdaCommand("List of All Enquiries", () -> {
            enquiryController.showAllEnquiries();
        });

        final Command showEnquiriesByUserCommand = new LambdaCommand("Your Enquiries", () -> {
            enquiryController.showEnquiriesByUser();
        });

        if(enquiryPolicy.canViewAllEnquiries(user).isAllowed()){
            commands.put(SHOW_ALL_ENQUIRIES_CMD, showAllEnquiriesCommand);
        }

        if(enquiryPolicy.canViewEnquiriesByUser(user).isAllowed()){
            commands.put(SHOW_ENQUIRIES_BY_USER_CMD, showEnquiriesByUserCommand);
        }
    }

    /**
     * Adds {@link Command} related {@code OfficerRegistration} 
     * such as displaying a list of registrations.
     * <p>
     * Each command is added conditionally based on the current user’s permissions
     * as determined by the {@link OfficerRegistrationPolicy}.
     *
     * @param user the current user
     * @param commands the command map to add to
     * 
     * @see Command
     * @see OfficerRegistrationPolicy
     */
    private static void addOfficerRegistrationRelatedCommands(User user, Map<Integer, Command> commands) {
        final OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);
        final OfficerRegistrationPolicy officerRegistrationPolicy = diManager.resolve(OfficerRegistrationPolicy.class);

        final Command showOfficerRegistrationsByOfficerCommand = new LambdaCommand("Your Registrations as Officer", () -> {
            officerRegistrationController.showOfficerRegistrationsByOfficer();
        });

        if(officerRegistrationPolicy.canViewOfficerRegistrationsByOfficer(user).isAllowed()){
            commands.put(SHOW_OFFICER_REGISTRATIONS_BY_OFFICER_CMD, showOfficerRegistrationsByOfficerCommand);
        }
    }
}
