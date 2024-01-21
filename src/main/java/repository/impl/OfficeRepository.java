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
    private static final String GET_OFFICE_ID_BY_EMPLOYEE_ID = "SELECT office_id FROM OfficeWorkerRelation WHERE employee_id = ?";
    private static final String DELETE_OFFICE_RELATION_SQL = "DELETE FROM OfficeWorkerRelation WHERE office_id=?";
    private static final String INSERT_OFFICE_RELATION_SQL = "INSERT INTO OfficeWorkerRelation (office_id, employee_id) VALUES (?, ?)";

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
        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement updateOfficeStatement = connection.prepareStatement(UPDATE_OFFICE_SQL)) {
                    updateOfficeStatement.setString(1, updatedElement.getAddress());
                    updateOfficeStatement.setLong(2, id);
                    updateOfficeStatement.executeUpdate();
                }

                try (PreparedStatement deleteRelationsStatement = connection.prepareStatement(DELETE_OFFICE_RELATION_SQL)) {
                    deleteRelationsStatement.setLong(1, id);
                    deleteRelationsStatement.executeUpdate();
                }

                try (PreparedStatement insertRelationsStatement = connection.prepareStatement(INSERT_OFFICE_RELATION_SQL)) {
                    for (int employeeId : updatedElement.getEmployeesIds()) {
                        insertRelationsStatement.setLong(1, id);
                        insertRelationsStatement.setInt(2, employeeId);
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
                try (PreparedStatement deleteRelationsStatement = connection.prepareStatement(DELETE_OFFICE_RELATION_SQL)) {
                    deleteRelationsStatement.setLong(1, id);
                    deleteRelationsStatement.executeUpdate();
                }

                try (PreparedStatement deleteOfficeStatement = connection.prepareStatement(DELETE_OFFICE_SQL)) {
                    deleteOfficeStatement.setLong(1, id);
                    int rowsAffected = deleteOfficeStatement.executeUpdate();
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

    public Office save(Office office) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_OFFICE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, office.getAddress());

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    office.setId(generatedKeys.getLong(1));
                    return office;
                }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Integer> findOfficeIdsByEmployeeId(Long employeeId) {
        List<Integer> officeIds = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_OFFICE_ID_BY_EMPLOYEE_ID)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                officeIds.add(resultSet.getInt("office_id"));
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
        List<Integer> employeeIds = employeeRepository.findEmployeeIdsByOfficeId(office.getId());
        office.setEmployeesIds(employeeIds);
        return office;
    }
}
