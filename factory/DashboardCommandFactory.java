package factory;

import java.util.LinkedHashMap;
import java.util.Map;

import command.Command;
import command.LambdaCommand;
import command.general.LogoutCommand;
import config.UserRole;
import controller.interfaces.ApplicationController;
import controller.interfaces.AuthController;
import controller.interfaces.BTOProjectController;
import controller.interfaces.EnquiryController;
import controller.interfaces.OfficerRegistrationController;
import manager.interfaces.ApplicationManager;
import model.User;
import policy.interfaces.ApplicationPolicy;
import policy.interfaces.EnquiryPolicy;
import policy.interfaces.OfficerRegistrationPolicy;

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

    private static void addBTOProjectsRelatedCommands(User user, Map<Integer, Command> commands) {
        final BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);

        final Command showAllBTOProjectsCommand = new LambdaCommand("List of All BTO Projects", () -> {
            btoProjectController.showAllBTOProjects();
        });

        final Command showBTOProjectsByHDBManagerCommand = new LambdaCommand("Your BTO Projects", () -> {
            btoProjectController.showBTOProjectsHandledByUser();
        });

        final Command addBTOProjectCommand = new LambdaCommand("Add New BTO Project", () -> {
            btoProjectController.addBTOProject();
        });
                
        commands.put(SHOW_ALL_BTO_PROJECTS_CMD, showAllBTOProjectsCommand);

        if(user.getUserRole() == UserRole.HDB_MANAGER || user.getUserRole() == UserRole.HDB_OFFICER){
            commands.put(SHOW_BTO_PROJECTS_HANDLED_BY_USER_CMD, showBTOProjectsByHDBManagerCommand);
        }

        if(user.getUserRole() == UserRole.HDB_MANAGER){
            commands.put(ADD_BTO_PROJECT_CMD, addBTOProjectCommand);
        }
    }

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
