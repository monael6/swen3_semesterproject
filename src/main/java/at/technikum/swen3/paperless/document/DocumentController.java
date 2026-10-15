package at.technikum.swen3.paperless.document;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService service;
    public DocumentController(DocumentService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse create(@Valid @RequestBody DocumentRequest request) { return service.create(request); }
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
