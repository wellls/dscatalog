package com.github.wellls.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wellls.dscatalog.dtos.CategoryDTO;
import com.github.wellls.dscatalog.dtos.ProductDTO;
import com.github.wellls.dscatalog.entities.Category;
import com.github.wellls.dscatalog.entities.Product;
import com.github.wellls.dscatalog.repositories.CategoryRepository;
import com.github.wellls.dscatalog.repositories.ProductRepository;
import com.github.wellls.dscatalog.services.exceptions.DatabaseException;
import com.github.wellls.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> new ProductDTO(product));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return productRepository.findById(id)
                .map(product -> new ProductDTO(product))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Transactional
    public ProductDTO insert(ProductDTO productDTO) {
        var product = new Product();
        copyDtoToEntity(productDTO, product);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        copyDtoToEntity(productDTO, product);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public void delete(Long id) {
        try {
            Product entity = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            productRepository.delete(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity violation. Cannot delete.");
        }
    }

    private void copyDtoToEntity(ProductDTO productDTO, Product product) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImgUrl(productDTO.getImgUrl());
        product.setDate(productDTO.getDate());

        product.getCategories().clear();
        for (CategoryDTO catDto : productDTO.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            product.getCategories().add(category);
        }
    }
}
