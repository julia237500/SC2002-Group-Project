package factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import command.Command;
import command.LambdaCommand;
import command.general.MenuBackCommand;
import config.UserRole;
import config.WithdrawalStatus;
import controller.interfaces.ApplicationController;
import manager.DIManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.Application;
import model.User;

public class ApplicationCommandFactory {
    private static final DIManager diManager = DIManager.getInstance();

    public static Map<Integer, Command> getShowApplicationsCommands(List<Application> applications) {
        final Map<Integer, Command> commands = new LinkedHashMap<>();

        final ApplicationController applicationController = diManager.resolve(ApplicationController.class);

        int index = 1;
        for(Application application:applications){
            commands.put(index++, getShowApplicationCommand(application, applicationController));
        }

        final MenuManager menuManager = diManager.resolve(MenuManager.class);
        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }

    private static Command getShowApplicationCommand(Application application, ApplicationController applicationController) {
        final String description = "%s - %s (%s)".formatted(
            application.getBTOProject().getName(), 
            application.getApplicant().getName(), 
            application.getApplicationStatus().getStoredString()
        );

        return new LambdaCommand(description, () -> {
            applicationController.showApplication(application);
        });
    }

    public static Map<Integer, Command> getApplicationOperationCommands(Application application) {
        final ApplicationController applicationController = diManager.resolve(ApplicationController.class);
        final Map<Integer, Command> commands = new LinkedHashMap<>();

        final SessionManager sessionManager = diManager.resolve(SessionManager.class);
        final User user = sessionManager.getUser();

        getApplicationUpdateRelatedCommands(user, application, commands, applicationController);
        getApplicationWithdrawalRelatedCommands(user, application, commands, applicationController);
        
        final MenuManager menuManager = diManager.resolve(MenuManager.class);
        commands.put(-1, new MenuBackCommand(menuManager));

        return commands;
    }

    private static void getApplicationUpdateRelatedCommands(User user, Application application, Map<Integer, Command> commands, ApplicationController applicationController) {
        Command approveApplicationCommand = new LambdaCommand("Approve Application", () -> {
            applicationController.approveApplication(application, true);
        });

        Command rejectApplicationCommand = new LambdaCommand("Reject Application", () -> {
            applicationController.approveApplication(application, false);
        });

        Command bookApplicationCommand = new LambdaCommand("Book Application", () -> {
            applicationController.bookApplication(application);
        });

        if(application.isApprovable() && user == application.getBTOProject().getHDBManager()){
            commands.put(1, approveApplicationCommand);
            commands.put(2, rejectApplicationCommand);
        }

        if(application.isBookable()){
            if(user.getUserRole() == UserRole.HDB_OFFICER && application.getBTOProject().isHandlingBy(user)){
                commands.put(3, bookApplicationCommand);
            }
        }
    }

    private static void getApplicationWithdrawalRelatedCommands(User user, Application application, Map<Integer, Command> commands, ApplicationController applicationController) {
        Command withdrawApplicationCommand = new LambdaCommand("Withdraw Application", () -> {
            applicationController.withdrawApplication(application);
        });

        Command approveWithdrawApplicationCommand = new LambdaCommand("Approve Withdrawal", () -> {
            applicationController.approveWithdrawApplication(application, true);
        });

        Command rejectWithdrawApplicationCommand = new LambdaCommand("Reject Withdrawal", () -> {
            applicationController.approveWithdrawApplication(application, false);
        });

        if(application.getApplicant() == user && application.isWithdrawable()){
            commands.put(4, withdrawApplicationCommand);
        }

        if(application.getWithdrawalStatus() == WithdrawalStatus.PENDING && user == application.getBTOProject().getHDBManager()){
            commands.put(41, approveWithdrawApplicationCommand);
            commands.put(42, rejectWithdrawApplicationCommand);
        }
    }
}
