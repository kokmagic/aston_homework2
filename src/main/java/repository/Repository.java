package repository;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();

    T findOne(Long id);

    T update(Long id, T updatedElement);

    boolean remove(Long id);
}
