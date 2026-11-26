package at.technikum.swen3.paperless.category;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryControllerTest {
    @Test
    void createsAndListsCategories() {
        CategoryRepository repository = mock(CategoryRepository.class);
        CategoryController controller = new CategoryController(repository);
        when(repository.save(any(Category.class))).thenAnswer(call -> call.getArgument(0));
        when(repository.findAll()).thenReturn(List.of(new Category("Invoices")));
        assertEquals("Invoices", controller.create(new CategoryRequest(" Invoices ")).name());
        assertEquals("Invoices", controller.findAll().getFirst().name());
    }
}
