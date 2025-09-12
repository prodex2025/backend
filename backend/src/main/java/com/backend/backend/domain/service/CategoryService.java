package com.backend.backend.domain.service;

import com.backend.backend.domain.dto.CategoryDto;
import com.backend.backend.domain.model.Category;
import com.backend.backend.domain.repository.CategoryRepository;
import com.backend.backend.domain.service.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }
}
