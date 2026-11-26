package at.technikum.swen3.paperless.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

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
}
