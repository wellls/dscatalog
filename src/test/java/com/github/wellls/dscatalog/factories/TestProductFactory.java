package com.github.wellls.dscatalog.factories;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import com.github.wellls.dscatalog.entities.Category;
import com.github.wellls.dscatalog.entities.Product;

public class TestProductFactory {

    public static Product createDefaultProductWithoutCategories() {
        Product product = new Product(1L,
                "Default Name",
                "Default Description",
                100.0,
                "https://example.com/default.jpg",
                Instant.parse("2023-10-20T10:00:00Z"));
        return product;
    }

    public static Product createProductWithCategory(Category category) {
        Product product = createDefaultProductWithoutCategories();
        return createProduct(product, Collections.singletonList(category));
    }

    public static Product createProduct(Product product, List<Category> categories) {
        Long id = product.getId() != null ? product.getId() : 1L;
        String name = product.getName() != null ? product.getName() : "Default Name";
        String description = product.getDescription() != null ? product.getDescription() : "Default Description";
        Double price = product.getPrice() != null ? product.getPrice() : 100.0;
        String imgUrl = product.getImgUrl() != null ? product.getImgUrl() : "https://example.com/default.jpg";
        Instant date = product.getDate() != null ? product.getDate() : Instant.now();

        product = new Product(id, name, description, price, imgUrl, date);

        if (categories != null && !categories.isEmpty()) {
            product.getCategories().addAll(categories);
        }

        return product;
    }

}
