package controller;

import config.ResponseStatus;
import service.ServiceResponse;
import view.interfaces.MessageView;

/**
 * Provides common functionality for displaying service responses.
 * This class handles the standard workflow of showing success/error messages from service operations.
 */
public class AbstractDefaultController {
    protected final MessageView messageView;

    /**
     * Constructs an AbstractDefaultController with the specified message view.
     * 
     * @param messageView the view component used for displaying messages
     */
    protected AbstractDefaultController(MessageView messageView){
        this.messageView = messageView;
    }

    /**
     * Displays a service response using the configured message view.
     * Shows success or error messages based on the response status.
     * 
     * @param serviceResponse the service response to display
     */
    protected void defaultShowServiceResponse(ServiceResponse<?> serviceResponse){
        if(serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS){
            messageView.success(serviceResponse.getMessage());
        }
        else{
            messageView.error(serviceResponse.getMessage());
        }
    }
}
    