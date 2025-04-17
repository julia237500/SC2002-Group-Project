package view.interfaces;

/**
 * Interface for displaying different types of messages to the user.
 *
 */
public interface MessageView {

    /**
     * Displays an informational message to the user.
     *
     * @param message The informational message to be displayed.
     */
    void info(String message);

    /**
     * Displays a success message to the user.
     *
     * @param message The success message to be displayed.
     */
    void success(String message);

    /**
     * Displays a non-critical error message (e.g., invalid input).
     *
     * @param message The error message to be displayed.
     */
    void error(String message);

    /**
     * Displays a critical failure message (e.g., operation failed).
     *
     * @param message The failure message to be displayed.
     */
    void failure(String message);
}
