package util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import exception.DependencyInjectorException;
import util.interfaces.DIContainer;

public class DefaultDIContainer implements DIContainer {
    private Map<Class<?>, Class<?>> implementations = new HashMap<>();
    private Map<Class<?>, Object> container = new HashMap<>();

    @Override
    public <T> void register(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        if(!interfaceClass.isAssignableFrom(implementationClass)){
            throw new DependencyInjectorException("Invalid Register for DI Container. Interface: " + interfaceClass + " , Implementation: "+ implementationClass);
        }
        implementations.put(interfaceClass, implementationClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(Class<T> type) {
        if (container.containsKey(type)) {
            return (T) container.get(type);
        }

        try {
            Class<T> implementation = (Class<T>) implementations.get(type);
            Constructor<T> constructor = (Constructor<T>) implementation.getDeclaredConstructors()[0];
            constructor.setAccessible(true);

            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                parameters[i] = resolve(parameterTypes[i]);
            }

            T instance = constructor.newInstance(parameters);
            container.put(type, instance);
            return instance;
        } catch (Exception e) {
            throw new DependencyInjectorException("Unable to create instance of " + type.getName() + e.getMessage());
        }
    }
}