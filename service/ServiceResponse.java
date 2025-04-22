package service;

import config.ResponseStatus;
import policy.PolicyResponse;

/**
 * A generic container for service operation responses, including status, message, and data.
 * 
 * @param <T> the type of data contained in this response
 */
public class ServiceResponse<T> {
    private final ResponseStatus responseStatus;
    private final String message;
    private final T data;

    /**
     * Constructs a response with only a status (empty message and null data).
     * 
     * @param responseStatus the status of the service operation
     */
    public ServiceResponse(ResponseStatus responseStatus){
        this.responseStatus = responseStatus;
        this.message = "";
        this.data = null;
    }

    /**
     * There is constructor overloading involved in this class, where there's
     * more than one constructor with different parameter lists. 
     */

    /**
     * Constructs a response with status and data (empty message).
     * 
     * @param responseStatus the status of the service operation
     * @param data the response data
     */
    public ServiceResponse(ResponseStatus responseStatus, T data){
        this.responseStatus = responseStatus;
        this.message = "";
        this.data = data;
    }

    /**
     * Constructs a response with status and message (null data).
     * 
     * @param responseStatus the status of the service operation 
     * @param message human-readable response message (empty string if null)
     */
    public ServiceResponse(ResponseStatus responseStatus, String message){
        this.responseStatus = responseStatus;
        this.message = message;
        this.data = null;
    }

    /**
     * Constructs a complete response with status, message, and data.
     * 
     * @param responseStatus the status of the service operation 
     * @param message human-readable response message
     * @param data the response data 
     */
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

    /**
     * Gets the status of the service operation.
     * 
     * @return the response status
     */
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    /**
     * Gets the human-readable response message.
     * 
     * @return the message (empty string if none was provided)
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the response data payload.
     * 
     * @return the data (may be null if no data was provided)
     */
    public T getData() {
        return data;
    }
}