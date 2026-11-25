package at.technikum.swen3.paperless.category;

import java.util.List;

public interface CategoryService {
    /**
     * @throws at.technikum.swen3.paperless.shared.ConflictException if a category with this name exists
     */
    CategoryResponse create(CategoryRequest request);
    List<CategoryResponse> findAll();
}
