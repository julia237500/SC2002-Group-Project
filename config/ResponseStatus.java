package config;

/**
 * Represents the possible response statuses returned by a service layer.
 * This enum is used to communicate the result of an operation to the controller,
 *
 * The design can be extended to support more granular statuses or to include
 * associated HTTP status codes, making it suitable for use in API development.
 */
public enum ResponseStatus {

    /**
     * Indicates that the action is successful.
     */
    SUCCESS,

    /**
     * Indicates that the action caused an error, such as access denial, 
     * failure to save, or other general issues.
     * 
     * This status is used for recoverable or non-critical errors that 
     * do not prevent the application from continuing to run.
     */
    ERROR,

    /**
     * Indicates that the action caused an fatal error,
     * such as unable to load initial data from file.
     * 
     * This status is used for unrecoverable or critical errors that
     * prevent the application from continuing to run. 
     */
    FAILURE,
}
