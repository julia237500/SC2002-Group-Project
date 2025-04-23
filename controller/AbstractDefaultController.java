package controller;

import config.ResponseStatus;
import service.ServiceResponse;
import view.interfaces.MessageView;

/**
 * Provides reusable functionality across Controller, 
 * such as showing success/error messages in {@link ServiceResponse}.
 * 
 * @see ServiceResponse
 */
public class AbstractDefaultController {
    protected final MessageView messageView;

    /**
     * Constructs an {@code AbstractDefaultController} with a {@link MessageView}.
     * 
     * @param messageView the view component used for displaying messages
     * 
     * @see MessageView
     */
    protected AbstractDefaultController(MessageView messageView){
        this.messageView = messageView;
    }

    /**
     * Displays a {@link ServiceResponse} using the {@link MessageView}.
     * Shows messages based on the {@link ResponseStatus}.
     * 
     * @param serviceResponse the service response to display
     * 
     * @see ServiceResponse
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
    