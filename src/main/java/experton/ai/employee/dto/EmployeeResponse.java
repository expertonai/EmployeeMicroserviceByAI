package experton.ai.employee.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import experton.ai.employee.enums.Department;
import experton.ai.employee.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {
    private Integer id;
    private String name;
    private Date dateOfJoining;
    private EmployeeStatus status;
    private Department department;
    private Double salary;
    private Integer managerId;
}
