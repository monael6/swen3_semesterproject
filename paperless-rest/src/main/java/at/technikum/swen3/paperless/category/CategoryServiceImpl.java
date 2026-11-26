package at.technikum.swen3.paperless.category;

import at.technikum.swen3.paperless.shared.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) { this.repository = repository; }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            log.warn("Rejected duplicate category name '{}'", name);
            throw new ConflictException("Category '" + name + "' already exists");
        }
        Category saved = repository.save(new Category(name));
        log.info("Created category id={} name='{}'", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
