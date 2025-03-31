package command.user;

import command.Command;
import command.general.LogoutCommand;
import controller.interfaces.AuthController;
import manager.interfaces.ApplicationManager;

public class ChangePasswordCommand implements Command { 
    private AuthController authController;
    private ApplicationManager applicationManager;

    public ChangePasswordCommand(AuthController authController, ApplicationManager applicationManager){
        this.authController = authController;
        this.applicationManager = applicationManager;
    }  

    @Override
    public void execute() {
        if(authController.changePassword()){
            LogoutCommand logoutCommand = new LogoutCommand(applicationManager);
            logoutCommand.execute();
        }
    }

    @Override
    public String getDescription() {
        return "Change Password";
    }
}
