package factory;

import manager.DIManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;

/**
 * An abstract base class that provides shared functionality and dependencies
 * for all {@code CommandFactory}.
 * <p>
 * This class supplies commonly used components such as {@code DIManager}, 
 * {@code SessionManager}, and {@code MenuManager}, which are required 
 * by most command factories.
 * </p>
 * <p>
 * It also manages a consistent command ID system, allowing each command 
 * to be uniquely and predictably identified.
 * It is useful when rendering selectable menus for commands based on user input.
 * </p>
 */
public class AbstractCommandFactory {
    // Commonly used component
    protected static final DIManager diManager = DIManager.getInstance();
    protected static final SessionManager sessionManager = diManager.resolve(SessionManager.class);
    protected static final MenuManager menuManager = diManager.resolve(MenuManager.class);

    // Reserved ID
    protected static final int BACK_CMD = -1;
    protected static final int LOGOUT_CMD = 9;
    protected static final int SET_FILTER_CMD = -2;
    protected static final int RESET_FILTER_CMD = -3;

    // Category ID
    protected static final int USER_CMD = 1;
    protected static final int BTO_PROJECT_CMD = 2;
    protected static final int APPLICATION_CMD = 3;
    protected static final int ENQUIRY_CMD = 4;
    protected static final int OFFICER_REGISTRATION_CMD = 5;
    
    // Operation ID
    protected static final int LIST_CMD = 0;
    protected static final int ADD_CMD = 1;
    protected static final int EDIT_CMD = 2;
    protected static final int DELETE_CMD = 3;
    protected static final int OTHER_OPERATION_CMD = 9;

    /**
     * Generates a unique command ID based on a combination of category, operation, and sub-operation identifiers.
     * <p>
     * The ID is computed using the formula: {@code categoryID * 100 + operationID * 10 + subID}, 
     * allowing for a structured and consistent encoding of command types.
     * </p>
     * <p>
     * This method also checks for collisions with reserved command IDs (e.g., BACK_CMD, LOGOUT_CMD),
     * and throws an {@code IllegalArgumentException} if the generated ID conflicts with any of them.
     * </p>
     *
     * @param categoryID the category component of the command.
     *                   It is recommended to use the constants defined in {@code AbstractCommandFactory}.
     * @param operationID the operation component of the command.
     *                    It is recommended to use the constants defined in {@code AbstractCommandFactory}.
     * @param subID the sub-operation or variation component of the command.
     *              This should be a number from 0 to 9.
     *              While there is no strict rule, it is recommended to use {@code n - 1}
     *              if this represents the n-th variation under a specific {@code operationID}.
     * 
     * @return a unique command ID representing the combination.
     * 
     * @throws IllegalArgumentException if the generated command ID matches a reserved one.
     */
    protected static int getCommandID(int categoryID, int operationID, int subID){
        final int commandID = categoryID * 100 + operationID * 10 + subID;

        switch (commandID) {
            case BACK_CMD:
            case LOGOUT_CMD:
            case SET_FILTER_CMD:
            case RESET_FILTER_CMD:
                throw new IllegalArgumentException("Command ID contradict with reserved ID: %d".formatted(commandID));        
            default:
                break;
        }

        return commandID;
    }
}
