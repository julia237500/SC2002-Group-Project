package view.interfaces;

/**
 * Represents a view interface responsible for getting a user confirmation.
 * 
 * <p>This interface helps to decouple the confirmation logic from the specific
 * user interface implementation, supporting clean architecture principles such
 * as the Model-View-Controller (MVC) pattern.</p>
 */
public interface ConfirmationView {

    /**
     * Prompts the user for a confirmation (e.g., yes/no).
     *
     * @return {@code true} if the user confirms the action, {@code false} otherwise.
     */
    boolean getConfirmation();
}
