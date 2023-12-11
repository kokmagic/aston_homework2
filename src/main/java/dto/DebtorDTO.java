package dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtorDTO {
    private Long id;
    private String name;
    private String address;
    private int debtAmount;
}
