package com.github.wellls.dscatalog.factories;

import java.time.Instant;

import com.github.wellls.dscatalog.dtos.ProductDTO;
import com.github.wellls.dscatalog.entities.Category;
import com.github.wellls.dscatalog.entities.Product;

public class ProductFactory {
    public static Product createProduct() {
        Product product = new Product(
                1L,
                "Phone XS",
                "Latest generation smartphone with advanced features",
                1299.99,
                "https://example.com/images/phonexs.jpg",
                Instant.parse("2023-10-20T10:00:00Z"));
        product.getCategories().add(new Category(1L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product);
    }
}