package config;

import exception.EnumParsingException;

public enum MaritalStatus {
    SINGLE("Single", FormField.FILTER_SINGLE),
    MARRIED("Married", FormField.FILTER_MARRIED);

    private final String storedString;
    private final FormField filterFormField;

    private MaritalStatus(String storedString, FormField filterFormField){
        this.storedString = storedString;
        this.filterFormField = filterFormField;
    }

    public static MaritalStatus parseMaritalStatus(String s){
        for(MaritalStatus maritalStatus:values()){
            if(s.equals(maritalStatus.getStoredString())) return maritalStatus;
        }

        throw new EnumParsingException("Cannot parse MaritalStatus: " + s);
    }

    public String getStoredString() {
        return storedString;
    }

    public FormField getFilterFormField() {
        return filterFormField;
    }
}
