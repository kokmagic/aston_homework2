package service.impl;

import dto.DebtorDto;
import entity.Debtor;
import lombok.RequiredArgsConstructor;
import mapper.DebtorMapper;
import repository.impl.DebtorRepository;
import service.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DebtorService implements Service<DebtorDto> {

    private final DebtorRepository debtorRepository;
    private final DebtorMapper debtorMapper;

    @Override
    public List<DebtorDto> getAll() {
        List<Debtor> debtors = debtorRepository.findAll();
        return debtors.stream()
                .map(debtorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DebtorDto getById(Long id) {
        Debtor debtor = debtorRepository.findOne(id);
        return debtorMapper.toDto(debtor);
    }


    @Override
    public DebtorDto update(Long id, DebtorDto updatedElement) {
        Debtor debtor = debtorMapper.fromDto(updatedElement);
        Debtor updatedDebtor = debtorRepository.update(id, debtor);
        return debtorMapper.toDto(updatedDebtor);
    }

    @Override
    public boolean remove(Long id) {
        return debtorRepository.remove(id);
    }

    @Override
    public DebtorDto create(DebtorDto debtorDto) {
        Debtor debtor = debtorMapper.fromDto(debtorDto);
        Debtor createdDebtor = debtorRepository.save(debtor);
        return debtorMapper.toDto(createdDebtor);
    }
}

