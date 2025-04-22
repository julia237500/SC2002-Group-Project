package config;

import exception.EnumParsingException;

/**
 * Represents the possible withdrawal status of an application.
 * This enum provides both programmatic constants and their string representations,
 * along with parsing functionality to convert strings to enum values.
 */
public enum WithdrawalStatus {
    /** Indicates that the application is not in a state where withdrawal is applicable. */
    NOT_APPLICABLE("Not Applicable"),

    /** Indicates that the application is requesting to withdraw. */
    PENDING("Pending"),

    /** Indicates that the application is withdrawal is approved. */
    SUCCESSFUL("Successful"),

    /** Indicates that the application is withdrawal is rejected. */
    UNSUCCESSFUL("Unsuccessful");

    private String storedString;

    /**
     * Constructs a {@code WithdrawalStatus} enum constant with its string representation.
     * @param storedString the human-readable string representation of this status.
     *                     This string is also used for file storage and is passed to the parser for reconstruction.
     */
    private WithdrawalStatus(String storedString){
        this.storedString = storedString;
    }

    /**
     * Parses a string into the corresponding {@code WithdrawalStatus} enum value.
     * The comparison is case-sensitive and requires an exact match.
     * @param s the string to parse (must match one of the stored string representations)
     * @return the matching WithdrawalStatus enum value
     * @throws EnumParsingException if the string doesn't match any withdrawal status 
     * @see EnumParsingException
     */
    public static WithdrawalStatus parseWithdrawalStatus(String s){
        for(WithdrawalStatus withdrawalStatus:values()){
            if(s.equals(withdrawalStatus.getStoredString())) return withdrawalStatus;
        }

        throw new EnumParsingException("Cannot parse WithdrawalStatus: " + s);
    }

    /**
     * Gets the string representation of this withdrawal status. 
     * This string is also used for file storage and is passed to the parser for reconstruction.
     * @return the human-readable string representation of this withdrawal status
     */
    public String getStoredString() {
        return storedString;
    }
}
