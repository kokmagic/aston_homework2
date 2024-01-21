package mapper;

import dto.EmployeeDto;
import entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeMapperTest {
    @Mock
    private EmployeeMapper employeeMapper;

    private EmployeeDto expectedEmployeeDto;
    private Employee expectedEmployee;

    @BeforeEach
    void setUp() {
        expectedEmployeeDto = new EmployeeDto().builder()
                .name("вася")
                .build();
        expectedEmployee = new Employee(null, "вася", null, null);
    }

    @Test
    void toDtoTest() {
        when(employeeMapper.toDto(expectedEmployee)).thenReturn(expectedEmployeeDto);
        EmployeeDto employeeDto = employeeMapper.toDto(expectedEmployee);
        assertEquals(expectedEmployeeDto, employeeDto);
    }

    @Test
    void fromDtoTest() {
        when(employeeMapper.fromDto(expectedEmployeeDto)).thenReturn(expectedEmployee);
        Employee employee = employeeMapper.fromDto(expectedEmployeeDto);
        assertEquals(expectedEmployee, employee);
    }
}
