package manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import exception.DataSavingException;
import model.CSVField;
import model.DataModel;
import model.User;
import parser.DataParser;
import util.CSVFileReader;
import util.CSVFileWriter;

public class CSVDataManager {
    private final Map<Class<? extends DataModel>, String> filePaths = new HashMap<>();
    private final Map<Class<? extends DataModel>, Map<String, ? extends DataModel>> data = new HashMap<>();

    public CSVDataManager(){
        configFilePath();
        loadData();
    }

    private void configFilePath(){
        filePaths.put(User.class, "./data/UserList.csv");
    }

    private void loadData(){
        try{
            for(Entry<Class<? extends DataModel>, String> filePath:filePaths.entrySet()){
                List<List<String>> rawData = CSVFileReader.readFile(filePath.getValue());

                Class<? extends DataModel> clazz = filePath.getKey();
                data.put(clazz, parseData(clazz, rawData));
            }
        } catch (Exception e){
            System.err.println("Fatal: Fail to read data, TERMINATING. Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private <T extends DataModel> Map<String, T> parseData(Class<T> clazz, List<List<String>> rawData) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Map<String, T> data = new HashMap<>();

        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);

        Field[] fields = clazz.getDeclaredFields();

        for(List<String> values:rawData){
            T obj = constructor.newInstance();

            for(Field field:fields) {
                if (field.isAnnotationPresent(CSVField.class)) {
                    CSVField annotation = field.getAnnotation(CSVField.class);
                    int index = annotation.index();
                    
                    field.setAccessible(true);
                    String value = values.get(index);
                    
                    field.set(obj, DataParser.parse(field.getType(), value));
                }
            }

            data.put(obj.getPK(), obj);
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getAll(Class<T> clazz){
        return (List<T>) List.copyOf(data.get(clazz).values());
    }

    @SuppressWarnings("unchecked")
    public <T extends DataModel> T getByPK(Class<T> clazz, String PK){
        return (T) data.get(clazz).get(PK);
    }

    @SuppressWarnings("unchecked")
    public <T extends DataModel> List<T> getByQuery(Class<T> clazz, List<Predicate<T>> predicates) {
        Map<String, T> classData = (Map<String, T>) data.get(clazz);
        
        if (classData == null) {
            return List.of();
        }

        return classData.values().stream()
                .filter(predicates.stream().reduce(Predicate::and).orElse(_ -> true))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends DataModel> void save(Class<T> clazz) throws DataSavingException{
        String filePath = filePaths.get(clazz);
        Collection<T> data = (Collection<T>) this.data.get(clazz).values();

        try {
            List<List<String>> rawData = toRawData(clazz, data);
            CSVFileWriter.writeFile(filePath, rawData);
        } catch (Exception e) {
            throw new DataSavingException(e.getMessage());
        }
    }

    public <T> List<List<String>> toRawData(Class<T> clazz, Collection<T> data) throws IllegalArgumentException, IllegalAccessException{
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
                line.add(DataParser.toString(field.get(obj)));
            }

            lines.add(line);
        }

        return lines;
    }
}
