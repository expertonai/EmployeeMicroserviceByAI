package experton.ai.employee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
