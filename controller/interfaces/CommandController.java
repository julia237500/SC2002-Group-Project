package controller.interfaces;

import java.util.Map;

import command.Command;

/**
 * A controller that executes {@link Command} objects in accordance with the MVC architecture.
 * It prompts the user to select an action and executes it via the corresponding command.
 *
 * @implNote This controller is allowed to handle the selection logic directly.
 * Since {@link Command} is not part of the model layer, it typically does not require access to a service.
 *
 * @see Command
 */
public interface CommandController {
    /**
     * Sets the title displayed above the list of available commands.
     *
     * @param commandsTitle the title or header for the command list.
     */
    void setCommandsTitle(String commandsTitle);

    /**
     * Assigns a map of {@link Command} to be selected by user.
     *
     * @param commands a map linking option numbers to their corresponding {@code Command} implementations.
     * 
     * @see Command
     */
    void setCommands(Map<Integer, Command> commands);

    /**
     * Displays the list of {@link Command}, prompts user to select, and executes the {@link Command} selected by the user.
     * 
     * @see Command
     */
    void executeCommand();
}