package repository.impl;

import connection.ConnectionPool;
import entity.Office;
import repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfficeRepository implements Repository<Office> {

    private final ConnectionPool connectionPool;

    private static final String GET_ALL_OFFICES_SQL = "SELECT * FROM Offices";
    private static final String GET_ONE_OFFICE_SQL = "SELECT * FROM Offices WHERE id = ?";
    private static final String UPDATE_OFFICE_SQL = "UPDATE Offices SET address=? WHERE id=?";
    private static final String DELETE_OFFICE_SQL = "DELETE FROM Offices WHERE id=?";
    private static final String SAVE_OFFICE_SQL = "INSERT INTO Offices (address) VALUES (?)";
    private static final String GET_OFFICE_ID_BY_EMPLOYEE_ID = "SELECT office_id FROM OfficeWorkerRelation WHERE worker_id = ?";
    public OfficeRepository() {
        this.connectionPool = new ConnectionPool();
    }

    public OfficeRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Office> findAll() {
        List<Office> offices = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_OFFICES_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Office office = mapResultSetToOffice(resultSet);
                offices.add(office);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return offices;
    }

    @Override
    public Office findOne(Long id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ONE_OFFICE_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToOffice(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Office update(Long id, Office updatedElement) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_OFFICE_SQL)) {
            preparedStatement.setString(1, updatedElement.getAddress());
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
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_OFFICE_SQL)) {
            preparedStatement.setLong(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Office save(Office office) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_OFFICE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, office.getAddress());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    office.setId(generatedKeys.getLong(1));
                    return office;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Long> findOfficeIdsByEmployeeId(Long employeeId) {
        List<Long> officeIds = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_OFFICE_ID_BY_EMPLOYEE_ID)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                officeIds.add(resultSet.getLong("office_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return officeIds;
    }

    private Office mapResultSetToOffice(ResultSet resultSet) throws SQLException {
        Office office = new Office();
        office.setId(resultSet.getLong("id"));
        office.setAddress(resultSet.getString("address"));

        EmployeeRepository employeeRepository = new EmployeeRepository(connectionPool);
        List<Long> employeeIds = employeeRepository.findEmployeeIdsByOfficeId(office.getId());
        office.setEmployeesIds(employeeIds);
        return office;
    }
}
