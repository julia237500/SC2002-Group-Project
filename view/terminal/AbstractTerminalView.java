package view.terminal;

import java.util.Scanner;

public abstract class AbstractTerminalView {
    protected static final Scanner sc = new Scanner(System.in);

    protected void showTitle(String title){
        System.out.println("\n--------- " + title + " ---------");
    }

    protected static void drawRule(){
        System.out.println("-------------------------------------------");
    }
}
