package relationship.resolver;

import manager.interfaces.DataManager;


/**
 * Interface for resolving and restoring relationships between data models after they are loaded.
 * 
 * <p>This interface is typically implemented by classes that handle complex relationships
 * between different models that are not restored automatically during deserialization or
 * CSV-based loading. It is especially useful when relationships involve references between 
 * objects (e.g., foreign keys or composition).</p>
 *
 * <p>For example, in CSVDataManager, resolveLoad() loads and parses all CSV data into memory and resolves object relationships.</p>
 *
 * @see CSVDataManager
 */
public interface LoadResolver {

    /**
     * Restores the in-memory object relationships after data has been loaded from persistent storage.
     *
     * @param dataManager the central manager containing all loaded data collections
     */
    public void resolveLoad(DataManager dataManager);
}
