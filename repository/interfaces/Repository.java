package repository.interfaces;

import java.util.List;

public interface Repository<T>{
    void save(T o);
    List<T> getAll();
}
