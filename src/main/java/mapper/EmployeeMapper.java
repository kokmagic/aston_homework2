package mapper;

import dto.EmployeeDTO;
import entity.Employee;

public class EmployeeMapper implements Mapper<Employee, EmployeeDTO> {

    @Override
    public Employee toEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setName(dto.getName());
        return employee;
    }

    @Override
    public EmployeeDTO toDto(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .build();
    }
}

