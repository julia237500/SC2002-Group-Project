package filter;

import java.util.function.Predicate;

public interface Filter<T> {
    Predicate<T> getFilter();
}
