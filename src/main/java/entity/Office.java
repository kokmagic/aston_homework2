package entity;

import lombok.Data;

import java.util.List;

@Data
public class Office {
    private Long id;
    private String address;
    private List<Long> employeesIds;
}
