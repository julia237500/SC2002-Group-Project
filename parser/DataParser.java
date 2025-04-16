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

    private static <T> void addParser(Class<T> clazz, Function<String, T> parser){
        parsers.put(clazz, parser);
    }

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

    private static <T> void addStringifiers(Class<T> clazz, Function<T, String> stringifier){
        stringifiers.put(clazz, stringifier);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(Class<T> clazz, String data){
        Function<String, T> parser = (Function<String, T>) parsers.get(clazz);
        if (parser == null) throw new DataParsingException("Unsupported Parsing Data Type: %s".formatted(clazz.getName()));
        
        return parser.apply(data);
    }

    @SuppressWarnings("unchecked")
    public static <T> String toString(T data){
        Class<?> clazz = data.getClass();
        if(clazz.getEnclosingClass() != null && clazz.getEnclosingClass().isEnum()) clazz = clazz.getEnclosingClass();

        Function<T, String> stringifier = (Function<T, String>) stringifiers.get(clazz);
        if (stringifier == null) throw new DataParsingException("Unsupported Stringifying Data Type: %s".formatted(clazz.getName()));
        
        return stringifier.apply(data);
    }

    private static String sanitize(String s){
        return s.replace(",", "\\,");
    }

    private static String desanitize(String s){
        return s.replace("\\,", ",");
    }
}
