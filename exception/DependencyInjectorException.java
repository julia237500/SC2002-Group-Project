package exception;

/**
 * Thrown to indicate a misconfiguration in the Dependency Injection (DI) system.
 * <p>
 * This exception is typically thrown by the {@link DIManager} or {@link DIContainer} when
 * there are issues with the configuration of the DIManager, such as missing dependencies,
 * circular dependencies, or other misconfigurations that prevent proper injection of dependencies.
 * 
 * @see DIManager
 * @see DIContainer
 */

public class DependencyInjectorException extends RuntimeException{
    public DependencyInjectorException(String message) {
        super(message);
    }
}
