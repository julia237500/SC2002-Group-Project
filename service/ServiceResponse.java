package service;

import config.ResponseStatus;
import policy.PolicyResponse;

public class ServiceResponse<T> {
    private final ResponseStatus responseStatus;
    private final String message;
    private final T data;

    public ServiceResponse(ResponseStatus responseStatus){
        this.responseStatus = responseStatus;
        this.message = "";
        this.data = null;
    }

    public ServiceResponse(ResponseStatus responseStatus, T data){
        this.responseStatus = responseStatus;
        this.message = "";
        this.data = data;
    }

    public ServiceResponse(ResponseStatus responseStatus, String message){
        this.responseStatus = responseStatus;
        this.message = message;
        this.data = null;
    }

    public ServiceResponse(ResponseStatus responseStatus, String message, T data){
        this.responseStatus = responseStatus;
        this.message = message;
        this.data = data;
    }

    public ServiceResponse(PolicyResponse policyResponse){
        this.responseStatus = policyResponse.isAllowed() ? ResponseStatus.SUCCESS : ResponseStatus.ERROR;
        this.message = policyResponse.getMessage();
        this.data = null;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
