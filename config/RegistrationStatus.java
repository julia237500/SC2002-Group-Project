package config;

import exception.EnumParsingException;

/**
 * Represents the possible statuses of an officer registration process.
 * This enum provides both programmatic constants and their string representations,
 * along with parsing functionality to convert strings to enum values.
 */
public enum RegistrationStatus {
    /**
     * Registration is pending approval.
     */
    PENDING("Pending"),

    /**
     * Registration is approved.
     */
    SUCCESSFUL("Successful"),

    /**
     * Registration is rejected.
     */
    UNSUCCESSFUL("Unsuccessful");

    private String storedString;

    /**
     * Constructs a RegistrationStatus enum constant.
     * @param storedString the human-readable string representation of this status.
     *                     This string is also used for file storage and is passed to the parser for reconstruction.
     */
    private RegistrationStatus(String storedString) {
        this.storedString = storedString;
    }

    /**
     * Parses a string into the corresponding {@code RegistrationStatus} enum value.
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
        
        throw new EnumParsingException("Cannot parse RegistrationStatus: " + s);
    }

    /**
     * Gets the string representation of this registration status.
     * This string is also used for file storage and is passed to the parser for reconstruction.
     * @return the human-readable string representation of this status
     */
    public String getStoredString() {
        return storedString;
    }
}