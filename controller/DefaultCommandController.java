package controller;

import java.util.Map;

import command.Command;
import controller.interfaces.CommandController;
import view.interfaces.CommandView;
import view.interfaces.MessageView;

public class DefaultCommandController implements CommandController{
    private CommandView commandView;
    private MessageView messageView;
    private String commandsTitle;
    private Map<Integer, Command> commands;

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
