package controller.interfaces;

import java.util.Map;

import command.Command;

public interface CommandController {
    void setCommandsTitle(String commandsTitle);
    void setCommands(Map<Integer, Command> commands);
    void executeCommand();
}