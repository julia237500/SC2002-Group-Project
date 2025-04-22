package config;

/**
 * Represents the different form fields used in the HDB application system.
 * This enum serves as a centralized reference for all form field identifiers,
 * ensuring consistent naming across the entire application.
 */
public enum FormField {
    /** NRIC number field */
    NRIC,

    /** User password field */
    PASSWORD,

    /** Password confirmation field */
    CONFIRM_PASSWORD,

    /** User's full name field */
    NAME,

    /** Neighborhood selection field for BTO projects */
    NEIGHBORHOOD,

    /** Field for number of available 2-Room flats */
    TWO_ROOM_FLAT_NUM,

    /** Field for price of 2-Room flats */
    TWO_ROOM_FLAT_PRICE,

    /** Field for number of available 3-Room flats */
    THREE_ROOM_FLAT_NUM,

    /** Field for price of 3-Room flats */
    THREE_ROOM_FLAT_PRICE,

    /** Field for BTO project application opening date */
    OPENING_DATE,

    /** Field for BTO project application closing date */
    CLOSING_DATE,

    /** Field for maximum number of HDB officers allowed for a project */
    HBD_OFFICER_LIMIT,

    /** Field for enquiry subject */
    SUBJECT,

    /** Field for enquiry content */
    ENQUIRY,

    /** Field for enquiry reply */
    REPLY,

    FILTER_TWO_ROOM_FLAT,
    FILTER_THREE_ROOM_FLAT,

    FILTER_SINGLE,
    FILTER_MARRIED
}