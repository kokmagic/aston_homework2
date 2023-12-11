package repository;

import entity.Employee;

import java.util.List;

public interface EmployeeRepository {

    Employee findById(Long id);

    List<Employee> findAll();

    void save(Employee employee);

    void update(Employee employee);

    boolean remove(Long id);

    // Другие методы при необходимости
}
