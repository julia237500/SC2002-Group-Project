package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.FlatType;
import config.UserRole;
import dto.BTOProjectDTO;
import exception.DataModelException;

/**
 * Represents a Build-To-Order (BTO) project managed by HDB.
 * Stores details such as project name, neighborhood, flat types available,
 * application periods, assigned HDB officers, and visibility.
 * Supports validation, editing, and state management through Memento.
 */
public class BTOProject implements DataModel{
    public static int MIN_HDB_OFFICER_LIMIT = 1;
    public static int MAX_HDB_OFFICER_LIMIT = 10;

    @CSVField(index = 0)
    private String name;
    @CSVField(index = 1)
    private String neighborhood;

    private Map<FlatType, FlatUnit> flatUnits = new HashMap<>();

    @CSVField(index = 2)
    private LocalDate openingDate;
    @CSVField(index = 3)
    private LocalDate closingDate;
    
    @CSVField(index = 4, foreignKey = true)
    private User HDBManager;

    @CSVField(index = 5)
    private int HDBOfficerLimit;
    private final List<User> HDBOfficers = new ArrayList<>();

    @CSVField(index = 6)
    private boolean visible = false;

    private Memento memento;

    /**
     * Required for reflective instantiation in CSVDataManager.
     */
    @SuppressWarnings("unused")
    private BTOProject(){}

    /**
     * Constructs a BTOProject manually via parameters.
     * 
     * @param HDBManager       the HDB manager assigned to this project
     * @param name             name of the project
     * @param neighborhood     neighborhood location of the project
     * @param openingDate      application opening date
     * @param closingDate      application closing date
     * @param HDBOfficerLimit  max number of officers allowed
     */
    public BTOProject(User HDBManager, String name, String neighborhood, LocalDate openingDate, LocalDate closingDate, int HDBOfficerLimit){
        this.HDBManager = HDBManager;
        this.name = name;
        this.neighborhood = neighborhood;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.HDBOfficerLimit = HDBOfficerLimit;
    }
    
    /**
     * Factory method to create a BTOProject from a DTO.
     * 
     * @param HBDManager      HDB Manager initiating the project
     * @param btoProjectDTO   Data transfer object containing project details
     * @return                a constructed BTOProject instance
     */
    public static BTOProject fromDTO(User HBDManager, BTOProjectDTO btoProjectDTO){
        if(HBDManager.getUserRole() != UserRole.HDB_MANAGER){
            throw new DataModelException("Access Denied. Only HDB Manager can open new project.");
        }

        if(btoProjectDTO.getClosingDate().isBefore(LocalDate.now())){
            throw new DataModelException("Closing date cannot be past.");
        }
        
        validate(btoProjectDTO);

        BTOProject btoProject = new BTOProject(
            HBDManager,
            btoProjectDTO.getName(), 
            btoProjectDTO.getNeighborhood(), 
            btoProjectDTO.getOpeningDate(), 
            btoProjectDTO.getClosingDate(),
            btoProjectDTO.getHDBOfficerLimit()
        );

        btoProject.changeFlatUnits(btoProjectDTO.getFlatNum(), btoProjectDTO.getFlatPrice());

        return btoProject;
    }

    @Override
    /**
     * Returns the primary key for this data model (project name).
     */
    public String getPK() {
        return name;
    }

    /**
     * Edits the current BTOProject using data from a DTO.
     * Preserves the current state in a Memento before changes.
     * 
     * @param btoProjectDTO updated project details
     */
    public void edit(BTOProjectDTO btoProjectDTO){
        validate(btoProjectDTO);
        if(btoProjectDTO.getHDBOfficerLimit() < HDBOfficers.size()){
            throw new DataModelException("New number of HDB Officers cannot be smaller than current number of HDB Officers in charge (%d)".formatted(HDBOfficers.size()));
        }

        memento = new Memento(this); // Save current state before editing

        this.neighborhood = btoProjectDTO.getNeighborhood();
        this.openingDate = btoProjectDTO.getOpeningDate();
        this.closingDate = btoProjectDTO.getClosingDate();
        this.HDBOfficerLimit = btoProjectDTO.getHDBOfficerLimit();

        this.changeFlatUnits(btoProjectDTO.getFlatNum(), btoProjectDTO.getFlatPrice());
    }

    public void revertEdit(){
        if(memento != null){
            memento.restore(this); // Restore the previous state
        }
    }

    /**
     * Validates a BTOProjectDTO for logical consistency and constraints.
     */
    private static void validate(BTOProjectDTO btoProjectDTO){
        Map<FlatType, Integer> flatNum = btoProjectDTO.getFlatNum();
        Map<FlatType, Integer> flatPrice = btoProjectDTO.getFlatPrice();

        for(FlatType flatType:FlatType.values()){
            if(flatNum.get(flatType) < 0){
                throw new DataModelException("Number of %s cannot be negative.".formatted(flatType.getStoredString()));
            }
            
            if(flatPrice.get(flatType) < 0){
                throw new DataModelException("Price of %s cannot be negative.".formatted(flatType.getStoredString()));
            }
        }

        if(btoProjectDTO.getClosingDate().isBefore(btoProjectDTO.getOpeningDate())){
            throw new DataModelException("Closing date cannot be before opening date.");
        }

        if(btoProjectDTO.getHDBOfficerLimit() < 0 || btoProjectDTO.getHDBOfficerLimit() > BTOProject.MAX_HDB_OFFICER_LIMIT){
            throw new DataModelException("Number of HDB Officers must be between 0 - %d".formatted(MAX_HDB_OFFICER_LIMIT));
        }
    }

    /** @return true if the project is visible to users */
    public boolean isVisible() {
        return visible;
    }

    /** Toggles project visibility. */
    public void toggleVisibility(){
        visible = visible ? false : true;
    }

    /**
     * Getters and setters
     */
    public String getName() {
        return name;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setFlatUnits(Map<FlatType, FlatUnit> flatUnits){
        this.flatUnits = flatUnits;
    }

    /**
     * Updates or creates FlatUnits with new quantity and price data.
     */
    public void changeFlatUnits(Map<FlatType, Integer> flatNums, Map<FlatType, Integer> flatPrices){
        for(FlatType flatType:FlatType.values()){
            FlatUnit flatUnit = flatUnits.get(flatType);
            int flatNum = flatNums.getOrDefault(flatType, 0);
            int flatPrice = flatPrices.getOrDefault(flatType, 0);

            if(flatUnit == null){
                flatUnit = new FlatUnit(this, flatType, flatNum, flatPrice);
                flatUnits.put(flatType, flatUnit);
            }
            else{
                flatUnit.setFlatNum(flatNum);
                flatUnit.setFlatPrice(flatPrice);
            }
        }
    }

    public List<FlatUnit> getFlatUnits() {
        return List.copyOf(flatUnits.values());
    }

    public int getFlatNum(FlatType flatType) {
        FlatUnit flatUnit = flatUnits.get(flatType);
        return flatUnit == null ? 0 : flatUnit.getFlatNum();
    }

    public int getFlatPrice(FlatType flatType) {
        FlatUnit flatUnit = flatUnits.get(flatType);
        return flatUnit == null ? 0 : flatUnit.getFlatPrice();
    }

    public boolean hasAvailableFlats(FlatType flatType){
        return getFlatNum(flatType) > 0;
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
                """, name, neighborhood, getFlatNum(FlatType.TWO_ROOM_FLAT), getFlatPrice(FlatType.TWO_ROOM_FLAT), getFlatNum(FlatType.THREE_ROOM_FLAT), getFlatPrice(FlatType.THREE_ROOM_FLAT), openingDate, closingDate, HDBOfficerLimit, visible ? "Visible" : "Hidden");
    }

    /** @return true if the project is currently active and visible. */
    public boolean isActive(){
        return isOverlappingWith(LocalDate.now(), LocalDate.now()) && visible;
    }

    public boolean isOverlappingWith(BTOProject btoProject){
        return isOverlappingWith(btoProject.getOpeningDate(), btoProject.getClosingDate());
    }

    public boolean isOverlappingWith(LocalDate openingDate, LocalDate closingDate){
        return !(openingDate.isAfter(this.closingDate) || closingDate.isBefore(this.openingDate));
    }
    
    /**
     * Adds an HDB Officer to the project, ensuring constraints are met.
     */
    public void addHDBOfficer(User HDBOfficer){
        if(HDBOfficer.getUserRole() != UserRole.HDB_OFFICER){
            throw new DataModelException("User added is not HDB Officer.");
        }

        if(isExceedingHDBOfficerLimit()){
            throw new DataModelException("Number of HDB Officers exceed limit (%d).".formatted(MAX_HDB_OFFICER_LIMIT));
        }

        HDBOfficers.add(HDBOfficer);
    }

    /** @return true if the number of officers exceeds the limit. */
    public boolean isExceedingHDBOfficerLimit(){
        return HDBOfficers.size() >= HDBOfficerLimit;
    }

    /**
     * Checks if a user is involved in managing this project.
     */
    public boolean isHandlingBy(User user){
        if(user.getUserRole() == UserRole.HDB_MANAGER){
            return user == HDBManager;
        }
        else if(user.getUserRole() == UserRole.HDB_OFFICER){
            return HDBOfficers.contains(user);
        }
        
        return false;
    }

    /**
     * Memento class for storing a backup copy of BTOProject state during edits.
     */
    /**
     * A Memento is a design pattern used in OOP to capture and restore an object's state 
     * without exposing its internal structure. 
     * It is especially useful for implementing features like undo/redo, rollback, or state history.
     * In Simple Terms, a memento stores a snapshot of an object’s state so that the object can be restored to that state later.
     * This is the inner class: private static class Memento 
     * And it’s used here: memento = new Memento(this);  // Save current state before editing
     * Then later, we do: memento.restore(this);  // Restore the previous state
     * This allows our BTOProject object to:
     * - Save a version of itself before being edited
     * - Restore that version if something goes wrong or if the user wants to cancel the edit
     * In our Memento class, we're storing:
     * - neighborhood
     * - openingDate and closingDate
     * - HDBOfficerLimit
     * - Number and price of flats (for each FlatType)
     * So, if someone edits a BTO project and changes the flat prices, officer limit, or dates
     * but later decides to revert, we can roll everything back cleanly using the memento.
     * Maintains encapsulation (internal state isn't exposed)
     * Keeps code clean and modular
     * Drawbacks of using mementos:
     * - Can increase memory usage (especially if many states are saved)
     * - If the object's state is large, copying might be expensive
     * - Can lead to complexity if not managed properly (e.g., when to discard old mementos?)
     * 
     * Memento is an inner class, private and only usable by the originator: private static class Memento {...}
     * - This encapsulation is key to the pattern: no external class can mess with the stored state.
     */
    private static class Memento {
        private final String neighborhood;
        private final Map<FlatType, Integer> flatNum;
        private final Map<FlatType, Integer> flatPrice;
        private final LocalDate openingDate;
        private final LocalDate closingDate;
        private final int HDBOfficerLimit;

        private Memento(BTOProject btoProject) {
            this.neighborhood = btoProject.neighborhood;
            this.openingDate = btoProject.openingDate;
            this.closingDate = btoProject.closingDate;
            this.HDBOfficerLimit = btoProject.HDBOfficerLimit;

            this.flatNum = new HashMap<>();
            this.flatPrice = new HashMap<>();

            for(FlatType flatType:FlatType.values()){
                FlatUnit flatUnit = btoProject.flatUnits.get(flatType);

                if(flatUnit == null){
                    flatNum.put(flatType, 0);
                    flatPrice.put(flatType, 0);
                }
                else{
                    flatNum.put(flatType, flatUnit.getFlatNum());
                    flatPrice.put(flatType, flatUnit.getFlatPrice());
                }
            }
        }

        private void restore(BTOProject btoProject) {
            btoProject.neighborhood = this.neighborhood;
            btoProject.openingDate = this.openingDate;
            btoProject.closingDate = this.closingDate;
            btoProject.HDBOfficerLimit = this.HDBOfficerLimit;

            btoProject.changeFlatUnits(flatNum, flatPrice);
        }
    }
}
