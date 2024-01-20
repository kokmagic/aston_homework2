package repository.impl;

import connection.ConnectionPool;
import entity.Employee;
import lombok.extern.slf4j.Slf4j;
import repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EmployeeRepository implements Repository<Employee> {

    private final ConnectionPool connectionPool;

    private static final String GET_ALL_EMPLOYEES_SQL = "SELECT * FROM Workers";
    private static final String GET_ONE_EMPLOYEE_SQL = "SELECT * FROM Workers WHERE id = ?";
    private static final String UPDATE_EMPLOYEE_SQL = "UPDATE Workers SET name=? WHERE id=?";
    private static final String DELETE_EMPLOYEE_SQL = "DELETE FROM Workers WHERE id=?";
    private static final String SAVE_EMPLOYEE_SQL = "INSERT INTO Workers (name) VALUES (?)";
    private static final String GET_EMPLOYEE_ID_BY_OFFICE_ID = "SELECT worker_id FROM OfficeWorkerRelation WHERE office_id = ?";

    public EmployeeRepository() {
        this.connectionPool = new ConnectionPool();
    }

    public EmployeeRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_EMPLOYEES_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Employee employee = mapResultSetToEmployee(resultSet);
                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }

    @Override
    public Employee findOne(Long id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ONE_EMPLOYEE_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToEmployee(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Employee update(Long id, Employee updatedElement) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EMPLOYEE_SQL)) {
            preparedStatement.setString(1, updatedElement.getName());
            preparedStatement.setLong(2, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return updatedElement;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean remove(Long id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_EMPLOYEE_SQL)) {
            preparedStatement.setLong(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Employee save(Employee employee) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_EMPLOYEE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, employee.getName());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getLong(1));
                    return employee;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public List<Long> findEmployeeIdsByOfficeId(Long officeId) {
        List<Long> employeeIds = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_ID_BY_OFFICE_ID)) {

            preparedStatement.setLong(1, officeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeIds.add(resultSet.getLong("worker_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employeeIds;
    }



    private Employee mapResultSetToEmployee(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();
        employee.setId(resultSet.getLong("id"));
        employee.setName(resultSet.getString("name"));

        DebtorRepository debtorRepository = new DebtorRepository(connectionPool);
        List<Long> debtorIds = debtorRepository.findDebtorIdsByEmployeeId(employee.getId());
        employee.setDebtorsIds(debtorIds);


        OfficeRepository officeRepository = new OfficeRepository(connectionPool);
        List<Long> officeIds = officeRepository.findOfficeIdsByEmployeeId(employee.getId());
        employee.setOfficesIds(officeIds);
        return employee;
    }


}


