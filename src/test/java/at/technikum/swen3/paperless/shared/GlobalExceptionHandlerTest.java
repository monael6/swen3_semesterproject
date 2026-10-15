package at.technikum.swen3.paperless.shared;

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

    @SuppressWarnings("unused") private void dummy(String value) {}
}
