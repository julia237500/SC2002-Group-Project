package config;

import exception.EnumParsingException;
import model.User;

/**
 * Represents the different types of HDB flats available.
 * Each flat type has specific eligibility criteria, {@code FormField} for quantity, price, and filter,
 * and a string representation for display and parsing purposes.
 * 
 * Each flat type overrides the {@code isEligible(User)} method to define its own egilibity rules.
 * @see FormField
 */
public enum FlatType {
    /**
     * 2-Room Flat type with specific eligibility rules:
     * <ul>
     *  <li> Singles must be at least 35 years old
     *  <li> Married couples must be at least 21 years old
     * <ul>
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
     * <ul>
     *  <li> Only available to married couples at least 21 years old
     *  <li> Not available to singles 
     * <ul> 
     */
    THREE_ROOM_FLAT("3-Room Flat", FormField.THREE_ROOM_FLAT_NUM, FormField.THREE_ROOM_FLAT_PRICE, FormField.FILTER_THREE_ROOM_FLAT){
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
     * @param storedString The human-readable string representation of the status.
     *                     This string is also used for file storage and is passed to the parser for reconstruction.
     * @param numFormField the {@code FormField} associated with quantity for this flat type
     * @param priceFormField the {@code FormField} associated with price for this flat type
     * @param filterFormField the {@code FormField} associated with filter for this flat type
     */
    private FlatType(String storedString, FormField numFormField, FormField priceFormField, FormField filterFormField){
        this.storedString = storedString;
        this.numFormField = numFormField;
        this.priceFormField = priceFormField;
        this.filterFormField = filterFormField;
    }

    /**
     * Parses a string into the corresponding {@code FlatType} enum value.
     * The comparison is case-sensitive and requires an exact match.
     * @param s the string to parse (must match one of the stored string representations)
     * @return the matching FlatType enum value
     * @throws EnumParsingException if the string doesn't match any flat type
     * @see EnumParsingException
     */
    public static FlatType parseFlatType(String s){
        for(FlatType flatType:values()){
            if(s.equals(flatType.getStoredString())) return flatType;
        }

        throw new EnumParsingException("Cannot parse FlatType: " + s);
    }

    /**
     * Gets the string representation of this flat type.
     * This string is also used for file storage and is passed to the parser for reconstruction.
     * @return The human-readable string representation of the type.
     */
    public String getStoredString() {
        return storedString;
    }

    /**
     * Gets the {@code FormField} associated with quantity for this flat type.
     * This method is to support polymorphism when creating {@code Form}.
     * @return the associated quantity FormField
     * @see FormField
     */
    public FormField getNumFormField() {
        return numFormField;
    }

    /**
     * Gets the {@code FormField} associated with price for this flat type.
     * This method is to support polymorphism when creating {@code Form}.
     * @return the associated price FormField
     * @see FormField
     */
    public FormField getPriceFormField() {
        return priceFormField;
    }

    /**
     * Gets the {@code FormField} associated with filter for this flat type.
     * This method is to support polymorphism when creating {@code Form}.
     * @return the associated filter FormField
     * @see FormField
     */
    public FormField getFilterFormField() {
        return filterFormField;
    }

    /**
     * Checks whether the given user is eligible for this flat type.
     * Each {@code FlatType} constant provides its own implementation
     * to handle eligibility based on different rules.
     * 
     * @param user The user to evaluate.
     * @return {@code true} if the user meets the flat's eligibility criteria; {@code false} otherwise.
     * @see User
     */
    public abstract boolean isEligible(User user);

    /**
     * Gets the description of the eligibility rules for this flat type.
     * Mainly used for displaying the criteria to users.
     *
     * @return a human-readable description of the eligibility rules
     */
    public abstract String getEligibilityDetail();
}
