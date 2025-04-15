package model;

import java.util.UUID;

import config.FlatType;
import exception.DataModelException;

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

    @SuppressWarnings("unused")
    private FlatUnit() {}

    public FlatUnit(BTOProject btoProject, FlatType flatType, int flatNum, int flatPrice) {
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
        
        this.btoProject = btoProject;
        this.flatType = flatType;
        this.flatNum = flatNum;
        this.flatPrice = flatPrice;
    }

    public int getFlatNum() {
        return flatNum;
    }

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

    public int getFlatPrice() {
        return flatPrice;
    }

    public void setFlatPrice(int flatPrice) {
        if(flatPrice < 0) {
            throw new DataModelException("Flat price cannot be negative.");
        }

        backup();
        this.flatPrice = flatPrice;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    @Override
    public String getPK() {
        return uuid;
    }

    public BTOProject getBTOProject() {
        return btoProject;
    }

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
