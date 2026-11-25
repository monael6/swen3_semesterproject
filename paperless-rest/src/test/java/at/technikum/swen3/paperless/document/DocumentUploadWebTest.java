package at.technikum.swen3.paperless.document;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Checks the HTTP side of the upload: multipart parsing, validation and error responses. */
@WebMvcTest(DocumentController.class)
class DocumentUploadWebTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean DocumentService service;

    private final MockMultipartFile pdf = new MockMultipartFile(
            "file", "invoice.pdf", "application/pdf", "%PDF-1.4".getBytes());

    @Test
    void uploadReturnsCreatedDocument() throws Exception {
        when(service.upload(any(), any())).thenReturn(new DocumentResponse(
                1L, "Invoice", "invoice.pdf", "application/pdf", 8L, Instant.now(), 2L, "Invoices"));

        mockMvc.perform(multipart("/api/documents").file(pdf)
                        .param("title", "Invoice")
                        .param("categoryId", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("invoice.pdf"))
                .andExpect(jsonPath("$.fileSize").value(8));

        verify(service).upload(eq(new DocumentUploadRequest("Invoice", 2L)), any());
    }

    @Test
    void uploadWithoutTitleIsRejected() throws Exception {
        mockMvc.perform(multipart("/api/documents").file(pdf).param("title", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists());

        verifyNoInteractions(service);
    }

    @Test
    void uploadWithoutFileIsRejected() throws Exception {
        mockMvc.perform(multipart("/api/documents").param("title", "Invoice"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.file").value("must not be empty"));

        verifyNoInteractions(service);
    }
}
