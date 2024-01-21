package service;

import java.util.List;

public interface Service<T> {
    List<T> getAll();

    T getById(Long id);

    T update(Long id, T updatedElement);

    boolean remove(Long id);

    T create(T element);
}
