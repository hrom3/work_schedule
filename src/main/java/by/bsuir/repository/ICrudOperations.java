package by.bsuir.repository;

import java.util.List;

/**
 * Generic interface for CRUD operations
 * CRUD = Create Read Update Delete
 * @param <K>  - primary key of object
 * @param <T> - object type
 */

public interface ICrudOperations<K, T> {

    List<T> findAll();

    T findOne(K id);

    T   save(T entity);

//    void addOne(T entity);

//    void save(List<T> entities);

    T update(T entity);

    void deleteHard(K id);

}
