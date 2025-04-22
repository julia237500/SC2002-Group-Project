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
     * Loads and parses all CSV data into memory and resolves object relationships.
     */
    private void loadData(){
        try{
            for(Entry<Class<? extends DataModel>, String> filePath:filePaths.entrySet()){

                /**
                 * Reads raw CSV for each model and parses into DataModel objects.
                 * But these objects are still disconnected — like rows in a table, not yet aware of each other.
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
            System.err.println("Fatal: Fail to read data, TERMINATING. Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Parses raw CSV data into a map of DataModel objects by using reflection and annotations.
     */
    @SuppressWarnings("unchecked")
    private <T extends DataModel> Map<String, DataModel> parseData(Class<T> clazz, List<List<String>> rawData) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Map<String, DataModel> data = new HashMap<>();

        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        /**
         * Even though the constructor is private, setAccessible(true) temporarily disables Java’s access checks so the reflection API can still use it.
         */

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
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getAll(Class<T> clazz){
        return (List<T>) List.copyOf(data.get(clazz).values());
    }

    @Override
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getAll(Class<T> clazz, Comparator<T> comparator){
        Stream<T> stream = (Stream<T>) data.get(clazz).values().stream();
        return stream.sorted(comparator).toList();
    }

    @Override
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T extends DataModel> T getByPK(Class<T> clazz, String PK){
        return (T) data.get(clazz).get(PK);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    private <T extends DataModel> Stream<T> getStreamByQueries(Class<T> clazz, List<Predicate<T>> queries){
        Map<String, T> classData = (Map<String, T>) data.get(clazz);

        if (classData == null) {
            return Stream.empty();
        }
        return classData.values().stream()
                .filter(queries.stream().reduce(Predicate::and).orElse(_ -> true));
    }

    @Override
    public <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> query) {
        return getStreamByQueries(clazz, List.of(query))
            .collect(Collectors.toList());
    }

    @Override
    /** {@inheritDoc} */
    public <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> query, Comparator<T> comparator) {
        return getStreamByQueries(clazz, List.of(query))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    /** {@inheritDoc} */
    public <T extends DataModel> List<T> getByQueries(Class<T> clazz, List<Predicate<T>> queries) {
        return getStreamByQueries(clazz, queries)
                .collect(Collectors.toList());
    }

    @Override
    /** {@inheritDoc} */
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
    
    @Override
    /** {@inheritDoc} */
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
     * Saves the current data of a given class to its CSV file.
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
     * Converts a list of data objects into raw CSV data for saving.
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
    /** {@inheritDoc} */
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
