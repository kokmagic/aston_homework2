package mapper;

import dto.DebtorDto;
import entity.Debtor;
import org.mapstruct.Mapper;

@Mapper
public interface DebtorMapper {
    DebtorDto toDto (Debtor debtor);
    Debtor fromDto (DebtorDto debtorDTO);
}
