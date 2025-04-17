package model;

public interface DataModel {
    String getPK();
    void backup();
    void restore();
}