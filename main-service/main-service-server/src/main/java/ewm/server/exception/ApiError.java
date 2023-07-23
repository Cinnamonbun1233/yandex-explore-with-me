package ewm.server.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {
    HttpStatus status;
    String message;
    String reason;
    LocalDateTime timestamp;
}