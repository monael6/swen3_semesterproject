package at.technikum.swen3.paperless.document;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    private final DocumentService service;
    public DocumentController(DocumentService service) { this.service = service; }

    /** Upload a PDF from the web UI: multipart form with the fields "file", "title" and optional "categoryId". */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse upload(@Valid @ModelAttribute DocumentUploadRequest request,
                                   @RequestPart("file") MultipartFile file) {
        log.info("POST /api/documents file={} size={} bytes", file.getOriginalFilename(), file.getSize());
        return service.upload(request, file);
    }
    @GetMapping
    public List<DocumentResponse> findAll() {
        log.debug("GET /api/documents");
        return service.findAll();
    }
    @GetMapping("/{id}")
    public DocumentResponse findById(@PathVariable long id) {
        log.debug("GET /api/documents/{}", id);
        return service.findById(id);
    }
    @PutMapping("/{id}")
    public DocumentResponse update(@PathVariable long id, @Valid @RequestBody DocumentRequest request) {
        log.info("PUT /api/documents/{}", id);
        return service.update(id, request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("DELETE /api/documents/{}", id);
        service.delete(id);
    }
}
