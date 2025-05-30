package util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import exception.DependencyInjectorException;
import util.interfaces.DIContainer;

/**
 * A default implementation of the {@link DIContainer} interface using reflection to inject dependencies.
 * <p>
 * This container manages the registration and resolution of dependencies for a given interface and its corresponding implementation.
 * It uses reflection to instantiate objects and inject their dependencies automatically.
 * </p>
 * <p>
 * All instances created and stored in this container is singleton pattern.
 * </p>
 */
public class DefaultDIContainer implements DIContainer {

    /** A map storing the registered interface to implementation classes */
    private Map<Class<?>, Class<?>> implementations = new HashMap<>();

    /** A map for storing instantiated objects for reuse */
    private Map<Class<?>, Object> container = new HashMap<>();

    /**
     * Registers an interface to its implementation in the container.
     * 
     * @param <T> the type of the interface
     * @param interfaceClass the interface to be registered
     * @param implementationClass the implementation of the interface
     * @throws DependencyInjectorException if the implementation class does not implement the interface
     */
    @Override
    public <T> void register(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        if(!interfaceClass.isAssignableFrom(implementationClass)){
            throw new DependencyInjectorException("Invalid Register for DI Container. Interface: " + interfaceClass + " , Implementation: "+ implementationClass);
        }
        implementations.put(interfaceClass, implementationClass);
    }


    /**
     * Resolves an instance of the given type, automatically injecting dependencies as needed.
     * 
     * @param <T> the type to resolve
     * @param type the class type to resolve
     * @return an instance of the requested type
     * @throws DependencyInjectorException if the type cannot be instantiated or its dependencies cannot be resolved
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(Class<T> type) {

        /** Return already created instance if available */
        if (container.containsKey(type)) {
            return (T) container.get(type);
        }

        try {
            /** Get the implementation class for the requested type */
            Class<T> implementation = (Class<T>) implementations.get(type);

            /** Get the first constructor of the implementation class 
             * The first constructor is the one which does reflective instantiation (private, no arguments)
             */
            Constructor<T> constructor = (Constructor<T>) implementation.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            /**
             * Even though the constructor is private, 
             * setAccessible(true) temporarily disables Java’s access checks so the reflection API can still use it.
             */

            /** Resolve dependencies for constructor parameters */
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];
            
            /** Resolve dependencies for each parameter */
            for (int i = 0; i < parameterTypes.length; i++) {
                parameters[i] = resolve(parameterTypes[i]);
            }
            
            /** Create a new instance of the implementation class */
            T instance = constructor.newInstance(parameters);

            /** Store the instance in the container for future reuse */
            container.put(type, instance);
            return instance;
        } catch (StackOverflowError e){
            throw new StackOverflowError("Unable to create instance of " + type.getSimpleName() + " due to circular dependencies.");
        } catch (Exception e) {
            throw new DependencyInjectorException("Unable to create instance of %s: \n%s".formatted(type.getSimpleName(), e.getMessage()));
        }
    }
}