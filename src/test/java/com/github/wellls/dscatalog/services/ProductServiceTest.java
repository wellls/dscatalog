package com.github.wellls.dscatalog.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import com.github.wellls.dscatalog.dtos.ProductDTO;
import com.github.wellls.dscatalog.entities.Category;
import com.github.wellls.dscatalog.entities.Product;
import com.github.wellls.dscatalog.repositories.CategoryRepository;
import com.github.wellls.dscatalog.repositories.ProductRepository;
import com.github.wellls.dscatalog.services.exceptions.DatabaseException;
import com.github.wellls.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Product product;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category(1L, "Books");
        product = new Product(1L, "Book", "A good book", 50.0, "http://image", Instant.now());
        product.getCategories().add(category);
        productDTO = new ProductDTO(product);
    }

    @Test
    void findAllShouldReturnPageOfProductDTO() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = productService.findAll(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(product.getId());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void findByIdShouldThrowWhenIdDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository).findById(99L);
    }

    @Test
    void insertShouldSaveAndReturnProductDTO() {
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.insert(productDTO);

        assertThat(result.getName()).isEqualTo(product.getName());
        verify(productRepository).save(argThat(saved -> saved.getName().equals("Book") &&
                saved.getCategories().contains(category)));
    }

    @Test
    void updateShouldUpdateAndReturnProductDTOWhenIdExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.update(1L, productDTO);

        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateShouldThrowWhenIdDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, productDTO))
                .isInstanceOf(EntityNotFoundException.class);

        verify(productRepository).findById(99L);
    }

    @Test
    void deleteShouldDeleteProductWhenIdExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository).findById(99L);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionOnIntegrityViolation() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doThrow(DataIntegrityViolationException.class).when(productRepository).delete(product);

        assertThatThrownBy(() -> productService.delete(1L))
                .isInstanceOf(DatabaseException.class);

        verify(productRepository).delete(product);
    }
}
