package model;

import java.util.UUID;

import config.FlatType;

public class FlatUnit implements DataModel{
    @CSVField(index = 0)
    private String uuid;

    @CSVField(index = 1, foreignKey = true)
    private BTOProject btoProject;

    @CSVField(index = 2)
    private FlatType flatType;

    @CSVField(index = 3)
    private int flatNum;

    @CSVField(index = 4)
    private int flatPrice;

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
        this.flatNum = flatNum;
    }

    public int getFlatPrice() {
        return flatPrice;
    }

    public void setFlatPrice(int flatPrice) {
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
}
