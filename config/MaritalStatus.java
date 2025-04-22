package config;

import exception.EnumParsingException;

/**
 * Represents a user's marital status.
 * This enum provides both programmatic constants and their string representations,
 * along with parsing functionality to convert strings to enum values.
 */
public enum MaritalStatus {
    /**
     * Represents an unmarried individual.
     */
    SINGLE("Single", FormField.FILTER_SINGLE),

    /**
     * Represents a married individual.
     */
    MARRIED("Married", FormField.FILTER_MARRIED);

    private final String storedString;
    private final FormField filterFormField;

    /**
     * Constructs a MaritalStatus enum.
     * @param storedString The human-readable string representation of the status.
     *                     This string is also used for file storage and is passed to the parser for reconstruction.
     */
    private MaritalStatus(String storedString, FormField filterFormField) {
        this.storedString = storedString;
        this.filterFormField = filterFormField;
    }

    /**
     * Parses a string into the corresponding {@code MaritalStatus} enum value.
     * The comparison is case-sensitive and must match exactly.
     * @param s The string to parse (must match one of the stored string representations)
     * @return The matching MaritalStatus enum value
     * @throws EnumParsingException if the string doesn't match any marital status
     * @see EnumParsingException
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
     * This string is also used for file storage and is passed to the parser for reconstruction.
     * @return The human-readable string representation of the status.  
     */
    public String getStoredString() {
        return storedString;
    }

    /**
     * Gets the {@code FormField} associated with filter for this marital status.
     * This method is to support polymorphism when creating {@code Form}.
     * @return the associated filter FormField
     * @see FormField
     */
    public FormField getFilterFormField() {
        return filterFormField;
    }
}