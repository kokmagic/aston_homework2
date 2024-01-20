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
    private List<Long> debtorsIds;
    private List<Long> officesIds;
}
