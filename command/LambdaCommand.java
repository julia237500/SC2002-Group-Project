package command;

public class LambdaCommand implements Command {
    private final Runnable action;
    private final String description;

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
