package com.backend.backend.domain.service.mapper;

import com.backend.backend.domain.dto.CategoryDto;
import com.backend.backend.domain.dto.RequestCategoryDto;
import com.backend.backend.domain.model.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public static Category toCategory(RequestCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
