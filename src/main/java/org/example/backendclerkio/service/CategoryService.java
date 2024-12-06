package org.example.backendclerkio.service;

import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllCategories() {

        return categoryRepository.findAll(Sort.by("categoryName").ascending());
    }
}
