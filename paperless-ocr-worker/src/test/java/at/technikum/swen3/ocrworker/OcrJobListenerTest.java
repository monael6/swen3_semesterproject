package at.technikum.swen3.ocrworker;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OcrJobListenerTest {
    private final OcrProcessor processor = mock(OcrProcessor.class);
    private final OcrJobListener listener = new OcrJobListener(processor);

    @Test
    void passesValidJobToProcessor() {
        OcrJobMessage job = new OcrJobMessage(1L, "invoice.pdf", "application/pdf", 42L, Instant.now());
        listener.onMessage(job);
        verify(processor).process(job);
    }

    @Test
    void rejectsJobWithoutDocumentId() {
        OcrJobMessage job = new OcrJobMessage(null, "invoice.pdf", "application/pdf", 42L, Instant.now());
        assertThrows(InvalidOcrJobException.class, () -> listener.onMessage(job));
        verifyNoInteractions(processor);
    }

    @Test
    void loggingProcessorAcceptsJob() {
        new LoggingOcrProcessor().process(
                new OcrJobMessage(1L, "invoice.pdf", "application/pdf", 42L, Instant.now()));
    }
}
