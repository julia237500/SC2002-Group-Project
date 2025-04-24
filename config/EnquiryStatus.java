package config;

import exception.EnumParsingException;

/**
 * Represents the possible statuses of an enquiry process.
 * This enum provides both programmatic constants and their string representations,
 * along with parsing functionality to convert strings to enum values.
 */
public enum EnquiryStatus {

    /**
     * Enquiry is unreplied.
     */
    UNREPLIED("Unreplied"),

    /**
     * Enquiry is replied.
     */
    REPLIED("Replied");

    private String storedString;

    /**
     * Constructs a {@code EnquiryStatus} enum constant.
     * @param storedString The human-readable string representation of the status.
     *                     This string is also used for file storage and is passed to the parser for reconstruction.
     */
    private EnquiryStatus(String storedString){
        this.storedString = storedString;
    }

    /**
     * Parses a string into the corresponding {@code EnquiryStatus} enum value.
     * The comparison is case-sensitive and requires an exact match.
     * @param s the string to parse (must match one of the stored string representations)
     * @return the matching EnquiryStatus enum value
     * @throws EnumParsingException if the string doesn't match any enquiry status
     * @see EnumParsingException
     */
    public static EnquiryStatus parseEnquiryStatus(String s){
        for(EnquiryStatus enquiryStatus:values()){
            if(s.equals(enquiryStatus.getStoredString())) return enquiryStatus;
        }

        throw new EnumParsingException("Cannot parse EnquiryStatus: " + s);
    }

    /**
     * Gets the string representation of this enquiry status.
     * This string is also used for file storage and is passed to the parser for reconstruction.
     * @return The human-readable string representation of the status.
     */
    public String getStoredString() {
        return storedString;
    }
}
