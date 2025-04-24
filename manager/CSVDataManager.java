package manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import exception.DataParsingException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.Application;
import model.BTOProject;
import model.CSVField;
import model.DataModel;
import model.Enquiry;
import model.FlatUnit;
import model.OfficerRegistration;
import model.User;
import parser.DataParser;
import relationship.*;
import relationship.resolver.*;
import util.CSVFileReader;
import util.CSVFileWriter;

/**
 * Default implementation of {@link DataManager} that operates on CSV files as the data source.
 * <p>
 * This class loads and persists {@link DataModel} instances using reflection and field-level annotations,
 * based on pre-configured CSV file paths. It automates object creation and field mapping from CSV data,
 * and supports primitive types and enums pre-config in {@link DataParser}.
 * </p>
 * 
 * <p>
 * It also supports single-object foreign key references. 
 * Foreign key fields referencing another {@code DataModel} are automatically resolved during data loading.
 * To support more advanced relationships, such as cascading operations, developers can register custom
 * {@link LoadResolver}, {@link SaveResolver}, and {@link DeleteResolver} implementations.
 * These resolvers allow the injection of custom behaviors for loading, saving, and deleting related models.
 * </p>
 *
 * @see DataManager
 * @see DataModel
 * @see DataParser
 * @see LoadResolver
 * @see SaveResolver
 * @see DeleteResolver
 */
public class CSVDataManager implements DataManager{
    private final Map<Class<? extends DataModel>, String> filePaths = new LinkedHashMap<>();
    private final Map<Class<? extends DataModel>, Map<String, DataModel>> data = new HashMap<>();

    private final List<LoadResolver> loadResolvers = new ArrayList<>();
    private final Map<Class<? extends DataModel>, DeleteResolver<?>> deleteResolvers = new HashMap<>();
    private final Map<Class<? extends DataModel>, SaveResolver<?>> saveResolvers = new HashMap<>();

    /**
     * Constructs a new CSVDataManager, configures file paths, resolvers, and loads data.
     */
    public CSVDataManager(){
        configFilePath();
        configLoadResolver();
        configDeleteResolver();
        configSaveResolver();
        loadData();
    }

    /**
     * Maps each data model class to its corresponding CSV file path.
     */
    private void configFilePath(){
        filePaths.put(User.class, "./data/UserList.csv");
        filePaths.put(BTOProject.class, "./data/ProjectList.csv");
        filePaths.put(FlatUnit.class, "./data/FlatUnitList.csv");
        filePaths.put(OfficerRegistration.class, "./data/OfficerRegistrationList.csv");
        filePaths.put(Enquiry.class, "./data/EnquiryList.csv");
        filePaths.put(Application.class, "./data/ApplicationList.csv");
    }

    /**
     * Configures any load-time resolvers for handling relationships.
     */
    private void configLoadResolver(){
        loadResolvers.add(new BTOProjectRelationshipResolver());
    }

    /**
     * Configures delete resolvers to handle any business logic when deleting objects.
     */
    private void configDeleteResolver(){
        deleteResolvers.put(BTOProject.class, new BTOProjectRelationshipResolver());
    }

    /**
     * Configures save resolvers to handle relationship updates when saving objects.
     */
    private void configSaveResolver(){
        saveResolvers.put(BTOProject.class, new BTOProjectRelationshipResolver());
        saveResolvers.put(Application.class, new ApplicationRelationshipResolver());
    }

    /**
     * Loads and parses all CSV data into memory, initializing {@link DataModel} instances 
     * and resolving defined object relationships.
     * <p>
     * This method reads from pre-configured CSV file paths, transforms raw data into model
     * objects using reflection and annotations, and applies any {@code LoadResolver} logic 
     * to establish relationships.
     * </p>
     * 
     * @throws DataParsingException at runtime if loading or parsing fails.
     *                              If caught, the exception should generally be rethrown to indicate a critical failure 
     *                              that may require terminating the application.
     */
    private void loadData(){
        try{
            for(Entry<Class<? extends DataModel>, String> filePath:filePaths.entrySet()){
                /**
                 * Reads raw data for each model.
                 */
                List<List<String>> rawData = CSVFileReader.readFile(filePath.getValue());
                    
                Class<? extends DataModel> clazz = filePath.getKey();
                data.put(clazz, parseData(clazz, rawData));
            }
            
            /**
             * This loop is where each LoadResolver instance is invoked after all data has been read and parsed. 
             * The LoadResolver implementations are responsible for linking related objects together in memory 
             */
            for(LoadResolver loadResolver:loadResolvers){
                loadResolver.resolveLoad(this);
            }
        } catch (Exception e){
            throw new DataParsingException(e.getMessage());
        }
    }

    /**
     * Parses raw CSV data into a map of {@link DataModel} instances using reflection and custom annotations.
     * <p>
     * This method dynamically constructs objects of the specified {@code DataModel} class by reading 
     * values from the provided CSV rows. It uses annotations to map CSV columns to the corresponding 
     * fields in the model class. Each resulting object is stored in a map using its primary key as the key.
     * </p>
     *
     * @param <T>      the type of {@code DataModel} being parsed.
     * @param clazz    the class object representing the specific {@code DataModel} type.
     * @param rawData  a list of CSV rows, where each inner list represents a line of values from the CSV file.
     * 
     * @return a map of {@code DataModel} instances indexed by their primary key for fast lookup.
     * 
     * @throws NoSuchMethodException        If a required constructor is not found in the model class.
     * @throws SecurityException            If access to the model class or its members is denied.
     * @throws InstantiationException       If the model class cannot be instantiated.
     * @throws IllegalAccessException       If the model constructor or fields are not accessible.
     * @throws IllegalArgumentException     If an invalid argument is used during instantiation or field assignment.
     * @throws InvocationTargetException    If the constructor or field accessors throw an exception.
     */
    @SuppressWarnings("unchecked")
    private <T extends DataModel> Map<String, DataModel> parseData(Class<T> clazz, List<List<String>> rawData) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Map<String, DataModel> data = new HashMap<>();

        Constructor<T> constructor = clazz.getDeclaredConstructor();
        // set constructor to be assessible if it is private
        constructor.setAccessible(true);

        Field[] fields = clazz.getDeclaredFields();

        for(List<String> values:rawData){
            T obj = constructor.newInstance();

            for(Field field:fields) {
                if (field.isAnnotationPresent(CSVField.class)) {
                    CSVField annotation = field.getAnnotation(CSVField.class);
                    int index = annotation.index();
                    boolean isForeignKey = annotation.foreignKey();
                    
                    field.setAccessible(true);
                    String value = values.get(index);
                    
                    if(isForeignKey){
                        if(!DataModel.class.isAssignableFrom(field.getType())){
                            throw new DataParsingException("Foreign key should be class implementing DataModel. Class: %s".formatted(field.getType()));
                        }

                        field.set(obj, getByPK((Class<T>) field.getType(), value));
                    }
                    else{
                        field.set(obj, DataParser.parse(field.getType(), value));
                    }
                }
            }

            data.put(obj.getPK(), obj);
        }

        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getAll(Class<T> clazz){
        return (List<T>) List.copyOf(data.get(clazz).values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getAll(Class<T> clazz, Comparator<T> comparator){
        Stream<T> stream = (Stream<T>) data.get(clazz).values().stream();
        return stream.sorted(comparator).toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataModel> T getByPK(Class<T> clazz, String PK){
        return (T) data.get(clazz).get(PK);
    }

    @Override
    public <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> query) {
        return getStreamByQueries(clazz, List.of(query))
                .collect(Collectors.toList());
    }

    @Override
    public <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> query, Comparator<T> comparator) {
        return getStreamByQueries(clazz, List.of(query))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends DataModel> List<T> getByQueries(Class<T> clazz, List<Predicate<T>> queries) {
        return getStreamByQueries(clazz, queries)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends DataModel> List<T> getByQueries(Class<T> clazz, List<Predicate<T>> queries, Comparator<T> comparator) {
        return getStreamByQueries(clazz, queries)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends DataModel> long countByQuery(Class<T> clazz, Predicate<T> query) {
        return getStreamByQueries(clazz, List.of(query))
                .count();
    }

    @Override
    public <T extends DataModel> long countByQueries(Class<T> clazz, List<Predicate<T>> queries) {
        return getStreamByQueries(clazz, queries)
                .count();
    }

    /**
     * Retrieves a stream of {@link DataModel} instances of the specified type that match
     * all given query predicates.
     * <p>
     * This is a helper method designed to streamline query-based operations
     * by applying a composite filter built from a list of {@link Predicate} conditions.
     * The stream returned allows further functional-style processing of the filtered results
     * such as {@code count()} or {@code collect()}.
     * </p>
     *
     * @param <T>     The type of {@code DataModel} to query.
     * @param clazz   The class representing the model type (used to look up the corresponding data "table").
     * @param queries A list of predicates that models must satisfy. All predicates will be combined with logical AND.
     * @return A {@code Stream<T>} of matching models, or an empty stream if no data is found for the given class.
     */
    @SuppressWarnings("unchecked")
    private <T extends DataModel> Stream<T> getStreamByQueries(Class<T> clazz, List<Predicate<T>> queries){
        Map<String, T> classData = (Map<String, T>) data.get(clazz);

        if (classData == null) {
            return Stream.empty();
        }
        return classData.values().stream()
                .filter(queries.stream().reduce(Predicate::and).orElse(_ -> true));
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p><strong>Note:</strong> Since data is stored in CSV format, locating and overwriting a specific line 
     * is non-trivial. For simplicity and maintainability (at the cost of efficiency), the entire dataset is 
     * rewritten whenever this method is called.</p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataModel> void save(T model) throws DataSavingException {
        Class<T> clazz = (Class<T>) model.getClass();

        boolean isAdding = !data.get(clazz).containsKey(model.getPK());
        if(isAdding) data.get(clazz).put(model.getPK(), model);

        try {
            SaveResolver<T> saveResolver = (SaveResolver<T>) saveResolvers.get(clazz);

            if(saveResolver != null){
                saveResolver.resolveSave(model, this);
            }
            
            saveData(clazz);
        } catch (DataSavingException e) {
            if(isAdding) data.get(clazz).remove(model.getPK());
            throw e;
        }
    }

    /**
     * Saves all in-memory data of the specified {@link DataModel} type to its associated CSV file.
     * <p>
     * Converts the model data to raw CSV format and writes it to the file path mapped to the given class.
     * </p>
     *
     * <p><strong>Note:</strong> Since data is stored in CSV format, locating and overwriting a specific line 
     * is non-trivial. For simplicity and maintainability (at the cost of efficiency), the entire dataset is 
     * rewritten whenever this method is called.</p>
     *
     * <p>
     * If performance becomes a concern or frequent small updates are needed, consider refactoring 
     * this logic to support more efficient line-level modifications, or migrating to a more update-friendly 
     * format (e.g., structured binary file).
     * </p>
     *
     * @param <T>   The type of {@code DataModel} to save.
     * @param clazz The class type of the model to save.
     * @throws DataSavingException If any error occurs during the conversion or file writing process.
     */
    @SuppressWarnings("unchecked")
    private <T extends DataModel> void saveData(Class<T> clazz) throws DataSavingException{
        String filePath = filePaths.get(clazz);
        Collection<T> data = (Collection<T>) this.data.get(clazz).values();

        try {
            List<List<String>> rawData = toRawData(clazz, data);
            CSVFileWriter.writeFile(filePath, rawData);
        } catch (Exception e) {
            throw new DataSavingException(e.getMessage());
        }
    }
    
    /**
     * Converts a collection of data objects into a list of CSV rows (raw data),
     * using reflection to read fields annotated with {@link CSVField}.
     * <p>
     * The fields are ordered by their {@code index()} attribute in the {@link CSVField} annotation.
     * Supports serialization of foreign key references by storing only their primary keys.
     * </p>
     *
     * @param <T>   The type of the data objects.
     * @param clazz The class of the data objects being serialized.
     * @param data  The collection of objects to convert.
     * @return A list of CSV rows, each representing one object.
     * @throws IllegalArgumentException If reflection fails due to type mismatch.
     * @throws IllegalAccessException   If access to a field is denied.
     * @throws DataParsingException     If a foreign key field is not a valid {@code DataModel}.
     */
    private <T> List<List<String>> toRawData(Class<T> clazz, Collection<T> data) throws IllegalArgumentException, IllegalAccessException{
        List<List<String>> lines = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        List<Field> savedFields = Arrays.stream(fields)
                                    .filter(field -> field.isAnnotationPresent(CSVField.class))
                                    .sorted(Comparator.comparingInt(f -> f.getAnnotation(CSVField.class).index()))
                                    .toList();

        for(T obj:data){
            List<String> line = new ArrayList<>();

            for(Field field:savedFields){
                field.setAccessible(true);
                CSVField annotation = field.getAnnotation(CSVField.class);

                if(annotation.foreignKey()){
                    if(!DataModel.class.isAssignableFrom(field.getType())){
                        throw new DataParsingException("Foreign key should be class implementing DataModel. Class: %s".formatted(field.getType()));
                    }

                    DataModel dataModel = (DataModel) field.get(obj);
                    line.add(dataModel.getPK());
                }
                else{
                    line.add(DataParser.toString(field.get(obj)));
                }
            }

            lines.add(line);
        }

        return lines;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataModel> void delete(T model) throws DataSavingException {
        Class<T> clazz = (Class<T>) model.getClass();
        
        data.get(clazz).remove(model.getPK());
        
        try {
            DeleteResolver<T> deleteResolver = (DeleteResolver<T>) deleteResolvers.get(clazz);

            if(deleteResolver != null){
                deleteResolver.resolveDelete(model, this);
            }
            
            saveData(clazz);
        } catch (DataSavingException e) {
            data.get(clazz).put(model.getPK(), model);
            throw e;
        }
    }
}
