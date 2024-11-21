package experton.ai.employee.dto;

import java.util.Date;

import experton.ai.employee.enums.Department;
import experton.ai.employee.enums.EmployeeStatus;
import lombok.Data;

@Data
public class EmployeeRequest {
    private String name;
    private Date dateOfJoining;
    private EmployeeStatus status;
    private Department department;
    private Double salary;
    private Integer managerId;
}
