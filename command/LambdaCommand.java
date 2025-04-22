package command;

/**
 * An implementation of {@link Command}.
 * 
 * {@code LambdaCommand} encapsulates an action to be executed upon user selection.
 * It defines its execution logic using a {@code Runnable} and provides a human-readable description.
 * <p>
 * This class is intended for simple or one-time-use commands, typically created using lambda expressions.
 * For more complex or reusable commands, consider creating a dedicated implementation of {@code Command} instead.
 * 
 * @see Command
 */
public class LambdaCommand implements Command {
    private final Runnable action;
    private final String description;

    /**
     * Constructs a {@code LambdaCommand} with a description and an action to execute.
     * 
     * @param description a human-readable description of the executed action,
     *                    used primarily for display purposes.
     * @param action the {@link Runnable} that encapsulates the logic to execute when this command is executed.
     *               This is commonly defined using lambda expression
     * @see Runnable
     */
    public LambdaCommand(String description, Runnable action) {
        this.description = description;
        this.action = action;
    }

    @Override
    public void execute() {
        action.run();
    }

    @Override
    public String getDescription() {
        return description;
    }
}
