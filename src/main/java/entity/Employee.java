package entity;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {
    private Long id;
    private String name;
    private List<Integer> debtorsIds;
    private List<Integer> officesIds;
}
