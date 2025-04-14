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
import model.BTOProject;
import model.CSVField;
import model.DataModel;
import model.Enquiry;
import model.FlatUnit;
import model.OfficerRegistration;
import model.User;
import parser.DataParser;
import relationship.BTOProjectRelationshipResolver;
import relationship.resolver.DeleteResolver;
import relationship.resolver.LoadResolver;
import relationship.resolver.SaveResolver;
import util.CSVFileReader;
import util.CSVFileWriter;

public class CSVDataManager implements DataManager{
    private final Map<Class<? extends DataModel>, String> filePaths = new LinkedHashMap<>();
    private final Map<Class<? extends DataModel>, Map<String, DataModel>> data = new HashMap<>();

    private final List<LoadResolver> loadResolvers = new ArrayList<>();
    private final Map<Class<? extends DataModel>, DeleteResolver<?>> deleteResolvers = new HashMap<>();
    private final Map<Class<? extends DataModel>, SaveResolver<?>> saveResolvers = new HashMap<>();

    public CSVDataManager(){
        configFilePath();
        configLoadResolver();
        configDeleteResolver();
        configSaveResolver();
        loadData();
    }

    private void configFilePath(){
        filePaths.put(User.class, "./data/UserList.csv");
        filePaths.put(BTOProject.class, "./data/ProjectList.csv");
        filePaths.put(FlatUnit.class, "./data/FlatUnitList.csv");
        filePaths.put(OfficerRegistration.class, "./data/OfficerRegistrationList.csv");
        filePaths.put(Enquiry.class, "./data/EnquiryList.csv");
    }

    private void configLoadResolver(){
        loadResolvers.add(new BTOProjectRelationshipResolver());
    }

    private void configDeleteResolver(){
        deleteResolvers.put(BTOProject.class, new BTOProjectRelationshipResolver());
    }

    private void configSaveResolver(){
        saveResolvers.put(BTOProject.class, new BTOProjectRelationshipResolver());
    }

    private void loadData(){
        try{
            for(Entry<Class<? extends DataModel>, String> filePath:filePaths.entrySet()){
                List<List<String>> rawData = CSVFileReader.readFile(filePath.getValue());

                Class<? extends DataModel> clazz = filePath.getKey();
                data.put(clazz, parseData(clazz, rawData));
            }

            for(LoadResolver loadResolver:loadResolvers){
                loadResolver.resolveLoad(this);
            }
        } catch (Exception e){
            System.err.println("Fatal: Fail to read data, TERMINATING. Error: " + e.getMessage());
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends DataModel> Map<String, DataModel> parseData(Class<T> clazz, List<List<String>> rawData) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Map<String, DataModel> data = new HashMap<>();

        Constructor<T> constructor = clazz.getDeclaredConstructor();
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
    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> query) {
        Map<String, T> classData = (Map<String, T>) data.get(clazz);
        
        if (classData == null) {
            return List.of();
        }

        return classData.values().stream()
                .filter(query)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getByQuery(Class<T> clazz, Predicate<T> query, Comparator<T> comparator) {
        Map<String, T> classData = (Map<String, T>) data.get(clazz);
        
        if (classData == null) {
            return List.of();
        }

        return classData.values().stream()
                .filter(query)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getByQueries(Class<T> clazz, List<Predicate<T>> queries) {
        Map<String, T> classData = (Map<String, T>) data.get(clazz);
        
        if (classData == null) {
            return List.of();
        }

        return classData.values().stream()
                .filter(queries.stream().reduce(Predicate::and).orElse(_ -> true))
                .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getByQueries(Class<T> clazz, List<Predicate<T>> queries, Comparator<T> comparator) {
        Map<String, T> classData = (Map<String, T>) data.get(clazz);
        
        if (classData == null) {
            return List.of();
        }

        return classData.values().stream()
                .filter(queries.stream().reduce(Predicate::and).orElse(_ -> true))
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
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
