package config;

import exception.EnumParsingException;
import model.User;

/**
 * Represents the different types of HDB flats available.
 * Each flat type has specific eligibility criteria, form fields for quantity and price,
 * and a string representation for display and parsing purposes.
 */


public enum FlatType {
    /**
     * 2-Room Flat type with specific eligibility rules:
     * - Singles must be at least 35 years old
     * - Married couples must be at least 21 years old
     * TWO_ROOM_FLAT and THREE_ROOM_FLAT are enum constants.
     * Since FlatType declares an abstract isEligible(User) method, each enum constant must provide its own implementation of the abstract method.
     * This allows each flat type to have different eligibility rules while keeping the logic neatly organized inside the enum itself.
     */
    TWO_ROOM_FLAT("2-Room Flat", FormField.TWO_ROOM_FLAT_NUM, FormField.TWO_ROOM_FLAT_PRICE, FormField.FILTER_TWO_ROOM_FLAT){
        @Override
        public boolean isEligible(User applicant) {
            if(applicant.getMaritalStatus() == MaritalStatus.SINGLE){
                return applicant.getAge() >= 35;
            }
            if(applicant.getMaritalStatus() == MaritalStatus.MARRIED){
                return applicant.getAge() >= 21;
            }
            return false;
        }

        @Override
        public String getEligibilityDetail() {
            return """
                1. Single and above 35 years old
                2. Married and above 21 years old
                    """;
        }
    },

    /**
     * 3-Room Flat type with specific eligibility rules:
     * - Only available to married couples at least 21 years old
     * - Not available to singles 
     */
    THREE_ROOM_FLAT("3-Room Flat", FormField.THREE_ROOM_FLAT_NUM, FormField.THREE_ROOM_FLAT_PRICE, FormField.FILTER_THREE_ROOM_FLAT){
        /**
         * Checks if a user is eligible for a 3-Room Flat.
         * @param applicant the user applying for the flat
         * @return true if eligible, false otherwise
         */
        @Override
        public boolean isEligible(User applicant){
            if(applicant.getMaritalStatus() == MaritalStatus.SINGLE){
                return false;
            }
            if(applicant.getMaritalStatus() == MaritalStatus.MARRIED){
                return applicant.getAge() >= 21;
            }
            return false;
        }

        @Override
        public String getEligibilityDetail() {
            return """
                1. Married and above 21 years old
                    """;
        }
    };

    private final String storedString;
    private final FormField numFormField;
    private final FormField priceFormField;
    private final FormField filterFormField;

    /**
     * Constructs a FlatType enum constant.
     * @param storedString the string representation of this flat type (eg. "2-Room Flat")
     * @param numFormField the form field associated with quantity for this flat type
     * @param priceFormField the form field associated with price for this flat type
     */
    private FlatType(String storedString, FormField numFormField, FormField priceFormField, FormField filterFormField){
        this.storedString = storedString;
        this.numFormField = numFormField;
        this.priceFormField = priceFormField;
        this.filterFormField = filterFormField;
    }

    /**
     * Parses a string into the corresponding FlatType enum value.
     * @param s the string to parse
     * @return the matching FlatType
     * @throws EnumParsingException if the string doesn't match any FlatType
     */
    public static FlatType parseFlatType(String s){
        for(FlatType flatType:values()){
            if(s.equals(flatType.getStoredString())) return flatType;
        }

        throw new EnumParsingException("Cannot parse FlatType: " + s);
    }

    /**
     * Gets the string representation of this flat type.
     * @return the stored string representation
     */
    public String getStoredString() {
        return storedString;
    }

    /**
     * Gets the form field associated with quantity for this flat type.
     * @return the quantity form field
     */
    public FormField getNumFormField() {
        return numFormField;
    }

    /**
     * Gets the form field associated with price for this flat type.
     * @return the price form field
     */
    public FormField getPriceFormField() {
        return priceFormField;
    }

    public FormField getFilterFormField() {
        return filterFormField;
    }

    public abstract boolean isEligible(User user);
    public abstract String getEligibilityDetail();
}
