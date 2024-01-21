package repository;

import connection.ConnectionPool;
import entity.Debtor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.impl.DebtorRepository;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DebtorRepositoryTest {

    @InjectMocks
    private DebtorRepository debtorRepository;

    @Spy
    private DebtorRepository spyRepository;

    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private Debtor expectedDebtor;

    @BeforeEach
    public void setUp() throws SQLException {
        debtorRepository = new DebtorRepository(connectionPool);
        spyRepository = spy(debtorRepository);
        expectedDebtor = new Debtor(1L, "вася", "пушкина", 1000, 1);
    }

    @Test
    public void testFindAll() {
        try {
            Field sqlField = DebtorRepository.class.getDeclaredField("GET_ALL_DEBTORS_SQL");
            sqlField.setAccessible(true);
            String getAllDebtorsSql = (String) sqlField.get(debtorRepository);

            when(connectionPool.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            List<Debtor> debtorList = debtorRepository.findAll();

            assertNotNull(debtorList);
            verify(connection).prepareStatement(eq(getAllDebtorsSql));
            verify(preparedStatement).executeQuery();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFindOne() {
        try {
            Field sqlField = DebtorRepository.class.getDeclaredField("GET_ONE_DEBTOR_SQL");
            sqlField.setAccessible(true);
            String getDebtorByIdSql = (String) sqlField.get(debtorRepository);

            when(connectionPool.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(spyRepository.findOne(1L)).thenReturn(expectedDebtor);

            Debtor debtor = spyRepository.findOne(1L);

            assertNotNull(debtor);
            assertEquals(1, debtor.getId());
            assertEquals("вася", debtor.getName());
            assertEquals(1000, debtor.getDebtAmount());
            assertEquals(1, debtor.getId());
            verify(connection).prepareStatement(eq(getDebtorByIdSql));
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSave() {
        try {
            Field sqlField = DebtorRepository.class.getDeclaredField("SAVE_DEBTOR_SQL");
            sqlField.setAccessible(true);
            String saveDebtorSql = (String) sqlField.get(debtorRepository);

            Mockito.doReturn(connection).when(connectionPool).getConnection();
            Mockito.doReturn(preparedStatement).when(connection).prepareStatement(eq(saveDebtorSql), eq(Statement.RETURN_GENERATED_KEYS));
            Mockito.doReturn(resultSet).when(preparedStatement).getGeneratedKeys();
            Mockito.doReturn(true).when(resultSet).next();


            Debtor savedDebtor = debtorRepository.save(expectedDebtor);

            assertNotNull(savedDebtor);
            Mockito.verify(connection).prepareStatement(eq(saveDebtorSql), eq(Statement.RETURN_GENERATED_KEYS));
            Mockito.verify(preparedStatement).setString(1, expectedDebtor.getName());
            Mockito.verify(preparedStatement).executeUpdate();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testUpdate() {
        try {
            Field sqlField = DebtorRepository.class.getDeclaredField("UPDATE_DEBTOR_SQL");
            sqlField.setAccessible(true);
            String updateDebtorSql = (String) sqlField.get(debtorRepository);

            Mockito.doReturn(connection).when(connectionPool).getConnection();
            Mockito.doReturn(preparedStatement).when(connection).prepareStatement(anyString());
            Mockito.doReturn(1).when(preparedStatement).executeUpdate();

            Debtor updatedDebtor = new Debtor();
            updatedDebtor.setId(1L);
            updatedDebtor.setName("вася пупкин");
            updatedDebtor.setAddress("дом колотушкина");
            updatedDebtor.setDebtAmount(1000);
            updatedDebtor.setEmployeeId(1);

            Debtor result = debtorRepository.update(1L, updatedDebtor);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("вася пупкин", result.getName());
            assertEquals(1, result.getEmployeeId());
            verify(connection).prepareStatement(eq(updateDebtorSql));
            verify(preparedStatement).executeUpdate();
            verify(preparedStatement).setString(eq(1), eq("вася пупкин"));
            verify(preparedStatement).setString(eq(2), eq("дом колотушкина"));
            verify(preparedStatement).setInt(eq(3), eq(1000));
            verify(preparedStatement).setInt(eq(4), eq(1));
            verify(preparedStatement).setLong(eq(5), eq(1L));
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRemove() {
        try {
            Field sqlField = DebtorRepository.class.getDeclaredField("DELETE_DEBTOR_SQL");
            sqlField.setAccessible(true);
            String removeDebtorSql = (String) sqlField.get(debtorRepository);

            when(connectionPool.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);

            boolean result = debtorRepository.remove(1L);

            assertTrue(result);
            verify(connection).prepareStatement(eq(removeDebtorSql));
            verify(preparedStatement).setLong(eq(1), eq(1L));
            verify(preparedStatement).executeUpdate();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

