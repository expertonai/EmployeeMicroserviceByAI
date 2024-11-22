package experton.ai.employee.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import experton.ai.employee.dto.EmployeeRequest;
import experton.ai.employee.dto.EmployeeResponse;
import experton.ai.employee.dto.ErrorResponse;
import experton.ai.employee.enums.SortOrder;
import experton.ai.employee.exception.ValidationException;
import experton.ai.employee.model.Employee;
import experton.ai.employee.service.EmployeeService;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Object> getAllEmployees(@RequestParam(required = false) String sort) {
        if (sort != null && !SortOrder.isValid(sort)) {
            ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Invalid sort order. Allowed values are: 'asc' or 'desc'",
                HttpStatus.BAD_REQUEST.toString()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        
        List<EmployeeResponse> employees = employeeService.getAllEmployees(sort);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEmployeeById(@PathVariable Integer id) {
        var employeeOptional = employeeService.getEmployeeById(id);
        if (employeeOptional.isPresent()) {
            return ResponseEntity.ok(employeeOptional.get());
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            "Employee not found with id: " + id,
            HttpStatus.NOT_FOUND.toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        Employee employee = convertToEntity(employeeRequest);
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateEmployee(@PathVariable Integer id, @RequestBody EmployeeRequest employeeRequest) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeRequest);
            return ResponseEntity.ok(updatedEmployee);
        } catch (ValidationException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.toString()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable Integer id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (ValidationException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.toString()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.toString()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private Employee convertToEntity(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setDateOfJoining(request.getDateOfJoining());
        employee.setStatus(request.getStatus());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());
        employee.setManagerId(request.getManagerId());
        return employee;
    }
}
