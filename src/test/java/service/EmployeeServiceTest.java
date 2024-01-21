package service;

import dto.EmployeeDto;
import entity.Employee;
import mapper.EmployeeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.impl.EmployeeRepository;
import service.impl.EmployeeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeMapper employeeMapper;
    @InjectMocks
    private EmployeeService employeeService;
    @Spy
    private EmployeeService spyService;

    private EmployeeDto expectedEmployeeDto;
    private Employee expectedEmployee;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, employeeMapper);
        spyService = spy(employeeService);

        expectedEmployeeDto = new EmployeeDto().builder()
                .name("вася")
                .build();
        expectedEmployee = new Employee(1L, "вася", null, null);
    }

    @Test
    void testGetAll() {
        when(employeeRepository.findAll()).thenReturn(List.of(expectedEmployee));
        when(employeeMapper.toDto(expectedEmployee)).thenReturn(expectedEmployeeDto);

        List<EmployeeDto> employeeDtoList = spyService.getAll();

        verify(employeeMapper).toDto(expectedEmployee);
        assertEquals(List.of(expectedEmployeeDto), employeeDtoList);
    }

    @Test
    void testGetById() {
        when(employeeRepository.findOne(1L)).thenReturn(expectedEmployee);
        when(spyService.getById(1L)).thenReturn(expectedEmployeeDto);

        EmployeeDto employeeDto = spyService.getById(1L);

        verify(employeeMapper).toDto(expectedEmployee);
        assertEquals(expectedEmployeeDto, employeeDto);
    }

    @Test
    void testCreate() {
        when(employeeMapper.fromDto(expectedEmployeeDto)).thenReturn(expectedEmployee);
        when(employeeRepository.save(expectedEmployee)).thenReturn(expectedEmployee);
        when(employeeMapper.toDto(expectedEmployee)).thenReturn(expectedEmployeeDto);

        EmployeeDto savedEmployeeDto = spyService.create(expectedEmployeeDto);

        verify(employeeRepository).save(expectedEmployee);
        verify(employeeMapper).toDto(expectedEmployee);
        verify(employeeMapper).fromDto(expectedEmployeeDto);
        assertEquals(expectedEmployeeDto, savedEmployeeDto);
    }

    @Test
    void testUpdate() {
        when(employeeMapper.toDto(expectedEmployee)).thenReturn(expectedEmployeeDto);
        when(employeeMapper.fromDto(expectedEmployeeDto)).thenReturn(expectedEmployee);
        when(employeeRepository.update(eq(1L), eq(expectedEmployee))).thenReturn(expectedEmployee);

        EmployeeDto result = spyService.update(1L, expectedEmployeeDto);

        verify(employeeMapper).toDto(expectedEmployee);
        verify(employeeMapper).fromDto(expectedEmployeeDto);
        verify(employeeRepository).update(eq(1L), eq(expectedEmployee));
        assertEquals(expectedEmployeeDto, result);
    }

    @Test
    void testRemove() {
        when(employeeRepository.remove(1L)).thenReturn(true);

        boolean result = spyService.remove(1L);

        assertTrue(result);
    }
}
