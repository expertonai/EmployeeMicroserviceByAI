package experton.ai.employee.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String status;
    private String message;
    private List<ValidationError> errors;

    public ErrorResponse(LocalDateTime timestamp, String status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(LocalDateTime timestamp, String status, String message, List<ValidationError> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
