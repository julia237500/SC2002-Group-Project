package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.UserRole;
import dto.BTOProjectDTO;
import exception.BTOProjectException;

public class BTOProject {
    public static int MAX_HDB_OFFICER_LIMIT = 10;

    private String name;
    private String neighborhood;
    private int twoRoomFlatNum;
    private int twoRoomFlatPrice;
    private int threeRoomFlatNum;
    private int threeRoomFlatPrice;
    private LocalDate openingDate;
    private LocalDate closingDate;
   
    private final User HDBManager;
    private int HDBOfficerLimit;
    private final List<User> HDBOfficers = new ArrayList<>();

    private boolean visible = false;

    public BTOProject(User HDBManager, String name, String neighborhood, int twoRoomFlatNum, int twoRoomFlatPrice, int threeRoomFlatNum, int threeRoomFlatPrice, LocalDate openingDate, LocalDate closingDate, int HDBOfficerLimit){
        this.HDBManager = HDBManager;
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
    
    public static BTOProject fromDTO(User HBDManager, BTOProjectDTO btoProjectDTO){
        if(HBDManager.getUserRole() != UserRole.HDB_MANAGER){
            throw new BTOProjectException("Access Denied. Only HDB Manager can open new project.");
        }
        validate(btoProjectDTO);

        return new BTOProject(
            HBDManager,
            btoProjectDTO.getName(), 
            btoProjectDTO.getNeighborhood(), 
            btoProjectDTO.getTwoRoomFlatNum(),
            btoProjectDTO.getTwoRoomFlatPrice(), 
            btoProjectDTO.getThreeRoomFlatNum(), 
            btoProjectDTO.getThreeRoomFlatPrice(), 
            btoProjectDTO.getOpeningDate(), 
            btoProjectDTO.getClosingDate(),
            btoProjectDTO.getHDBOfficerLimit()
        );
    }

    public void edit(BTOProjectDTO btoProjectDTO){
        validate(btoProjectDTO);
        if(btoProjectDTO.getHDBOfficerLimit() < HDBOfficers.size()){
            throw new BTOProjectException("New number of HDB Officers cannot be smaller than current number of HDB Officers in charge (%d)".formatted(HDBOfficers.size()));
        }

        this.name = btoProjectDTO.getName();
        this.neighborhood = btoProjectDTO.getNeighborhood();
        this.twoRoomFlatNum = btoProjectDTO.getTwoRoomFlatNum();
        this.twoRoomFlatPrice = btoProjectDTO.getTwoRoomFlatPrice();
        this.threeRoomFlatNum = btoProjectDTO.getThreeRoomFlatNum();
        this.threeRoomFlatPrice = btoProjectDTO.getThreeRoomFlatPrice();
        this.openingDate = btoProjectDTO.getOpeningDate();
        this.closingDate = btoProjectDTO.getClosingDate();
        this.HDBOfficerLimit = btoProjectDTO.getHDBOfficerLimit();
    }

    private static void validate(BTOProjectDTO btoProjectDTO){
        if(btoProjectDTO.getTwoRoomFlatNum() < 0){
            throw new BTOProjectException("Number of 2-Room Flat cannot be negative.");
        }

        if(btoProjectDTO.getTwoRoomFlatPrice() < 0){
            throw new BTOProjectException("Price of 2-Room Flat cannot be negative.");
        }

        if(btoProjectDTO.getThreeRoomFlatNum() < 0){
            throw new BTOProjectException("Number of 3-Room Flat cannot be negative.");
        }

        if(btoProjectDTO.getThreeRoomFlatPrice() < 0){
            throw new BTOProjectException("Number of 3-Room Flat cannot be negative.");
        }

        if(btoProjectDTO.getClosingDate().isBefore(LocalDate.now())){
            throw new BTOProjectException("Closing date cannot be past.");
        }

        if(btoProjectDTO.getClosingDate().isBefore(btoProjectDTO.getOpeningDate())){
            throw new BTOProjectException("Closing date cannot be before opening date.");
        }

        if(btoProjectDTO.getHDBOfficerLimit() < 0 || btoProjectDTO.getHDBOfficerLimit() > BTOProject.MAX_HDB_OFFICER_LIMIT){
            throw new BTOProjectException("Number of HDB Officers must be between 0 - %d".formatted(MAX_HDB_OFFICER_LIMIT));
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void toggleVisibility(){
        visible = visible ? false : true;
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

    public int getHDBOfficerLimit() {
        return HDBOfficerLimit;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public User getHDBManager() {
        return HDBManager;
    }

    public String toString(){
        return String.format("""
                Name                  : %s
                Neighbourhood         : %s
                Number of 2-Room Flat : %d
                Price of 2-Room Flat  : %d
                Number of 3-Room Flat : %d
                Price of 3-Room Flat  : %d
                Opening Date          : %s
                Closing Date          : %s
                HDB Officer Limit     : %d
                Visibility            : %s
                """, name, neighborhood, twoRoomFlatNum, twoRoomFlatPrice, threeRoomFlatNum, threeRoomFlatPrice, openingDate, closingDate, HDBOfficerLimit, visible ? "Visible" : "Hidden");
    }

    public boolean isOverlappingWith(LocalDate openingDate, LocalDate closingDate){
        return !(openingDate.isAfter(this.closingDate) || closingDate.isBefore(this.openingDate));
    }
}
