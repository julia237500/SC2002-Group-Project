package config;

import exception.EnumParsingException;

public enum FlatType {
    TWO_ROOM_FLAT("2-Room Flat", FormField.TWO_ROOM_FLAT_NUM, FormField.TWO_ROOM_FLAT_PRICE),
    THREE_ROOM_FLAT("3-Room Flat", FormField.THREE_ROOM_FLAT_NUM, FormField.THREE_ROOM_FLAT_PRICE);

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
}
