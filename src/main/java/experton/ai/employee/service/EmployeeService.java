package experton.ai.employee.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import experton.ai.employee.dto.EmployeeRequest;
import experton.ai.employee.dto.EmployeeResponse;
import experton.ai.employee.enums.SortOrder;
import experton.ai.employee.exception.ValidationException;
import experton.ai.employee.model.Employee;
import experton.ai.employee.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee) {
        validateEmployee(employee);
        return employeeRepository.save(employee);
    }

    public List<EmployeeResponse> getAllEmployees(String sortOrder) {
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
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    public Employee updateEmployee(Integer id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Employee not found with id: " + id));

        // Update only the non-null fields
        if (request.getName() != null) {
            if (request.getName().trim().isEmpty()) {
                throw new ValidationException("Employee name cannot be empty");
            }
            employee.setName(request.getName());
        }

        if (request.getDateOfJoining() != null) {
            employee.setDateOfJoining(request.getDateOfJoining());
        }

        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }

        if (request.getDepartment() != null) {
            employee.setDepartment(request.getDepartment());
        }

        if (request.getSalary() != null) {
            if (request.getSalary() < 0) {
                throw new ValidationException("Salary cannot be negative");
            }
            employee.setSalary(request.getSalary());
        }

        if (request.getManagerId() != null) {
            if (request.getManagerId().equals(id)) {
                throw new ValidationException("Employee cannot be their own manager");
            }
            // Optionally validate if manager exists
            if (!employeeRepository.existsById(request.getManagerId())) {
                throw new ValidationException("Manager not found with id: " + request.getManagerId());
            }
            employee.setManagerId(request.getManagerId());
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

    private void validateEmployee(Employee employee) {
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new ValidationException("Employee name is mandatory");
        }

        if (employee.getDateOfJoining() == null) {
            throw new ValidationException("Date of joining is mandatory");
        }

        if (employee.getStatus() == null) {
            throw new ValidationException("Employee status is mandatory");
        }

        if (employee.getDepartment() == null) {
            throw new ValidationException("Department is mandatory");
        }

        // Optional fields don't need validation as they can be null
    }
}
