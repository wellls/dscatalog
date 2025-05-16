package com.github.wellls.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.github.wellls.dscatalog.dtos.ProductDTO;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ProductServiceIntegrationTest {
    @Autowired
    private ProductService productService;

    @Test
    void findAllShouldReturnSeededProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = productService.findAll(pageable);

        assertEquals(25, result.getTotalElements());
        assertEquals(10, result.getSize());
        assertEquals("The Lord of the Rings", result.getContent().get(0).getName());
        assertTrue(
                result.getContent().stream().noneMatch(p -> p.getId().equals(11L)),
                "Product with ID 11 should be missing from the list");
    }

}