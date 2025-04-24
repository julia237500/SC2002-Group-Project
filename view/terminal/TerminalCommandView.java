package view.terminal;

import java.util.Map;
import java.util.Map.Entry;

import command.Command;
import view.interfaces.CommandView;
import view.interfaces.MessageView;


/**
 * Terminal-based implementation of the {@link CommandView} interface.
 * <p>
 * Displays a list of available commands in the terminal and handles user input
 * to select one of the commands. It also provides feedback for invalid inputs
 * using the {@link MessageView}.
 * </p>
 */
public class TerminalCommandView extends AbstractTerminalView implements CommandView{
    private MessageView messageView;
    

    /**
     * Constructs a {@code TerminalCommandView} with the provided message view
     * for displaying error messages and feedback.
     *
     * @param messageView the {@link MessageView} used to show messages to the user
     */
    public TerminalCommandView(MessageView messageView){
        this.messageView = messageView;
    }

    /**
     * Displays a list of available commands for the user to choose from.
     * Each command is shown with a corresponding numeric key.
     *
     * @param title    the title to display above the command list
     * @param commands a map of command numbers to {@link Command} objects
     */
    public void showCommandSelection(String title, Map<Integer, Command> commands){
        showTitle(title);
        
        for(Entry<Integer, Command> command:commands.entrySet()){
            System.out.println(String.format("%d. %s", command.getKey(), command.getValue().getDescription()));
        }

        drawRule();
    }

    /**
     * Prompts the user to enter a numeric selection from the command list.
     * Validates input and displays error messages if the input is not a number.
     *
     * @return the selected command number
     */
    public int getCommandSelection(){
        while(true){
            System.out.print("Enter Selection (Number): ");

            try {
                int selection = sc.nextInt();
                sc.nextLine();
                return selection;
            } catch (Exception e) {
                messageView.error("Invalid selection. Please enter number only.");
                sc.nextLine();
            }
        }
    }
}
