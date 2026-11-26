package at.technikum.swen3.paperless.category;

import at.technikum.swen3.paperless.shared.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock CategoryRepository repository;
    @InjectMocks CategoryServiceImpl service;

    @Test
    void createTrimsNameAndSaves() {
        when(repository.existsByNameIgnoreCase("Invoices")).thenReturn(false);
        when(repository.save(any(Category.class))).thenAnswer(call -> call.getArgument(0));

        assertEquals("Invoices", service.create(new CategoryRequest("  Invoices ")).name());
        verify(repository).save(any(Category.class));
    }

    @Test
    void createRejectsDuplicateName() {
        when(repository.existsByNameIgnoreCase("Invoices")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(new CategoryRequest("Invoices")));
        verify(repository, never()).save(any());
    }

    @Test
    void findAllMapsCategories() {
        when(repository.findAll()).thenReturn(List.of(new Category("A"), new Category("B")));

        assertEquals(List.of("A", "B"), service.findAll().stream().map(CategoryResponse::name).toList());
    }
}
