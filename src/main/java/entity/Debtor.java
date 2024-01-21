package entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Debtor {
    private Long id;
    private String name;
    private String address;
    private int debtAmount;
    private Integer employeeId;
}
