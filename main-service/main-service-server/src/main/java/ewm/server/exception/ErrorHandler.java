package ewm.server.exception;

import ewm.server.exception.category.CategoryNotFoundException;
import ewm.server.exception.compilation.CompilationNotFoundException;
import ewm.server.exception.event.EventNotFoundException;
import ewm.server.exception.event.IllegalPublicationException;
import ewm.server.exception.place.PlaceNotFoundException;
import ewm.server.exception.request.IllegalRequestException;
import ewm.server.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            UserNotFoundException.class,
            CategoryNotFoundException.class,
            EventNotFoundException.class,
            CompilationNotFoundException.class,
            PlaceNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundExceptions(final RuntimeException runtimeException) {

        log.error(runtimeException.getMessage());

        return ApiError
                .builder()
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .message(runtimeException.getMessage())
                .reason(runtimeException.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler({
            IllegalPublicationException.class,
            IllegalRequestException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handeConflicts(final RuntimeException runtimeException) {

        log.error(runtimeException.getMessage());

        return ApiError
                .builder()
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .message(runtimeException.getMessage())
                .reason(runtimeException.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUniqueConstraintViolation(final DataIntegrityViolationException dataIntegrityViolationException) {

        log.error(dataIntegrityViolationException.getMessage());

        return ApiError
                .builder()
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .message(dataIntegrityViolationException.getMessage())
                .reason(dataIntegrityViolationException.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleException(final Exception exception) {

        log.error(exception.getMessage());

        return ApiError
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .reason(exception.getLocalizedMessage())
                .build();
    }
}