package config;

import exception.EnumParsingException;

public enum WithdrawalStatus {
    NOT_APPLICABLE("Not Applicable"),
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful");

    private String storedString;

    private WithdrawalStatus(String storedString){
        this.storedString = storedString;
    }

    public static WithdrawalStatus parseWithdrawalStatus(String s){
        for(WithdrawalStatus withdrawalStatus:values()){
            if(s.equals(withdrawalStatus.getStoredString())) return withdrawalStatus;
        }

        throw new EnumParsingException("Cannot parse WithdrawalStatus: " + s);
    }

    public String getStoredString() {
        return storedString;
    }
}
