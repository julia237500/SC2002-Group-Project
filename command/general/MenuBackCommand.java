package command.general;

import command.Command;
import manager.interfaces.MenuManager;

public class MenuBackCommand implements Command {
    private MenuManager menuManager;

    public MenuBackCommand(MenuManager menuManager){
        this.menuManager = menuManager;
    }

    @Override
    public void execute() {
       menuManager.back();
    }

    @Override
    public String getDescription() {
        return "Back";
    }
    
}
