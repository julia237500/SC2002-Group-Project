package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.FlatType;
import config.UserRole;
import dto.BTOProjectDTO;
import exception.DataModelException;

public class BTOProject implements DataModel{
    public static final Comparator<BTOProject> DEFAULT_COMPARATOR = 
        Comparator.comparing(BTOProject::isActive, Comparator.reverseOrder())
        .thenComparing(BTOProject::getOpeningDate);

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

    @SuppressWarnings("unused")
    private BTOProject(){}

    public BTOProject(User HDBManager, String name, String neighborhood, LocalDate openingDate, LocalDate closingDate, int HDBOfficerLimit){
        this.HDBManager = HDBManager;
        this.name = name;
        this.neighborhood = neighborhood;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.HDBOfficerLimit = HDBOfficerLimit;
    }
    
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

    public void edit(BTOProjectDTO btoProjectDTO){
        validate(btoProjectDTO);
        if(btoProjectDTO.getHDBOfficerLimit() < HDBOfficers.size()){
            throw new DataModelException("New number of HDB Officers cannot be smaller than current number of HDB Officers in charge (%d)".formatted(HDBOfficers.size()));
        }

        memento = new Memento(this);

        this.neighborhood = btoProjectDTO.getNeighborhood();
        this.openingDate = btoProjectDTO.getOpeningDate();
        this.closingDate = btoProjectDTO.getClosingDate();
        this.HDBOfficerLimit = btoProjectDTO.getHDBOfficerLimit();

        this.changeFlatUnits(btoProjectDTO.getFlatNum(), btoProjectDTO.getFlatPrice());
    }

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

    public String getName() {
        return name;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setFlatUnits(Map<FlatType, FlatUnit> flatUnits){
        this.flatUnits = flatUnits;
    }

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

    private FlatUnit getFlatUnit(FlatType flatType) {
        FlatUnit flatUnit = flatUnits.get(flatType);
        if(flatUnit == null){
            throw new DataModelException("Flat type %s not found".formatted(flatType.getStoredString()));
        }
        return flatUnit;
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

    public void bookFlat(FlatType flatType){
        FlatUnit flatUnit = getFlatUnit(flatType);

        try {
            flatUnit.adjustFlatNum(-1);
        } catch (DataModelException e) {
            throw new DataModelException("Flat type %s is not available.".formatted(flatType.getStoredString()));
        }
    }

    public void unbookFlat(FlatType flatType){
        FlatUnit flatUnit = getFlatUnit(flatType);

        flatUnit.adjustFlatNum(1);
    }

    public boolean hasAvailableFlats(FlatType flatType){
        return getFlatNum(flatType) > 0;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public boolean isVisible() {
        return visible;
    }

    public void toggleVisibility(){
        visible = visible ? false : true;
    }

    public boolean isActive(){
        return isOverlappingWith(LocalDate.now(), LocalDate.now()) && visible;
    }

    public boolean isOverlappingWith(BTOProject btoProject){
        return isOverlappingWith(btoProject.getOpeningDate(), btoProject.getClosingDate());
    }

    public boolean isOverlappingWith(LocalDate openingDate, LocalDate closingDate){
        return !(openingDate.isAfter(this.closingDate) || closingDate.isBefore(this.openingDate));
    }

    public User getHDBManager() {
        return HDBManager;
    }
    
    public List<User> getHDBOfficers() {
        return HDBOfficers;
    }

    public void addHDBOfficer(User HDBOfficer){
        if(HDBOfficer.getUserRole() != UserRole.HDB_OFFICER){
            throw new DataModelException("User added is not HDB Officer.");
        }

        if(isExceedingHDBOfficerLimit()){
            throw new DataModelException("Number of HDB Officers exceed limit (%d).".formatted(MAX_HDB_OFFICER_LIMIT));
        }

        HDBOfficers.add(HDBOfficer);
    }

    public int getHDBOfficerLimit() {
        return HDBOfficerLimit;
    }

    public boolean isExceedingHDBOfficerLimit(){
        return HDBOfficers.size() >= HDBOfficerLimit;
    }

    public boolean isHandlingBy(User user){
        if(user.getUserRole() == UserRole.HDB_MANAGER){
            return user == HDBManager;
        }
        else if(user.getUserRole() == UserRole.HDB_OFFICER){
            return HDBOfficers.contains(user);
        }
        
        return false;
    }

    @Override
    public String getPK() {
        return name;
    }

    @Override
    public void backup(){
        memento = new Memento(this);
    }

    @Override
    public void restore(){
        if(memento != null){
            memento.restore(this);
        }
    }

    private static class Memento {
        private final String neighborhood;
        private final LocalDate openingDate;
        private final LocalDate closingDate;
        private final int HDBOfficerLimit;

        private Memento(BTOProject btoProject) {
            this.neighborhood = btoProject.neighborhood;
            this.openingDate = btoProject.openingDate;
            this.closingDate = btoProject.closingDate;
            this.HDBOfficerLimit = btoProject.HDBOfficerLimit;

            for(FlatUnit flatUnit:btoProject.getFlatUnits()){
                flatUnit.backup();
            }
        }

        private void restore(BTOProject btoProject) {
            btoProject.neighborhood = this.neighborhood;
            btoProject.openingDate = this.openingDate;
            btoProject.closingDate = this.closingDate;
            btoProject.HDBOfficerLimit = this.HDBOfficerLimit;

            for(FlatUnit flatUnit:btoProject.getFlatUnits()){
                flatUnit.backup();
            }
        }
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

    public boolean canBeAppliedBy(User user){
        for(FlatType flatType:FlatType.values()){
            if(hasAvailableFlats(flatType) && flatType.isEligible(user)){
                return true;
            }
        }

        return false;
    }
}
