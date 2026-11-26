package at.technikum.swen3.paperless.shared;

import at.technikum.swen3.paperless.messaging.QueuePublishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Translates the exceptions of all layers into HTTP responses with one JSON format ({@link ApiError}):
 * <ul>
 *   <li>Presentation layer (validation, missing file part): 400</li>
 *   <li>Business layer: {@link NotFoundException} 404, {@link InvalidFileException} 400,
 *       {@link ConflictException} 409</li>
 *   <li>Messaging layer: {@link QueuePublishException} 503</li>
 *   <li>Data access layer: constraint violation 409, other {@link DataAccessException} 503</li>
 * </ul>
 * Other errors (broken JSON, an id like "abc", unknown URLs) keep Spring Boot's default responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError handleNotFound(NotFoundException exception) {
        return new ApiError(Instant.now(), 404, exception.getMessage(), Map.of());
    }

    /** Covers JSON bodies (@RequestBody) and form fields (@ModelAttribute). */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleValidation(BindException exception) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(error -> error.getField(),
                        error -> error.getDefaultMessage() == null ? "invalid" : error.getDefaultMessage(),
                        (first, ignored) -> first));
        return new ApiError(Instant.now(), 400, "Validation failed", errors);
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleInvalidFile(InvalidFileException exception) {
        return new ApiError(Instant.now(), 400, "Validation failed", Map.of("file", exception.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleMissingFile(MissingServletRequestPartException exception) {
        return new ApiError(Instant.now(), 400, "Validation failed",
                Map.of(exception.getRequestPartName(), "must not be empty"));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    ApiError handleTooLarge(MaxUploadSizeExceededException exception) {
        log.warn("Rejected upload: {}", exception.getMessage());
        return new ApiError(Instant.now(), 413, "File is too large (max. 10 MB)", Map.of());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError handleConflict(ConflictException exception) {
        return new ApiError(Instant.now(), 409, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(QueuePublishException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    ApiError handleQueueUnavailable(QueuePublishException exception) {
        return new ApiError(Instant.now(), 503,
                "Document could not be queued for processing, please try again later", Map.of());
    }

    /** e.g. a unique constraint in the database was violated (two requests at the same time). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError handleDataConflict(DataIntegrityViolationException exception) {
        log.warn("Database constraint violated: {}", exception.getMostSpecificCause().getMessage());
        return new ApiError(Instant.now(), 409, "Data conflicts with existing data", Map.of());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    ApiError handleDatabaseError(DataAccessException exception) {
        log.error("Database error", exception);
        return new ApiError(Instant.now(), 503, "Database is not available, please try again later", Map.of());
    }
}
