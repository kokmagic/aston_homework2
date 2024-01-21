package mapper;

import dto.OfficeDto;
import entity.Office;
import org.mapstruct.Mapper;

@Mapper
public interface OfficeMapper {
    OfficeDto toDto (Office office);
    Office fromDto (OfficeDto officeDto);
}
