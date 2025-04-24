package view.terminal;

import view.interfaces.MessageView;

/**
 * Terminal-based implementation of the {@link MessageView} interface.
 * <p>
 * This class is responsible for displaying colored messages to the user via the terminal.
 * It provides visual feedback in the form of info, success, error, and failure messages,
 * using ANSI escape codes for color formatting.
 * <p>
 * Output is printed to {@code System.err} to separate it from regular output (e.g., System.out),
 * making messages more visible or easier to redirect/log in some terminal environments.
 */
public class TerminalMessageView implements MessageView{

    // ANSI escape codes for terminal text colors
    private static final String RED = "\u001B[31m";
    private static final String ORANGE = "\u001B[38;5;214m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    /**
     * Using ANSI escape codes to color terminal output is a common technique in CLI apps to improve UX.
     * There is minimal overhead as there is no need for external libraries or GUI dependencies — 
     * ANSI codes are lightweight and built into terminal standards.
     * Cross-platform (Mostly):
     * - Most modern terminals (Linux, macOS, and even Windows Terminal / VSCode's terminal) support ANSI codes.
     * Good Fit for MVC Design:
     * - Since we abstracted the message logic into a MessageView interface,
     * - we can easily swap the terminal implementation for another — e.g., a GUI or web version — without touching your core app logic.
     */

    /**
     * Displays an informational message in the terminal.
     * 
     * @param message the information to display
     */
    public void info(String message){
        System.err.println("\n" + "INFO: " + message + "\n");
    }

    /**
     * Displays a success message in green text.
     * 
     * @param message the success message to display
     */
    public void success(String message){
        System.err.println("\n" + GREEN + "SUCCESS: " + message + RESET + "\n");
    }

    /**
     * Displays an error message in orange text.
     * 
     * @param message the error message to display
     */
    public void error(String message){
        System.err.println("\n" + ORANGE + "ERROR: " + message + RESET + "\n");
    }    

    /**
     * Displays a failure message in red text.
     * 
     * @param message the failure message to display
     */
    public void failure(String message){
        System.err.println("\n" + RED + "FAILURE: " + message + RESET + "\n");
    }
}
