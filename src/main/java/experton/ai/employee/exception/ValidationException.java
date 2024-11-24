package experton.ai.employee.exception;

import java.util.ArrayList;
import java.util.List;

import experton.ai.employee.dto.ValidationError;

public class ValidationException extends RuntimeException {
    final private List<ValidationError> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    public ValidationException(String message, List<ValidationError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
