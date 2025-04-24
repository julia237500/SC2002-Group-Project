package model;

import java.util.UUID;

import config.FlatType;
import exception.DataModelException;
import manager.CSVDataManager;

/**
 * Represents a flat unit available under a BTO project.
 * <p>
 * Each unit has a unique identifier, is associated with a specific BTO project,
 * and has information such as its flat type, number, and price.
 * </p>
 *
 * In addition to its data, this class encapsulates business logic related to the
 * application process, adhering to the principles of a rich domain model. 
 * It ensures that the application state and behaviors are consistent with the 
 * domain rules, and manipulates its data through methods that enforce business 
 * rules rather than relying solely on external procedures.
 */
public class FlatUnit implements DataModel{
    @CSVField(index = 0)
    private String uuid;

    @CSVField(index = 1, foreignKey = true)
    private BTOProject btoProject;

    @CSVField(index = 2)
    private FlatType flatType;

    @CSVField(index = 3)
    private int flatNum;
    private int backupFlatNum;

    @CSVField(index = 4)
    private int flatPrice;
    private int backupFlatPrice;

    /**
     * Default no-argument constructor used exclusively for reflective instantiation.
     * This constructor is necessary for classes like {@link CSVDataManager} 
     * to create model objects via reflection.
     */
    @SuppressWarnings("unused")
    private FlatUnit() {}

    /**
     * Constructs a new FlatUnit with the given parameters.
     *
     * @param btoProject the BTO project this flat unit belongs to
     * @param flatType the type of the flat (e.g., 2-room, 3-room)
     * @param flatNum the number of the flat unit
     * @param flatPrice the price of the flat unit
     */
    public FlatUnit(BTOProject btoProject, FlatType flatType, int flatNum, int flatPrice) {
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
        
        this.btoProject = btoProject;
        this.flatType = flatType;
        this.flatNum = flatNum;
        this.flatPrice = flatPrice;
    }

    /**
     * Returns the number of this flat unit.
     *
     * @return the flat number
     */
    public int getFlatNum() {
        return flatNum;
    }

    /**
     * Updates the flat number.
     *
     * @param flatNum the new flat number
     * @throws DataModelException if flat number is negative
     */
    public void setFlatNum(int flatNum) throws DataModelException {
        if(flatNum < 0) {
            throw new DataModelException("Flat number cannot be negative.");
        }

        backup();
        this.flatNum = flatNum;
    }

    /**
     * Updates the flat number by adding a change.
     *
     * @param flatNum the change to add, can be negative
     * @throws DataModelException if new flat number is negative
     */
    public void adjustFlatNum(int change) throws DataModelException {
        if(flatNum + change < 0) {
            throw new DataModelException("Flat number cannot be negative.");
        }

        this.flatNum += change;
    }

    /**
     * Returns the price of the flat.
     *
     * @return the flat price
     */
    public int getFlatPrice() {
        return flatPrice;
    }

    /**
     * Updates the price of the flat.
     *
     * @param flatPrice the new flat price
     * @throws DataModelException if flat price is negative
     */
    public void setFlatPrice(int flatPrice) throws DataModelException {
        if(flatPrice < 0) {
            throw new DataModelException("Flat price cannot be negative.");
        }

        backup();
        this.flatPrice = flatPrice;
    }

    /**
     * Returns the flat type of this unit.
     *
     * @return the flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Returns the BTO project associated with this flat unit.
     *
     * @return the related BTOProject object
     */
    public BTOProject getBTOProject() {
        return btoProject;
    }

    /**
     * Returns the primary key (UUID string) of this flat unit.
     *
     * @return the UUID of the flat unit
     */
    @Override
    public String getPK() {
        return uuid;
    }

    public void backup(){
        this.backupFlatNum = this.flatNum;
        this.backupFlatPrice = this.flatPrice;
    }

    public void restore() {
        this.flatNum = this.backupFlatNum;
        this.flatPrice = this.backupFlatPrice;
    }
}
