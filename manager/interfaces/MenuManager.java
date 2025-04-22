package manager.interfaces;

import java.util.Map;
import java.util.function.Supplier;

import command.Command;

/**
 * The {@code MenuManager} interface defines methods for managing
 * a stack-based menu system within the application.
 * 
 * It allows adding command menus, navigating back through previous menus,
 * and controlling the dashboard loop lifecycle.
 */
public interface MenuManager {

    /**
     * Starts the dashboard loop, which continuously handles user input
     * and executes the selected commands from the current menu.
     * 
     * This loop should keep running until explicitly stopped or when there
     * are no more menus in the stack.
     */
    public void startDashboardLoop();

    /**
     * Adds a new set of commands to the menu stack along with a title.
     * This effectively navigates the application to a new menu screen.
     *
     * @param commandTitle The title or label for the new command menu.
     * @param commands     A map of command options where keys are option numbers and values are {@code Command} instances.
     */
    void addCommands(String commandTitle,  Supplier<Map<Integer, Command>> commandGenerator);

    /**
     * Navigates back to the previous menu by popping the current command set
     * and its title from the stack. If the stack becomes empty, the dashboard loop may terminate.
     */
    void back();

     /**
     * Stops the dashboard loop and clears the command menu stack.
     * This is called during logout or application shutdown.
     */
    void stopDashboardLoop();
}
