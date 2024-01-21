package mapper;

import dto.DebtorDto;
import entity.Debtor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DebtorMapperTest {

    @Mock
    private DebtorMapper debtorMapper;

    private DebtorDto expectedDebtorDto;
    private Debtor expectedDebtor;

    @BeforeEach
    void setUp() {
        expectedDebtorDto = new DebtorDto().builder()
                .address("пушкина")
                .name("вася")
                .debtAmount(1000)
                .employeeId(1)
                .build();
        expectedDebtor = new Debtor(null, "вася", "пушкина", 1000, 1);
    }

    @Test
    void toDtoTest() {
        when(debtorMapper.toDto(expectedDebtor)).thenReturn(expectedDebtorDto);
        DebtorDto debtorDto = debtorMapper.toDto(expectedDebtor);
        assertEquals(expectedDebtorDto, debtorDto);
    }

    @Test
    void fromDtoTest() {
        when(debtorMapper.fromDto(expectedDebtorDto)).thenReturn(expectedDebtor);
        Debtor debtor = debtorMapper.fromDto(expectedDebtorDto);
        assertEquals(expectedDebtor, debtor);
    }
}
