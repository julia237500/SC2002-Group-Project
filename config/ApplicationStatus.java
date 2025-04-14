package config;

import exception.EnumParsingException;

public enum ApplicationStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful"),
    BOOKED("Booked");

    private String storedString;

    private ApplicationStatus(String storedString){
        this.storedString = storedString;
    }

    public static ApplicationStatus parseApplicationStatus(String s){
        for(ApplicationStatus applicationStatus:values()){
            if(s.equals(applicationStatus.getStoredString())) return applicationStatus;
        }

        throw new EnumParsingException("Cannot parse ApplicationStatus: " + s);
    }

    public String getStoredString() {
        return storedString;
    }
}
