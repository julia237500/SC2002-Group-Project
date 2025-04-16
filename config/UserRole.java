package config;

import exception.EnumParsingException;

public enum UserRole {
    APPLICANT("Applicant"),
    HDB_OFFICER("HDB Officer"),
    HDB_MANAGER("HDB Manager");

    private String storedString;

    private UserRole(String storedString){
        this.storedString = storedString;
    }

    public static UserRole parseUserRole(String s){
        for(UserRole userRole:values()){
            if(s.equals(userRole.getStoredString())) return userRole;
        }

        throw new EnumParsingException("Cannot parse UserRole: " + s);
    }

    public String getStoredString() {
        return storedString;
    }
}
