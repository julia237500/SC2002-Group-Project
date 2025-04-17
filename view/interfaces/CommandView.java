package view.interfaces;

import java.util.Map;

import command.Command;

/**
 * Interface for displaying a list of commands to the user and retrieving their selection.
 * <p>
 * This interface supports the separation of concerns by decoupling the user interface
 * logic from the underlying command execution logic.
 */
public interface CommandView {
    /**
     * Displays a list of selectable commands to the user with a specified title.
     *
     * @param title the title or heading of the command selection menu
     * @param commands a map of integer options to their corresponding {@link Command} objects
     */
    void showCommandSelection(String title, Map<Integer, Command> commands);

    /**
     * Retrieves the user's selected command option.
     *
     * @return the integer corresponding to the user's selected command
     */
    int getCommandSelection();
}
