package manager.interfaces;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import exception.DataSavingException;
import model.DataModel;

/**
 * Interface for managing data access and operations on various types of {@link DataModel}.
 * Provides generic CRUD (Create, Read, Update, Delete - 4 basic operations a software system should perform)
 * and query operations for any model extending {@code DataModel}.
 */
public interface DataManager {

    /**
     * Retrieves all records of a given data model type.
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
     * A predicate in Java is basically a function that takes in a value and returns true or false — it's used to test or filter values.
     * Predicates are great for filtering, matching, and validating data. 
     * In our DataManager interface, we're using predicates to query/filter data models:
     * <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> predicate);
     * - This means the method will return only the data models that pass the test defined by the predicate.
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
     * Saves a new or updated data model to the storage.
     *
     * @param <T>   the type of data model
     * @param model the data model to be saved
     * @throws DataSavingException if saving fails due to I/O or logic error
     */
    <T extends DataModel> void save(T model) throws DataSavingException;

    /**
     * Deletes a specific data model from storage.
     *
     * @param <T>   the type of data model
     * @param model the data model to delete
     * @throws DataSavingException if deletion fails due to saving issues
     * @throws Exception if the model cannot be deleted due to dependency or logic issues
     */
    <T extends DataModel> void delete(T model) throws DataSavingException, Exception;
}
