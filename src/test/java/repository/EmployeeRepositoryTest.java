package repository;

import connection.ConnectionPool;
import entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.impl.EmployeeRepository;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeRepositoryTest {
    @InjectMocks
    private EmployeeRepository employeeRepository;

    @Spy
    private EmployeeRepository spyRepository;

    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private Employee expectedEmployee;

    @BeforeEach
    public void setUp() throws SQLException {
        employeeRepository = new EmployeeRepository(connectionPool);
        spyRepository = spy(employeeRepository);
        expectedEmployee = new Employee(1L, "васек", null, null);

    }

    @Test
    public void testFindAll() {
        try {
            Field sqlField = EmployeeRepository.class.getDeclaredField("GET_ALL_EMPLOYEES_SQL");
            sqlField.setAccessible(true);
            String getAllEmployeesSql = (String) sqlField.get(employeeRepository);

            when(connectionPool.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            List<Employee> employeeList = employeeRepository.findAll();

            assertNotNull(employeeList);
            verify(connection).prepareStatement(eq(getAllEmployeesSql));
            verify(preparedStatement).executeQuery();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFindOne() {
        try {
            Field sqlField = EmployeeRepository.class.getDeclaredField("GET_ONE_EMPLOYEE_SQL");
            sqlField.setAccessible(true);
            String getEmployeeByIdSql = (String) sqlField.get(employeeRepository);

            when(connectionPool.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(spyRepository.findOne(1L)).thenReturn(expectedEmployee);

            Employee employee = spyRepository.findOne(1L);

            assertNotNull(employee);
            assertEquals(1, employee.getId());
            assertEquals("васек", employee.getName());
            verify(connection).prepareStatement(eq(getEmployeeByIdSql));
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSave() {
        try {
            Field sqlField = EmployeeRepository.class.getDeclaredField("SAVE_EMPLOYEE_SQL");
            sqlField.setAccessible(true);
            String saveEmployeeSql = (String) sqlField.get(employeeRepository);

            Mockito.doReturn(connection).when(connectionPool).getConnection();
            Mockito.doReturn(preparedStatement).when(connection).prepareStatement(eq(saveEmployeeSql), eq(Statement.RETURN_GENERATED_KEYS));
            Mockito.doReturn(resultSet).when(preparedStatement).getGeneratedKeys();
            Mockito.doReturn(true).when(resultSet).next();
            Mockito.doReturn(1L).when(resultSet).getLong(1); // Возвращает положительное значение (id)

            Employee savedEmployee = employeeRepository.save(expectedEmployee);

            assertNotNull(savedEmployee);
            assertNotNull(savedEmployee.getId()); // Проверка, что id не null
            Mockito.verify(connection).prepareStatement(eq(saveEmployeeSql), eq(Statement.RETURN_GENERATED_KEYS));
            Mockito.verify(preparedStatement).setString(1, expectedEmployee.getName());
            Mockito.verify(preparedStatement).executeUpdate();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
