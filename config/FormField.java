package config;

/**
 * Represents the different form fields used in the {@code Form}.
 * This enum mimics the behavior of HTML input {@code name} attributes
 * and PHP's {@code $_POST} keys, serving as identifiers for form data.
 * 
 * It provides type safety and prevents errors from using raw string keys
 * when accessing or processing form input values.
 */
public enum FormField {
    /** Field for NRIC */
    NRIC,

    /** Field for Password */
    PASSWORD,

    /** Field for Confirm password */
    CONFIRM_PASSWORD,

    /** Field for Name, can be used for any name */
    NAME,

    /** Field for Neighborhood of BTO project */
    NEIGHBORHOOD,

    /** Field for number of 2-Room flats */
    TWO_ROOM_FLAT_NUM,

    /** Field for price of 2-Room flats */
    TWO_ROOM_FLAT_PRICE,

    /** Field for number of 3-Room flats */
    THREE_ROOM_FLAT_NUM,

    /** Field for price of 3-Room flats */
    THREE_ROOM_FLAT_PRICE,

    /** Field for Application opening date of BTO project */
    OPENING_DATE,

    /** Field for Application closing date of BTO project */
    CLOSING_DATE,

    /** Field for Maximum number of HDB officers allowed of BTO project */
    HBD_OFFICER_LIMIT,

    /** Field for enquiry subject */
    SUBJECT,

    /** Field for enquiry content */
    ENQUIRY,

    /** Field for enquiry reply */
    REPLY,

    /** Field for filter of 2-Room flat */
    FILTER_TWO_ROOM_FLAT,

    /** Field for filter for 3-Room flat */
    FILTER_THREE_ROOM_FLAT,

    /** Field for filter of Single */
    FILTER_SINGLE,

    /** Field for filter of Married */
    FILTER_MARRIED
}