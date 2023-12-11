package service;

import dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {

    EmployeeDTO getEmployeeById(Long id);

    List<EmployeeDTO> getAllEmployees();

    void saveEmployee(EmployeeDTO employeeDTO);

    void updateEmployee(EmployeeDTO employeeDTO);

    boolean deleteEmployee(Long id);
}

