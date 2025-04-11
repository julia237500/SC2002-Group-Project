package manager.interfaces;

import java.util.List;
import java.util.function.Predicate;

import exception.DataSavingException;
import model.DataModel;

public interface DataManager {
    <T extends DataModel> List<T> getAll(Class<T> clazz);
    <T extends DataModel> T getByPK(Class<T> clazz, String PK);
    <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> predicate);
    <T extends DataModel> List<T> getByQueries(Class<T> clazz, List<Predicate<T>> predicates);
    <T extends DataModel> void save(T model) throws DataSavingException;
