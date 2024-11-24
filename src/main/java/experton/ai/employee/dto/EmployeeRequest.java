package experton.ai.employee.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import experton.ai.employee.enums.Department;
import experton.ai.employee.enums.EmployeeStatus;
import lombok.Data;

@Data
public class EmployeeRequest {
    private String name;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfJoining;
    
    private EmployeeStatus status;
    private Department department;
    private Double salary;
    private Integer managerId;
}
