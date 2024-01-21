package service;

import dto.OfficeDto;
import entity.Office;
import mapper.OfficeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.impl.OfficeRepository;
import service.impl.OfficeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OfficeServiceTest {

    @Mock
    private OfficeRepository officeRepository;
    @Mock
    private OfficeMapper officeMapper;
    @InjectMocks
    private OfficeService officeService;
    @Spy
    private OfficeService spyService;

    private OfficeDto expectedOfficeDto;
    private Office expectedOffice;

    @BeforeEach
    void setUp() {
        officeService = new OfficeService(officeRepository, officeMapper);
        spyService = spy(officeService);

        expectedOfficeDto = new OfficeDto().builder()
                .address("дом колотушкина")
                .build();
        expectedOffice = new Office(1L, "дом колотушкина", null);
    }

    @Test
    void testGetAll() {
        when(officeRepository.findAll()).thenReturn(List.of(expectedOffice));
        when(officeMapper.toDto(expectedOffice)).thenReturn(expectedOfficeDto);

        List<OfficeDto> officeDtoList = spyService.getAll();

        verify(officeMapper).toDto(expectedOffice);
        assertEquals(List.of(expectedOfficeDto), officeDtoList);
    }

    @Test
    void testGetById() {
        when(officeRepository.findOne(1L)).thenReturn(expectedOffice);
        when(spyService.getById(1L)).thenReturn(expectedOfficeDto);

        OfficeDto officeDto = spyService.getById(1L);

        verify(officeMapper).toDto(expectedOffice);
        assertEquals(expectedOfficeDto, officeDto);
    }

    @Test
    void testCreate() {
        when(officeMapper.fromDto(expectedOfficeDto)).thenReturn(expectedOffice);
        when(officeRepository.save(expectedOffice)).thenReturn(expectedOffice);
        when(officeMapper.toDto(expectedOffice)).thenReturn(expectedOfficeDto);

        OfficeDto savedOfficeDto = spyService.create(expectedOfficeDto);

        verify(officeRepository).save(expectedOffice);
        verify(officeMapper).toDto(expectedOffice);
        verify(officeMapper).fromDto(expectedOfficeDto);
        assertEquals(expectedOfficeDto, savedOfficeDto);
    }

    @Test
    void testUpdate() {
        when(officeMapper.toDto(expectedOffice)).thenReturn(expectedOfficeDto);
        when(officeMapper.fromDto(expectedOfficeDto)).thenReturn(expectedOffice);
        when(officeRepository.update(eq(1L), eq(expectedOffice))).thenReturn(expectedOffice);

        OfficeDto result = spyService.update(1L, expectedOfficeDto);

        verify(officeMapper).toDto(expectedOffice);
        verify(officeMapper).fromDto(expectedOfficeDto);
        verify(officeRepository).update(eq(1L), eq(expectedOffice));
        assertEquals(expectedOfficeDto, result);
    }

    @Test
    void testRemove() {
        when(officeRepository.remove(1L)).thenReturn(true);

        boolean result = spyService.remove(1L);

        assertTrue(result);
    }
}
