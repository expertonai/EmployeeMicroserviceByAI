package experton.ai.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import experton.ai.employee.exception.ValidationException;
import experton.ai.employee.model.Employee;
import experton.ai.employee.service.EmployeeService;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(@RequestParam(required = false) String sort) {
        List<EmployeeResponse> employees = employeeService.getAllEmployees(sort);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ValidationException("Employee not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        Employee employee = convertToEntity(employeeRequest);
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Integer id, @RequestBody EmployeeRequest employeeRequest) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employeeRequest);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
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
