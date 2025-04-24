package manager.interfaces;

import java.util.Map;
import java.util.function.Supplier;

import command.Command;

/**
 * The {@code MenuManager} interface defines methods for managing
 * a stack-based menu system within the application.
 * <p>
 * Each menu is represented as a {@code Map<Integer, Command>}, allowing users to 
 * interact with available actions via numeric input. To support dynamic behavior, 
 * menus are supplied using {@code Supplier<Map<Integer, Command>>}, enabling automatic 
 * refresh or regeneration of menu items when revisited.
 * </p>
 * This interface provides functionality to add new menus, navigate backward through 
 * the menu history, and manage the overall lifecycle of the menu system.
 * 
 * @see Command
 */
public interface MenuManager {
    /**
     * Starts the dashboard loop, which continuously handles user selection
     * and executes the selected commands from the current menu.
     * 
     * This loop should keep running until explicitly stopped or when there
     * are no more menus in the stack.
     */
    public void startDashboardLoop();

    /**
     * Pushes a new command menu onto the menu stack with the given title.
     * This represents navigating to a new menu screen in the application.
     * <p>
     * The command menu is provided as a {@code Supplier} to allow lazy evaluation
     * and automatic refreshing of commands whenever the menu is displayed.
     * </p>
     *
     * @param commandTitle    The title or label for the new command menu.
     * @param commandGenerator A supplier that generates the map of command options,
     *                         where keys are option numbers and values are {@code Command} instances.
     * 
     * @see Command
     * @see Supplier
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
