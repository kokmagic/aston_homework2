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

    private static final String GET_ALL_EMPLOYEES_SQL = "SELECT * FROM Employees";
    private static final String GET_ONE_EMPLOYEE_SQL = "SELECT * FROM Employees WHERE id = ?";
    private static final String UPDATE_EMPLOYEE_SQL = "UPDATE Employees SET name=? WHERE id=?";
    private static final String DELETE_EMPLOYEE_SQL = "DELETE FROM Employees WHERE id=?";
    private static final String SAVE_EMPLOYEE_SQL = "INSERT INTO Employees (name) VALUES (?)";
    private static final String GET_EMPLOYEE_ID_BY_OFFICE_ID = "SELECT employee_id FROM OfficeWorkerRelation WHERE office_id = ?";
    private static final String DELETE_EMPLOYEE_RELATION_SQL = "DELETE FROM OfficeWorkerRelation WHERE employee_id=?";
    private static final String INSERT_EMPLOYEE_RELATION_SQL = "INSERT INTO OfficeWorkerRelation (office_id, employee_id) VALUES (?, ?)";
    private static final String DELETE_DEBTORS_BY_EMPLOYEE_ID_SQL = "DELETE FROM Debtors WHERE employee_id = ?";

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
        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement updateEmployeeStatement = connection.prepareStatement(UPDATE_EMPLOYEE_SQL)) {
                    updateEmployeeStatement.setString(1, updatedElement.getName());
                    updateEmployeeStatement.setLong(2, id);
                    updateEmployeeStatement.executeUpdate();
                }

                try (PreparedStatement deleteRelationsStatement = connection.prepareStatement(DELETE_EMPLOYEE_RELATION_SQL)) {
                    deleteRelationsStatement.setLong(1, id);
                    deleteRelationsStatement.executeUpdate();
                }

                try (PreparedStatement insertRelationsStatement = connection.prepareStatement(INSERT_EMPLOYEE_RELATION_SQL)) {
                    for (int officeId : updatedElement.getOfficesIds()) {
                        insertRelationsStatement.setInt(1, officeId);
                        insertRelationsStatement.setLong(2, id);
                        insertRelationsStatement.executeUpdate();
                    }
                }
                connection.commit();
                return updatedElement;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean remove(Long id) {
        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement deleteDebtorsStatement = connection.prepareStatement(DELETE_DEBTORS_BY_EMPLOYEE_ID_SQL)) {
                    deleteDebtorsStatement.setLong(1, id);
                    deleteDebtorsStatement.executeUpdate();
                }

                try (PreparedStatement deleteRelationsStatement = connection.prepareStatement(DELETE_EMPLOYEE_RELATION_SQL)) {
                    deleteRelationsStatement.setLong(1, id);
                    deleteRelationsStatement.executeUpdate();
                }

                try (PreparedStatement deleteEmployeeStatement = connection.prepareStatement(DELETE_EMPLOYEE_SQL)) {
                    deleteEmployeeStatement.setLong(1, id);
                    int rowsAffected = deleteEmployeeStatement.executeUpdate();
                    connection.commit();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Employee save(Employee employee) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_EMPLOYEE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, employee.getName());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                employee.setId(generatedKeys.getLong(1));
                return employee;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public List<Integer> findEmployeeIdsByOfficeId(Long officeId) {
        List<Integer> employeeIds = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE_ID_BY_OFFICE_ID)) {

            preparedStatement.setLong(1, officeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeIds.add(resultSet.getInt("employee_id"));
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
        List<Integer> debtorIds = debtorRepository.findDebtorIdsByEmployeeId(employee.getId());
        employee.setDebtorsIds(debtorIds);


        OfficeRepository officeRepository = new OfficeRepository(connectionPool);
        List<Integer> officeIds = officeRepository.findOfficeIdsByEmployeeId(employee.getId());
        employee.setOfficesIds(officeIds);
        return employee;
    }


}


