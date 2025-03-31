package util.interfaces;

public interface DIContainer {
    <T> void register(Class<T> interfaceClass, Class<? extends T> implementationClass);
    <T> T resolve(Class<T> type);
}
