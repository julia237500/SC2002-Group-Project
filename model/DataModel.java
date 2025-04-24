package model;

/**
 * Represents a generic persistent data entity with a unique primary key.
 * <p>
 * This interface is intended to be implemented by model classes such as
 * applications, users, or BTO projects that need to be serialized to and
 * deserialized from file storage, much like records in a database.
 * </p>
 * 
 * <p>
 * Each implementing class must define a primary key (PK) that uniquely
 * identifies an instance. The PK ensures data consistency and prevents
 * accidental overwriting of records during saving. It is recommended to use
 * a universally unique identifier (UUID) for the PK, unless a naturally
 * unique field like an NRIC is available.
 * </p>
 * 
 * <p>
 * Implementations must also support basic backup and restore mechanisms to
 * maintain object data integrity, allowing rollback in case of failure during
 * save operations.
 * </p>
 */
public interface DataModel {

    /**
     * Returns the primary key (unique identifier) of the data model instance.
     *
     * @return a {@code String} representing the primary key
     */
    String getPK();

    /**
     * Creates a backup of the current state of the model instance.
     * <p>
     * This should be called by model before modifying or saving the instance, to ensure
     * changes can be rolled back if necessary.
     * </p>
     */
    void backup();

    /**
     * Restores the model instance to its last backed-up state.
     * <p>
     * This is typically used to revert changes if a save operation fails,
     * preserving the integrity of the in-memory object.
     * </p>
     */
    void restore();
}