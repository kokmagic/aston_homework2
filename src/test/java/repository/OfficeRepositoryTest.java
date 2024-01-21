package repository;

import connection.ConnectionPool;
import entity.Office;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.impl.OfficeRepository;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OfficeRepositoryTest {

    @InjectMocks
    private OfficeRepository officeRepository;

    @Spy
    private OfficeRepository spyRepository;

    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private Office expectedOffice;

    @BeforeEach
    public void setUp() throws SQLException {
        officeRepository = new OfficeRepository(connectionPool);
        spyRepository = spy(officeRepository);
        expectedOffice = new Office(1L, "колотушкина", null);
    }

    @Test
    public void testFindAll() {
        try {
            Field sqlField = OfficeRepository.class.getDeclaredField("GET_ALL_OFFICES_SQL");
            sqlField.setAccessible(true);
            String getAllOfficesSql = (String) sqlField.get(officeRepository);

            when(connectionPool.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);

            List<Office> officeList = officeRepository.findAll();

            assertNotNull(officeList);
            verify(connection).prepareStatement(eq(getAllOfficesSql));
            verify(preparedStatement).executeQuery();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFindOne() {
        try {
            Field sqlField = OfficeRepository.class.getDeclaredField("GET_ONE_OFFICE_SQL");
            sqlField.setAccessible(true);
            String getOfficeByIdSql = (String) sqlField.get(officeRepository);

            when(connectionPool.getConnection()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(spyRepository.findOne(1L)).thenReturn(expectedOffice);

            Office office = spyRepository.findOne(1L);

            assertNotNull(office);
            assertEquals(1, office.getId());
            assertEquals("колотушкина", office.getAddress());
            verify(connection).prepareStatement(eq(getOfficeByIdSql));
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSave() {
        try {
            Field sqlField = OfficeRepository.class.getDeclaredField("SAVE_OFFICE_SQL");
            sqlField.setAccessible(true);
            String saveOfficeSql = (String) sqlField.get(officeRepository);

            Mockito.doReturn(connection).when(connectionPool).getConnection();
            Mockito.doReturn(preparedStatement).when(connection).prepareStatement(eq(saveOfficeSql), eq(Statement.RETURN_GENERATED_KEYS));
            Mockito.doReturn(resultSet).when(preparedStatement).getGeneratedKeys();
            Mockito.doReturn(true).when(resultSet).next();
            Mockito.doReturn(1L).when(resultSet).getLong(1);

            Office savedOffice = officeRepository.save(expectedOffice);

            assertNotNull(savedOffice);
            assertNotNull(savedOffice.getId());
            Mockito.verify(connection).prepareStatement(eq(saveOfficeSql), eq(Statement.RETURN_GENERATED_KEYS));
            Mockito.verify(preparedStatement).setString(1, expectedOffice.getAddress());
            Mockito.verify(preparedStatement).executeUpdate();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
