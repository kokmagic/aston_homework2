package service;

import dto.DebtorDTO;
import entity.Debtor;
import mapper.DebtorMapper;
import mapper.Mapper;
import repository.DebtorRepository;
import repository.DebtorRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

public class DebtorServiceImpl implements DebtorService {

    private final DebtorRepository debtorRepository;
    private final Mapper<Debtor, DebtorDTO> debtorMapper;

    public DebtorServiceImpl() {
        this.debtorRepository = new DebtorRepositoryImpl();
        this.debtorMapper = new DebtorMapper();
    }

    public DebtorServiceImpl(DebtorRepository debtorRepository, Mapper<Debtor, DebtorDTO> debtorMapper) {
        this.debtorRepository = debtorRepository;
        this.debtorMapper = debtorMapper;
    }

    @Override
    public DebtorDTO getDebtorById(Long id) {
        Debtor debtor = debtorRepository.findById(id);
        return (debtor != null) ? debtorMapper.toDto(debtor) : null;
    }

    @Override
    public List<DebtorDTO> getAllDebtors() {
        return debtorRepository.findAll().stream()
                .map(debtorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveDebtor(DebtorDTO debtorDTO) {
        Debtor debtor = debtorMapper.toEntity(debtorDTO);
        debtorRepository.save(debtor);
    }

    @Override
    public void updateDebtor(DebtorDTO debtorDTO) {
        Debtor existingDebtor = debtorRepository.findById(debtorDTO.getId());
        if (existingDebtor != null) {
            Debtor updatedDebtor = debtorMapper.toEntity(debtorDTO);
            debtorRepository.update(updatedDebtor);
        }
    }

    @Override
    public boolean deleteDebtor(Long id) {
        return debtorRepository.remove(id);
    }
}

