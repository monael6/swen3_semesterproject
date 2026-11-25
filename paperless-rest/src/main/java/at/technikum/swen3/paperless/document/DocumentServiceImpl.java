package at.technikum.swen3.paperless.document;

import at.technikum.swen3.paperless.category.Category;
import at.technikum.swen3.paperless.category.CategoryRepository;
import at.technikum.swen3.paperless.shared.InvalidFileException;
import at.technikum.swen3.paperless.shared.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {
    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final byte[] PDF_SIGNATURE = "%PDF-".getBytes(StandardCharsets.US_ASCII);
    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final DocumentMapper mapper;

    public DocumentServiceImpl(DocumentRepository documentRepository,
                               CategoryRepository categoryRepository,
                               DocumentMapper mapper) {
        this.documentRepository = documentRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public DocumentResponse upload(DocumentUploadRequest request, MultipartFile file) {
        requirePdf(file);
        Document saved = documentRepository.save(new Document(
                request.title().trim(), fileNameOf(file), PDF_CONTENT_TYPE, file.getSize(),
                findCategory(request.categoryId())));
        log.info("Uploaded document id={} fileName={} size={} bytes",
                saved.getId(), saved.getFileName(), saved.getFileSize());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> findAll() {
        return documentRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse findById(long id) { return mapper.toResponse(requireDocument(id)); }

    @Override
    public DocumentResponse update(long id, DocumentRequest request) {
        Document document = requireDocument(id);
        mapper.update(request, document);
        document.setTitle(request.title().trim());
        document.setFileName(request.fileName().trim());
        document.setContentType(request.contentType().trim());
        document.setCategory(findCategory(request.categoryId()));
        log.info("Updated document id={}", id);
        return mapper.toResponse(document);
    }

    @Override
    public void delete(long id) {
        documentRepository.delete(requireDocument(id));
        log.info("Deleted document id={}", id);
    }

    /** Accepts only non-empty files that really start with the PDF signature "%PDF-". */
    private void requirePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File must not be empty");
        }
        try (InputStream content = file.getInputStream()) {
            byte[] header = content.readNBytes(PDF_SIGNATURE.length);
            if (!Arrays.equals(header, PDF_SIGNATURE)) {
                log.warn("Rejected upload {}: not a PDF", file.getOriginalFilename());
                throw new InvalidFileException("Only PDF files are allowed");
            }
        } catch (IOException exception) {
            throw new InvalidFileException("File could not be read", exception);
        }
    }

    private String fileNameOf(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            return "document.pdf";
        }
        // Browsers on Windows may send a full path; keep only the file name.
        String fileName = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1).trim();
        if (fileName.isEmpty()) {
            return "document.pdf";
        }
        return fileName.length() > 255 ? fileName.substring(fileName.length() - 255) : fileName;
    }

    private Document requireDocument(long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document " + id + " not found"));
    }

    private Category findCategory(Long id) {
        if (id == null) return null;
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category " + id + " not found"));
    }
}
