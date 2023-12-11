package mapper;

import dto.DebtorDTO;
import entity.Debtor;

public class DebtorMapper implements Mapper<Debtor, DebtorDTO> {

    @Override
    public Debtor toEntity(DebtorDTO dto) {
        Debtor debtor = new Debtor();
        debtor.setId(dto.getId());
        debtor.setName(dto.getName());
        debtor.setAddress(dto.getAddress());
        debtor.setDebtAmount(dto.getDebtAmount());
        return debtor;
    }

    @Override
    public DebtorDTO toDto(Debtor debtor) {
        return DebtorDTO.builder()
                .id(debtor.getId())
                .name(debtor.getName())
                .address(debtor.getAddress())
                .debtAmount(debtor.getDebtAmount())
                .build();
    }
}

