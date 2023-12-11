package repository;

import connection.ConnectionPool;
import entity.Debtor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DebtorRepositoryImpl implements DebtorRepository {

    private final ConnectionPool connectionPool;

    public DebtorRepositoryImpl() {
        this.connectionPool = new ConnectionPool();
    }

    public DebtorRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Debtor findById(Long id) {
        String sql = "SELECT * FROM debtor WHERE id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapDebtor(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Debtor> findAll() {
        List<Debtor> debtors = new ArrayList<>();
        String sql = "SELECT * FROM debtor";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                debtors.add(mapDebtor(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return debtors;
    }

    @Override
    public void save(Debtor debtor) {
        String sql = "INSERT INTO debtor (name, address, debt_amount) VALUES (?, ?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, debtor.getName());
            statement.setString(2, debtor.getAddress());
            statement.setInt(3, debtor.getDebtAmount());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Debtor debtor) {
        String sql = "UPDATE debtor SET name = ?, address = ?, debt_amount = ? WHERE id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, debtor.getName());
            statement.setString(2, debtor.getAddress());
            statement.setInt(3, debtor.getDebtAmount());
            statement.setLong(4, debtor.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean remove(Long id) {
        String sql = "DELETE FROM debtor WHERE id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Debtor mapDebtor(ResultSet resultSet) throws SQLException {
        Debtor debtor = new Debtor();
        debtor.setId(resultSet.getLong("id"));
        debtor.setName(resultSet.getString("name"));
        debtor.setAddress(resultSet.getString("address"));
        debtor.setDebtAmount(resultSet.getInt("debt_amount"));
        return debtor;
    }
}

