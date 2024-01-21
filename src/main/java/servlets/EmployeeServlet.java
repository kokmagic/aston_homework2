package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EmployeeDto;
import mapper.EmployeeMapper;
import org.mapstruct.factory.Mappers;
import repository.impl.EmployeeRepository;
import service.impl.EmployeeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = StandardCharsets.UTF_8.name();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), encoding));
        return objectMapper.readValue(reader, clazz);
    }

    private void sendResponse(HttpServletResponse response, int status, Object responseObject) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        if (responseObject != null) {
            response.getWriter().write(objectMapper.writeValueAsString(responseObject));
        }
    }
}

