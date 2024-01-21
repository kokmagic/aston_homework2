package entity;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Office {
    private Long id;
    private String address;
    private List<Integer> employeesIds;
}
