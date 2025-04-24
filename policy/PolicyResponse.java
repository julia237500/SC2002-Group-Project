package policy;


/**
 * Represents the result of a policy check, indicating whether an action is allowed or denied,
 * and optionally providing a message when the action is denied.
 */
public class PolicyResponse {
    private final boolean allowed;
    private final String message;

    /**
     * Constructs a PolicyResponse with the specified allowed status and message.
     *
     * @param allowed whether the action is allowed
     * @param message the message explaining the reason (used when denied)
     */
    public PolicyResponse(boolean allowed, String message) {
        this.allowed = allowed;
        this.message = message;
    }

    /**
     * Returns whether the action is allowed.
     *
     * @return true if the action is allowed, false otherwise
     */
    public boolean isAllowed() {
        return allowed;
    }

    /**
     * Returns the message associated with the policy response.
     *
     * @return the denial message, or null if the action is allowed
     */
    public String getMessage() {
        return message;
    }

    /**
     * Creates a PolicyResponse indicating that the action is allowed.
     *
     * @return a new PolicyResponse with allowed set to true
     */
    public static PolicyResponse allow() {
        return new PolicyResponse(true, null);
    }

    /**
     * Creates a PolicyResponse indicating that the action is denied, with a reason message.
     *
     * @param message the reason for denial
     * @return a new PolicyResponse with allowed set to false and the given message
     */
    public static PolicyResponse deny(String message) {
        return new PolicyResponse(false, message);
    }
}
