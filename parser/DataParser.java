package parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import config.ApplicationStatus;
import config.EnquiryStatus;
import config.FlatType;
import config.MaritalStatus;
import config.RegistrationStatus;
import config.UserRole;
import config.WithdrawalStatus;
import exception.DataParsingException;

/**
 * Utility class for parsing string data into objects and vice versa.
 * Supports parsing for primitive types, standard types like LocalDate, and application-specific enums.
 * 
 * <p>Used mainly for converting between CSV string values and strongly-typed objects.</p>
 */
public class DataParser {
    /** Internal map linking classes to their corresponding string-to-object parsers */
    private final static Map<Class<?>, Function<String, ?>> parsers = new HashMap<>();
    /** Internal map linking classes to their corresponding object-to-string functions */
    private final static Map<Class<?>, Function<?, String>> stringifiers = new HashMap<>();

    // Static initializer block for setting up parsers and stringifiers
    static {
        configParsers();
        configStringifiers();
    }

    /**
     * Registers all parsers that convert string values into objects.
     */
    private static void configParsers(){
        addParser(int.class, Integer::parseInt);
        addParser(boolean.class, s -> s.equals("1"));
        addParser(String.class, s -> desanitize(s));

        addParser(LocalDate.class, LocalDate::parse);
        addParser(LocalDateTime.class, LocalDateTime::parse);

        addParser(MaritalStatus.class, MaritalStatus::parseMaritalStatus);
        addParser(UserRole.class, UserRole::parseUserRole);
        addParser(FlatType.class, FlatType::parseFlatType);
        addParser(RegistrationStatus.class, RegistrationStatus::parseRegistrationStatus);
        addParser(EnquiryStatus.class, EnquiryStatus::parseEnquiryStatus);
        addParser(ApplicationStatus.class, ApplicationStatus::parseApplicationStatus);
        addParser(WithdrawalStatus.class, WithdrawalStatus::parseWithdrawalStatus);
    }

    /**
     * Registers a parser for a specific class.
     * 
     * @param <T>    The type to be parsed
     * @param clazz  The class representing the type
     * @param parser A function that parses a string into the given type
     */
    private static <T> void addParser(Class<T> clazz, Function<String, T> parser){
        parsers.put(clazz, parser);
    }

    /**
     * Registers all stringifiers that convert objects into string values.
     */
    private static void configStringifiers(){
        addStringifiers(Integer.class, String::valueOf);
        addStringifiers(Boolean.class, b -> b?"1":"0");
        addStringifiers(String.class, s -> sanitize(s));
        
        addStringifiers(LocalDate.class, LocalDate::toString);
        addStringifiers(LocalDateTime.class, LocalDateTime::toString);
        
        addStringifiers(MaritalStatus.class, MaritalStatus::getStoredString);
        addStringifiers(UserRole.class, UserRole::getStoredString);
        addStringifiers(FlatType.class, FlatType::getStoredString);
        addStringifiers(RegistrationStatus.class, RegistrationStatus::getStoredString);
        addStringifiers(EnquiryStatus.class, EnquiryStatus::getStoredString);
        addStringifiers(ApplicationStatus.class, ApplicationStatus::getStoredString);
        addStringifiers(WithdrawalStatus.class, WithdrawalStatus::getStoredString);
    }

    /**
     * Registers a stringifier for a specific class.
     * 
     * @param <T>         The type to stringify
     * @param clazz       The class representing the type
     * @param stringifier A function that converts the type to a string
     */
    private static <T> void addStringifiers(Class<T> clazz, Function<T, String> stringifier){
        stringifiers.put(clazz, stringifier);
    }

    /**
     * Parses a string into an object of the given type.
     *
     * @param <T>   The type to parse into
     * @param clazz The class of the type
     * @param data  The string representation of the object
     * @return The parsed object of type T
     * @throws DataParsingException If parsing is unsupported or fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(Class<T> clazz, String data){
        Function<String, T> parser = (Function<String, T>) parsers.get(clazz);
        if (parser == null) throw new DataParsingException("Unsupported Parsing Data Type: %s".formatted(clazz.getName()));
        
        return parser.apply(data);
    }

    /**
     * Converts an object to its string representation for storage.
     *
     * @param <T>  The type of the object
     * @param data The object to convert
     * @return The string representation of the object
     * @throws DataParsingException If conversion is unsupported or fails
     */
    @SuppressWarnings("unchecked")
    public static <T> String toString(T data){
        Class<?> clazz = data.getClass();
        if(clazz.getEnclosingClass() != null && clazz.getEnclosingClass().isEnum()) clazz = clazz.getEnclosingClass();

        Function<T, String> stringifier = (Function<T, String>) stringifiers.get(clazz);
        if (stringifier == null) throw new DataParsingException("Unsupported Stringifying Data Type: %s".formatted(clazz.getName()));
        
        return stringifier.apply(data);
    }

    /**
     * Escapes commas in strings so they don't break CSV formatting.
     * 
     * @param s Original string
     * @return Sanitized string
     */
    private static String sanitize(String s){
        return s.replace(",", "\\,");
    }

    /**
     * Reverts escaped commas back to normal for proper parsing.
     * 
     * @param s Sanitized string
     * @return Original string
     */
    private static String desanitize(String s){
        return s.replace("\\,", ",");
    }
}
