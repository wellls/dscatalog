package com.github.wellls.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wellls.dscatalog.dtos.CategoryDTO;
import com.github.wellls.dscatalog.entities.Category;
import com.github.wellls.dscatalog.repositories.CategoryRepository;
import com.github.wellls.dscatalog.services.exceptions.DatabaseException;
import com.github.wellls.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(category -> new CategoryDTO(category));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO categoryDTO) {
        var category = new Category();
        category.setName(categoryDTO.getName());
        category = categoryRepository.save(category);
        return new CategoryDTO(category.getId(), category.getName());
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(categoryDTO.getName());
        category = categoryRepository.save(category);
        return new CategoryDTO(category);
    }

    @Transactional
    public void delete(Long id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            categoryRepository.delete(category);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity violation. Cannot delete.");
        }
    }
}
