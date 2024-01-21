package mapper;

import dto.EmployeeDto;
import entity.Employee;
import org.mapstruct.Mapper;

@Mapper
public interface EmployeeMapper {
    EmployeeDto toDto (Employee employee);
    Employee fromDto (EmployeeDto employeeDto);
}
