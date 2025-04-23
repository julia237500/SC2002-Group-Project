package filter;

import java.util.function.Predicate;

/**
 * A generic interface for creating filter criteria for any type {@code T}.
 *
 * <p>Implementations of this interface provide a reusable {@link Predicate} 
 * that can be applied to streams, collections, or other data processing workflows 
 * to filter out elements based on custom conditions.</p>
 *
 * <p>This interface helps in separating filtering logic from the core business logic,
 * allowing for cleaner and more modular code.</p>
 * @param <T> the type of objects this filter works on
 */
public interface Filter<T> {

    /**
     * Returns a {@link Predicate} representing the filter condition for objects of type {@code T}.
     *
     * @return a predicate used to filter objects
     */
    Predicate<T> getFilter();
}
