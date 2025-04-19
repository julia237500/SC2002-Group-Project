package factory;

import manager.DIManager;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;

public class AbstractCommandFactory {
    protected static final DIManager diManager = DIManager.getInstance();
    protected static final SessionManager sessionManager = diManager.resolve(SessionManager.class);
    protected static final MenuManager menuManager = diManager.resolve(MenuManager.class);

    protected static final int BACK_CMD = -1;
    protected static final int LOGOUT_CMD = 9;
    protected static final int SET_FILTER_CMD = -2;
    protected static final int RESET_FILTER_CMD = -3;

    protected static final int USER_CMD = 1;
    protected static final int BTO_PROJECT_CMD = 2;
    protected static final int APPLICATION_CMD = 3;
    protected static final int ENQUIRY_CMD = 4;
    protected static final int OFFICER_REGISTRATION_CMD = 5;
    
    protected static final int LIST_CMD = 0;
    protected static final int ADD_CMD = 1;
    protected static final int EDIT_CMD = 2;
    protected static final int DELETE_CMD = 3;
    protected static final int OTHER_OPERATION_CMD = 9;

    protected static int getCommandID(int categoryID, int operationID, int subID){
        final int commandID = categoryID * 100 + operationID * 10 + subID;

        if(commandID == BACK_CMD || commandID == LOGOUT_CMD){
            throw new IllegalArgumentException("Command ID contradict with BACK or LOGOUT");
        }

        return commandID;
    }
}
