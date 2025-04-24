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

    /**
     * Returns the name of the BTO project.
     * 
     * @return the project name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the neighborhood in which the BTO project is located.
     * 
     * @return the neighborhood name
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Sets the flat unit data for this project.
     * 
     * @param flatUnits the map of flat types to corresponding flat units
     */
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

    /**
     * Retrieves the {@link FlatUnit} associated with the specified flat type.
     *
     * @param flatType the flat type to retrieve
     * @return the {@link FlatUnit} corresponding to the specified flat type
     * @throws DataModelException if the flat type is not found
     */
    private FlatUnit getFlatUnit(FlatType flatType) throws DataModelException {
        FlatUnit flatUnit = flatUnits.get(flatType);
        if(flatUnit == null){
            throw new DataModelException("Flat type %s not found".formatted(flatType.getStoredString()));
        }
        return flatUnit;
    }

    /**
     * Returns an unmodifiable list of all flat units in the project.
     *
     * @return a {@link List} containing all {@link FlatUnit} objects
     */
    public List<FlatUnit> getFlatUnits() {
        return List.copyOf(flatUnits.values());
    }

    /**
     * Retrieves the number of available flats for a specific flat type.
     *
     * @param flatType the flat type to check
     * @return the number of available units of the given type, or {@code 0} if not found
     */
    public int getFlatNum(FlatType flatType) {
        FlatUnit flatUnit = flatUnits.get(flatType);
        return flatUnit == null ? 0 : flatUnit.getFlatNum();
    }

    /**
     * Retrieves the price of flats for a specific flat type.
     *
     * @param flatType the flat type to check
     * @return the price of the flat type, or {@code 0} if not found
     */
    public int getFlatPrice(FlatType flatType) {
        FlatUnit flatUnit = flatUnits.get(flatType);
        return flatUnit == null ? 0 : flatUnit.getFlatPrice();
    }

    /**
     * Books one unit of the specified flat type by decrementing its available count.
     *
     * @param flatType the type of flat to book
     * @throws DataModelException if the specified flat type does not exist or is unavailable
     */
    public void bookFlat(FlatType flatType) throws DataModelException{
        FlatUnit flatUnit = getFlatUnit(flatType);

        flatUnit.adjustFlatNum(-1);
    }

    /**
     * Cancels a flat booking by incrementing the available count for the specified flat type.
     *
     * @param flatType the type of flat to unbook
     * @throws DataModelException if the specified flat type does not exist
     */
    public void unbookFlat(FlatType flatType) throws DataModelException{
        FlatUnit flatUnit = getFlatUnit(flatType);

        flatUnit.adjustFlatNum(1);
    }

    /**
     * Checks whether any units of the specified flat type are available for booking.
     *
     * @param flatType the flat type to check for availability
     * @return {@code true} if there is at least one available unit of the specified type; {@code false} otherwise
     */
    public boolean hasAvailableFlats(FlatType flatType){
        return getFlatNum(flatType) > 0;
    }

    /**
     * Returns the application opening date of the project.
     * 
     * @return the opening date
     */
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    /**
     * Returns the application closing date of the project.
     * 
     * @return the closing date
     */
    public LocalDate getClosingDate() {
        return closingDate;
    }

    /**
     * Returns whether the project is visible to the public.
     * 
     * @return {@code true} if visible, otherwise {@code false}
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Returns whether the project is visible to the public.
     * 
     * @return {@code true} if visible, otherwise {@code false}
     */
    public void toggleVisibility(){
        visible = visible ? false : true;
    }

    /** @return true if the project is currently active and visible. */
    public boolean isActive(){
        return isOverlappingWith(LocalDate.now(), LocalDate.now()) && visible;
    }

    /**
     * Checks whether this BTO project's application period overlaps with another BTO project's period.
     *
     * @param btoProject the other BTO project to compare with
     * @return {@code true} if the two projects' application periods overlap; {@code false} otherwise
     */
    public boolean isOverlappingWith(BTOProject btoProject){
        return isOverlappingWith(btoProject.getOpeningDate(), btoProject.getClosingDate());
    }

    /**
     * Checks whether this BTO project's application period overlaps with a specified date range.
     *
     * @param openingDate the start date of the range to check
     * @param closingDate the end date of the range to check
     * @return {@code true} if the specified date range overlaps with this project's period; {@code false} otherwise
     */
    public boolean isOverlappingWith(LocalDate openingDate, LocalDate closingDate){
        return !(openingDate.isAfter(this.closingDate) || closingDate.isBefore(this.openingDate));
    }

    /**
     * Returns the HDB Manager in charge of the project.
     * 
     * @return the HDB Manager
     */
    public User getHDBManager() {
        return HDBManager;
    }

    /**
     * Returns a list of HDB Officers assigned to the project.
     * 
     * @return list of HDB Officers
     */
    public List<User> getHDBOfficers() {
        return HDBOfficers;
    }

    /**
     * Adds a HDB Officer to the list of officers managing this BTO project.
     * <p>
     * This method performs the following validations before adding:
     * <ul>
     *   <li>Ensures the user has the role {@code HDB_OFFICER}</li>
     *   <li>Ensures that adding the user does not exceed the officer limit</li>
     * </ul>
     * </p>
     *
     * @param HDBOfficer the user to be added as a HDB Officer
     * @throws DataModelException if the user is not an HDB Officer or if the officer limit is exceeded
     */
    public void addHDBOfficer(User HDBOfficer) throws DataModelException{
        if(HDBOfficer.getUserRole() != UserRole.HDB_OFFICER){
            throw new DataModelException("User added is not HDB Officer.");
        }

        if(isExceedingHDBOfficerLimit()){
            throw new DataModelException("Number of HDB Officers exceed limit (%d).".formatted(MAX_HDB_OFFICER_LIMIT));
        }

        HDBOfficers.add(HDBOfficer);
    }

    /**
     * Returns the officer limit for the project.
     * 
     * @return the maximum number of HDB Officers allowed
     */
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

    /**
     * Checks whether a given user is eligible to apply for any available flat in this BTO project.
     * <p>
     * A user can apply if there is at least one flat type that:
     * <ul>
     *   <li>Has available units (i.e., the number of units > 0)</li>
     *   <li>The user is eligible for, according to the flat type's eligibility rules</li>
     * </ul>
     * </p>
     *
     * @param user the user to check application eligibility for
     * @return {@code true} if the user is eligible to apply for at least one available flat type;
     *         {@code false} otherwise
     */
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

    /**
     * Creates a backup of the current state of the BTOProject.
     * <p>
     * This method uses the Memento design pattern to capture a snapshot
     * of the project's editable fields, such as neighborhood, application period,
     * officer limit, and flat unit states. This backup can later be restored
     * using {@link #restore()} in case an edit needs to be reverted.
     * </p>
     */
    @Override
    public void backup(){
        memento = new Memento(this);
    }

    /**
     * Restores the BTOProject's state from the previously created backup.
     * <p>
     * This reverts any changes made since the last {@link #backup()} call.
     * If no backup exists, this method does nothing.
     * </p>
     */
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
