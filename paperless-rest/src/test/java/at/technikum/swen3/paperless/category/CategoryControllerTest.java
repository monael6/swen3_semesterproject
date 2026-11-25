package at.technikum.swen3.paperless.category;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryControllerTest {
    @Test
    void delegatesToService() {
        CategoryService service = mock(CategoryService.class);
        CategoryController controller = new CategoryController(service);
        CategoryRequest request = new CategoryRequest("Invoices");
        when(service.create(request)).thenReturn(new CategoryResponse(1L, "Invoices"));
        when(service.findAll()).thenReturn(List.of(new CategoryResponse(1L, "Invoices")));

        assertEquals("Invoices", controller.create(request).name());
        assertEquals(1, controller.findAll().size());
    }
}
