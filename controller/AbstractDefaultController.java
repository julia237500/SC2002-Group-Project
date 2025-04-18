package controller;

import config.ResponseStatus;
import service.ServiceResponse;
import view.interfaces.MessageView;

public class AbstractDefaultController {
    protected final MessageView messageView;

    protected AbstractDefaultController(MessageView messageView){
        this.messageView = messageView;
    }

    protected void defaultShowServiceResponse(ServiceResponse<?> serviceResponse){
        if(serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS){
            messageView.success(serviceResponse.getMessage());
        }
        else{
            messageView.error(serviceResponse.getMessage());
        }
    }
}
