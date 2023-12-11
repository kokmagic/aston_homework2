package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EmployeeDTO;
import entity.Employee;
import mapper.EmployeeMapper;
import mapper.Mapper;
import service.EmployeeService;
import service.EmployeeServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/employees/*")
public class EmployeeServlet extends HttpServlet {

    private final EmployeeService employeeService;
    private final Mapper<Employee, EmployeeDTO> employeeMapper;

    public EmployeeServlet() {
        this.employeeService = new EmployeeServiceImpl();
        this.employeeMapper = new EmployeeMapper();
    }

    public EmployeeServlet(EmployeeService employeeService, Mapper<Employee, EmployeeDTO> employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();

        String employeesJson = new ObjectMapper().writeValueAsString(employees);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(employeesJson);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        EmployeeDTO newEmployeeDTO = objectMapper.readValue(requestBody.toString(), EmployeeDTO.class);

        employeeService.saveEmployee(newEmployeeDTO);

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("Employee saved successfully");
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        StringBuilder jsonRequest = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonRequest.append(line);
        }

        EmployeeDTO updatedEmployeeDTO = new ObjectMapper().readValue(jsonRequest.toString(), EmployeeDTO.class);

        employeeService.updateEmployee(updatedEmployeeDTO);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long employeeId = Long.parseLong(request.getParameter("id"));

        boolean success = employeeService.deleteEmployee(employeeId);

        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Employee deleted successfully");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Employee with this id not found");
        }
    }
}

