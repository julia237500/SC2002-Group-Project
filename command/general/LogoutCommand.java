package command.general;

import command.Command;
import manager.interfaces.ApplicationManager;

public class LogoutCommand implements Command{
    private ApplicationManager applicationManager;

    public LogoutCommand(ApplicationManager applicationManager){
        this.applicationManager = applicationManager;
    }

    @Override
    public void execute() {
        applicationManager.logout();
    }

    @Override
    public String getDescription() {
        return "Log Out";
    }
}
