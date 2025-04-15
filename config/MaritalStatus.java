package config;

import exception.EnumParsingException;

/**
 * Represents a user's marital status in the HDB application system.
 * This enum provides both constants and string representations
 * for marital status, along with parsing capability.
 */
public enum MaritalStatus {
    /**
     * Represents an unmarried individual.
     */
    SINGLE("Single"),

    /**
     * Represents a married individual.
     */
    MARRIED("Married");

    private String storedString;

    /**
     * Constructs a MaritalStatus enum constant with its string representation.
     * @param storedString The human-readable string representation of this marital status
     */
    private MaritalStatus(String storedString) {
        this.storedString = storedString;
    }

    /**
     * Parses a string into the corresponding MaritalStatus enum value.
     * The comparison is case-sensitive and must match exactly.
     * @param s The string to parse (must be either "Single" or "Married")
     * @return The matching MaritalStatus enum value
     * @throws EnumParsingException if the string doesn't match any marital status
     */
    public static MaritalStatus parseMaritalStatus(String s) {
        for(MaritalStatus maritalStatus : values()) {
            if(s.equals(maritalStatus.getStoredString())) {
                return maritalStatus;
            }
        }
        throw new EnumParsingException("Cannot parse MaritalStatus: " + s);
    }

    /**
     * Returns the string representation of this marital status.
     * @return The human-readable string representation
     */
    public String getStoredString() {
        return storedString;
    }
}