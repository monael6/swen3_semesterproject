package at.technikum.swen3.paperless.document;

import at.technikum.swen3.paperless.category.Category;
import at.technikum.swen3.paperless.category.CategoryRepository;
import at.technikum.swen3.paperless.shared.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {
    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
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
    public DocumentResponse create(DocumentRequest request) {
        Document saved = documentRepository.save(new Document(
                request.title().trim(), request.fileName().trim(), request.contentType().trim(),
                findCategory(request.categoryId())));
        log.info("Created document id={}", saved.getId());
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
