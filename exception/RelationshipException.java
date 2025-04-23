package exception;

import manager.interfaces.DataManager;
import relationship.resolver.*;;

/**
 * Thrown when an expected relationship operation in fails.
 * This exception is typically thrown by {@link LoadResolver}, {@link SaveResolver}, and {@link DeleteResolver}.
 * <p>
 * This exception is typically thrown when there is a failure in performing an operation that 
 * requires related models to be processed, such as cascading deletes. This could be due to 
 * misconfigurations or other unexpected errors occurring during data management operations, 
 * such as the failure to delete related models or resolving dependencies.
 * 
 * @see DataManager
 * @see LoadResolver
 * @see SaveResolver
 * @see DeleteResolver
 */
public class RelationshipException extends RuntimeException{
    public RelationshipException(String message) {
        super(message);
    }
}