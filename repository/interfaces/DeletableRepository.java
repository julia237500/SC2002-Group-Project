package repository.interfaces;

public interface DeletableRepository<T> extends Repository<T> {
    void delete(T o);
}
