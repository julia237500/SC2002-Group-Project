package util.interfaces;


/**
 * Interface for a Dependency Injection (DI) container.
 * <p>
 * Provides methods to register and resolve dependencies. The DI container
 * allows decoupled and flexible object creation, enabling inversion of control (IoC).
 * </p>
 */
public interface DIContainer {

    /**
     * Registers an interface and its corresponding implementation class with the container.
     *
     * @param <T> the type of the interface
     * @param interfaceClass the interface to register
     * @param implementationClass the class that implements the interface
     * @throws IllegalArgumentException if the implementation class does not implement the interface
     */
    <T> void register(Class<T> interfaceClass, Class<? extends T> implementationClass);

    /**
     * Resolves and returns an instance of the requested type.
     * <p>
     * If the type was previously registered and has dependencies, they are automatically resolved and injected.
     * </p>
     *
     * @param <T> the type to resolve
     * @param type the class of the type to resolve
     * @return an instance of the resolved type
     * @throws RuntimeException if the type is not registered or cannot be instantiated
     */
    <T> T resolve(Class<T> type);
}

/**
 * Design Principles involved:
 * 1. Inversion of Control (IoC) Principle:
 * - Instead of the application creating and managing its dependencies directly, 
 * - it delegates that responsibility to a container (the DI container).
 * - This inverts the control of dependency creation—from the application code to the container.
 * - This reduces tight coupling between classes.
 */