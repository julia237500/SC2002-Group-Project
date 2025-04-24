package manager;

import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import command.Command;
import controller.interfaces.CommandController;
import factory.DashboardCommandFactory;
import manager.interfaces.MenuManager;

/**
 * Default implementation of {@link MenuManager} that manages the command menus
 * using a stack-based navigation system. It supports starting the main dashboard loop,
 * adding nested command menus, navigating back, and stopping the loop.
 * We are using a stack based approach here because stacks offer a Natural Back Navigation:
 * The stack is a Last In, First Out (LIFO) data structure, 
 * meaning that the most recently added item is the first one to be removed. 
 * This is perfect for scenarios like menu navigation, where when we enter a submenu, it's added to the stack.
 * When we press "Back" or want to return to the previous menu, the top item is popped off, and we're brought back to the last menu.
 */
public class DefaultMenuManager implements MenuManager{
    final private CommandController commandController;

    final private Stack<Supplier<Map<Integer, Command>>> commandGeneratorsStack = new Stack<>();
    final private Stack<String> commandsTitleStack = new Stack<>();

    /**
     * Constructs a DefaultMenuManager with the specified {@link CommandController}.
     *
     * @param commandController the controller responsible for handling command execution
     * 
     * @see CommandController
     */
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
