package config;

import exception.EnumParsingException;

/**
 * Represents the possible statuses of a HDB application process.
 * This enum provides both programmatic constants and their string representations,
 * along with parsing functionality to convert strings to enum values.
 */
public enum ApplicationStatus {
    /**
     * Application is pending approval.
     * Entry status upon application – No conclusive decision made about the outcome of the application 
     */
    PENDING("Pending"),

    /**
     * Application is successful.
     * Hence invited to make a flat booking with the HDB Officer 
     */
    SUCCESSFUL("Successful"),

    /**
     * Application is unsuccessful.
     * Hence cannot make a flat booking for this application. Applicant may apply for another project. 
     */
    UNSUCCESSFUL("Unsuccessful"),

    /**
     * Application is booked.
     * Secured a unit after a successful application and completed a flat booking with the HDB Officer. 
     */
    BOOKED("Booked");

    private String storedString;

    /**
     * Constructs a {@code ApplicationStatus} enum constant.
      * @param storedString The human-readable string representation of the status.
      *                     This string is also used for file storage and is passed to the parser for reconstruction.
     */
    private ApplicationStatus(String storedString){
        this.storedString = storedString;
    }

    /**
     * Parses a string into the corresponding {@code ApplicationStatus} enum value.
     * The comparison is case-sensitive and requires an exact match.
     * @param s the string to parse (must match one of the stored string representations)
     * @return the matching ApplicationStatus enum value
     * @throws EnumParsingException if the string doesn't match any application status
     * @see EnumParsingException
     */
    public static ApplicationStatus parseApplicationStatus(String s){
        for(ApplicationStatus applicationStatus:values()){
            if(s.equals(applicationStatus.getStoredString())) return applicationStatus;
        }

        throw new EnumParsingException("Cannot parse ApplicationStatus: " + s);
    }

    /**
     * Gets the string representation of this application status.
     * This string is also used for file storage and is passed to the parser for reconstruction.
     * @return The human-readable string representation of the status.
     */
    public String getStoredString() {
        return storedString;
    }
}
