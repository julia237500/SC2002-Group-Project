package manager;

import java.util.Map;
import java.util.Stack;

import command.Command;
import controller.interfaces.CommandController;
import factory.DashboardCommandFactory;
import manager.interfaces.MenuManager;

public class DefaultMenuManager implements MenuManager{
    private CommandController commandController;

    private Stack<Map<Integer, Command>> commandsStack = new Stack<>();
    private Stack<String> commandsTitleStack = new Stack<>();

    public DefaultMenuManager(CommandController commandController) {
        this.commandController = commandController;
    }

    @Override
    public void startDashboardLoop() {
        commandsStack.add(DashboardCommandFactory.getCommands());
        commandsTitleStack.add("Dashboard");
        
        while(!commandsStack.isEmpty()){
            commandController.setCommands(commandsStack.peek());
            commandController.setCommandsTitle(commandsTitleStack.peek());

            commandController.executeCommand();
        }
    }

    public void addCommands(String commandTitle, Map<Integer, Command> commands){
        commandsTitleStack.add(commandTitle);
        commandsStack.add(commands);
    }

    public void back(){
        commandsTitleStack.pop();
        commandsStack.pop();
    }

    public void stopDashboardLoop(){
        commandsTitleStack.removeAllElements();
        commandsStack.removeAllElements();
    }
}
