package experton.ai.employee.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import experton.ai.employee.dto.ErrorResponse;
import experton.ai.employee.dto.ValidationError;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.toString(),
            "Validation failed",
            ex.getErrors()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        List<ValidationError> errors = new ArrayList<>();
        String message = ex.getMessage();
        String field = "";
        String errorMessage = "";

        if (message.contains("EmployeeStatus")) {
            field = "status";
            errorMessage = "Invalid status value. Allowed values are: Active, Not_Active";
        } else if (message.contains("Department")) {
            field = "department";
            errorMessage = "Invalid department value. Allowed values are: HR, IT, Finance, Sales, Marketing";
        } else if (message.contains("LocalDate")) {
            field = "dateOfJoining";
            errorMessage = "Invalid date format. Required format is: yyyy-MM-dd";
        } else {
            field = "request";
            errorMessage = "Invalid request format";
        }

        errors.add(new ValidationError(field, errorMessage));

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.toString(),
            "Invalid request format",
            errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        List<ValidationError> errors = new ArrayList<>();
        errors.add(new ValidationError("error", ex.getMessage()));

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            "An unexpected error occurred",
            errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
