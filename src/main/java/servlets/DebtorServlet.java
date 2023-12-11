package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.DebtorDTO;
import entity.Debtor;
import mapper.DebtorMapper;
import mapper.Mapper;
import service.DebtorService;
import service.DebtorServiceImpl;

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
    private final Mapper<Debtor, DebtorDTO> debtorMapper;

    public DebtorServlet() {
        this.debtorService = new DebtorServiceImpl();
        this.debtorMapper = new DebtorMapper();
    }

    public DebtorServlet(DebtorService debtorService, Mapper<Debtor, DebtorDTO> debtorMapper) {
        this.debtorService = debtorService;
        this.debtorMapper = debtorMapper;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<DebtorDTO> debtors = debtorService.getAllDebtors();
        String debtorsJson = new ObjectMapper().writeValueAsString(debtors);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(debtorsJson);
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
        DebtorDTO newDebtorDTO = objectMapper.readValue(requestBody.toString(), DebtorDTO.class);


        debtorService.saveDebtor(newDebtorDTO);

        response.setCharacterEncoding("UTF-8");

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("Debtor saved successfully");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        DebtorDTO updatedDebtorDTO = objectMapper.readValue(requestBody.toString(), DebtorDTO.class);

        Debtor updatedDebtor = debtorMapper.toEntity(updatedDebtorDTO);

        debtorService.updateDebtor(updatedDebtorDTO);

        response.setCharacterEncoding("UTF-8");

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Debtor updated successfully");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long debtorId = Long.parseLong(request.getParameter("id"));

        boolean success = debtorService.deleteDebtor(debtorId);

        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Debtor deleted successfully");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Debtor with this id not found");
        }
    }
}

