package config;

import exception.EnumParsingException;

public enum MaritalStatus {
    SINGLE("Single"),
    MARRIED("Married");

    private String storedString;

    private MaritalStatus(String storedString){
        this.storedString = storedString;
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
}
