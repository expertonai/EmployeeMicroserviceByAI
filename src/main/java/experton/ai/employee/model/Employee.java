package experton.ai.employee.model;

import java.time.LocalDate;

import experton.ai.employee.enums.Department;
import experton.ai.employee.enums.EmployeeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmployeeStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Department department;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "manager_id")
    private Integer managerId;
}
