package at.technikum.swen3.paperless.shared;

import at.technikum.swen3.paperless.messaging.QueuePublishException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsNotFound() {
        assertEquals(404, handler.handleNotFound(new NotFoundException("missing")).status());
    }

    @Test
    void mapsValidation() throws Exception {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(new Object(), "request");
        result.addError(new FieldError("request", "title", "must not be blank"));
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class);
        ApiError error = handler.handleValidation(new MethodArgumentNotValidException(
                new MethodParameter(method, 0), result));
        assertEquals("must not be blank", error.validationErrors().get("title"));
    }

    @Test
    void mapsInvalidFileToFileError() {
        ApiError error = handler.handleInvalidFile(new InvalidFileException("Only PDF files are allowed"));
        assertEquals(400, error.status());
        assertEquals("Only PDF files are allowed", error.validationErrors().get("file"));
    }

    @Test
    void mapsConflictTo409() {
        assertEquals(409, handler.handleConflict(new ConflictException("exists")).status());
    }

    @Test
    void mapsQueueFailureTo503() {
        assertEquals(503, handler.handleQueueUnavailable(new QueuePublishException("down", null)).status());
    }

    @SuppressWarnings("unused") private void dummy(String value) {}
}
