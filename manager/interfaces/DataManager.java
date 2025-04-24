package manager.interfaces;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import exception.DataSavingException;
import model.DataModel;

/**
 * Interface for managing data access and operations on various types of {@link DataModel}.
 * <p>
 * Inspired by database systems, this manager operates on in-memory data that is initially loaded from files.
 * It provides generic CRUD (Create, Read, Update, Delete) functionality, along with database-like query operations.
 * Each {@code DataModel} is uniquely identified by a primary key and stored in a map for efficient lookup.
 * </p>
 * <p>
 * Each {@code Class<T>} acts as a logical representation of a table name,
 * associating a specific data type with its corresponding storage or dataset.
 * </p>
 * <p>
 * Queries are supported via {@code Predicate} filters, enabling flexible and expressive
 * data retrieval using the Stream API. This allows operations such as:
 * </p>
 * <ul>
 *   <li>{@link #getByPK(String PK)} — Retrieve a specific model by its primary key.</li>
 *   <li>{@link #getByQuery(Class, Predicate)} — Retrieve a list of models matching a query.</li>
 *   <li>{@link #countByQuery(Class, Predicate)} — Count the number of matching records.</li>
 * </ul>
 * <p>
 * Sorting are supported via {@code Comparator}, enabling flexible order for different query
 * </p>
 * <p>
 * Data edited or deleted through the manager can be persisted back to file, supporting a file-based persistence model.
 * 
 * @param <T> the type of the {@code DataModel}
 * 
 * @see DataModel
 */
public interface DataManager {
    /**
     * Retrieves all records of a given {@link DataModel} type.
     *
     * @param <T>   the type of data model
     * @param clazz the class object of the data model
     * @return a list of all records of the specified type
     */
    <T extends DataModel> List<T> getAll(Class<T> clazz);

    /**
     * Retrieves all records of a given data model type, sorted using a comparator.
     *
     * @param <T>        the type of data model
     * @param clazz      the class object of the data model
     * @param comparator the comparator used to sort the records
     * @return a sorted list of all records of the specified type
     */
    <T extends DataModel> List<T> getAll(Class<T> clazz, Comparator<T> comparator);

    /**
     * Retrieves a specific record by its primary key.
     *
     * @param <T>   the type of data model
     * @param clazz the class object of the data model
     * @param PK    the primary key of the record
     * @return the record with the matching primary key, or {@code null} if not found
     */
    <T extends DataModel> T getByPK(Class<T> clazz, String PK);

    /**
     * Retrieves records that match a specific query condition.
     *
     * @param <T>      the type of data model
     * @param clazz    the class object of the data model
     * @param predicate a predicate that defines the query condition
     * @return a list of records matching the condition
     */
    <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> predicate);

    /**
     * Retrieves records that match a specific query condition and sorts them.
     *
     * @param <T>        the type of data model
     * @param clazz      the class object of the data model
     * @param predicate  a predicate that defines the query condition
     * @param comparator a comparator used to sort the filtered records
     * @return a sorted list of records matching the condition
     */
    <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> predicate, Comparator<T> comparator);

    /**
     * Retrieves records that match all given query conditions.
     *
     * @param <T>        the type of data model
     * @param clazz      the class object of the data model
     * @param predicates a list of predicates that define the query conditions
     * @return a list of records matching all conditions
     */
    <T extends DataModel> List<T> getByQueries(Class<T> clazz, List<Predicate<T>> predicates);

    /**
     * Retrieves records that match all given query conditions and sorts them.
     *
     * @param <T>        the type of data model
     * @param clazz      the class object of the data model
     * @param predicates a list of predicates that define the query conditions
     * @param comparator a comparator used to sort the filtered records
     * @return a sorted list of records matching all conditions
     */
    <T extends DataModel> List<T> getByQueries(Class<T> clazz, List<Predicate<T>> predicates, Comparator<T> comparator);

    /**
     * Counts records that match a specific query condition.
     *
     * @param <T>      the type of data model
     * @param clazz    the class object of the data model
     * @param predicate a predicate that defines the query condition
     * @return a number of records matching the condition
     */
    <T extends DataModel> long countByQuery(Class<T> clazz, Predicate<T> predicate);

    /**
     * Counts records that match all given query conditions.
     *
     * @param <T>      the type of data model
     * @param clazz    the class object of the data model
     * @param predicate a predicate that defines the query condition
     * @return a number of records matching the condition
     */
    <T extends DataModel> long countByQueries(Class<T> clazz, List<Predicate<T>> predicates);

    /**
     * Saves a new or updated data model to the storage.
     *
     * @param <T>   the type of data model
     * @param model the data model to be saved
     * @throws DataSavingException if saving fails due to I/O or other error
     */
    <T extends DataModel> void save(T model) throws DataSavingException;

    /**
     * Deletes a specific data model from storage.
     *
     * @param <T>   the type of data model
     * @param model the data model to delete
     * @throws DataSavingException if deletion fails due to saving issues
     * @throws Exception if the model cannot be deleted due to I/O or other error
     */
    <T extends DataModel> void delete(T model) throws DataSavingException;
}
