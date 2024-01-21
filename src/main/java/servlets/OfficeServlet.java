package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.OfficeDto;
import mapper.OfficeMapper;
import org.mapstruct.factory.Mappers;
import repository.impl.OfficeRepository;
import service.impl.OfficeService;

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

@WebServlet("/offices/*")
public class OfficeServlet extends HttpServlet {

    private final OfficeService officeService;
    private final ObjectMapper objectMapper;

    public OfficeServlet() {
        this.officeService = new OfficeService(new OfficeRepository(), Mappers.getMapper(OfficeMapper.class));
        this.objectMapper = new ObjectMapper();
    }

    public OfficeServlet(OfficeService officeService) {
        this.officeService = officeService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OfficeDto officeDto = readRequestObject(request, OfficeDto.class);
        OfficeDto createdOffice = officeService.create(officeDto);
        sendResponse(response, HttpServletResponse.SC_CREATED, createdOffice);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<OfficeDto> allOffices = officeService.getAll();
            sendResponse(response, HttpServletResponse.SC_OK, allOffices);
        } else {
            String officeIdString = pathInfo.substring(1);
            Long officeId = Long.parseLong(officeIdString);
            OfficeDto office = officeService.getById(officeId);
            if (office != null) {
                sendResponse(response, HttpServletResponse.SC_OK, office);
            } else {
                sendResponse(response, HttpServletResponse.SC_NOT_FOUND, null);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String officeIdString = pathInfo.substring(1);
            Long officeId = Long.parseLong(officeIdString);

            OfficeDto updatedOffice = readRequestObject(request, OfficeDto.class);
            OfficeDto result = officeService.update(officeId, updatedOffice);
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
            String officeIdString = pathInfo.substring(1); // убираем первый слеш
            Long officeId = Long.parseLong(officeIdString);

            boolean deleted = officeService.remove(officeId);
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

