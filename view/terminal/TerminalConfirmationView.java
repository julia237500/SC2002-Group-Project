package view.terminal;

import view.interfaces.ConfirmationView;
import view.interfaces.MessageView;


/**
 * Terminal-based implementation of the {@link ConfirmationView} interface.
 * <p>
 * This view asks the user for a confirmation to proceed with an action.
 * The user is prompted with a choice to either confirm or decline by entering
 * a 'Y' for yes or 'N' for no. If the user enters an invalid input, they are
 * prompted again.
 * </p>
 */
public class TerminalConfirmationView extends AbstractTerminalView implements ConfirmationView{
    private static final String YES_SELECTION = "Y";
    private static final String NO_SELECTION = "N";

    private MessageView messageView;

    /**
     * Constructs a {@code TerminalConfirmationView} with the provided message view.
     * This message view is used to show error messages when the user provides invalid input.
     *
     * @param messageView the {@link MessageView} used for error messages
     */
    public TerminalConfirmationView(MessageView messageView){
        this.messageView = messageView;
    }

    /**
     * Prompts the user for a confirmation (Y/N). If the user provides an invalid input,
     * they are prompted again until they provide a valid response.
     * 
     * @return {@code true} if the user selects 'Y', {@code false} if the user selects 'N'
     * 
     * Separation of Concerns: This class focuses on managing user interaction, while the MessageView is 
     * responsible for displaying error messages (messageView.error).
     */
    public boolean getConfirmation(){
        while(true){
            System.out.print("Confirm the action? (%s/%s) ".formatted(YES_SELECTION, NO_SELECTION));
            
            String input = sc.nextLine().trim().toUpperCase();
            switch (input) {
                case YES_SELECTION:
                    return true;
                case NO_SELECTION:
                    return false;
                default:
                    messageView.error("Invalid input. Please try again.");
                    break;
            }
        }
    }
}
