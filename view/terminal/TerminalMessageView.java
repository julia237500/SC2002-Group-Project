package view.terminal;

import view.interfaces.MessageView;

public class TerminalMessageView implements MessageView{
    private static final String RED = "\u001B[31m";
    private static final String ORANGE = "\u001B[38;5;214m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    public void info(String message){
        System.err.println("\n" + "INFO: " + message + "\n");
    }

    public void success(String message){
        System.err.println("\n" + GREEN + "SUCCESS: " + message + RESET + "\n");
    }

    public void error(String message){
        System.err.println("\n" + ORANGE + "ERROR: " + message + RESET + "\n");
    }    

    public void failure(String message){
        System.err.println("\n" + RED + "FAILURE: " + message + RESET + "\n");
    }
}
