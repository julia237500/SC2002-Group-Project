package parser;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import config.FlatType;
import config.MaritalStatus;
import config.UserRole;
import exception.DataParsingException;

public class DataParser {
    private final static Map<Class<?>, Function<String, ?>> parsers = new HashMap<>();
    private final static Map<Class<?>, Function<?, String>> stringifiers = new HashMap<>();

    static {
        configParsers();
        configStringifiers();
    }

    private static void configParsers(){
        addParser(int.class, Integer::parseInt);
        addParser(boolean.class, s -> s.equals("1"));
        addParser(String.class, s -> s);

        addParser(LocalDate.class, LocalDate::parse);

        addParser(MaritalStatus.class, MaritalStatus::parseMaritalStatus);
        addParser(UserRole.class, UserRole::parseUserRole);
        addParser(FlatType.class, FlatType::parseFlatType);
    }

    private static <T> void addParser(Class<T> clazz, Function<String, T> parser){
        parsers.put(clazz, parser);
    }

    private static void configStringifiers(){
        addStringifiers(Integer.class, String::valueOf);
        addStringifiers(Boolean.class, b -> b?"1":"0");
        addStringifiers(String.class, s -> s);
        
        addStringifiers(LocalDate.class, LocalDate::toString);
        
        addStringifiers(MaritalStatus.class, MaritalStatus::getStoredString);
        addStringifiers(UserRole.class, UserRole::getStoredString);
        addStringifiers(FlatType.class, FlatType::getStoredString);
    }

    private static <T> void addStringifiers(Class<T> clazz, Function<T, String> stringifier){
        stringifiers.put(clazz, stringifier);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(Class<T> clazz, String data){
        Function<String, T> parser = (Function<String, T>) parsers.get(clazz);
        if (parser == null) throw new DataParsingException("Unsupported Data Type: %s".formatted(clazz.getSimpleName()));
        
        return parser.apply(data);
    }

    @SuppressWarnings("unchecked")
    public static <T> String toString(T data){
        Function<T, String> stringifier = (Function<T, String>) stringifiers.get(data.getClass());
        if (stringifier == null) throw new DataParsingException("Unsupported Data Type: %s".formatted(data.getClass().getSimpleName()));
        
        return stringifier.apply(data);
    }
}
