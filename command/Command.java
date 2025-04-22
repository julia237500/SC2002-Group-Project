package command;

/**
 * Represents a user action following the Command Pattern.
 * 
 * This interface encapsulates an action to be executed upon user selection.
 * Each command defines its execution logic and a human-readable description.
 * 
 *  @implNote This interface should be designed to be lightweight. Any execution
 *          logic should delegate the actual work to a controller, manager,
 *          or another relevant class, ensuring that the command remains simple
 *          and focused on its execution logic.
 */
public interface Command {
    /**
     * Executes the encapsulated user action.
     * 
     * @implNote Implementations should delegate actual logic to control classes
     * such as controllers or managers to keep commands lightweight.
     */
    void execute();

    /**
     * Returns a description of the executed action.
     * 
     * In some cases, the description may simply be a name or identifier 
     * (e.g., "Alice", "Bob") when the command represents an action 
     * like showing details for a specific user.
     * 
     * This is primarily used for displaying information to the user.
     * 
     * @return a human-readable description of the action.
     */
    String getDescription();
}