package view.terminal;

import view.interfaces.ConfirmationView;
import view.interfaces.MessageView;

public class TerminalConfirmationView extends AbstractTerminalView implements ConfirmationView{
    private static final String YES_SELECTION = "Y";
    private static final String NO_SELECTION = "N";

    private MessageView messageView;

    public TerminalConfirmationView(MessageView messageView){
        this.messageView = messageView;
    }

    public boolean confirm(){
        return confirm("Confirm the action? This will be irreversible.");
    }

    public boolean confirm(String message){
        while(true){
            System.out.print("%s (%s/%s) ".formatted(message, YES_SELECTION, NO_SELECTION));
            
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
