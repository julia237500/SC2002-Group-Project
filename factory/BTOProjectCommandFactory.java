package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import command.LambdaCommand;
import command.general.MenuBackCommand;
import config.FlatType;
import config.UserRole;
import controller.interfaces.ApplicationController;
import controller.interfaces.BTOProjectController;
import controller.interfaces.EnquiryController;
import controller.interfaces.OfficerRegistrationController;
import manager.DIManager;
import model.Application;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;

public class BTOProjectCommandFactory extends AbstractCommandFactory{
    private static final DIManager diManager = DIManager.getInstance();

    private static final int EDIT_BTO_PROJECT_CMD = getCommandID(BTO_PROJECT_CMD, EDIT_CMD, 0);
    private static final int TOGGLE_BTO_PROJECT_VISIBILITY_CMD = getCommandID(BTO_PROJECT_CMD, EDIT_CMD, 1);
    private static final int DELETE_BTO_PROJECT_CMD = getCommandID(BTO_PROJECT_CMD, DELETE_CMD, 0);

    private static final int SHOW_APPLICATIONS_CMD = getCommandID(APPLICATION_CMD, LIST_CMD, 0);
    private static final int SHOW_APPLICATION_CMD = getCommandID(APPLICATION_CMD, LIST_CMD, 1);

    private static final int SHOW_ENQUIRIES_CMD = getCommandID(ENQUIRY_CMD, LIST_CMD, 0);
    private static final int ADD_ENQUIRY_CMD = getCommandID(ENQUIRY_CMD, ADD_CMD, 0);

    private static final int SHOW_OFFICER_REGISTRATIONS_CMD = getCommandID(OFFICER_REGISTRATION_CMD, LIST_CMD, 0);
    private static final int SHOW_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, LIST_CMD, 1);
    private static final int ADD_OFFICER_REGISTRATION_CMD = getCommandID(OFFICER_REGISTRATION_CMD, ADD_CMD, 0);

    public static Map<Integer, Command> getShowBTOProjectsCommands(List<BTOProject> btoProjects) {
        final Map<Integer, Command> commands = new LinkedHashMap<>();

        final BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);

        int index = 1;
        for(BTOProject btoProject:btoProjects){
            commands.put(index++, getShowBTOProjectCommand(btoProjectController, btoProject));
        }

        commands.put(BACK_CMD, new MenuBackCommand(menuManager));

        return commands;
    }

    private static Command getShowBTOProjectCommand(BTOProjectController btoProjectController, BTOProject btoProject) {
        final String description = "%s (%s)".formatted(
            btoProject.getName(),
            btoProject.getNeighborhood()
        ); 

        return new LambdaCommand(description, () -> {
            btoProjectController.showBTOProject(btoProject);
        });
    }

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

    private static void addBTOProjectUpdateRelatedCommands(User user, BTOProject btoProject, Map<Integer, Command> commands) {
        final BTOProjectController btoProjectController = diManager.resolve(BTOProjectController.class);

        final Command editBTOProjectCommand = new LambdaCommand("Edit BTO Project", () -> {
            btoProjectController.editBTOProject(btoProject);
        });

        final Command toggleBTOProjectVisibilityCommand = new LambdaCommand("Toggle BTO Project Visibility", () -> {
            btoProjectController.toggleBTOProjectVisibilty(btoProject);
        });

        final Command deleteBTOProjectCommand = new LambdaCommand("Delete BTO Project", () -> {
            btoProjectController.deleteBTOProject(btoProject);
        });

        if(user.getUserRole() == UserRole.HDB_MANAGER && btoProject.isHandlingBy(user)){
            commands.put(EDIT_BTO_PROJECT_CMD, editBTOProjectCommand);
            commands.put(TOGGLE_BTO_PROJECT_VISIBILITY_CMD, toggleBTOProjectVisibilityCommand);
            commands.put(DELETE_BTO_PROJECT_CMD, deleteBTOProjectCommand);
        }
    }

    private static void addApplicationRelatedCommands(User user, BTOProject btoProject, Map<Integer, Command> commands) {
        final ApplicationController applicationController = diManager.resolve(ApplicationController.class);

        final Command showApplicationsByBTOProjectCommand = new LambdaCommand("Applications of the Project", () -> {
            applicationController.showApplicationsByBTOProject(btoProject);
        });

        final Command showApplicationByUserAndBTOProjectCommand = new LambdaCommand("Your Application", () -> {
            applicationController.showApplicationByUserAndBTOProject(btoProject);
        });

        if(btoProject.isHandlingBy(user)){
            commands.put(SHOW_APPLICATIONS_CMD, showApplicationsByBTOProjectCommand);
        }

        if(user.getUserRole() == UserRole.APPLICANT || 
            (user.getUserRole() == UserRole.HDB_OFFICER && !btoProject.isHandlingBy(user))){
            
            final Application application = applicationController.getApplicationByUserAndBTOProject(btoProject);
            if(application != null){
                commands.put(SHOW_APPLICATION_CMD, showApplicationByUserAndBTOProjectCommand);
            }
            else if(btoProject.isActive()){
                int subID = 0;
                for(FlatType flatType:FlatType.values()){
                    int key = getCommandID(APPLICATION_CMD, ADD_CMD, subID);

                    if(btoProject.hasAvailableFlats(flatType) && flatType.isEligible(user)){
                        commands.put(key, getAddApplicationCommand(applicationController, btoProject, flatType));
                    }

                    subID++;
                }
            }
        }
    }

    private static Command getAddApplicationCommand(ApplicationController applicationController, BTOProject btoProject, FlatType flatType) {
        final String description = "Apply for %s".formatted(flatType.getStoredString());
        return new LambdaCommand(description, () -> {
            applicationController.addApplication(btoProject, flatType);
        });
    }

    private static void addEnquiryRelatedCommands(User user, BTOProject btoProject, Map<Integer, Command> commands) {
        final EnquiryController enquiryController = diManager.resolve(EnquiryController.class);

        final Command showEnquiriesByBTOProjectCommand = new LambdaCommand("Enquiries of the Project", () -> {
            enquiryController.showEnquiriesByBTOProject(btoProject);
        });

        final Command addEnquiryCommand = new LambdaCommand("Add Enquiry", () -> {
            enquiryController.addEnquiry(btoProject);
        });

        if(user.getUserRole() == UserRole.HDB_MANAGER || btoProject.isHandlingBy(user)){
            commands.put(SHOW_ENQUIRIES_CMD, showEnquiriesByBTOProjectCommand);
        }

        if(user.getUserRole() == UserRole.APPLICANT || (user.getUserRole() == UserRole.HDB_OFFICER && !btoProject.isHandlingBy(user))){
            commands.put(ADD_ENQUIRY_CMD, addEnquiryCommand);
        }
    }

    private static void addOfficerRegistrationRelatedCommands(User user, BTOProject btoProject, Map<Integer, Command> commands) {
        final OfficerRegistrationController officerRegistrationController = diManager.resolve(OfficerRegistrationController.class);

        final Command showOfficerRegistrationsByBTOProjectCommand = new LambdaCommand("List of Officer Registrations", () -> {
            officerRegistrationController.showOfficerRegistrationsByBTOProject(btoProject);
        });

        final Command addOfficerRegistrationCommand = new LambdaCommand("Add Officer Registration", () -> {
            officerRegistrationController.addOfficerRegistration(btoProject);
        });

        final OfficerRegistration officerRegistration = officerRegistrationController.getOfficerRegistrationByOfficerAndBTOProject(btoProject);
        final Command showOfficerRegistrationCommand = new LambdaCommand("Your Officer Registration", () -> {
            officerRegistrationController.showOfficerRegistration(officerRegistration);
        });

        if(user.getUserRole() == UserRole.HDB_MANAGER && btoProject.isHandlingBy(user)){
            commands.put(SHOW_OFFICER_REGISTRATIONS_CMD, showOfficerRegistrationsByBTOProjectCommand);
        }

        if(user.getUserRole() == UserRole.HDB_OFFICER){
            if(officerRegistration == null){
                commands.put(ADD_OFFICER_REGISTRATION_CMD, addOfficerRegistrationCommand);
            }
            else{
                commands.put(SHOW_OFFICER_REGISTRATION_CMD, showOfficerRegistrationCommand);
            }
        }
    }
}