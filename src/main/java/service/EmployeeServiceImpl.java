package service;

import dto.EmployeeDTO;
import entity.Employee;
import mapper.EmployeeMapper;
import mapper.Mapper;
import repository.EmployeeRepository;
import repository.EmployeeRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final Mapper<Employee, EmployeeDTO> employeeMapper;

    public EmployeeServiceImpl() {
        this.employeeRepository = new EmployeeRepositoryImpl();
        this.employeeMapper = new EmployeeMapper();
    }

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, Mapper<Employee, EmployeeDTO> employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id);
        return (employee != null) ? employeeMapper.toDto(employee) : null;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employeeRepository.save(employee);
    }

    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee existingEmployee = employeeRepository.findById(employeeDTO.getId());
        if (existingEmployee != null) {
            Employee updatedEmployee = employeeMapper.toEntity(employeeDTO);
            employeeRepository.update(updatedEmployee);
        }
    }

    @Override
    public boolean deleteEmployee(Long id) {
        return employeeRepository.remove(id);
    }
}


