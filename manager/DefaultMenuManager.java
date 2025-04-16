package manager;

import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import command.Command;
import controller.interfaces.CommandController;
import factory.DashboardCommandFactory;
import manager.interfaces.MenuManager;

public class DefaultMenuManager implements MenuManager{
    final private CommandController commandController;

    final private Stack<Supplier<Map<Integer, Command>>> commandGeneratorsStack = new Stack<>();
    final private Stack<String> commandsTitleStack = new Stack<>();

    public DefaultMenuManager(CommandController commandController) {
        this.commandController = commandController;
    }

    @Override
    public void startDashboardLoop() {
        addCommands("Dashboard", () -> DashboardCommandFactory.getCommands());
        
        while(!commandGeneratorsStack.isEmpty()){
            final Supplier<Map<Integer, Command>> commandGenerator = commandGeneratorsStack.peek();
            final Map<Integer, Command> commands = commandGenerator.get();

            if(commands == null || commands.isEmpty()){
                back();
                continue;
            }
            
            commandController.setCommandsTitle(commandsTitleStack.peek());
            commandController.setCommands(commands);

            commandController.executeCommand();
        }
    }

    public void addCommands(String commandTitle, Supplier<Map<Integer, Command>> commandGenerator){
        commandsTitleStack.add(commandTitle);
        commandGeneratorsStack.add(commandGenerator);
    }

    public void back(){
        commandsTitleStack.pop();
        commandGeneratorsStack.pop();
    }

    public void stopDashboardLoop(){
        commandsTitleStack.removeAllElements();
        commandGeneratorsStack.removeAllElements();
    }
}
