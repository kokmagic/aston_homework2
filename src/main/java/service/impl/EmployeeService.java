package service.impl;

import dto.EmployeeDto;
import entity.Employee;
import lombok.RequiredArgsConstructor;
import mapper.EmployeeMapper;
import mapper.EmployeeMapperImpl;
import repository.impl.EmployeeRepository;
import service.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EmployeeService implements Service<EmployeeDto> {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(){
        employeeMapper = new EmployeeMapperImpl();
        employeeRepository = new EmployeeRepository();
    }

    @Override
    public List<EmployeeDto> getAll() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto getById(Long id) {
        Employee employee = employeeRepository.findOne(id);
        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto update(Long id, EmployeeDto updatedElement) {
        Employee employee = employeeMapper.fromDto(updatedElement);
        Employee updatedEmployee = employeeRepository.update(id, employee);
        return employeeMapper.toDto(updatedEmployee);
    }

    @Override
    public boolean remove(Long id) {
        return employeeRepository.remove(id);
    }

    @Override
    public EmployeeDto create(EmployeeDto employeeDto) {
        Employee employee = employeeMapper.fromDto(employeeDto);
        Employee createdEmployee = employeeRepository.save(employee);
        return employeeMapper.toDto(createdEmployee);
    }
}


