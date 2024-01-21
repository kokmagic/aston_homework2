package mapper;

import dto.OfficeDto;
import entity.Office;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfficeMapperTest {
    @Mock
    private OfficeMapper officeMapper;

    private OfficeDto expectedOfficeDto;
    private Office expectedOffice;

    @BeforeEach
    void setUp() {
        expectedOfficeDto = new OfficeDto().builder()
                .address("дом колотушкина")
                .build();
        expectedOffice = new Office(null, "дом колотушкина", null);
    }

    @Test
    void toDtoTest() {
        when(officeMapper.toDto(expectedOffice)).thenReturn(expectedOfficeDto);
        OfficeDto officeDto = officeMapper.toDto(expectedOffice);
        assertEquals(expectedOfficeDto, officeDto);
    }

    @Test
    void fromDtoTest() {
        when(officeMapper.fromDto(expectedOfficeDto)).thenReturn(expectedOffice);
        Office office = officeMapper.fromDto(expectedOfficeDto);
        assertEquals(expectedOffice, office);
    }
}
