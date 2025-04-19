package policy;

public class PolicyResponse {
    private final boolean allowed;
    private final String message;

    public PolicyResponse(boolean allowed, String message) {
        this.allowed = allowed;
        this.message = message;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getMessage() {
        return message;
    }

    public static PolicyResponse allow() {
        return new PolicyResponse(true, null);
    }

    public static PolicyResponse deny(String message) {
        return new PolicyResponse(false, message);
    }
}
