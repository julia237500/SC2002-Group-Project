package controller.interfaces;

import java.util.Map;

import command.Command;

/**
 * Interface for handling user-interactive commands within the application.
 * <p>
 * This controller is responsible for managing command listings, assigning commands,
 * and executing the command selected by the user.
 * </p>
 */
public interface CommandController {

    /**
     * Sets the title displayed above the list of available commands.
     *
     * @param commandsTitle the title or header for the command list.
     */
    void setCommandsTitle(String commandsTitle);

    /**
     * Assigns a list of commands to be made available for user interaction.
     *
     * @param commands a map linking option numbers to their corresponding {@link Command} implementations.
     */
    void setCommands(Map<Integer, Command> commands);

    /**
     * Displays the list of commands (with titles) and executes the command selected by the user.
     */
    void executeCommand();
}