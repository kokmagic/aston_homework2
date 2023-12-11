package repository;

import entity.Debtor;

import java.util.List;

public interface DebtorRepository {

    Debtor findById(Long id);

    List<Debtor> findAll();

    void save(Debtor debtor);

    void update(Debtor debtor);

    boolean remove(Long id);

    // Другие методы при необходимости
}
