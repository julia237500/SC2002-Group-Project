package command.general;

import command.Command;
import manager.interfaces.MenuManager;

/**
 * An implementation of {@code Command} for user to go back to previous menu.
 * 
 * @see Command
 */
public class MenuBackCommand implements Command {
    private MenuManager menuManager;

    /**
     * Constructs a {@code MenuBackCommand} with a {@code MenuManager}.
     * @param menuManager the {@code MenuManager} that control the menu.
     * 
     * @see MenuManager
     */
    public MenuBackCommand(MenuManager menuManager){
        this.menuManager = menuManager;
    }

    /**
     * Go back to previous menu using {@code MenuManager}.
     * 
     * @see MenuManager
     */
    @Override
    public void execute() {
       menuManager.back();
    }

    @Override
    public String getDescription() {
        return "Back";
    }
    
}
