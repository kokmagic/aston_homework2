package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.DebtorDto;
import entity.Debtor;
import mapper.DebtorMapper;
import org.mapstruct.factory.Mappers;
import repository.impl.DebtorRepository;
import service.impl.DebtorService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/debtors/*")
public class DebtorServlet extends HttpServlet {
    private final DebtorService debtorService;
    private final ObjectMapper objectMapper;

    public DebtorServlet() {
        this.debtorService = new DebtorService(new DebtorRepository(), Mappers.getMapper(DebtorMapper.class));
        this.objectMapper = new ObjectMapper();
    }

    public DebtorServlet(DebtorService debtorService) {
        this.debtorService = debtorService;
        this.objectMapper = new ObjectMapper();
    }


    //    private final DebtorService debtorService;
//    private final ObjectMapper objectMapper;

//    public DebtorServlet(DebtorService debtorService, ObjectMapper objectMapper) {
//        this.debtorService = debtorService;
//        this.objectMapper = objectMapper;
//    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DebtorDto debtorDto = readRequestObject(request, DebtorDto.class);
        DebtorDto createdDebtor = debtorService.create(debtorDto);
        sendResponse(response, HttpServletResponse.SC_CREATED, createdDebtor);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<DebtorDto> allDebtors = debtorService.getAll();
            sendResponse(response, HttpServletResponse.SC_OK, allDebtors);
        } else {
            String debtorIdString = pathInfo.substring(1); // убираем первый слеш
            Long debtorId = Long.parseLong(debtorIdString);
            DebtorDto debtor = debtorService.getById(debtorId);
            if (debtor != null) {
                sendResponse(response, HttpServletResponse.SC_OK, debtor);
            } else {
                sendResponse(response, HttpServletResponse.SC_NOT_FOUND, null);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String debtorIdString = pathInfo.substring(1); // убираем первый слеш
            Long debtorId = Long.parseLong(debtorIdString);

            DebtorDto updatedDebtor = readRequestObject(request, DebtorDto.class);
            DebtorDto result = debtorService.update(debtorId, updatedDebtor);
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
            String debtorIdString = pathInfo.substring(1); // убираем первый слеш
            Long debtorId = Long.parseLong(debtorIdString);

            boolean deleted = debtorService.remove(debtorId);
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
}

