package service;

import dto.DebtorDto;
import entity.Debtor;
import mapper.DebtorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.impl.DebtorRepository;
import service.impl.DebtorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DebtorServiceTest {
    @Mock
    private DebtorRepository debtorRepository;
    @Mock
    private DebtorMapper debtorMapper;
    @InjectMocks
    private DebtorService debtorService;
    @Spy
    private DebtorService spyService;

    private DebtorDto expectedDebtorDto;
    private Debtor expectedDebtor;

    @BeforeEach
    void setUp() {
        debtorService = new DebtorService(debtorRepository, debtorMapper);
        spyService = spy(debtorService);

        expectedDebtorDto = new DebtorDto().builder()
                .address("пушкина")
                .name("вася")
                .debtAmount(1000)
                .employeeId(1)
                .build();
        expectedDebtor = new Debtor(1L, "вася", "пушкина", 1000, 1);
    }

    @Test
    void testGetAll() {
        when(debtorRepository.findAll()).thenReturn(List.of(expectedDebtor));
        when(debtorMapper.toDto(expectedDebtor)).thenReturn(expectedDebtorDto);

        List<DebtorDto> employeeDtoList = spyService.getAll();

        verify(debtorMapper).toDto(expectedDebtor);
        assertEquals(List.of(expectedDebtorDto), employeeDtoList);
    }

    @Test
    void testGetById() {
        when(debtorRepository.findOne(1L)).thenReturn(expectedDebtor);
        when(spyService.getById(1L)).thenReturn(expectedDebtorDto);

        DebtorDto employeeDto = spyService.getById(1L);

        verify(debtorMapper).toDto(expectedDebtor);
        assertEquals(expectedDebtorDto, employeeDto);
    }

    @Test
    void testCreate() {
        when(debtorMapper.fromDto(expectedDebtorDto)).thenReturn(expectedDebtor);
        when(debtorRepository.save(expectedDebtor)).thenReturn(expectedDebtor);
        when(debtorMapper.toDto(expectedDebtor)).thenReturn(expectedDebtorDto);

        DebtorDto savedDebtorDto = spyService.create(expectedDebtorDto);

        verify(debtorRepository).save(expectedDebtor);
        verify(debtorMapper).toDto(expectedDebtor);
        verify(debtorMapper).fromDto(expectedDebtorDto);
        assertEquals(expectedDebtorDto, savedDebtorDto);
    }

    @Test
    void testUpdate() {
        when(debtorMapper.toDto(expectedDebtor)).thenReturn(expectedDebtorDto);
        when(debtorMapper.fromDto(expectedDebtorDto)).thenReturn(expectedDebtor);
        when(debtorRepository.update(eq(1L), eq(expectedDebtor))).thenReturn(expectedDebtor);

        DebtorDto result = spyService.update(1L, expectedDebtorDto);

        verify(debtorMapper).toDto(expectedDebtor);
        verify(debtorMapper).fromDto(expectedDebtorDto);
        verify(debtorRepository).update(eq(1L), eq(expectedDebtor));
        assertEquals(expectedDebtorDto, result);
    }

    @Test
    void testRemove() {
        when(debtorRepository.remove(1L)).thenReturn(true);

        boolean result = spyService.remove(1L);

        assertTrue(result);
    }
}
