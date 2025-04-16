package config;

import exception.EnumParsingException;

/**
 * Represents the possible statuses of a user registration process.
 * This enum provides both programmatic constants and their string representations,
 * along with parsing functionality to convert strings to enum values.
 */
public enum RegistrationStatus {
    /**
     * Registration is pending approval or processing.
     */
    PENDING("Pending"),

    /**
     * Registration was completed successfully.
     */
    SUCCESSFUL("Successful"),

    /**
     * Registration failed or was rejected.
     */
    UNSUCCESSFUL("Unsuccessful");

    private String storedString;

    /**
     * Constructs a RegistrationStatus enum constant with its string representation.
     * @param storedString the human-readable string representation of this status
     * In Java enums, the constructor is implicitly private
     * We explicitly declare it as private for clarity.
     * The compiler enforces that enum constructors are only callable within the enum itself.
     * Only the enum constants (like PENDING, SUCCESSFUL) can call this constructor.
     * Enums are singleton-like and cannot be arbitrarily instantiated.
     */
    private RegistrationStatus(String storedString) {
        this.storedString = storedString;
    }

    /**
     * Parses a string into the corresponding RegistrationStatus enum value.
     * The comparison is case-sensitive and requires an exact match.
     * @param s the string to parse (must match one of the stored string representations)
     * @return the matching RegistrationStatus enum value
     * @throws EnumParsingException if the string doesn't match any registration status
     * @see EnumParsingException
     */
    public static RegistrationStatus parseRegistrationStatus(String s) {
        for(RegistrationStatus registrationStatus : values()) {
            if(s.equals(registrationStatus.getStoredString())) {
                return registrationStatus;
            }
        }
        /**
         * throws a custom exception when string parsing fails.
         * Use a custom exception, because the built-in ones like
         * 'throw new IllegalArgumentException("Invalid status")' is too generic, loses semantic meaning
         * Must use the 'new' keyword because exceptions are objects
         * and like any other object in Java, you must instantiate them with 'new' before use.
         * 'throw' expects an instance – you need a concrete object.
         * 'new' enables customisation by letting us pass a detailed error message ("Cannot parse...") at creation time.
         */
        throw new EnumParsingException("Cannot parse RegistrationStatus: " + s);
    }

    /**
     * Gets the string representation of this registration status.
     *
     * @return the human-readable string representation of this status
     */
    public String getStoredString() {
        return storedString;
    }
}