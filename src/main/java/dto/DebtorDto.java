package dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtorDto {
    private String name;
    private String address;
    private int debtAmount;
    private Long employeeId;
}
