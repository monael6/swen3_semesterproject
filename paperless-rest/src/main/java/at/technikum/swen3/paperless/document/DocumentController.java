package at.technikum.swen3.paperless.document;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService service;
    public DocumentController(DocumentService service) { this.service = service; }

    /** Upload a PDF from the web UI: multipart form with the fields "file", "title" and optional "categoryId". */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse upload(@Valid @ModelAttribute DocumentUploadRequest request,
                                   @RequestPart("file") MultipartFile file) {
        return service.upload(request, file);
    }
    @GetMapping
    public List<DocumentResponse> findAll() { return service.findAll(); }
    @GetMapping("/{id}")
    public DocumentResponse findById(@PathVariable long id) { return service.findById(id); }
    @PutMapping("/{id}")
    public DocumentResponse update(@PathVariable long id, @Valid @RequestBody DocumentRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) { service.delete(id); }
}
