package entity;

import lombok.Data;

@Data
public class Debtor {
    private Long id;
    private String name;
    private String address;
    private int debtAmount;
    private Long employeeId;
}
