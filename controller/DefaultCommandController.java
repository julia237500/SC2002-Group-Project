package controller;

import java.util.Map;

import command.Command;
import controller.interfaces.CommandController;
import view.interfaces.CommandView;
import view.interfaces.MessageView;


/**
 * Controller responsible for managing and executing user commands.
 * <p>
 * This controller:
 * <ul>
 *   <li>Displays a list of available commands to the user</li>
 *   <li>Handles user input for selecting a command</li>
 *   <li>Executes the selected command</li>
 * </ul>
 * It delegates UI responsibilities to {@link CommandView} and displays
 * messages (like invalid selections) using {@link MessageView}.
 */
public class DefaultCommandController implements CommandController{
    private CommandView commandView;
    private MessageView messageView;
    private String commandsTitle;
    private Map<Integer, Command> commands;

    /**
     * Constructs a DefaultCommandController with the specified views.
     *
     * @param commandView  the view for displaying and selecting commands
     * @param messageView  the view for displaying error/info messages
     */
    public DefaultCommandController(CommandView commandView, MessageView messageView){
        this.commandView = commandView;
        this.messageView = messageView;
    }

    /**
     * Sets the title shown above the list of commands.
     *
     * @param commandsTitle the title of the command group
     */
    @Override
    public void setCommandsTitle(String commandsTitle) {
        this.commandsTitle = commandsTitle;
    }  

    /**
     * Sets the list of commands available to the user.
     *
     * @param commands a map of command indices to {@link Command} objects
     */
    @Override
    public void setCommands(Map<Integer, Command> commands) {
        this.commands = commands;
    }

    /**
     * Displays the commands to the user, prompts for selection, and executes the selected command.
     */
    public void executeCommand(){
        Command command = selectCommand();
        command.execute();
    }

    /**
     * Prompts the user to select a command and returns the selected command.
     * Repeats prompt until a valid selection is made.
     *
     * @return the selected {@link Command}
     */
    private Command selectCommand(){
        commandView.showCommandSelection(commandsTitle, commands);

        while(true){
            int selection = commandView.getCommandSelection();

            if(commands.containsKey(selection)){
                return commands.get(selection);
            }
            else{
                messageView.error("Invalid Selection. Please try again.");
            }
        }
    }
}
