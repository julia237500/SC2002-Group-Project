package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.FlatType;
import config.UserRole;
import dto.BTOProjectDTO;
import exception.BTOProjectException;

public class BTOProject {
    public static int MIN_HDB_OFFICER_LIMIT = 1;
    public static int MAX_HDB_OFFICER_LIMIT = 10;

    private final String name;
    private String neighborhood;

    private Map<FlatType, Integer> flatNum = new HashMap<>();
    private Map<FlatType, Integer> flatPrice = new HashMap<>();

    private LocalDate openingDate;
    private LocalDate closingDate;
   
    private final User HDBManager;
    private int HDBOfficerLimit;
    private final List<User> HDBOfficers = new ArrayList<>();

    private boolean visible = false;

    private Memento memento;

    public BTOProject(User HDBManager, String name, String neighborhood, Map<FlatType, Integer> flatNum, Map<FlatType, Integer> flatPrice, LocalDate openingDate, LocalDate closingDate, int HDBOfficerLimit){
        this.HDBManager = HDBManager;
        this.name = name;
        this.neighborhood = neighborhood;
        this.flatNum = flatNum;
        this.flatPrice = flatPrice;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.HDBOfficerLimit = HDBOfficerLimit;
    }
    
    public static BTOProject fromDTO(User HBDManager, BTOProjectDTO btoProjectDTO){
        if(HBDManager.getUserRole() != UserRole.HDB_MANAGER){
            throw new BTOProjectException("Access Denied. Only HDB Manager can open new project.");
        }

        if(btoProjectDTO.getClosingDate().isBefore(LocalDate.now())){
            throw new BTOProjectException("Closing date cannot be past.");
        }
        
        validate(btoProjectDTO);

        return new BTOProject(
            HBDManager,
            btoProjectDTO.getName(), 
            btoProjectDTO.getNeighborhood(), 
            btoProjectDTO.getFlatNum(),
            btoProjectDTO.getFlatPrice(),
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

        memento = new Memento(this);

        this.neighborhood = btoProjectDTO.getNeighborhood();
        this.flatNum = btoProjectDTO.getFlatNum();
        this.flatPrice = btoProjectDTO.getFlatPrice();
        this.openingDate = btoProjectDTO.getOpeningDate();
        this.closingDate = btoProjectDTO.getClosingDate();
        this.HDBOfficerLimit = btoProjectDTO.getHDBOfficerLimit();
    }

    public void revertEdit(){
        if(memento != null){
            memento.restore(this);
        }
    }

    private static void validate(BTOProjectDTO btoProjectDTO){
        Map<FlatType, Integer> flatNum = btoProjectDTO.getFlatNum();
        Map<FlatType, Integer> flatPrice = btoProjectDTO.getFlatPrice();

        for(FlatType flatType:FlatType.values()){
            if(flatNum.get(flatType) < 0){
                throw new BTOProjectException("Number of %s cannot be negative.".formatted(flatType.getStoredString()));
            }
            
            if(flatPrice.get(flatType) < 0){
                throw new BTOProjectException("Price of %s cannot be negative.".formatted(flatType.getStoredString()));
            }
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

    public int getFlatNum(FlatType flatType) {
        return flatNum.getOrDefault(flatType, 0);
    }

    public int getFlatPrice(FlatType flatType) {
        return flatPrice.getOrDefault(flatType, 0);
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

    public List<User> getHDBOfficers() {
        return HDBOfficers;
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
                """, name, neighborhood, flatNum.get(FlatType.TWO_ROOM_FLAT), flatPrice.get(FlatType.TWO_ROOM_FLAT), flatNum.get(FlatType.THREE_ROOM_FLAT), flatPrice.get(FlatType.THREE_ROOM_FLAT), openingDate, closingDate, HDBOfficerLimit, visible ? "Visible" : "Hidden");
    }

    public boolean isActive(){
        return isOverlappingWith(LocalDate.now(), LocalDate.now()) && visible;
    }

    public boolean isOverlappingWith(LocalDate openingDate, LocalDate closingDate){
        return !(openingDate.isAfter(this.closingDate) || closingDate.isBefore(this.openingDate));
    }

    public void addHDBOfficer(User HDBOfficer){
        if(HDBOfficer.getUserRole() != UserRole.HDB_OFFICER){
            throw new BTOProjectException("User added is not HDB Officer.");
        }

        if(HDBOfficers.size() >= HDBOfficerLimit){
            throw new BTOProjectException("Number of HDB Officers exceed limit (%d).".formatted(MAX_HDB_OFFICER_LIMIT));
        }

        HDBOfficers.add(HDBOfficer);
    }

    private static class Memento {
        private final String neighborhood;
        private final Map<FlatType, Integer> flatNum;
        private final Map<FlatType, Integer> flatPrice;
        private final LocalDate openingDate;
        private final LocalDate closingDate;
        private final int HDBOfficerLimit;

        private Memento(BTOProject btoProject) {
            this.neighborhood = btoProject.neighborhood;
            this.flatNum = new HashMap<>(btoProject.flatNum);
            this.flatPrice = new HashMap<>(btoProject.flatPrice);
            this.openingDate = btoProject.openingDate;
            this.closingDate = btoProject.closingDate;
            this.HDBOfficerLimit = btoProject.HDBOfficerLimit;
        }

        private void restore(BTOProject btoProject) {
            btoProject.neighborhood = this.neighborhood;
            btoProject.flatNum = new HashMap<>(this.flatNum);
            btoProject.flatPrice = new HashMap<>(this.flatPrice);
            btoProject.openingDate = this.openingDate;
            btoProject.closingDate = this.closingDate;
            btoProject.HDBOfficerLimit = this.HDBOfficerLimit;
        }
    }
}
