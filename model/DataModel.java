package model;

/**
 * Implemented by other model classes 
 * Represents a generic data model with a primary key.
 * This interface is implemented by classes that represent
 * persistent data entities (e.g., applications, users, projects).
 * It provides a method to retrieve the unique identifier (primary key)
 * for each instance of the model.
 */
public interface DataModel {

    /**
     * Returns the primary key (unique identifier) of the data model instance.
     *
     * @return a {@code String} representing the primary key
     */
    String getPK();
    void backup();
    void restore();
}