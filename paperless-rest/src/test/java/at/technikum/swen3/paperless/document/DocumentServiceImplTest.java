package at.technikum.swen3.paperless.document;

import at.technikum.swen3.paperless.category.Category;
import at.technikum.swen3.paperless.category.CategoryRepository;
import at.technikum.swen3.paperless.shared.InvalidFileException;
import at.technikum.swen3.paperless.shared.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {
    @Mock DocumentRepository documentRepository;
    @Mock CategoryRepository categoryRepository;
    private DocumentServiceImpl service;

    private static MockMultipartFile pdf(String name) {
        return new MockMultipartFile("file", name, "application/pdf", "%PDF-1.4 test".getBytes());
    }

    @BeforeEach
    void setUp() {
        service = new DocumentServiceImpl(documentRepository, categoryRepository,
                Mappers.getMapper(DocumentMapper.class));
    }

    @Test
    void uploadSavesPdfWithMetadataFromFile() {
        Category category = new Category("Invoices");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(documentRepository.save(any(Document.class))).thenAnswer(call -> call.getArgument(0));
        DocumentResponse response = service.upload(new DocumentUploadRequest(" Invoice ", 1L), pdf("invoice.pdf"));
        assertEquals("Invoice", response.title());
        assertEquals("invoice.pdf", response.fileName());
        assertEquals("application/pdf", response.contentType());
        assertEquals(13L, response.fileSize());
        assertEquals("Invoices", response.categoryName());
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void uploadAllowsDocumentWithoutCategory() {
        when(documentRepository.save(any(Document.class))).thenAnswer(call -> call.getArgument(0));
        DocumentResponse response = service.upload(new DocumentUploadRequest("Notes", null), pdf("notes.pdf"));
        assertNull(response.categoryId());
    }

    @Test
    void uploadKeepsOnlyFileNameFromPath() {
        when(documentRepository.save(any(Document.class))).thenAnswer(call -> call.getArgument(0));
        DocumentResponse response = service.upload(new DocumentUploadRequest("Scan", null),
                pdf("C:\\Users\\me\\scan.pdf"));
        assertEquals("scan.pdf", response.fileName());
    }

    @Test
    void uploadRejectsEmptyFile() {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);
        assertThrows(InvalidFileException.class,
                () -> service.upload(new DocumentUploadRequest("Empty", null), empty));
        verifyNoInteractions(documentRepository);
    }

    @Test
    void uploadRejectsFileThatIsNotPdf() {
        MockMultipartFile text = new MockMultipartFile("file", "fake.pdf", "application/pdf", "hello".getBytes());
        assertThrows(InvalidFileException.class,
                () -> service.upload(new DocumentUploadRequest("Fake", null), text));
        verifyNoInteractions(documentRepository);
    }

    @Test
    void findAllMapsDocuments() {
        when(documentRepository.findAll()).thenReturn(List.of(
                new Document("One", "one.pdf", "application/pdf", null),
                new Document("Two", "two.pdf", "application/pdf", null)));
        assertEquals(2, service.findAll().size());
    }

    @Test
    void findByIdReturnsDocument() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(
                new Document("Invoice", "invoice.pdf", "application/pdf", null)));
        assertEquals("Invoice", service.findById(1L).title());
    }

    @Test
    void updateChangesDocument() {
        Document document = new Document("Old", "old.pdf", "application/pdf", null);
        Category category = new Category("Invoices");
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        DocumentResponse response = service.update(1L,
                new DocumentRequest("New", "new.pdf", "application/pdf", 2L));
        assertEquals("New", response.title());
        assertEquals("Invoices", response.categoryName());
    }

    @Test
    void updateWithoutCategoryRemovesExistingCategory() {
        Document document = new Document("Old", "old.pdf", "application/pdf", new Category("Old category"));
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        DocumentResponse response = service.update(1L,
                new DocumentRequest("New", "new.pdf", "application/pdf", null));

        assertEquals("New", response.title());
        assertNull(response.categoryId());
        assertNull(response.categoryName());
    }

    @Test
    void deleteUsesRepository() {
        Document document = new Document("Invoice", "invoice.pdf", "application/pdf", null);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        service.delete(1L);
        verify(documentRepository).delete(document);
    }

    @Test
    void missingDocumentThrowsNotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void missingCategoryThrowsNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.upload(
                new DocumentUploadRequest("Invoice", 99L), pdf("invoice.pdf")));
    }
}
