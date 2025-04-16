package config;

import exception.EnumParsingException;

public enum RegistrationStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful");

    private String storedString;

    private RegistrationStatus(String storedString){
        this.storedString = storedString;
    }

    public static RegistrationStatus parseRegistrationStatus(String s){
        for(RegistrationStatus registrationStatus:values()){
            if(s.equals(registrationStatus.getStoredString())) return registrationStatus;
        }

        throw new EnumParsingException("Cannot parse RegistrationStatus: " + s);
    }

    public String getStoredString() {
        return storedString;
    }
}