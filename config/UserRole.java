package config;

import exception.EnumParsingException;

/**
 * Represents the possible user roles.
 * This enum provides both programmatic constants and their string representations,
 * along with parsing functionality to convert strings to enum values.
 */
public enum UserRole {

    /**
     * User is an applicant
     */
    APPLICANT("Applicant"),

    /**
     * User is a HDB Officer
     */
    HDB_OFFICER("HDB Officer"),

    /**
     * User is a HDB Manager
     */
    HDB_MANAGER("HDB Manager");

    private String storedString;

    /**
     * Constructs a {@code UserRole} enum constant with its string representation.
     * @param storedString the human-readable string representation of this status.
     *                     This string is also used for file storage and is passed to the parser for reconstruction.
     */
    private UserRole(String storedString){
        this.storedString = storedString;
    }

    /**
     * Parses a string into the corresponding UserRole enum value.
     * The comparison is case-sensitive and requires an exact match.
     * @param s the string to parse (must match one of the stored string representations)
     * @return the matching UserRole enum value
     * @throws EnumParsingException if the string doesn't match any user roles 
     * @see EnumParsingException
     */
    public static UserRole parseUserRole(String s){
        for(UserRole userRole:values()){
            if(s.equals(userRole.getStoredString())) return userRole;
        }

        throw new EnumParsingException("Cannot parse UserRole: " + s);
    }

    /**
     * Gets the string representation of this user role. 
     * This string is also used for file storage and is passed to the parser for reconstruction.
     * @return the human-readable string representation of this user role 
     */
    public String getStoredString() {
        return storedString;
    }
}
