package view.terminal;

import java.util.Map;
import java.util.Map.Entry;

import command.Command;
import view.interfaces.CommandView;
import view.interfaces.MessageView;

public class TerminalCommandView extends AbstractTerminalView implements CommandView{
    private MessageView messageView;
    
    public TerminalCommandView(MessageView messageView){
        this.messageView = messageView;
    }

    public void showCommandSelection(String title, Map<Integer, Command> commands){
        showTitle(title);
        
        for(Entry<Integer, Command> command:commands.entrySet()){
            System.out.println(String.format("%d. %s", command.getKey(), command.getValue().getDescription()));
        }

        drawRule();
    }

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
