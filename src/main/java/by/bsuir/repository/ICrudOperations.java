package by.bsuir.repository;

import java.util.List;

public interface ICrudOperations<K, T> {

    //CRUD = Create Read Update Delete

    List<T> findAll();

    T findOne(K id);

    T save(T entity);

    void addOne(T entity);

    void save(List<T> entities);

    T update(T entity);

    void delete(K id);
}
