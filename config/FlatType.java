package config;

import exception.EnumParsingException;
import model.User;

public enum FlatType {
    TWO_ROOM_FLAT("2-Room Flat", FormField.TWO_ROOM_FLAT_NUM, FormField.TWO_ROOM_FLAT_PRICE){
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
    THREE_ROOM_FLAT("3-Room Flat", FormField.THREE_ROOM_FLAT_NUM, FormField.THREE_ROOM_FLAT_PRICE){
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

    private String storedString;
    private FormField numFormField;
    private FormField priceFormField;

    private FlatType(String storedString, FormField numFormField, FormField priceFormField){
        this.storedString = storedString;
        this.numFormField = numFormField;
        this.priceFormField = priceFormField;
    }

    public static FlatType parseFlatType(String s){
        for(FlatType flatType:values()){
            if(s.equals(flatType.getStoredString())) return flatType;
        }

        throw new EnumParsingException("Cannot parse FlatType: " + s);
    }

    public String getStoredString() {
        return storedString;
    }

    public FormField getNumFormField() {
        return numFormField;
    }

    public FormField getPriceFormField() {
        return priceFormField;
    }

    public abstract boolean isEligible(User user);
    public abstract String getEligibilityDetail();
}
