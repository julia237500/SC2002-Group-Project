package command.general;

import command.Command;
import manager.interfaces.ApplicationManager;

/**
 * An implementation of {@code Command} for user to log out.
 * 
 * @see Command
 */
public class LogoutCommand implements Command{
    private ApplicationManager applicationManager;

    /**
     * Constructs a {@code LogoutCommand} with an {@code ApplicationManager}.
     * @param applicationManager the {@code ApplicationManager} to logout of.
     * 
     * @see ApplicationManager
     */
    public LogoutCommand(ApplicationManager applicationManager){
        this.applicationManager = applicationManager;
    }

    /**
     * Logout the current user using {@code ApplicationManager}.
     * 
     * @see ApplicationManager
     */
    @Override
    public void execute() {
        applicationManager.logout();
    }

    @Override
    public String getDescription() {
        return "Log Out";
    }
}
