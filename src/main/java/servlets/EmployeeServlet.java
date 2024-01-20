package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EmployeeDto;
import entity.Employee;
import mapper.DebtorMapper;
import mapper.EmployeeMapper;
import org.mapstruct.factory.Mappers;
import repository.impl.DebtorRepository;
import repository.impl.EmployeeRepository;
import service.impl.DebtorService;
import service.impl.EmployeeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/employees/*")
public class EmployeeServlet extends HttpServlet {


    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    public EmployeeServlet() {
        this.employeeService = new EmployeeService(new EmployeeRepository(), Mappers.getMapper(EmployeeMapper.class));
        this.objectMapper = new ObjectMapper();
    }

    public EmployeeServlet(EmployeeService employeeService) {
        this.employeeService = employeeService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EmployeeDto employeeDto = readRequestObject(request, EmployeeDto.class);
        EmployeeDto createdEmployee = employeeService.create(employeeDto);
        sendResponse(response, HttpServletResponse.SC_CREATED, createdEmployee);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<EmployeeDto> allEmployees = employeeService.getAll();
            sendResponse(response, HttpServletResponse.SC_OK, allEmployees);
        } else {
            String employeeIdString = pathInfo.substring(1); // убираем первый слеш
            Long employeeId = Long.parseLong(employeeIdString);
            EmployeeDto employee = employeeService.getById(employeeId);
            if (employee != null) {
                sendResponse(response, HttpServletResponse.SC_OK, employee);
            } else {
                sendResponse(response, HttpServletResponse.SC_NOT_FOUND, null);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String employeeIdString = pathInfo.substring(1); // убираем первый слеш
            Long employeeId = Long.parseLong(employeeIdString);

            EmployeeDto updatedEmployee = readRequestObject(request, EmployeeDto.class);
            EmployeeDto result = employeeService.update(employeeId, updatedEmployee);
            if (result != null) {
                sendResponse(response, HttpServletResponse.SC_OK, result);
            } else {
                sendResponse(response, HttpServletResponse.SC_NOT_FOUND, null);
            }
        } else {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, null);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String employeeIdString = pathInfo.substring(1); // убираем первый слеш
            Long employeeId = Long.parseLong(employeeIdString);

            boolean deleted = employeeService.remove(employeeId);
            if (deleted) {
                sendResponse(response, HttpServletResponse.SC_NO_CONTENT, null);
            } else {
                sendResponse(response, HttpServletResponse.SC_NOT_FOUND, null);
            }
        } else {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, null);
        }
    }

    private <T> T readRequestObject(HttpServletRequest request, Class<T> clazz) throws IOException {
        BufferedReader reader = request.getReader();
        return objectMapper.readValue(reader, clazz);
    }

    private void sendResponse(HttpServletResponse response, int status, Object responseObject) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        if (responseObject != null) {
            response.getWriter().write(objectMapper.writeValueAsString(responseObject));
        }
    }

//    private final EmployeeService employeeService;
//    private final Mapper<Employee, EmployeeDto> employeeMapper;
//
//    public EmployeeServlet() {
//        this.employeeService = new EmployeeService();
//        this.employeeMapper = new EmployeeMapper();
//    }
//
//    public EmployeeServlet(EmployeeService employeeService, Mapper<Employee, EmployeeDto> employeeMapper) {
//        this.employeeService = employeeService;
//        this.employeeMapper = employeeMapper;
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        List<EmployeeDto> employees = employeeService.getAllEmployees();
//
//        String employeesJson = new ObjectMapper().writeValueAsString(employees);
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(employeesJson);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        request.setCharacterEncoding("UTF-8");
//
//        BufferedReader reader = request.getReader();
//        StringBuilder requestBody = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            requestBody.append(line);
//        }
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        EmployeeDto newEmployeeDto = objectMapper.readValue(requestBody.toString(), EmployeeDto.class);
//
//        employeeService.saveEmployee(newEmployeeDto);
//
//        response.setCharacterEncoding("UTF-8");
//        response.setStatus(HttpServletResponse.SC_CREATED);
//        response.getWriter().write("Employee saved successfully");
//    }
//
//
//    @Override
//    protected void doPut(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        BufferedReader reader = request.getReader();
//        StringBuilder jsonRequest = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            jsonRequest.append(line);
//        }
//
//        EmployeeDto updatedEmployeeDto = new ObjectMapper().readValue(jsonRequest.toString(), EmployeeDto.class);
//
//        employeeService.updateEmployee(updatedEmployeeDto);
//
//        response.setStatus(HttpServletResponse.SC_OK);
//    }
//
//    @Override
//    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        Long employeeId = Long.parseLong(request.getParameter("id"));
//
//        boolean success = employeeService.deleteEmployee(employeeId);
//
//        if (success) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            response.getWriter().write("Employee deleted successfully");
//        } else {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.getWriter().write("Employee with this id not found");
//        }
//    }
}

