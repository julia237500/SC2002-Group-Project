package dto;

import java.time.LocalDate;

public class BTOProjectDTO {
    private String name;
    private String neighborhood;
    private int twoRoomFlatNum;
    private int twoRoomFlatPrice;
    private int threeRoomFlatNum;
    private int threeRoomFlatPrice;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private int HDBOfficerLimit;

    public BTOProjectDTO(String name, String neighborhood, int twoRoomFlatNum, int twoRoomFlatPrice, int threeRoomFlatNum, int threeRoomFlatPrice, LocalDate openingDate, LocalDate closingDate, int HDBOfficerLimit){
        this.name = name;
        this.neighborhood = neighborhood;
        this.twoRoomFlatNum = twoRoomFlatNum;
        this.twoRoomFlatPrice = twoRoomFlatPrice;
        this.threeRoomFlatNum = threeRoomFlatNum;
        this.threeRoomFlatPrice = threeRoomFlatPrice;
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

    public int getTwoRoomFlatNum() {
        return twoRoomFlatNum;
    }

    public int getTwoRoomFlatPrice() {
        return twoRoomFlatPrice;
    }

    public int getThreeRoomFlatNum() {
        return threeRoomFlatNum;
    }

    public int getThreeRoomFlatPrice() {
        return threeRoomFlatPrice;
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
