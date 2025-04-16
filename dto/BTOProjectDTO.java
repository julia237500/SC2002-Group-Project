package dto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import config.FlatType;

public class BTOProjectDTO {
    private String name;
    private String neighborhood;
    
    private Map<FlatType, Integer> flatNum = new HashMap<>();
    private Map<FlatType, Integer> flatPrice = new HashMap<>();

    private LocalDate openingDate;
    private LocalDate closingDate;
    private int HDBOfficerLimit;

    public BTOProjectDTO(String name, String neighborhood, Map<FlatType, Integer> flatNum, Map<FlatType, Integer> flatPrice, LocalDate openingDate, LocalDate closingDate, int HDBOfficerLimit){
        this.name = name;
        this.neighborhood = neighborhood;
        this.flatNum = flatNum;
        this.flatPrice = flatPrice;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.HDBOfficerLimit = HDBOfficerLimit;
    }

    public String getName() {
        return name;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public Map<FlatType, Integer> getFlatNum() {
        return flatNum;
    }

    public Map<FlatType, Integer> getFlatPrice() {
        return flatPrice;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public int getHDBOfficerLimit() {
        return HDBOfficerLimit;
    }
}
