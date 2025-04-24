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
import manager.CSVDataManager;

/**
 * Represents a Build-To-Order (BTO) project managed by HDB.
 * Stores details such as project name, neighborhood, flat types available,
 * application periods, assigned HDB officers, and visibility.
 * <p>
 * In addition to its data, this class encapsulates business logic related to the
 * application process, adhering to the principles of a rich domain model. 
 * It ensures that the application state and behaviors are consistent with the 
 * domain rules, and manipulates its data through methods that enforce business 
 * rules rather than relying solely on external procedures.
 */
public class BTOProject implements DataModel{
    /**
     * Default comparator for sorting BTO projects.
     * Projects are sorted with active ones first (descending by isActive),
     * and then alphabetically by project name.
     */
    public static final Comparator<BTOProject> DEFAULT_COMPARATOR = 
        Comparator.comparing(BTOProject::isActive, Comparator.reverseOrder())
                .thenComparing(BTOProject::getName);

    public static final int MIN_HDB_OFFICER_LIMIT = 1;
    public static final int MAX_HDB_OFFICER_LIMIT = 10;

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
     * Default no-argument constructor used exclusively for reflective instantiation.
     * This constructor is necessary for classes like {@link CSVDataManager} 
     * to create model objects via reflection.
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
     * Factory method to create a BTOProject from a {@link BTOProjectDTO}.
     * 
     * @param HBDManager      HDB Manager initiating the project
     * @param btoProjectDTO   Data transfer object containing project details
     * @return                a constructed BTOProject instance
     * @throws DataModelException if the creation fail due to invalid data, such as closing dat is before opening date
     * 
     * @see BTOProjectDTO
     */
    public static BTOProject fromDTO(User HBDManager, BTOProjectDTO btoProjectDTO) throws DataModelException{
        if(HBDManager.getUserRole() != UserRole.HDB_MANAGER){
            throw new DataModelException("Access Denied. Only HDB Manager can open new project.");
        }

        if(btoProjectDTO.getClosingDate().isBefore(LocalDate.now())){
            throw new DataModelException("Closing date cannot be past.");
        }
        
        validate(btoProjectDTO);

        final BTOProject btoProject = new BTOProject(
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

    /**
     * Updates the current BTOProject with values from the given DTO (Data Transfer Object).
     * The current state is backup and can be revert by {@link #restore()}.
     *
     * @param btoProjectDTO the DTO containing the updated project details
     * @throws DataModelException if any validation or update logic fails during the edit process
     */
    public void edit(BTOProjectDTO btoProjectDTO) throws DataModelException{
        validate(btoProjectDTO);
        if(btoProjectDTO.getHDBOfficerLimit() < HDBOfficers.size()){
            throw new DataModelException("New number of HDB Officers cannot be smaller than current number of HDB Officers in charge (%d)".formatted(HDBOfficers.size()));
        }

        backup();

        this.neighborhood = btoProjectDTO.getNeighborhood();
        this.openingDate = btoProjectDTO.getOpeningDate();
        this.closingDate = btoProjectDTO.getClosingDate();
        this.HDBOfficerLimit = btoProjectDTO.getHDBOfficerLimit();

        this.changeFlatUnits(btoProjectDTO.getFlatNum(), btoProjectDTO.getFlatPrice());
    }

    /**
     * Validates a BTOProjectDTO with domain rules such as closing date cannot be before opening date.
     * 
     * @throws DataModelException if validation fail
     */
    private static void validate(BTOProjectDTO btoProjectDTO) throws DataModelException{
        final Map<FlatType, Integer> flatNum = btoProjectDTO.getFlatNum();
        final Map<FlatType, Integer> flatPrice = btoProjectDTO.getFlatPrice();

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

    /**
     * Updates or creates FlatUnits with new quantity and price data.
     * @throws DataModelException 
     */
    public void changeFlatUnits(Map<FlatType, Integer> flatNums, Map<FlatType, Integer> flatPrices) throws DataModelException{
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

    private FlatUnit getFlatUnit(FlatType flatType) throws DataModelException {
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

    public void bookFlat(FlatType flatType) throws DataModelException{
        FlatUnit flatUnit = getFlatUnit(flatType);

        flatUnit.adjustFlatNum(-1);
    }

    public void unbookFlat(FlatType flatType) throws DataModelException{
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

    public User getHDBManager() {
        return HDBManager;
    }
    
    public List<User> getHDBOfficers() {
        return HDBOfficers;
    }

    public void addHDBOfficer(User HDBOfficer) throws DataModelException{
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

    public boolean canBeAppliedBy(User user){
        for(FlatType flatType:FlatType.values()){
            if(hasAvailableFlats(flatType) && flatType.isEligible(user)){
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the primary key for this data model (project name).
     */
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

    /**
     * Memento class for storing a backup copy of a BTOProject's state.
     * <p>
     * This inner class implements the Memento design pattern, allowing the 
     * BTOProject to save and later restore its internal state without violating 
     * encapsulation. It's used to support rollback functionality in case 
     * an edit operation needs to be undone.
     * </p>
     * <p>
     * The state captured includes:
     * - Neighborhood
     * - Opening and closing dates
     * - HDB officer limit
     * - State of all associated FlatUnits
     * </p>
     * <p>
     * <strong>Note</strong>: Memento is private and only accessible by BTOProject to preserve 
     * encapsulation and prevent external modification.
     * </p>
     */
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
}
