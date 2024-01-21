package repository.impl;

import connection.ConnectionPool;
import entity.Debtor;
import repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DebtorRepository implements Repository<Debtor> {

    private final ConnectionPool connectionPool;

    private static final String GET_ALL_DEBTORS_SQL = "SELECT * FROM Debtors";
    private static final String GET_ONE_DEBTOR_SQL = "SELECT * FROM Debtors WHERE id = ?";
    private static final String UPDATE_DEBTOR_SQL = "UPDATE Debtors SET name=?, address=?, debt_amount=?, employee_id=? WHERE id=?";
    private static final String DELETE_DEBTOR_SQL = "DELETE FROM Debtors WHERE id=?";
    private static final String SAVE_DEBTOR_SQL = "INSERT INTO Debtors (name, address, debt_amount, employee_id) VALUES (?, ?, ?, ?)";
    private static final String GET_DEBTOR_ID_BY_EMPLOYEE_ID_SQL = "SELECT id FROM Debtors WHERE employee_id = ?";

    public DebtorRepository() {
        this.connectionPool = new ConnectionPool();
    }

    public DebtorRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public List<Debtor> findAll() {
        List<Debtor> debtors = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_DEBTORS_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Debtor debtor = mapResultSetToDebtor(resultSet);
                debtors.add(debtor);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return debtors;
    }

    @Override
    public Debtor findOne(Long id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ONE_DEBTOR_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToDebtor(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Debtor update(Long id, Debtor updatedElement) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DEBTOR_SQL)) {
            preparedStatement.setString(1, updatedElement.getName());
            preparedStatement.setString(2, updatedElement.getAddress());
            preparedStatement.setInt(3, updatedElement.getDebtAmount());
            preparedStatement.setInt(4, updatedElement.getEmployeeId());
            preparedStatement.setLong(5, id);

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
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_DEBTOR_SQL)) {
            preparedStatement.setLong(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Debtor save(Debtor debtor) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_DEBTOR_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, debtor.getName());
            preparedStatement.setString(2, debtor.getAddress());
            preparedStatement.setInt(3, debtor.getDebtAmount());
            preparedStatement.setInt(4, debtor.getEmployeeId());

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                debtor.setId(generatedKeys.getLong(1));
                return debtor;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Integer> findDebtorIdsByEmployeeId(Long employeeId) {
        List<Integer> debtorIds = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_DEBTOR_ID_BY_EMPLOYEE_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                debtorIds.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return debtorIds;
    }

    private Debtor mapResultSetToDebtor(ResultSet resultSet) throws SQLException {
        Debtor debtor = new Debtor();
        debtor.setId(resultSet.getLong("id"));
        debtor.setName(resultSet.getString("name"));
        debtor.setAddress(resultSet.getString("address"));
        debtor.setDebtAmount(resultSet.getInt("debt_amount"));
        debtor.setEmployeeId(resultSet.getInt("employee_id"));
        return debtor;
    }
}
