package com.backend.backend.domain.service.mapper;

import com.backend.backend.domain.dto.RequestCategoryDto;
import com.backend.backend.domain.model.Category;

public class CategoryMapper {
    public static Category toCategory(RequestCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
