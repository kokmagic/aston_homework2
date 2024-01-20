package entity;

import lombok.Data;

import java.util.List;

@Data
public class Employee {
    private Long id;
    private String name;
    private List<Long> debtorsIds;
    private List<Long> officesIds;
}
