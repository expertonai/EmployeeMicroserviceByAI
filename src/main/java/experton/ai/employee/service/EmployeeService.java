package experton.ai.employee.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import experton.ai.employee.dto.EmployeeRequest;
import experton.ai.employee.dto.EmployeeResponse;
import experton.ai.employee.dto.ValidationError;
import experton.ai.employee.enums.Department;
import experton.ai.employee.enums.EmployeeStatus;
import experton.ai.employee.enums.SortOrder;
import experton.ai.employee.exception.ValidationException;
import experton.ai.employee.model.Employee;
import experton.ai.employee.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee) {
        List<ValidationError> errors = validateEmployee(employee);
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
        return employeeRepository.save(employee);
    }

    public List<EmployeeResponse> getAllEmployees(String sortOrder) {
        if (sortOrder != null && !SortOrder.isValid(sortOrder)) {
            List<ValidationError> errors = new ArrayList<>();
            errors.add(new ValidationError("sort", 
                "Invalid sort order. Allowed values are: " + 
                Arrays.stream(SortOrder.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "))));
            throw new ValidationException("Invalid sort parameter", errors);
        }
        
        List<Employee> employees = employeeRepository.findAll();
        
        if (sortOrder != null) {
            employees = sortEmployeesBySalary(employees, sortOrder);
        }
        
        return employees.stream()
                .map(this::convertToEmployeeResponse)
                .collect(Collectors.toList());
    }

    private List<Employee> sortEmployeesBySalary(List<Employee> employees, String sortOrder) {
        Comparator<Employee> salaryComparator = Comparator.comparing(Employee::getSalary);
        
        if (SortOrder.DESC.name().equalsIgnoreCase(sortOrder)) {
            salaryComparator = salaryComparator.reversed();
        }
        
        return employees.stream()
                .sorted(salaryComparator)
                .collect(Collectors.toList());
    }

    public Optional<EmployeeResponse> getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .map(this::convertToEmployeeResponse);
    }

    public void deleteEmployee(Integer id) {
        if (!employeeRepository.existsById(id)) {
            List<ValidationError> errors = new ArrayList<>();
            errors.add(new ValidationError("id", "Employee not found with id: " + id));
            throw new ValidationException("Employee not found", errors);
        }
        employeeRepository.deleteById(id);
    }

    public Employee updateEmployee(Integer id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    List<ValidationError> errors = new ArrayList<>();
                    errors.add(new ValidationError("id", "Employee not found with id: " + id));
                    return new ValidationException("Employee not found", errors);
                });

        List<ValidationError> errors = new ArrayList<>();

        // Update and validate name
        if (request.getName() != null) {
            if (request.getName().trim().isEmpty()) {
                errors.add(new ValidationError("name", "Employee name cannot be empty"));
            } else {
                employee.setName(request.getName());
            }
        }

        // Update and validate dateOfJoining
        if (request.getDateOfJoining() != null) {
            employee.setDateOfJoining(request.getDateOfJoining());
        }

        // Update and validate status
        if (request.getStatus() != null) {
            try {
                employee.setStatus(request.getStatus());
            } catch (IllegalArgumentException e) {
                String allowedValues = Arrays.stream(EmployeeStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
                errors.add(new ValidationError("status", 
                    "Invalid status. Allowed values are: " + allowedValues));
            }
        }

        // Update and validate department
        if (request.getDepartment() != null) {
            try {
                employee.setDepartment(request.getDepartment());
            } catch (IllegalArgumentException e) {
                String allowedValues = Arrays.stream(Department.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
                errors.add(new ValidationError("department", 
                    "Invalid department. Allowed values are: " + allowedValues));
            }
        }

        // Update and validate salary
        if (request.getSalary() != null) {
            if (request.getSalary() < 0) {
                errors.add(new ValidationError("salary", 
                    "Salary must be a positive number"));
            } else {
                employee.setSalary(request.getSalary());
            }
        }

        // Update and validate managerId
        if (request.getManagerId() != null) {
            if (request.getManagerId().equals(id)) {
                errors.add(new ValidationError("managerId", 
                    "Employee cannot be their own manager"));
            } else if (!employeeRepository.existsById(request.getManagerId())) {
                errors.add(new ValidationError("managerId", 
                    "Manager not found with id: " + request.getManagerId()));
            } else {
                employee.setManagerId(request.getManagerId());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }

        return employeeRepository.save(employee);
    }

    private EmployeeResponse convertToEmployeeResponse(Employee employee) {
        return new EmployeeResponse(
            employee.getId(),
            employee.getName(),
            employee.getDateOfJoining(),
            employee.getStatus(),
            employee.getDepartment(),
            employee.getSalary(),
            employee.getManagerId()
        );
    }

    private List<ValidationError> validateEmployee(Employee employee) {
        List<ValidationError> errors = new ArrayList<>();

        // Validate name
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            errors.add(new ValidationError("name", "Employee name is mandatory"));
        }

        // Validate dateOfJoining
        if (employee.getDateOfJoining() == null) {
            errors.add(new ValidationError("dateOfJoining", 
                "Date of joining is mandatory. Format should be: yyyy-MM-dd"));
        }

        // Validate status
        if (employee.getStatus() == null) {
            String allowedValues = Arrays.stream(EmployeeStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
            errors.add(new ValidationError("status", 
                "Employee status is mandatory. Allowed values are: " + allowedValues));
        }

        // Validate department
        if (employee.getDepartment() == null) {
            String allowedValues = Arrays.stream(Department.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
            errors.add(new ValidationError("department", 
                "Department is mandatory. Allowed values are: " + allowedValues));
        }

        // Validate salary if provided
        if (employee.getSalary() != null && employee.getSalary() < 0) {
            errors.add(new ValidationError("salary", 
                "Salary must be a positive number"));
        }

        // Validate managerId if provided
        if (employee.getManagerId() != null) {
            if (employee.getId() != null && employee.getManagerId().equals(employee.getId())) {
                errors.add(new ValidationError("managerId", 
                    "Employee cannot be their own manager"));
            } else if (!employeeRepository.existsById(employee.getManagerId())) {
                errors.add(new ValidationError("managerId", 
                    "Manager not found with id: " + employee.getManagerId()));
            }
        }

        return errors;
    }
}
