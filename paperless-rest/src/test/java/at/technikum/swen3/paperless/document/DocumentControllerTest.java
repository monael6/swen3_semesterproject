package at.technikum.swen3.paperless.document;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DocumentControllerTest {
    @Test
    void delegatesCrudOperations() {
        DocumentService service = mock(DocumentService.class);
        DocumentController controller = new DocumentController(service);
        DocumentUploadRequest upload = new DocumentUploadRequest("Invoice", null);
        MockMultipartFile file = new MockMultipartFile("file", "invoice.pdf", "application/pdf",
                "%PDF-1.4".getBytes());
        DocumentRequest request = new DocumentRequest("Invoice", "invoice.pdf", "application/pdf", null);
        DocumentResponse response = new DocumentResponse(1L, "Invoice", "invoice.pdf",
                "application/pdf", 8L, Instant.now(), null, null);
        when(service.upload(upload, file)).thenReturn(response);
        when(service.findAll()).thenReturn(List.of(response));
        when(service.findById(1L)).thenReturn(response);
        when(service.update(1L, request)).thenReturn(response);
        assertEquals(response, controller.upload(upload, file));
        assertEquals(1, controller.findAll().size());
        assertEquals(response, controller.findById(1L));
        assertEquals(response, controller.update(1L, request));
        controller.delete(1L);
        verify(service).delete(1L);
    }
}
