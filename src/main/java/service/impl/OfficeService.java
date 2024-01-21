package service.impl;

import dto.OfficeDto;
import entity.Office;
import lombok.RequiredArgsConstructor;
import mapper.OfficeMapper;
import mapper.OfficeMapperImpl;
import repository.impl.OfficeRepository;
import service.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OfficeService implements Service<OfficeDto> {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    public OfficeService(){
        officeMapper = new OfficeMapperImpl();
        officeRepository = new OfficeRepository();
    }

    @Override
    public List<OfficeDto> getAll() {
        List<Office> offices = officeRepository.findAll();
        return offices.stream()
                .map(officeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OfficeDto getById(Long id) {
        Office office = officeRepository.findOne(id);
        return officeMapper.toDto(office);
    }

    @Override
    public OfficeDto update(Long id, OfficeDto updatedElement) {
        Office office = officeMapper.fromDto(updatedElement);
        Office updatedOffice = officeRepository.update(id, office);
        return officeMapper.toDto(updatedOffice);
    }

    @Override
    public boolean remove(Long id) {
        return officeRepository.remove(id);
    }

    @Override
    public OfficeDto create(OfficeDto officeDto) {
        Office office = officeMapper.fromDto(officeDto);
        Office createdOffice = officeRepository.save(office);
        return officeMapper.toDto(createdOffice);
    }
}
