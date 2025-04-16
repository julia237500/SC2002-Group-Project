package config;

import exception.EnumParsingException;

public enum EnquiryStatus {
    UNREPLIED("Unreplied"),
    REPLIED("Replied");

    private String storedString;

    private EnquiryStatus(String storedString){
        this.storedString = storedString;
    }

    public static EnquiryStatus parseEnquiryStatus(String s){
        for(EnquiryStatus enquiryStatus:values()){
            if(s.equals(enquiryStatus.getStoredString())) return enquiryStatus;
        }

        throw new EnumParsingException("Cannot parse EnquiryStatus: " + s);
    }

    public String getStoredString() {
        return storedString;
    }
}
