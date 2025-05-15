package com.github.wellls.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.wellls.dscatalog.dtos.CategoryDTO;
import com.github.wellls.dscatalog.entities.Category;
import com.github.wellls.dscatalog.repositories.CategoryRepository;
import com.github.wellls.dscatalog.services.exceptions.DatabaseException;
import com.github.wellls.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Category category;
    private CategoryDTO categoryDTO;
    private PageImpl<Category> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        category = new Category(existingId, "Computadores");
        categoryDTO = new CategoryDTO(existingId, "Computadores");
        page = new PageImpl<>(List.of(category));

        // findAll
        when(repository.findAll((Pageable) ArgumentMatchers.any()))
                .thenReturn(page);

        // findById
        when(repository.findById(existingId))
                .thenReturn(Optional.of(category));
        when(repository.findById(dependentId))
                .thenReturn(Optional.of(category));
        when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        // save
        when(repository.save(ArgumentMatchers.any()))
                .thenReturn(category);

        // delete
        doNothing().when(repository).delete(ArgumentMatchers.any());
    }

    @Test
    public void findAllShouldReturnPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<CategoryDTO> result = service.findAll(pageable);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenIdExists() {
        // Act
        CategoryDTO result = service.findById(existingId);

        // Assert
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals(category.getName(), result.getName());
        verify(repository, times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void insertShouldReturnCategoryDTO() {
        // Assert
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        // Act
        CategoryDTO result = service.insert(categoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(category.getId(), result.getId());
        assertEquals(category.getName(), result.getName());

        verify(repository, times(1)).save(categoryCaptor.capture());
        Category capturedCategory = categoryCaptor.getValue();
        assertEquals(categoryDTO.getName(), capturedCategory.getName());
    }

    @Test
    public void updateShouldReturnCategoryDTOWhenIdExists() {
        // Act
        CategoryDTO result = service.update(existingId, categoryDTO);

        // Assert
        assertNotNull(result);
        assertEquals(existingId, result.getId());
        assertEquals(categoryDTO.getName(), result.getName());

        verify(repository, times(1)).findById(existingId);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Assert & Act
        assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, categoryDTO);
        });

        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        doNothing().when(repository).delete(category);

        // Act & Assert
        assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).delete(category);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        // Arrange
        doThrow(new DataIntegrityViolationException("Integrity violation"))
                .when(repository).delete(category);

        // Act & Assert
        assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        verify(repository, times(1)).findById(dependentId);
        verify(repository, times(1)).delete(category);
    }
}
