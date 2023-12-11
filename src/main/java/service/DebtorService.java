package service;

import dto.DebtorDTO;

import java.util.List;

public interface DebtorService {

    DebtorDTO getDebtorById(Long id);

    List<DebtorDTO> getAllDebtors();

    void saveDebtor(DebtorDTO debtorDTO);

    void updateDebtor(DebtorDTO debtorDTO);

    boolean deleteDebtor(Long id);
}

