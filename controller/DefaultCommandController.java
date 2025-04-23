package controller;

import java.util.Map;

import command.Command;
import controller.interfaces.CommandController;
import view.interfaces.CommandView;
import view.interfaces.MessageView;


/**
 * Default implementation of {@link CommandController}.
 * <p>
 * This controller is responsible for coordinating user-driven logic related to 
 * selection and execution of {@link Command}. It select Command using {@link CommandView}.
 * 
 * @see CommandController
 * @see Command
 * @see CommandView
 */
public class DefaultCommandController implements CommandController{
    private CommandView commandView;
    private MessageView messageView;
    private String commandsTitle;
    private Map<Integer, Command> commands;

    /**
     * Constructs a new {@code DefaultCommandController}.
     *
     * @param commandView  the view for displaying and selecting commands
     * @param messageView  the view for displaying general messages
     * 
     * @see CommandView
     * @see MessageView
     */
    public DefaultCommandController(CommandView commandView, MessageView messageView){
        this.commandView = commandView;
        this.messageView = messageView;
    }

    @Override
    public void setCommandsTitle(String commandsTitle) {
        this.commandsTitle = commandsTitle;
    }  

    @Override
    public void setCommands(Map<Integer, Command> commands) {
        this.commands = commands;
    }

    public void executeCommand(){
        Command command = selectCommand();
        command.execute();
    }

    /**
     * Prompts the user to select a command and returns the selected command.
     * Repeats prompt until a valid selection is made.
     *
     * @return the selected {@link Command}
     * 
     * @see Command
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
