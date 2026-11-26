package at.technikum.swen3.paperless.category;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService service;

    public CategoryController(CategoryService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        log.info("POST /api/categories");
        return service.create(request);
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        log.debug("GET /api/categories");
        return service.findAll();
    }
}
