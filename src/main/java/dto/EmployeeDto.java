package dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private String name;
    private List<Integer> debtorsIds;
    private List<Integer> officesIds;
}
