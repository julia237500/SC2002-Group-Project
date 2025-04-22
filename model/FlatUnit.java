package model;

import java.util.UUID;

import config.FlatType;
import exception.DataModelException;

/**
 * Represents a flat unit available under a BTO project.
 * <p>
 * Each unit has a unique identifier, is associated with a specific BTO project,
 * and has information such as its flat type, number, and price.
 * </p>
 *
 * <p>This class implements the {@link DataModel} interface to support generic data handling operations.</p>
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
     * Private no-args constructor for reflective instantiation.
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
     */
    public void setFlatNum(int flatNum) {
        if(flatNum < 0) {
            throw new DataModelException("Flat number cannot be negative.");
        }

        backup();
        this.flatNum = flatNum;
    }

    public void adjustFlatNum(int change) {
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
     */
    public void setFlatPrice(int flatPrice) {
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
     * Returns the primary key (UUID string) of this flat unit.
     *
     * @return the UUID of the flat unit
     */
    @Override
    public String getPK() {
        return uuid;
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
     * Returns a formatted string representation of this flat unit.
     *
     * @return a string containing UUID, project ID, flat number, and price
     */
    @Override
    public String toString() {
        return "%s, %s, %d, %d".formatted(
            uuid,
            btoProject.getPK(),
            flatNum,
            flatPrice
        );
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
