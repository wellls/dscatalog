package com.github.wellls.dscatalog.factories;

import java.util.Arrays;
import java.util.List;

import com.github.wellls.dscatalog.entities.Category;

public class TestCategoryFactory {

    public static Category createDefaultCategory() {
        return new Category(1L, "Electronics");
    }

    public static Category createCategory(Long id, String name) {
        return new Category(
                id != null ? id : 1L,
                name != null ? name : "Default Category");
    }

    public static List<Category> createDefaultCategoryList() {
        return Arrays.asList(
                new Category(1L, "Electronics"),
                new Category(2L, "Computers"),
                new Category(3L, "Books"),
                new Category(4L, "Fashion"),
                new Category(5L, "Home Appliances"));
    }

    public static List<Category> createCategoryList(Category... categories) {
        return Arrays.asList(categories);
    }
}
