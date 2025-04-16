package manager;

import java.util.Map;
import java.util.Stack;

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
    private CommandController commandController;

    private Stack<Map<Integer, Command>> commandsStack = new Stack<>();
    private Stack<String> commandsTitleStack = new Stack<>();

    /**
     * Constructs a DefaultMenuManager with the specified {@link CommandController}.
     *
     * @param commandController the controller responsible for handling command execution
     */
    public DefaultMenuManager(CommandController commandController) {
        this.commandController = commandController;
    }

     /**
     * Starts the main dashboard loop by initializing the first set of commands from the
     * {@link DashboardCommandFactory}, and continues running as long as the command stack is not empty.
     * This loop allows navigation through nested menus via a stack-based approach.
     */
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

     /**
     * Adds a new command set and its associated title to the top of the stack.
     * This is used for navigating into a submenu or a new context.
     *
     * @param commandTitle the title of the new command set/menu
     * @param commands     the map of command options to be added to the stack
     */
    public void addCommands(String commandTitle, Map<Integer, Command> commands){
        commandsTitleStack.add(commandTitle);
        commandsStack.add(commands);
    }

    /**
     * Navigates back by removing the most recent command set and its title from the stack.
     * This is used when a "Back" option is selected in the menu.
     */
    public void back(){
        commandsTitleStack.pop();
        commandsStack.pop();
    }

    /**
     * Stops the dashboard loop by clearing all stacked command sets and titles.
     * This is invoked during logout or application exit.
     */
    public void stopDashboardLoop(){
        commandsTitleStack.removeAllElements();
        commandsStack.removeAllElements();
    }
}
