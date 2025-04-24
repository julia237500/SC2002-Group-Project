import manager.DIManager;
import manager.interfaces.ApplicationManager;
import util.DefaultDIContainer;
import util.interfaces.DIContainer;


/**
 * Entry point of the application.
 * 
 * <p>This class sets up the Dependency Injection (DI) container,
 * initializes the DIManager with the container, and starts the
 * application by resolving and running the {@code ApplicationManager}.</p>
 */
public class Main {

    /**
     * Main method that launches the application.
     *
     * <p>It performs the following:
     * <ul>
     *   <li>Creates a default dependency injection container.</li>
     *   <li>Initializes the DIManager singleton with the container.</li>
     *   <li>Resolves the {@code ApplicationManager} from the container.</li>
     *   <li>Starts the application.</li>
     * </ul>
     * If any unexpected error occurs, it logs the error and stack trace.</p>
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            DIContainer container = new DefaultDIContainer();
            DIManager.createInstance(container);            

            ApplicationManager applicationManager = DIManager.getInstance().resolve(ApplicationManager.class);
            applicationManager.startApplication();
        } catch(Exception e){
            System.err.println("Program terminated unexpectedly:");
            e.printStackTrace();
        }
    }
}