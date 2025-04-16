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
     * Constructs a EnquiryStatus enum constant with its string representation.
     * @param storedString the human-readable string representation of this status
     */
    private EnquiryStatus(String storedString){
        this.storedString = storedString;
    }

    /**
     * Parses a string into the corresponding EnquiryStatus enum value.
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
     *
     * @return the human-readable string representation of this status
     */
    public String getStoredString() {
        return storedString;
    }
}
