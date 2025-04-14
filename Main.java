import manager.DIManager;
import manager.interfaces.ApplicationManager;
import util.DefaultDIContainer;
import util.interfaces.DIContainer;

public class Main {
    public static void main(String[] args) {
        try {
            DIContainer container = new DefaultDIContainer();
            DIManager.createInstance(container);            

            ApplicationManager applicationManager = DIManager.getInstance().resolve(ApplicationManager.class);
            applicationManager.startApplication();
        } catch(Exception e){
            System.err.println("Program terminated unexpectedly: " + e.getMessage());
            e.printStackTrace();
        }
    }
}